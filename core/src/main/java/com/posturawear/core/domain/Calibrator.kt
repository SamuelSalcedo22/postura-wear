package com.posturawear.core.domain

import com.posturawear.core.domain.model.Calibration
import com.posturawear.core.domain.model.SensorWindow

sealed interface CalibrationResult {
    data class Success(val calibration: Calibration) : CalibrationResult
    data object TooMuchMovement : CalibrationResult
    data object NotEnoughData : CalibrationResult
}

/** Convierte unos segundos de quietud en la postura de referencia del usuario. */
object Calibrator {
    const val DURATION_SECONDS = 5
    const val MIN_SAMPLES = 20

    /** Por encima de esto (rad/s) se considera que el usuario se movió. */
    const val MAX_GYRO_MEAN = 0.35f
    const val MAX_ACCEL_STD = 1.2f

    fun calibrate(window: SensorWindow, nowEpochMs: Long): CalibrationResult {
        if (window.samples.size < MIN_SAMPLES) return CalibrationResult.NotEnoughData
        val features = FeatureExtractor.extract(window) ?: return CalibrationResult.NotEnoughData
        if (features.gyroMean > MAX_GYRO_MEAN || features.accelStd > MAX_ACCEL_STD) {
            return CalibrationResult.TooMuchMovement
        }
        return CalibrationResult.Success(Calibration(features.orientation, nowEpochMs))
    }
}
