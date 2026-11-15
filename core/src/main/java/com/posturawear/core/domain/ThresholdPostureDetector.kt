package com.posturawear.core.domain

import com.posturawear.core.domain.model.Calibration
import com.posturawear.core.domain.model.Detection
import com.posturawear.core.domain.model.DetectionSource
import com.posturawear.core.domain.model.PostureClass
import com.posturawear.core.domain.model.Sensitivity
import kotlin.math.abs
import kotlin.math.max

/**
 * Detección local simple por umbrales. Es el respaldo cuando el backend no
 * responde: compara la orientación de la ventana con la referencia calibrada.
 */
object ThresholdPostureDetector {
    /** Movimiento (rad/s) a partir del cual no se evalúa la postura. */
    const val MOVING_GYRO_MEAN = 0.6f
    const val MOVING_ACCEL_STD = 1.8f

    fun detect(features: WindowFeatures, calibration: Calibration, sensitivity: Sensitivity): Detection {
        val deviation = features.orientation - calibration.baseline
        val threshold = sensitivity.tiltThresholdDeg

        if (features.gyroMean > MOVING_GYRO_MEAN || features.accelStd > MOVING_ACCEL_STD) {
            return Detection(PostureClass.MOVING, confidence = 0.8f, deviation, DetectionSource.LOCAL)
        }

        val pitch = abs(deviation.pitchDeg)
        val roll = abs(deviation.rollDeg)
        val worst = max(pitch, roll)
        val posture = when {
            worst < threshold -> PostureClass.ALIGNED
            pitch >= roll -> PostureClass.FORWARD_TILT
            else -> PostureClass.SIDE_TILT
        }
        return Detection(posture, confidenceFor(worst, threshold), deviation, DetectionSource.LOCAL)
    }

    /** La confianza crece con la distancia al umbral: cerca del límite, la duda es mayor. */
    private fun confidenceFor(worstDeg: Float, thresholdDeg: Float): Float {
        val margin = abs(worstDeg - thresholdDeg) / thresholdDeg
        return (0.5f + margin * 0.5f).coerceIn(0.5f, 0.99f)
    }
}
