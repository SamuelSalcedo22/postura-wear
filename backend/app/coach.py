"""Consejos breves y resúmenes de sesión con Gemini en Vertex AI.

El modelo, el proyecto y la región salen de variables de entorno. Si Gemini no
está configurado o falla, se responde con un texto de respaldo y `generated=False`.
"""

from __future__ import annotations

import logging
from typing import Any

from .config import Settings
from .schemas import SummaryRequest, TextResponse, TipRequest

log = logging.getLogger(__name__)

SYSTEM_INSTRUCTION = """\
Eres la voz de una app de reloj que acompaña la postura al estudiar o trabajar.
Voz: cercana, calmada y motivadora. Hablas de tú.
Reglas estrictas:
- Es un prototipo experimental de ergonomía. Nunca diagnostiques ni menciones
  enfermedades, lesiones, dolor, tratamientos ni medicamentos.
- Habla de "posible mala postura" y ofrece solo recomendaciones generales.
- Responde en el idioma indicado, en texto plano, sin listas, emojis ni comillas.
- Máximo 2 frases y 220 caracteres: se lee en una pantalla muy pequeña.
"""

FALLBACK_TIPS = {
    "es": {
        "aligned": "Vas muy bien. Mantén los hombros sueltos y respira con calma.",
        "forward_tilt": "Lleva la espalda al respaldo y sube un poco la pantalla, a la altura de los ojos.",
        "side_tilt": "Reparte el peso en ambos lados y apoya los dos pies en el piso.",
        "moving": "Moverte también ayuda. Al volver, acomódate con la espalda apoyada.",
        "unknown": "Cada 30 minutos, levántate y estira el cuello y los hombros.",
    },
    "en": {
        "aligned": "You're doing great. Keep your shoulders loose and breathe calmly.",
        "forward_tilt": "Rest your back on the chair and raise your screen to eye level.",
        "side_tilt": "Balance your weight on both sides and keep both feet on the floor.",
        "moving": "Moving helps too. When you sit back down, rest your back on the chair.",
        "unknown": "Every 30 minutes, stand up and stretch your neck and shoulders.",
    },
}


def _lang(language: str) -> str:
    return "en" if language.lower().startswith("en") else "es"


def fallback_summary(request: SummaryRequest) -> str:
    minutes = max(1, round(request.duration_seconds / 60))
    if _lang(request.language) == "en":
        opener = "Great session!" if request.aligned_percent >= 80 else "Nice progress." if request.aligned_percent >= 50 else "Every session counts."
        return f"{opener} {minutes} min with {request.aligned_percent}% aligned. Next time, take a short stretch break halfway through."
    opener = "¡Gran sesión!" if request.aligned_percent >= 80 else "Buen avance." if request.aligned_percent >= 50 else "Cada sesión cuenta."
    return f"{opener} {minutes} min con {request.aligned_percent}% alineada. La próxima vez, haz una pausa corta para estirarte a mitad de camino."


class Coach:
    def __init__(self, settings: Settings) -> None:
        self._settings = settings
        self._client: Any = None

    def _generate(self, prompt: str) -> str | None:
        if not self._settings.gemini_enabled:
            return None
        try:
            from google import genai
            from google.genai import types

            if self._client is None:
                self._client = genai.Client(
                    vertexai=True, project=self._settings.project, location=self._settings.location
                )
            response = self._client.models.generate_content(
                model=self._settings.gemini_model,
                contents=prompt,
                config=types.GenerateContentConfig(
                    system_instruction=SYSTEM_INSTRUCTION,
                    temperature=0.7,
                    max_output_tokens=200,
                ),
            )
            text = (response.text or "").strip()
            return text[:280] or None
        except Exception:  # noqa: BLE001 - cualquier fallo de la nube cae al texto de respaldo
            log.exception("Gemini no respondió; se usa el texto de respaldo.")
            return None

    def tip(self, request: TipRequest) -> TextResponse:
        lang = _lang(request.language)
        prompt = (
            f"Idioma de la respuesta: {lang}. Actividad: {request.mode}. "
            f"Patrón detectado por los sensores: {request.posture}. "
            "Da un consejo breve y general de ergonomía para este momento."
        )
        text = self._generate(prompt)
        if text:
            return TextResponse(text=text, generated=True)
        return TextResponse(text=FALLBACK_TIPS[lang][request.posture], generated=False)

    def summary(self, request: SummaryRequest) -> TextResponse:
        prompt = (
            f"Idioma de la respuesta: {_lang(request.language)}. Actividad: {request.mode}. "
            f"Duración: {round(request.duration_seconds / 60)} min. "
            f"Tiempo con postura alineada: {request.aligned_percent}%. Avisos: {request.alerts}. "
            "Resume la sesión con ánimo y termina con una recomendación general para la próxima."
        )
        text = self._generate(prompt)
        if text:
            return TextResponse(text=text, generated=True)
        return TextResponse(text=fallback_summary(request), generated=False)
