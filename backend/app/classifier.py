"""Clasificador de postura con tres niveles, del más capaz al más simple:

1. Endpoint de Vertex AI (si `VERTEX_ENDPOINT_ID` está definido).
2. Modelo local entrenado con `ml/train.py` (si existe el archivo `.joblib`).
3. Reglas por umbrales, las mismas que usa el reloj sin conexión.

Si un nivel falla, se pasa al siguiente: el servicio siempre responde algo útil.
"""

from __future__ import annotations

import logging
from typing import Any

from .config import Settings
from .features import window_features
from .schemas import ClassifyRequest, ClassifyResponse

log = logging.getLogger(__name__)

LABELS = ("aligned", "forward_tilt", "side_tilt", "moving")
MOVING_GYRO_MEAN = 0.6
MOVING_ACCEL_STD = 1.8


def classify_by_rules(features: list[float]) -> tuple[str, float]:
    pitch_ratio, roll_ratio, accel_std, gyro_mean, _ = features
    if gyro_mean > MOVING_GYRO_MEAN or accel_std > MOVING_ACCEL_STD:
        return "moving", 0.8
    pitch, roll = abs(pitch_ratio), abs(roll_ratio)
    worst = max(pitch, roll)
    confidence = min(0.99, max(0.5, 0.5 + abs(worst - 1.0) * 0.5))
    if worst < 1.0:
        return "aligned", confidence
    return ("forward_tilt" if pitch >= roll else "side_tilt"), confidence


class PostureClassifier:
    def __init__(self, settings: Settings) -> None:
        self._settings = settings
        self._local_model: Any = None
        self._endpoint: Any = None
        self._load_local_model()

    def _load_local_model(self) -> None:
        path = self._settings.model_path
        if not path.exists():
            log.info("Sin modelo local en %s: se usarán reglas.", path)
            return
        try:
            import joblib

            self._local_model = joblib.load(path)
            log.info("Modelo local cargado desde %s.", path)
        except Exception:  # noqa: BLE001 - un modelo dañado no debe tumbar el servicio
            log.exception("No se pudo cargar el modelo local; se usarán reglas.")

    def _vertex_endpoint(self) -> Any:
        if self._endpoint is None:
            from google.cloud import aiplatform

            aiplatform.init(project=self._settings.project, location=self._settings.location)
            self._endpoint = aiplatform.Endpoint(self._settings.vertex_endpoint_id)
        return self._endpoint

    def classify(self, request: ClassifyRequest) -> ClassifyResponse:
        features = window_features(
            request.samples, request.baseline_pitch, request.baseline_roll, request.tilt_threshold
        )

        if self._settings.vertex_classifier_enabled:
            try:
                prediction = self._vertex_endpoint().predict(instances=[features]).predictions[0]
                if prediction in LABELS:
                    return ClassifyResponse(posture=prediction, confidence=0.85, engine="vertex")
            except Exception:  # noqa: BLE001
                log.exception("Falló el endpoint de Vertex AI; se sigue con el modelo local.")

        if self._local_model is not None:
            try:
                probabilities = self._local_model.predict_proba([features])[0]
                best = int(probabilities.argmax())
                label = str(self._local_model.classes_[best])
                if label in LABELS:
                    return ClassifyResponse(posture=label, confidence=float(probabilities[best]), engine="model")
            except Exception:  # noqa: BLE001
                log.exception("Falló el modelo local; se sigue con reglas.")

        posture, confidence = classify_by_rules(features)
        return ClassifyResponse(posture=posture, confidence=confidence, engine="rules")
