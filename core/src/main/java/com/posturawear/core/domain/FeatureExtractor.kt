package com.posturawear.core.domain

import com.posturawear.core.domain.model.Orientation
import com.posturawear.core.domain.model.SensorWindow
import kotlin.math.atan2
import kotlin.math.sqrt

/**
 * Características de una ventana. El mismo cálculo existe en `backend/app/features.py`:
 * si se cambia aquí, hay que cambiarlo allá para que el modelo vea lo mismo.
 */
data class WindowFeatures(
    val meanAx: Float,
    val meanAy: Float,
    val meanAz: Float,
    val stdAx: Float,
    val stdAy: Float,
    val stdAz: Float,
    /** Media de la magnitud del giroscopio (rad/s): cuánto se mueve el dispositivo. */
    val gyroMean: Float,
    val gyroStd: Float,
    val orientation: Orientation,
) {
    val accelStd: Float get() = sqrt(stdAx * stdAx + stdAy * stdAy + stdAz * stdAz)
}

object FeatureExtractor {
    /** Devuelve `null` si la ventana no tiene lecturas. */
    fun extract(window: SensorWindow): WindowFeatures? {
        val s = window.samples
        if (s.isEmpty()) return null

        val mx = s.meanOf { it.ax }
        val my = s.meanOf { it.ay }
        val mz = s.meanOf { it.az }
        val gyro = s.map { sqrt(it.gx * it.gx + it.gy * it.gy + it.gz * it.gz) }
        val gyroMean = gyro.average().toFloat()

        return WindowFeatures(
            meanAx = mx,
            meanAy = my,
            meanAz = mz,
            stdAx = s.stdOf(mx) { it.ax },
            stdAy = s.stdOf(my) { it.ay },
            stdAz = s.stdOf(mz) { it.az },
            gyroMean = gyroMean,
            gyroStd = sqrt(gyro.sumOf { ((it - gyroMean) * (it - gyroMean)).toDouble() } / gyro.size).toFloat(),
            orientation = orientationOf(mx, my, mz),
        )
    }

    /** Cabeceo y alabeo a partir del vector de gravedad promedio. */
    fun orientationOf(ax: Float, ay: Float, az: Float): Orientation {
        val pitch = Math.toDegrees(atan2(-ax.toDouble(), sqrt((ay * ay + az * az).toDouble())))
        val roll = Math.toDegrees(atan2(ay.toDouble(), az.toDouble()))
        return Orientation(pitch.toFloat(), roll.toFloat())
    }

    private inline fun <T> List<T>.meanOf(selector: (T) -> Float): Float =
        (sumOf { selector(it).toDouble() } / size).toFloat()

    private inline fun <T> List<T>.stdOf(mean: Float, selector: (T) -> Float): Float =
        sqrt(sumOf { val d = selector(it) - mean; (d * d).toDouble() } / size).toFloat()
}
