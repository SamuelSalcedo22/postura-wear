package com.posturawear.core.sensors

import com.posturawear.core.domain.model.SensorSample
import com.posturawear.core.domain.model.SensorWindow
import com.posturawear.core.domain.model.SimScenario
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/** Postura que el simulador "actúa" en un instante. */
enum class SimPose(val pitchDeg: Float, val rollDeg: Float, val gyro: Float, val accelNoise: Float) {
    GOOD(pitchDeg = 2f, rollDeg = 1f, gyro = 0.03f, accelNoise = 0.08f),
    FORWARD(pitchDeg = 34f, rollDeg = 3f, gyro = 0.04f, accelNoise = 0.1f),
    SIDE(pitchDeg = 4f, rollDeg = 31f, gyro = 0.04f, accelNoise = 0.1f),
    MOVING(pitchDeg = 10f, rollDeg = 8f, gyro = 1.6f, accelNoise = 2.6f),
}

/**
 * Genera lecturas sintéticas de acelerómetro y giroscopio. Kotlin puro: lo usan
 * el simulador de la app y las pruebas unitarias. `ml/generate_synthetic.py`
 * sigue el mismo modelo físico para entrenar el clasificador.
 */
object SyntheticMotion {
    const val GRAVITY = 9.81f
    const val SAMPLE_RATE_HZ = 25

    /** Guion del escenario mixto: (postura, segundos). Se repite en bucle. */
    private val mixedScript = listOf(
        SimPose.GOOD to 18,
        SimPose.FORWARD to 16,
        SimPose.GOOD to 12,
        SimPose.MOVING to 5,
        SimPose.SIDE to 14,
        SimPose.GOOD to 10,
    )
    private val mixedTotalSeconds = mixedScript.sumOf { it.second }

    fun poseAt(scenario: SimScenario, elapsedMs: Long): SimPose = when (scenario) {
        SimScenario.GOOD -> SimPose.GOOD
        SimScenario.BAD -> SimPose.FORWARD
        SimScenario.MIXED -> {
            var second = (elapsedMs / 1000) % mixedTotalSeconds
            mixedScript.first { (_, seconds) ->
                if (second < seconds) true else { second -= seconds; false }
            }.first
        }
    }

    fun sample(pose: SimPose, timestampMs: Long, random: Random): SensorSample {
        val pitch = Math.toRadians(pose.pitchDeg.toDouble())
        val roll = Math.toRadians(pose.rollDeg.toDouble())
        fun noise(scale: Float) = (random.nextFloat() * 2f - 1f) * scale
        return SensorSample(
            timestampMs = timestampMs,
            ax = (-GRAVITY * sin(pitch)).toFloat() + noise(pose.accelNoise),
            ay = (GRAVITY * cos(pitch) * sin(roll)).toFloat() + noise(pose.accelNoise),
            az = (GRAVITY * cos(pitch) * cos(roll)).toFloat() + noise(pose.accelNoise),
            gx = noise(pose.gyro),
            gy = noise(pose.gyro),
            gz = noise(pose.gyro),
        )
    }

    fun window(
        pose: SimPose,
        durationMs: Long = 3000,
        random: Random = Random(7),
        startMs: Long = 0,
    ): SensorWindow {
        val stepMs = 1000L / SAMPLE_RATE_HZ
        val count = (durationMs / stepMs).toInt() + 1
        return SensorWindow(List(count) { i -> sample(pose, startMs + i * stepMs, random) })
    }
}
