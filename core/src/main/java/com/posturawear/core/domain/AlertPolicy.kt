package com.posturawear.core.domain

import com.posturawear.core.domain.model.Detection
import com.posturawear.core.domain.model.PostureClass
import com.posturawear.core.domain.model.Settings

/**
 * Decide cuándo vibrar. Avisa solo si un patrón configurado como incorrecto se
 * sostiene el tiempo elegido por el usuario, y deja un descanso entre avisos
 * para no volverse molesto.
 */
class AlertPolicy(private val cooldownMs: Long = DEFAULT_COOLDOWN_MS) {
    private var incorrectSinceMs: Long? = null
    private var lastAlertMs: Long? = null

    /** @return `true` si corresponde avisar ahora. */
    fun onDetection(detection: Detection, nowMs: Long, settings: Settings): Boolean {
        if (!isIncorrect(detection, settings)) {
            incorrectSinceMs = null
            return false
        }
        val since = incorrectSinceMs ?: nowMs.also { incorrectSinceMs = it }
        val sustained = nowMs - since >= settings.alertDelaySeconds * 1000L
        val rested = lastAlertMs?.let { nowMs - it >= cooldownMs } ?: true
        if (sustained && rested) {
            lastAlertMs = nowMs
            return true
        }
        return false
    }

    fun reset() {
        incorrectSinceMs = null
        lastAlertMs = null
    }

    companion object {
        const val DEFAULT_COOLDOWN_MS = 30_000L

        /** ¿Esta detección cuenta como patrón incorrecto según la configuración personal? */
        fun isIncorrect(detection: Detection, settings: Settings): Boolean = when (detection.posture) {
            PostureClass.FORWARD_TILT -> settings.alertOnForward
            PostureClass.SIDE_TILT -> settings.alertOnSide
            PostureClass.ALIGNED -> detection.unusual && settings.alertOnUnusual
            PostureClass.MOVING, PostureClass.UNKNOWN -> false
        }
    }
}
