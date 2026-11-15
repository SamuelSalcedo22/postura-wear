package com.posturawear.core.ui.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import com.posturawear.core.R
import com.posturawear.core.domain.HabitModel
import com.posturawear.core.domain.StatsCalculator
import com.posturawear.core.domain.model.SessionMode
import com.posturawear.core.domain.model.SessionRecord
import com.posturawear.core.state.PosturaEngine
import com.posturawear.core.tokens.theme.PosturaTheme
import com.posturawear.core.ui.atoms.BodyText
import com.posturawear.core.ui.atoms.GlyphIcon
import com.posturawear.core.ui.atoms.PosturaButton
import com.posturawear.core.ui.atoms.SectionLabel
import com.posturawear.core.ui.formatDuration
import com.posturawear.core.ui.molecules.ChoiceGroup
import com.posturawear.core.ui.molecules.StatBlock
import com.posturawear.core.ui.organisms.DialStatus
import com.posturawear.core.ui.organisms.HeroHeader
import com.posturawear.core.ui.organisms.StatPair
import com.posturawear.core.ui.organisms.WeekChart
import com.posturawear.core.ui.templates.ListTemplate
import java.time.LocalDate
import java.time.ZoneId

/** Filtro de estadísticas. Con tan poco contenido, filtrar reemplaza a buscar. */
private enum class StatsFilter(val mode: SessionMode?) { ALL(null), STUDY(SessionMode.STUDY), WORK(SessionMode.WORK) }

/**
 * Página: estadísticas de los últimos 7 días. Jerarquía: una cifra (promedio
 * alineada) → su evolución (gráfico) → el detalle (sesiones, tiempo, avisos).
 */
@Composable
fun StatsPage(
    history: List<SessionRecord>,
    habit: HabitModel,
    canStartSession: Boolean,
    onStartSession: () -> Unit,
) {
    var filter by rememberSaveable { mutableStateOf(StatsFilter.ALL) }
    val stats = remember(history, filter) {
        StatsCalculator.weekly(history, LocalDate.now(), ZoneId.systemDefault(), filter.mode)
    }
    val initials = stringArrayResource(R.array.weekday_initials).toList()

    ListTemplate {
        if (history.isEmpty()) {
            item {
                HeroHeader(
                    stringResource(R.string.stats_empty_title),
                    stringResource(R.string.stats_empty_body),
                    DialStatus.Idle,
                )
            }
            if (canStartSession) {
                item { PosturaButton(stringResource(R.string.action_start_session), onStartSession, icon = GlyphIcon.Play) }
            }
            return@ListTemplate
        }

        item { SectionLabel(stringResource(R.string.stats_period)) }
        item {
            StatBlock(
                value = stats.alignedPercent?.let { stringResource(R.string.percent_value, it) }
                    ?: stringResource(R.string.value_none),
                label = stringResource(R.string.stats_aligned_average),
                valueColor = PosturaTheme.colors.statusAligned,
                large = true,
            )
        }
        item { WeekChart(stats.days, initials, stringResource(R.string.stats_chart_description)) }
        if (stats.isEmpty) {
            item { BodyText(stringResource(R.string.stats_empty_filter_body)) }
        } else {
            item {
                StatPair(
                    leftValue = stats.sessions.toString(),
                    leftLabel = stringResource(R.string.stats_sessions_label),
                    rightValue = formatDuration(stats.totalDurationMs),
                    rightLabel = stringResource(R.string.stats_time_label),
                )
            }
            item { BodyText(pluralStringResource(R.plurals.session_alert_count, stats.alerts, stats.alerts)) }
        }
        item {
            ChoiceGroup(
                options = listOf(
                    StatsFilter.ALL to stringResource(R.string.stats_filter_all),
                    StatsFilter.STUDY to stringResource(R.string.stats_filter_study),
                    StatsFilter.WORK to stringResource(R.string.stats_filter_work),
                ),
                selected = filter,
                onSelect = { filter = it },
                stacked = true,
            )
        }

        item { SectionLabel(stringResource(R.string.stats_habit_heading)) }
        item {
            BodyText(
                if (habit.isReady) {
                    stringResource(R.string.stats_habit_ready)
                } else {
                    stringResource(
                        R.string.stats_habit_learning,
                        habit.learnedMinutes(PosturaEngine.WINDOW_SECONDS),
                        HabitModel.READY_WINDOWS * PosturaEngine.WINDOW_SECONDS / 60,
                    )
                },
            )
        }
    }
}
