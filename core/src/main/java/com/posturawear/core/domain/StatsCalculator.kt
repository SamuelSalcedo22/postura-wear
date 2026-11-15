package com.posturawear.core.domain

import com.posturawear.core.domain.model.SessionMode
import com.posturawear.core.domain.model.SessionRecord
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import kotlin.math.roundToInt

/** Un día de la semana en el gráfico. [alignedPercent] es `null` si no hubo sesiones. */
data class DayStat(val date: LocalDate, val alignedPercent: Int?, val isToday: Boolean)

data class WeeklyStats(
    val days: List<DayStat>,
    val sessions: Int,
    val alerts: Int,
    val totalDurationMs: Long,
    /** Promedio ponderado por tiempo analizado; `null` si no hay datos. */
    val alignedPercent: Int?,
) {
    val isEmpty: Boolean get() = sessions == 0
}

object StatsCalculator {
    const val DAYS = 7

    /**
     * Resume los últimos [DAYS] días (incluido hoy), del más antiguo al más reciente.
     * [mode] filtra por tipo de sesión; `null` las incluye todas.
     */
    fun weekly(
        records: List<SessionRecord>,
        today: LocalDate,
        zone: ZoneId,
        mode: SessionMode? = null,
    ): WeeklyStats {
        val first = today.minusDays(DAYS - 1L)
        val byDay = records
            .filter { mode == null || it.mode == mode }
            .groupBy { Instant.ofEpochMilli(it.startedAtEpochMs).atZone(zone).toLocalDate() }
            .filterKeys { !it.isBefore(first) && !it.isAfter(today) }

        val days = (0 until DAYS).map { offset ->
            val date = first.plusDays(offset.toLong())
            DayStat(date, percent(byDay[date].orEmpty()), isToday = date == today)
        }
        val inRange = byDay.values.flatten()
        return WeeklyStats(
            days = days,
            sessions = inRange.size,
            alerts = inRange.sumOf { it.alerts },
            totalDurationMs = inRange.sumOf { it.durationMs },
            alignedPercent = percent(inRange),
        )
    }

    private fun percent(records: List<SessionRecord>): Int? {
        val analyzed = records.sumOf { it.analyzedMs }
        if (analyzed <= 0) return null
        return (records.sumOf { it.alignedMs } * 100.0 / analyzed).roundToInt().coerceIn(0, 100)
    }
}
