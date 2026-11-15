package com.posturawear.core.domain

import com.posturawear.core.domain.model.PostureClass
import com.posturawear.core.domain.model.SessionMode
import com.posturawear.core.domain.model.SessionRecord

/** Acumula el tiempo por categoría y los avisos de una sesión en curso. */
class SessionTracker(val mode: SessionMode, private val startedAtEpochMs: Long) {
    var alignedMs = 0L
        private set
    var tiltedMs = 0L
        private set
    var movingMs = 0L
        private set
    var alerts = 0
        private set

    val elapsedMs: Long get() = alignedMs + tiltedMs + movingMs

    fun onWindow(posture: PostureClass, windowMs: Long) {
        when (posture) {
            PostureClass.ALIGNED -> alignedMs += windowMs
            PostureClass.FORWARD_TILT, PostureClass.SIDE_TILT -> tiltedMs += windowMs
            PostureClass.MOVING -> movingMs += windowMs
            PostureClass.UNKNOWN -> Unit
        }
    }

    fun onAlert() {
        alerts++
    }

    fun toRecord(): SessionRecord = SessionRecord(
        id = startedAtEpochMs,
        mode = mode,
        startedAtEpochMs = startedAtEpochMs,
        durationMs = elapsedMs,
        alignedMs = alignedMs,
        tiltedMs = tiltedMs,
        movingMs = movingMs,
        alerts = alerts,
    )

    companion object {
        /** Sesiones más cortas que esto no se guardan en el historial. */
        const val MIN_SAVED_DURATION_MS = 30_000L
    }
}
