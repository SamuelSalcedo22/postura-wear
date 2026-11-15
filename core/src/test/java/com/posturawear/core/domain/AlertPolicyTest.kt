package com.posturawear.core.domain

import com.posturawear.core.domain.model.Detection
import com.posturawear.core.domain.model.DetectionSource
import com.posturawear.core.domain.model.Orientation
import com.posturawear.core.domain.model.PostureClass
import com.posturawear.core.domain.model.Settings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AlertPolicyTest {

    private val settings = Settings(alertDelaySeconds = 10)

    private fun detection(posture: PostureClass, unusual: Boolean = false) =
        Detection(posture, 0.9f, Orientation.Zero, DetectionSource.LOCAL, unusual)

    /** Alimenta una ventana cada 3 s y devuelve los instantes (en s) en que avisó. */
    private fun run(policy: AlertPolicy, postures: List<PostureClass>, settings: Settings = this.settings): List<Int> =
        postures.mapIndexedNotNull { i, posture ->
            val seconds = i * 3
            if (policy.onDetection(detection(posture), seconds * 1000L, settings)) seconds else null
        }

    @Test
    fun `avisa solo cuando el patron se sostiene el tiempo configurado`() {
        val alerts = run(AlertPolicy(), List(5) { PostureClass.FORWARD_TILT })
        assertEquals(listOf(12), alerts)
    }

    @Test
    fun `volver a alinearse reinicia la cuenta`() {
        val postures = listOf(
            PostureClass.FORWARD_TILT, PostureClass.FORWARD_TILT, PostureClass.FORWARD_TILT,
            PostureClass.ALIGNED,
            PostureClass.FORWARD_TILT, PostureClass.FORWARD_TILT, PostureClass.FORWARD_TILT,
        )
        assertTrue(run(AlertPolicy(), postures).isEmpty())
    }

    @Test
    fun `descansa entre avisos`() {
        val alerts = run(AlertPolicy(cooldownMs = 30_000), List(20) { PostureClass.SIDE_TILT })
        assertEquals(listOf(12, 42), alerts)
    }

    @Test
    fun `respeta los patrones que el usuario desactivo`() {
        val onlyForward = settings.copy(alertOnSide = false)
        assertTrue(run(AlertPolicy(), List(10) { PostureClass.SIDE_TILT }, onlyForward).isEmpty())
    }

    @Test
    fun `el movimiento nunca avisa`() {
        assertTrue(run(AlertPolicy(), List(10) { PostureClass.MOVING }).isEmpty())
    }

    @Test
    fun `lo inusual cuenta solo si el usuario lo activa`() {
        val unusual = detection(PostureClass.ALIGNED, unusual = true)
        assertFalse(AlertPolicy.isIncorrect(unusual, settings))
        assertTrue(AlertPolicy.isIncorrect(unusual, settings.copy(alertOnUnusual = true)))
    }

    @Test
    fun `reiniciar olvida el descanso pendiente`() {
        val policy = AlertPolicy()
        run(policy, List(5) { PostureClass.FORWARD_TILT })
        policy.reset()
        assertEquals(listOf(12), run(policy, List(5) { PostureClass.FORWARD_TILT }))
    }
}
