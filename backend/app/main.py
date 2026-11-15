"""API del Entrenador de Postura. Pensada para Cloud Run: sin estado y sin guardar
las lecturas que recibe."""

from __future__ import annotations

import logging

from fastapi import FastAPI
from fastapi.concurrency import run_in_threadpool

from .classifier import PostureClassifier
from .coach import Coach
from .config import load_settings
from .schemas import ClassifyRequest, ClassifyResponse, SummaryRequest, TextResponse, TipRequest

logging.basicConfig(level=logging.INFO)

settings = load_settings()
classifier = PostureClassifier(settings)
coach = Coach(settings)

app = FastAPI(
    title="Postura Wear API",
    description="Prototipo experimental de ergonomía. No ofrece diagnósticos médicos.",
    version="0.1.0",
)


@app.get("/health")
def health() -> dict[str, object]:
    return {
        "status": "ok",
        "gemini": settings.gemini_enabled,
        "vertex_classifier": settings.vertex_classifier_enabled,
    }


@app.post("/v1/classify", response_model=ClassifyResponse)
async def classify(request: ClassifyRequest) -> ClassifyResponse:
    return await run_in_threadpool(classifier.classify, request)


@app.post("/v1/tips", response_model=TextResponse)
async def tips(request: TipRequest) -> TextResponse:
    return await run_in_threadpool(coach.tip, request)


@app.post("/v1/sessions/summary", response_model=TextResponse)
async def session_summary(request: SummaryRequest) -> TextResponse:
    return await run_in_threadpool(coach.summary, request)
