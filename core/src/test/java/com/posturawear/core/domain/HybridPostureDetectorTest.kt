package com.posturawear.core.domain

import com.posturawear.core.domain.model.Calibration
import com.posturawear.core.domain.model.DetectionSource
import com.posturawear.core.domain.model.Orientation
import com.posturawear.core.domain.model.PostureClass
import com.posturawear.core.domain.model.Sensitivity
import com.posturawear.core.sensors.SimPose
import com.posturawear.core.sensors.SyntheticMotion
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class HybridPostureDetectorTest {

    private val calibration = Calibration(Orientation(SimPose.GOOD.pitchDeg, SimPose.GOOD.rollDeg), 0)
    private val forward = SyntheticMotion.window(SimPose.FORWARD)
    private var now = 0L
    private var remoteCalls = 0

    private fun detector(remote: RemoteClassifier?) =
        HybridPostureDetector(remote, retryAfterMs = 30_000, clockMs = { now })

    private val working = RemoteClassifier { _, _, _ ->
        remoteCalls++
        RemoteClassification(PostureClass.SIDE_TILT, 0.93f)
    }
    private val failing = RemoteClassifier { _, _, _ ->
        remoteCalls++
        throw IOException("sin red")
    }

    private suspend fun HybridPostureDetector.run(cloud: Boolean = true) =
        detect(forward, calibration, Sensitivity.MEDIUM, cloud)!!

    @Test
    fun `usa la nube cuando responde y conserva la desviacion local`() = runTest {
        val d = detector(working).run()
        assertEquals(PostureClass.SIDE_TILT, d.posture)
        assertEquals(DetectionSource.CLOUD, d.source)
        assertEquals(0.93f, d.confidence, 0.001f)
        assertTrue(d.deviation.pitchDeg > 20f)
    }

    @Test
    fun `si la nube falla sigue con la deteccion local`() = runTest {
        val detector = detector(failing)
        val d = detector.run()
        assertEquals(PostureClass.FORWARD_TILT, d.posture)
        assertEquals(DetectionSource.LOCAL, d.source)
        assertTrue(detector.isRemoteDown)
    }

    @Test
    fun `tras un fallo espera antes de reintentar`() = runTest {
        val detector = detector(failing)
        detector.run()
        now = 10_000
        detector.run()
        assertEquals(1, remoteCalls)
        now = 31_000
        detector.run()
        assertEquals(2, remoteCalls)
    }

    @Test
    fun `con la nube desactivada no llama al backend`() = runTest {
        val detector = detector(working)
        val d = detector.run(cloud = false)
        assertEquals(0, remoteCalls)
        assertEquals(DetectionSource.LOCAL, d.source)
        assertFalse(detector.isRemoteDown)
    }

    @Test
    fun `una respuesta desconocida de la nube cede a la local`() = runTest {
        val d = detector { _, _, _ -> RemoteClassification(PostureClass.UNKNOWN, 0.2f) }.run()
        assertEquals(PostureClass.FORWARD_TILT, d.posture)
        assertEquals(DetectionSource.LOCAL, d.source)
    }
}
