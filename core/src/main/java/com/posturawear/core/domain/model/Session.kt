package com.posturawear.core.domain.model

import kotlin.math.roundToInt

enum class SessionMode { STUDY, WORK }

/** Sesión terminada, tal como se guarda en el historial. */
data class SessionRecord(
    val id: Long,
    val mode: SessionMode,
    val startedAtEpochMs: Long,
    val durationMs: Long,
    val alignedMs: Long,
    val tiltedMs: Long,
    val movingMs: Long,
    val alerts: Int,
) {
    /** Tiempo analizado en reposo (sin contar movimiento). */
    val analyzedMs: Long get() = alignedMs + tiltedMs

    /** Porcentaje del tiempo en reposo con postura alineada (0-100). */
    val alignedPercent: Int
        get() = if (analyzedMs <= 0) 0 else (alignedMs * 100.0 / analyzedMs).roundToInt().coerceIn(0, 100)
}
