package com.posturawear.core.domain

import com.posturawear.core.domain.model.Orientation
import com.posturawear.core.domain.model.SensorWindow
import com.posturawear.core.sensors.SimPose
import com.posturawear.core.sensors.SyntheticMotion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FeatureExtractorTest {

    @Test
    fun `ventana vacia no produce caracteristicas`() {
        assertNull(FeatureExtractor.extract(SensorWindow(emptyList())))
    }

    @Test
    fun `dispositivo plano tiene cabeceo y alabeo cercanos a cero`() {
        val o = FeatureExtractor.orientationOf(0f, 0f, SyntheticMotion.GRAVITY)
        assertEquals(0f, o.pitchDeg, 0.01f)
        assertEquals(0f, o.rollDeg, 0.01f)
    }

    @Test
    fun `recupera la inclinacion con la que se genero la ventana`() {
        val forward = FeatureExtractor.extract(SyntheticMotion.window(SimPose.FORWARD))
        val side = FeatureExtractor.extract(SyntheticMotion.window(SimPose.SIDE))
        assertNotNull(forward)
        assertNotNull(side)
        assertEquals(SimPose.FORWARD.pitchDeg, forward!!.orientation.pitchDeg, 1.5f)
        assertEquals(SimPose.SIDE.rollDeg, side!!.orientation.rollDeg, 1.5f)
    }

    @Test
    fun `el movimiento eleva la energia del giroscopio y del acelerometro`() {
        val still = FeatureExtractor.extract(SyntheticMotion.window(SimPose.GOOD))!!
        val moving = FeatureExtractor.extract(SyntheticMotion.window(SimPose.MOVING))!!
        assertTrue(moving.gyroMean > still.gyroMean * 10)
        assertTrue(moving.accelStd > still.accelStd * 5)
    }

    @Test
    fun `la diferencia de orientacion se normaliza al rango corto`() {
        val d = Orientation(pitchDeg = 0f, rollDeg = 170f) - Orientation(pitchDeg = 0f, rollDeg = -170f)
        assertEquals(-20f, d.rollDeg, 0.001f)
    }

    @Test
    fun `la duracion de la ventana sale de sus marcas de tiempo`() {
        assertEquals(3000L, SyntheticMotion.window(SimPose.GOOD, durationMs = 3000).durationMs)
    }
}
