"""Características de una ventana de sensores.

Mismo cálculo que `FeatureExtractor.kt` en la app. Las inclinaciones se expresan
como desviación absoluta respecto a la referencia calibrada, dividida por el
umbral de sensibilidad, de modo que el modelo aprende una frontera cercana a 1.0 que sirve
para cualquier usuario y cualquier sensibilidad.
"""

from __future__ import annotations

import math
from collections.abc import Sequence

FEATURE_NAMES = ["pitch_ratio", "roll_ratio", "accel_std", "gyro_mean", "gyro_std"]


def _mean(values: Sequence[float]) -> float:
    return sum(values) / len(values)


def _std(values: Sequence[float], mean: float) -> float:
    return math.sqrt(sum((v - mean) ** 2 for v in values) / len(values))


def normalize_angle(deg: float) -> float:
    deg = math.fmod(deg, 360.0)
    if deg > 180:
        deg -= 360
    if deg < -180:
        deg += 360
    return deg


def orientation(ax: float, ay: float, az: float) -> tuple[float, float]:
    """Cabeceo y alabeo (grados) a partir del vector de gravedad promedio."""
    pitch = math.degrees(math.atan2(-ax, math.sqrt(ay * ay + az * az)))
    roll = math.degrees(math.atan2(ay, az))
    return pitch, roll


def window_features(
    samples: Sequence[Sequence[float]],
    baseline_pitch: float,
    baseline_roll: float,
    tilt_threshold: float,
) -> list[float]:
    ax, ay, az = ([s[i] for s in samples] for i in range(3))
    mx, my, mz = _mean(ax), _mean(ay), _mean(az)
    pitch, roll = orientation(mx, my, mz)
    gyro = [math.sqrt(s[3] ** 2 + s[4] ** 2 + s[5] ** 2) for s in samples]
    gyro_mean = _mean(gyro)
    accel_std = math.sqrt(_std(ax, mx) ** 2 + _std(ay, my) ** 2 + _std(az, mz) ** 2)
    return [
        abs(normalize_angle(pitch - baseline_pitch)) / tilt_threshold,
        abs(normalize_angle(roll - baseline_roll)) / tilt_threshold,
        accel_std,
        gyro_mean,
        _std(gyro, gyro_mean),
    ]
