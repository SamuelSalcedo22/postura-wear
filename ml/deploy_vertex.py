"""Sube el modelo entrenado a Vertex AI y lo despliega en un endpoint.

Requisitos previos:
    1. `python ml/train.py` (crea ml/artifacts/model.joblib).
    2. `gcloud auth application-default login` con permisos de Vertex AI y Storage.
    3. Variables de entorno (ver `.env.example`): GOOGLE_CLOUD_PROJECT,
       GOOGLE_CLOUD_LOCATION, VERTEX_STAGING_BUCKET y VERTEX_SERVING_IMAGE.

Uso:
    python ml/deploy_vertex.py

Al terminar imprime el ID del endpoint: cópialo en VERTEX_ENDPOINT_ID del backend.
Un endpoint desplegado tiene costo mientras exista; elimínalo cuando no lo uses.
"""

from __future__ import annotations

import os
import sys
from pathlib import Path

ARTIFACTS = Path(__file__).resolve().parent / "artifacts"
REQUIRED = ("GOOGLE_CLOUD_PROJECT", "GOOGLE_CLOUD_LOCATION", "VERTEX_STAGING_BUCKET", "VERTEX_SERVING_IMAGE")


def main() -> None:
    missing = [name for name in REQUIRED if not os.getenv(name)]
    if missing:
        sys.exit(f"Faltan variables de entorno: {', '.join(missing)}. Revisa .env.example.")
    if not (ARTIFACTS / "model.joblib").exists():
        sys.exit("No existe ml/artifacts/model.joblib. Ejecuta primero: python ml/train.py")

    from google.cloud import aiplatform

    aiplatform.init(
        project=os.environ["GOOGLE_CLOUD_PROJECT"],
        location=os.environ["GOOGLE_CLOUD_LOCATION"],
        staging_bucket=os.environ["VERTEX_STAGING_BUCKET"],
    )

    # upload() copia la carpeta local de artefactos al bucket de staging.
    model = aiplatform.Model.upload(
        display_name="postura-classifier",
        artifact_uri=str(ARTIFACTS),
        serving_container_image_uri=os.environ["VERTEX_SERVING_IMAGE"],
    )
    endpoint = model.deploy(
        machine_type=os.getenv("VERTEX_MACHINE_TYPE", "n1-standard-2"),
        min_replica_count=1,
        max_replica_count=1,
    )
    print(f"Modelo: {model.resource_name}")
    print(f"VERTEX_ENDPOINT_ID={endpoint.name}")


if __name__ == "__main__":
    main()
