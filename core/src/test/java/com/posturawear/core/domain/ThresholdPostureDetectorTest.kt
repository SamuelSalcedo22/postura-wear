package com.posturawear.core.domain

import com.posturawear.core.domain.model.Calibration
import com.posturawear.core.domain.model.DetectionSource
import com.posturawear.core.domain.model.Orientation
import com.posturawear.core.domain.model.PostureClass
import com.posturawear.core.domain.model.Sensitivity
import com.posturawear.core.sensors.SimPose
import com.posturawear.core.sensors.SyntheticMotion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ThresholdPostureDetectorTest {

    private val baseline = Calibration(Orientation(SimPose.GOOD.pitchDeg, SimPose.GOOD.rollDeg), 0)

    private fun detect(pose: SimPose, sensitivity: Sensitivity = Sensitivity.MEDIUM, calibration: Calibration = baseline) =
        ThresholdPostureDetector.detect(
            FeatureExtractor.extract(SyntheticMotion.window(pose))!!,
            calibration,
            sensitivity,
        )

    @Test
    fun `cerca de la referencia es postura alineada`() {
        val d = detect(SimPose.GOOD)
        assertEquals(PostureClass.ALIGNED, d.posture)
        assertEquals(DetectionSource.LOCAL, d.source)
    }

    @Test
    fun `inclinarse hacia adelante se reconoce`() {
        assertEquals(PostureClass.FORWARD_TILT, detect(SimPose.FORWARD).posture)
    }

    @Test
    fun `inclinarse de lado se reconoce`() {
        assertEquals(PostureClass.SIDE_TILT, detect(SimPose.SIDE).posture)
    }

    @Test
    fun `en movimiento no se evalua la postura`() {
        assertEquals(PostureClass.MOVING, detect(SimPose.MOVING).posture)
    }

    @Test
    fun `la deteccion es relativa a la referencia calibrada`() {
        val calibratedLeaning = Calibration(Orientation(SimPose.FORWARD.pitchDeg, SimPose.FORWARD.rollDeg), 0)
        assertEquals(PostureClass.ALIGNED, detect(SimPose.FORWARD, calibration = calibratedLeaning).posture)
        assertEquals(PostureClass.FORWARD_TILT, detect(SimPose.GOOD, calibration = calibratedLeaning).posture)
    }

    @Test
    fun `la sensibilidad cambia el umbral`() {
        // Una inclinación de 15° queda entre el umbral alto (12°) y el medio (18°).
        val features = FeatureExtractor.extract(SyntheticMotion.window(SimPose.GOOD))!!
        val calibration = Calibration(features.orientation - Orientation(15f, 0f), 0)
        assertEquals(
            PostureClass.FORWARD_TILT,
            ThresholdPostureDetector.detect(features, calibration, Sensitivity.HIGH).posture,
        )
        assertEquals(
            PostureClass.ALIGNED,
            ThresholdPostureDetector.detect(features, calibration, Sensitivity.MEDIUM).posture,
        )
    }

    @Test
    fun `la confianza es mayor lejos del umbral`() {
        val clear = detect(SimPose.FORWARD)
        assertTrue(clear.confidence > 0.7f)
        assertTrue(clear.confidence <= 0.99f)
    }
}
