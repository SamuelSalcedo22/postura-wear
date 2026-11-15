FROM python:3.12-slim

ENV PYTHONDONTWRITEBYTECODE=1 \
    PYTHONUNBUFFERED=1

WORKDIR /srv

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY app ./app
# El modelo entrenado es opcional: sin él, el servicio clasifica por reglas.
COPY model ./model

# Cloud Run ejecuta el contenedor sin privilegios y define $PORT.
RUN useradd --create-home appuser
USER appuser

CMD ["sh", "-c", "uvicorn app.main:app --host 0.0.0.0 --port ${PORT:-8080}"]
