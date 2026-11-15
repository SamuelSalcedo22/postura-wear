"""Configuración del backend. Todo sale de variables de entorno: no hay valores fijos
de proyecto, región ni modelo, y ningún secreto vive en el código."""

from __future__ import annotations

import os
from dataclasses import dataclass
from pathlib import Path


@dataclass(frozen=True)
class Settings:
    project: str | None
    location: str | None
    gemini_model: str | None
    vertex_endpoint_id: str | None
    model_path: Path

    @property
    def gemini_enabled(self) -> bool:
        return bool(self.project and self.location and self.gemini_model)

    @property
    def vertex_classifier_enabled(self) -> bool:
        return bool(self.project and self.location and self.vertex_endpoint_id)


def load_settings() -> Settings:
    default_model = Path(__file__).resolve().parent.parent / "model" / "posture.joblib"
    return Settings(
        project=os.getenv("GOOGLE_CLOUD_PROJECT") or None,
        location=os.getenv("GOOGLE_CLOUD_LOCATION") or None,
        gemini_model=os.getenv("GEMINI_MODEL") or None,
        vertex_endpoint_id=os.getenv("VERTEX_ENDPOINT_ID") or None,
        model_path=Path(os.getenv("MODEL_PATH") or default_model),
    )
