package com.posturawear.core.sensors

import com.posturawear.core.domain.model.SensorSample
import com.posturawear.core.domain.model.SimScenario
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

/**
 * Simulador de sensores: permite probar la app sin hardware (emulador, celular
 * o reloj). El escenario se consulta en cada lectura, así que cambia en vivo.
 *
 * @param forcedPose si devuelve una postura, se usa en lugar del escenario
 *   (por ejemplo, postura buena mientras se calibra).
 */
class SimulatedSensorSource(
    private val scenario: () -> SimScenario,
    private val forcedPose: () -> SimPose? = { null },
) : SensorSource {
    override val isAvailable: Boolean = true

    override fun samples(): Flow<SensorSample> = flow {
        val random = Random(System.nanoTime())
        val stepMs = 1000L / SyntheticMotion.SAMPLE_RATE_HZ
        var elapsedMs = 0L
        while (true) {
            val pose = forcedPose() ?: SyntheticMotion.poseAt(scenario(), elapsedMs)
            emit(SyntheticMotion.sample(pose, elapsedMs, random))
            delay(stepMs)
            elapsedMs += stepMs
        }
    }
}
