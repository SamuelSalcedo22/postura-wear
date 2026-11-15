package com.posturawear.core.domain.model

/**
 * Una lectura combinada de acelerómetro (m/s²) y giroscopio (rad/s).
 * [timestampMs] es un reloj monótono; solo importa la diferencia entre muestras.
 */
data class SensorSample(
    val timestampMs: Long,
    val ax: Float,
    val ay: Float,
    val az: Float,
    val gx: Float,
    val gy: Float,
    val gz: Float,
)

/** Secuencia corta de lecturas (2 a 5 s) que se analiza como una unidad. */
data class SensorWindow(val samples: List<SensorSample>) {
    val durationMs: Long
        get() = if (samples.size < 2) 0L else samples.last().timestampMs - samples.first().timestampMs
}

/** Orientación del dispositivo respecto a la gravedad, en grados. */
data class Orientation(val pitchDeg: Float, val rollDeg: Float) {
    /** Diferencia angular con [other], cada eje normalizado a [-180, 180]. */
    operator fun minus(other: Orientation) = Orientation(
        pitchDeg = normalizeAngle(pitchDeg - other.pitchDeg),
        rollDeg = normalizeAngle(rollDeg - other.rollDeg),
    )

    companion object {
        val Zero = Orientation(0f, 0f)

        fun normalizeAngle(deg: Float): Float {
            var d = deg % 360f
            if (d > 180f) d -= 360f
            if (d < -180f) d += 360f
            return d
        }
    }
}
