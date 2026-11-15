package com.posturawear.core.domain

import com.posturawear.core.domain.model.Orientation
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sqrt

/**
 * Patrón habitual del usuario: media y varianza de su desviación respecto a la
 * referencia mientras está en reposo. Con suficientes ventanas, marca como
 * "inusual" lo que se aleja varias desviaciones estándar de lo acostumbrado.
 *
 * Es inmutable: [learn] devuelve un modelo nuevo, lo que facilita guardarlo y probarlo.
 */
data class HabitModel(
    val windows: Int = 0,
    val meanPitch: Float = 0f,
    val meanRoll: Float = 0f,
    val varPitch: Float = 0f,
    val varRoll: Float = 0f,
) {
    val isReady: Boolean get() = windows >= READY_WINDOWS

    /** Minutos aprendidos, suponiendo ventanas de [windowSeconds] s. */
    fun learnedMinutes(windowSeconds: Int): Int = windows * windowSeconds / 60

    /** Incorpora una ventana en reposo. Pasado [MEMORY_WINDOWS], olvida poco a poco lo antiguo. */
    fun learn(deviation: Orientation): HabitModel {
        val n = (windows + 1).coerceAtMost(MEMORY_WINDOWS)
        val rate = 1f / n
        val dPitch = deviation.pitchDeg - meanPitch
        val dRoll = deviation.rollDeg - meanRoll
        return copy(
            windows = windows + 1,
            meanPitch = meanPitch + rate * dPitch,
            meanRoll = meanRoll + rate * dRoll,
            varPitch = (1 - rate) * (varPitch + rate * dPitch * dPitch),
            varRoll = (1 - rate) * (varRoll + rate * dRoll * dRoll),
        )
    }

    /** Qué tan lejos está [deviation] de lo habitual, en desviaciones estándar. */
    fun zScore(deviation: Orientation): Float {
        val zPitch = abs(deviation.pitchDeg - meanPitch) / max(sqrt(varPitch), MIN_STD_DEG)
        val zRoll = abs(deviation.rollDeg - meanRoll) / max(sqrt(varRoll), MIN_STD_DEG)
        return max(zPitch, zRoll)
    }

    fun isUnusual(deviation: Orientation): Boolean = isReady && zScore(deviation) > UNUSUAL_Z

    companion object {
        /** Ventanas necesarias para confiar en el patrón (10 min con ventanas de 3 s). */
        const val READY_WINDOWS = 200
        const val MEMORY_WINDOWS = 2000
        const val UNUSUAL_Z = 3f

        /** Piso de desviación estándar: evita marcar como inusual un cambio mínimo. */
        const val MIN_STD_DEG = 4f
    }
}
