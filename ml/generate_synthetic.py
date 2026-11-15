"""Genera ventanas sintéticas de acelerómetro y giroscopio, ya convertidas en
características, para entrenar el clasificador de postura.

Sigue el mismo modelo físico que el simulador de la app (`SyntheticMotion.kt`):
la gravedad se proyecta según el cabeceo y el alabeo, y se añade ruido.
Son datos de ejemplo: para un modelo real hay que grabar personas reales.

Uso:
    python ml/generate_synthetic.py --rows 4000 --out ml/data/generated/windows.csv
"""

from __future__ import annotations

import argparse
import csv
import math
import random
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent.parent / "backend"))

from app.features import FEATURE_NAMES, window_features  # noqa: E402

G = 9.81
SAMPLE_RATE_HZ = 25
WINDOW_SECONDS = 3
LABELS = ("aligned", "forward_tilt", "side_tilt", "moving")


def make_window(rng: random.Random, pitch: float, roll: float, gyro: float, noise: float) -> list[list[float]]:
    p, r = math.radians(pitch), math.radians(roll)
    base = (-G * math.sin(p), G * math.cos(p) * math.sin(r), G * math.cos(p) * math.cos(r))
    # Parte del giro es sostenida (un giro continuo) y parte es temblor: así el
    # modelo no aprende que "moverse" siempre significa giro irregular.
    steady = rng.random()
    axis = rng.randrange(3)
    bias = [gyro * steady * rng.choice((-1, 1)) if i == axis else 0.0 for i in range(3)]
    jitter = gyro * (1 - steady)
    return [
        [*(v + rng.uniform(-noise, noise) for v in base), *(b + rng.uniform(-jitter, jitter) for b in bias)]
        for _ in range(SAMPLE_RATE_HZ * WINDOW_SECONDS)
    ]


def make_example(rng: random.Random, label: str) -> list[float]:
    """Una ventana con su referencia y umbral aleatorios, como características."""
    threshold = rng.choice((12.0, 18.0, 26.0))
    base_pitch, base_roll = rng.uniform(-40, 40), rng.uniform(-40, 40)
    sign = rng.choice((-1, 1))
    small = lambda: rng.uniform(-0.75, 0.75) * threshold  # noqa: E731
    big = lambda: sign * rng.uniform(1.25, 3.0) * threshold  # noqa: E731

    if label == "aligned":
        d_pitch, d_roll, gyro, noise = small(), small(), rng.uniform(0.01, 0.15), rng.uniform(0.03, 0.3)
    elif label == "forward_tilt":
        d_pitch, d_roll, gyro, noise = big(), small(), rng.uniform(0.01, 0.15), rng.uniform(0.03, 0.3)
    elif label == "side_tilt":
        d_pitch, d_roll, gyro, noise = small(), big(), rng.uniform(0.01, 0.15), rng.uniform(0.03, 0.3)
    else:  # moving
        d_pitch, d_roll = rng.uniform(-2, 2) * threshold, rng.uniform(-2, 2) * threshold
        # Moverse puede notarse en el giroscopio, en el acelerómetro o en ambos.
        kind = rng.choice(("gyro", "accel", "both"))
        gyro = rng.uniform(0.01, 0.4) if kind == "accel" else rng.uniform(0.9, 3.0)
        noise = rng.uniform(0.03, 1.0) if kind == "gyro" else rng.uniform(2.3, 4.0)

    window = make_window(rng, base_pitch + d_pitch, base_roll + d_roll, gyro, noise)
    return window_features(window, base_pitch, base_roll, threshold)


def generate(rows: int, seed: int = 7) -> list[tuple[list[float], str]]:
    rng = random.Random(seed)
    return [(make_example(rng, label), label) for i in range(rows) for label in (LABELS[i % len(LABELS)],)]


def write_csv(path: Path, examples: list[tuple[list[float], str]]) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    with path.open("w", newline="", encoding="utf-8") as f:
        writer = csv.writer(f)
        writer.writerow([*FEATURE_NAMES, "label"])
        for features, label in examples:
            writer.writerow([*(f"{v:.5f}" for v in features), label])


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("--rows", type=int, default=4000)
    parser.add_argument("--seed", type=int, default=7)
    parser.add_argument("--out", type=Path, default=Path("ml/data/generated/windows.csv"))
    args = parser.parse_args()
    write_csv(args.out, generate(args.rows, args.seed))
    print(f"{args.rows} ventanas escritas en {args.out}")
