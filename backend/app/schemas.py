"""Contrato con la app (ver `core/.../network/ApiModels.kt`)."""

from __future__ import annotations

from typing import Literal

from pydantic import BaseModel, Field, field_validator

Posture = Literal["aligned", "forward_tilt", "side_tilt", "moving", "unknown"]


class ClassifyRequest(BaseModel):
    # Cada lectura es [ax, ay, az, gx, gy, gz]. Los límites evitan cargas abusivas.
    samples: list[list[float]] = Field(min_length=10, max_length=500)
    sample_rate_hz: int = Field(default=25, ge=1, le=200)
    baseline_pitch: float = Field(ge=-180, le=180)
    baseline_roll: float = Field(ge=-180, le=180)
    tilt_threshold: float = Field(default=18, ge=5, le=60)

    @field_validator("samples")
    @classmethod
    def six_values_per_sample(cls, samples: list[list[float]]) -> list[list[float]]:
        if any(len(s) != 6 for s in samples):
            raise ValueError("cada lectura debe tener 6 valores: ax, ay, az, gx, gy, gz")
        return samples


class ClassifyResponse(BaseModel):
    posture: Posture
    confidence: float = Field(ge=0, le=1)
    # Quién respondió: "vertex", "model" o "rules". Útil para depurar; la app lo ignora.
    engine: str


class TipRequest(BaseModel):
    posture: Posture
    mode: Literal["study", "work"] = "study"
    language: str = Field(default="es", max_length=8)


class SummaryRequest(BaseModel):
    mode: Literal["study", "work"]
    duration_seconds: int = Field(ge=0, le=24 * 3600)
    aligned_percent: int = Field(ge=0, le=100)
    alerts: int = Field(ge=0, le=10_000)
    language: str = Field(default="es", max_length=8)


class TextResponse(BaseModel):
    text: str
    # Verdadero si lo redactó Gemini; falso si es un texto de respaldo.
    generated: bool
