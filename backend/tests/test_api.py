import math

import pytest
from fastapi.testclient import TestClient

from app.classifier import classify_by_rules
from app.features import normalize_angle, window_features
from app.main import app

client = TestClient(app)
G = 9.81


def window(pitch_deg: float = 0, roll_deg: float = 0, gyro: float = 0.02, n: int = 75) -> list[list[float]]:
    p, r = math.radians(pitch_deg), math.radians(roll_deg)
    sample = [-G * math.sin(p), G * math.cos(p) * math.sin(r), G * math.cos(p) * math.cos(r)]
    return [[*sample, gyro if i % 2 else -gyro, 0.0, 0.0] for i in range(n)]


def body(samples: list[list[float]], **extra: float) -> dict:
    return {"samples": samples, "baseline_pitch": 0, "baseline_roll": 0, "tilt_threshold": 18, **extra}


def test_health() -> None:
    response = client.get("/health")
    assert response.status_code == 200
    assert response.json()["status"] == "ok"


def test_features_recuperan_la_inclinacion() -> None:
    features = window_features(window(pitch_deg=36), 0, 0, 18)
    assert features[0] == pytest.approx(2.0, abs=0.01)
    assert features[1] == pytest.approx(0.0, abs=0.01)


def test_normaliza_angulos() -> None:
    assert normalize_angle(340) == pytest.approx(-20)


@pytest.mark.parametrize(
    ("pitch", "roll", "gyro", "expected"),
    [
        (2, 1, 0.02, "aligned"),
        (34, 3, 0.02, "forward_tilt"),
        (4, 31, 0.02, "side_tilt"),
        (5, 5, 1.5, "moving"),
    ],
)
def test_clasifica_cada_patron(pitch: float, roll: float, gyro: float, expected: str) -> None:
    response = client.post("/v1/classify", json=body(window(pitch, roll, gyro)))
    assert response.status_code == 200
    data = response.json()
    assert data["posture"] == expected
    assert 0 <= data["confidence"] <= 1


def test_la_clasificacion_es_relativa_a_la_referencia() -> None:
    response = client.post("/v1/classify", json=body(window(pitch_deg=30), baseline_pitch=30))
    assert response.json()["posture"] == "aligned"


def test_reglas_respetan_el_umbral() -> None:
    assert classify_by_rules([0.9, 0.0, 0.1, 0.02, 0.0])[0] == "aligned"
    assert classify_by_rules([1.1, 0.0, 0.1, 0.02, 0.0])[0] == "forward_tilt"


def test_rechaza_lecturas_mal_formadas() -> None:
    assert client.post("/v1/classify", json=body([[0.0, 0.0, 9.8]] * 20)).status_code == 422
    assert client.post("/v1/classify", json=body(window(n=3))).status_code == 422


def test_resumen_tiene_respaldo_sin_gemini() -> None:
    payload = {"mode": "study", "duration_seconds": 1500, "aligned_percent": 82, "alerts": 2, "language": "es"}
    data = client.post("/v1/sessions/summary", json=payload).json()
    assert data["generated"] is False
    assert "82%" in data["text"]


def test_consejo_en_ingles_y_sin_lenguaje_medico() -> None:
    data = client.post("/v1/tips", json={"posture": "forward_tilt", "language": "en"}).json()
    assert data["generated"] is False
    assert "screen" in data["text"]
    assert not any(word in data["text"].lower() for word in ("diagnos", "injur", "disease"))
