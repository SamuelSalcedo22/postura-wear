"""Entrena el clasificador ligero de postura (scikit-learn) y lo deja listo para
el backend y para Vertex AI.

Uso:
    python ml/train.py                      # genera datos sintéticos y entrena
    python ml/train.py --data mis_datos.csv # entrena con un CSV propio

Salidas:
    backend/model/posture.joblib   -> lo carga el backend en local o en Cloud Run
    ml/artifacts/model.joblib      -> lo sube ml/deploy_vertex.py a Vertex AI
"""

from __future__ import annotations

import argparse
import csv
import shutil
import sys
from pathlib import Path

import joblib
from sklearn.linear_model import LogisticRegression
from sklearn.metrics import classification_report
from sklearn.model_selection import train_test_split
from sklearn.pipeline import make_pipeline
from sklearn.preprocessing import StandardScaler

ROOT = Path(__file__).resolve().parent.parent
sys.path.insert(0, str(ROOT / "ml"))

from generate_synthetic import FEATURE_NAMES, generate  # noqa: E402


def load_csv(path: Path) -> tuple[list[list[float]], list[str]]:
    with path.open(encoding="utf-8") as f:
        rows = list(csv.DictReader(f))
    return [[float(r[name]) for name in FEATURE_NAMES] for r in rows], [r["label"] for r in rows]


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("--data", type=Path, help="CSV con las columnas de características y 'label'")
    parser.add_argument("--rows", type=int, default=6000, help="ventanas sintéticas si no se pasa --data")
    args = parser.parse_args()

    if args.data:
        x, y = load_csv(args.data)
    else:
        examples = generate(args.rows)
        x, y = [e[0] for e in examples], [e[1] for e in examples]

    x_train, x_test, y_train, y_test = train_test_split(x, y, test_size=0.2, random_state=7, stratify=y)

    # Modelo deliberadamente pequeño: responde en milisegundos y cabe en cualquier contenedor.
    model = make_pipeline(StandardScaler(), LogisticRegression(max_iter=2000, C=10.0))
    model.fit(x_train, y_train)
    print(classification_report(y_test, model.predict(x_test), digits=3))

    backend_path = ROOT / "backend" / "model" / "posture.joblib"
    vertex_path = ROOT / "ml" / "artifacts" / "model.joblib"
    vertex_path.parent.mkdir(parents=True, exist_ok=True)
    joblib.dump(model, backend_path)
    shutil.copyfile(backend_path, vertex_path)
    print(f"Modelo guardado en {backend_path} y {vertex_path}")


if __name__ == "__main__":
    main()
