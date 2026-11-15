package com.posturawear.core.domain

import com.posturawear.core.domain.model.SensorWindow
import com.posturawear.core.sensors.SimPose
import com.posturawear.core.sensors.SyntheticMotion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CalibratorTest {

    @Test
    fun `quieto guarda la orientacion como referencia`() {
        val result = Calibrator.calibrate(SyntheticMotion.window(SimPose.GOOD, durationMs = 5000), nowEpochMs = 42)
        assertTrue(result is CalibrationResult.Success)
        val calibration = (result as CalibrationResult.Success).calibration
        assertEquals(SimPose.GOOD.pitchDeg, calibration.baseline.pitchDeg, 1.5f)
        assertEquals(42L, calibration.createdAtEpochMs)
    }

    @Test
    fun `con mucho movimiento no calibra`() {
        val result = Calibrator.calibrate(SyntheticMotion.window(SimPose.MOVING, durationMs = 5000), nowEpochMs = 0)
        assertEquals(CalibrationResult.TooMuchMovement, result)
    }

    @Test
    fun `con pocas lecturas pide mas datos`() {
        val few = SensorWindow(SyntheticMotion.window(SimPose.GOOD).samples.take(Calibrator.MIN_SAMPLES - 1))
        assertEquals(CalibrationResult.NotEnoughData, Calibrator.calibrate(few, nowEpochMs = 0))
    }
}
