package com.posturawear.core.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Contrato con el backend (ver `backend/app/schemas.py`).
 * No viaja ningún dato personal ni identificador: solo lecturas de movimiento.
 */
@Serializable
data class ClassifyRequest(
    /** Cada lectura es `[ax, ay, az, gx, gy, gz]`. */
    val samples: List<List<Float>>,
    @SerialName("sample_rate_hz") val sampleRateHz: Int,
    @SerialName("baseline_pitch") val baselinePitch: Float,
    @SerialName("baseline_roll") val baselineRoll: Float,
    @SerialName("tilt_threshold") val tiltThreshold: Float,
)

@Serializable
data class ClassifyResponse(
    val posture: String,
    val confidence: Float,
)

@Serializable
data class SummaryRequest(
    val mode: String,
    @SerialName("duration_seconds") val durationSeconds: Long,
    @SerialName("aligned_percent") val alignedPercent: Int,
    val alerts: Int,
    val language: String,
)

@Serializable
data class SummaryResponse(
    val text: String,
    /** Verdadero si lo redactó el modelo generativo; falso si es un texto de respaldo. */
    val generated: Boolean = false,
)
