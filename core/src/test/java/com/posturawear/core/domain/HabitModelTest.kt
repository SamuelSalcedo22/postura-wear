package com.posturawear.core.domain

import com.posturawear.core.domain.model.Orientation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class HabitModelTest {

    private fun trained(windows: Int, center: Orientation = Orientation(3f, -2f)): HabitModel {
        val random = Random(1)
        var model = HabitModel()
        repeat(windows) {
            val jitter = Orientation(random.nextFloat() * 4f - 2f, random.nextFloat() * 4f - 2f)
            model = model.learn(Orientation(center.pitchDeg + jitter.pitchDeg, center.rollDeg + jitter.rollDeg))
        }
        return model
    }

    @Test
    fun `no esta listo hasta reunir suficientes ventanas`() {
        assertFalse(trained(HabitModel.READY_WINDOWS - 1).isReady)
        assertTrue(trained(HabitModel.READY_WINDOWS).isReady)
    }

    @Test
    fun `aprende el centro del patron habitual`() {
        val model = trained(400)
        assertEquals(3f, model.meanPitch, 0.5f)
        assertEquals(-2f, model.meanRoll, 0.5f)
    }

    @Test
    fun `marca como inusual lo que se aleja del patron`() {
        val model = trained(400)
        assertFalse(model.isUnusual(Orientation(4f, -1f)))
        assertTrue(model.isUnusual(Orientation(25f, -2f)))
    }

    @Test
    fun `sin estar listo nunca marca inusual`() {
        assertFalse(trained(10).isUnusual(Orientation(60f, 60f)))
    }

    @Test
    fun `el piso de desviacion evita falsos avisos con un patron muy estable`() {
        var model = HabitModel()
        repeat(300) { model = model.learn(Orientation.Zero) }
        assertFalse(model.isUnusual(Orientation(HabitModel.MIN_STD_DEG * 2, 0f)))
    }

    @Test
    fun `convierte ventanas a minutos aprendidos`() {
        assertEquals(10, trained(200).learnedMinutes(windowSeconds = 3))
    }
}
