package com.posturawear.core.domain

import com.posturawear.core.domain.model.PostureClass
import com.posturawear.core.domain.model.SessionMode
import com.posturawear.core.domain.model.SessionRecord
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneOffset

class SessionAndStatsTest {

    private val zone = ZoneOffset.UTC
    private val today = LocalDate.of(2026, 11, 15)

    private fun record(daysAgo: Long, alignedMin: Int, tiltedMin: Int, mode: SessionMode = SessionMode.STUDY, alerts: Int = 1) =
        SessionRecord(
            id = daysAgo * 100 + alignedMin,
            mode = mode,
            startedAtEpochMs = today.minusDays(daysAgo).atTime(10, 0).toInstant(zone).toEpochMilli(),
            durationMs = (alignedMin + tiltedMin) * 60_000L,
            alignedMs = alignedMin * 60_000L,
            tiltedMs = tiltedMin * 60_000L,
            movingMs = 0,
            alerts = alerts,
        )

    @Test
    fun `la sesion acumula tiempo por categoria y avisos`() {
        val tracker = SessionTracker(SessionMode.WORK, startedAtEpochMs = 1000)
        repeat(6) { tracker.onWindow(PostureClass.ALIGNED, 3000) }
        repeat(2) { tracker.onWindow(PostureClass.FORWARD_TILT, 3000) }
        tracker.onWindow(PostureClass.MOVING, 3000)
        tracker.onWindow(PostureClass.UNKNOWN, 3000)
        tracker.onAlert()

        val record = tracker.toRecord()
        assertEquals(27_000L, record.durationMs)
        assertEquals(75, record.alignedPercent)
        assertEquals(1, record.alerts)
        assertEquals(SessionMode.WORK, record.mode)
    }

    @Test
    fun `una sesion sin tiempo analizado tiene cero por ciento`() {
        assertEquals(0, SessionTracker(SessionMode.STUDY, 0).toRecord().alignedPercent)
    }

    @Test
    fun `la semana tiene siete dias y termina hoy`() {
        val stats = StatsCalculator.weekly(emptyList(), today, zone)
        assertEquals(7, stats.days.size)
        assertEquals(today, stats.days.last().date)
        assertTrue(stats.days.last().isToday)
        assertTrue(stats.isEmpty)
        assertNull(stats.alignedPercent)
    }

    @Test
    fun `promedia ponderando por tiempo y agrupa por dia`() {
        val records = listOf(
            record(daysAgo = 0, alignedMin = 30, tiltedMin = 10),
            record(daysAgo = 0, alignedMin = 10, tiltedMin = 10),
            record(daysAgo = 2, alignedMin = 5, tiltedMin = 15),
        )
        val stats = StatsCalculator.weekly(records, today, zone)
        assertEquals(3, stats.sessions)
        assertEquals(3, stats.alerts)
        assertEquals(67, stats.days.last().alignedPercent)
        assertEquals(25, stats.days[4].alignedPercent)
        assertNull(stats.days[5].alignedPercent)
        assertEquals(56, stats.alignedPercent)
    }

    @Test
    fun `ignora sesiones fuera de los ultimos siete dias`() {
        val stats = StatsCalculator.weekly(listOf(record(daysAgo = 7, alignedMin = 10, tiltedMin = 0)), today, zone)
        assertTrue(stats.isEmpty)
    }

    @Test
    fun `filtra por tipo de sesion`() {
        val records = listOf(
            record(daysAgo = 1, alignedMin = 10, tiltedMin = 0, mode = SessionMode.STUDY),
            record(daysAgo = 1, alignedMin = 0, tiltedMin = 10, mode = SessionMode.WORK),
        )
        assertEquals(100, StatsCalculator.weekly(records, today, zone, SessionMode.STUDY).alignedPercent)
        assertEquals(0, StatsCalculator.weekly(records, today, zone, SessionMode.WORK).alignedPercent)
        assertEquals(50, StatsCalculator.weekly(records, today, zone).alignedPercent)
    }
}
