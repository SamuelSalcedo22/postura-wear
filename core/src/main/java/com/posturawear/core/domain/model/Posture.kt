package com.posturawear.core.domain.model

import kotlinx.serialization.Serializable

/**
 * Categorías de postura/movimiento que reconoce el prototipo.
 * Son patrones de orientación del dispositivo, no un diagnóstico: por eso la
 * interfaz siempre habla de "posible mala postura".
 */
enum class PostureClass {
    ALIGNED,
    FORWARD_TILT,
    SIDE_TILT,
    MOVING,
    UNKNOWN,
    ;

    val isTilt: Boolean get() = this == FORWARD_TILT || this == SIDE_TILT
}

/** Quién clasificó la ventana. */
enum class DetectionSource { CLOUD, LOCAL }

/** Resultado de analizar una ventana de sensores. */
data class Detection(
    val posture: PostureClass,
    /** Confianza entre 0 y 1. */
    val confidence: Float,
    /** Desviación respecto a la postura de referencia calibrada. */
    val deviation: Orientation,
    val source: DetectionSource,
    /** Verdadero si la orientación se aleja del patrón habitual aprendido. */
    val unusual: Boolean = false,
)

/** Postura de referencia que el usuario guarda al calibrar. */
@Serializable
data class Calibration(val baseline: Orientation, val createdAtEpochMs: Long)
