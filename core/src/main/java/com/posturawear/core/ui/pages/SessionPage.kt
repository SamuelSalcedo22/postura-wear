package com.posturawear.core.ui.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import com.posturawear.core.R
import com.posturawear.core.domain.model.DetectionSource
import com.posturawear.core.domain.model.Orientation
import com.posturawear.core.domain.model.SessionMode
import com.posturawear.core.state.SessionNotice
import com.posturawear.core.state.SessionState
import com.posturawear.core.state.SummaryState
import com.posturawear.core.state.TipState
import com.posturawear.core.tokens.component.Tone
import com.posturawear.core.tokens.component.colors
import com.posturawear.core.tokens.theme.PosturaTheme
import com.posturawear.core.ui.atoms.BodyText
import com.posturawear.core.ui.atoms.ButtonVariant
import com.posturawear.core.ui.atoms.GlyphIcon
import com.posturawear.core.ui.atoms.PosturaButton
import com.posturawear.core.ui.atoms.SectionLabel
import com.posturawear.core.ui.atoms.TitleText
import com.posturawear.core.ui.formatClock
import com.posturawear.core.ui.formatDuration
import com.posturawear.core.ui.molecules.MessageBlock
import com.posturawear.core.ui.molecules.NavRow
import com.posturawear.core.ui.organisms.SessionFace
import com.posturawear.core.ui.organisms.StatPair
import com.posturawear.core.ui.postureUi
import com.posturawear.core.ui.templates.FaceTemplate
import com.posturawear.core.ui.templates.ListTemplate

/**
 * Página: sesión. Tiene tres momentos y muestra solo uno a la vez:
 * elegir modo → sesión en vivo → resumen.
 */
@Composable
fun SessionPage(
    session: SessionState?,
    summary: SummaryState?,
    onChooseMode: (SessionMode) -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onFinish: () -> Unit,
    onDone: () -> Unit,
) {
    when {
        session != null -> LiveSession(session, onPause, onResume, onFinish)
        summary != null -> SessionSummary(summary, onDone)
        else -> ModeChooser(onChooseMode)
    }
}

@Composable
private fun ModeChooser(onChooseMode: (SessionMode) -> Unit) {
    ListTemplate {
        item { TitleText(stringResource(R.string.session_mode_title)) }
        item {
            NavRow(
                icon = GlyphIcon.Book,
                label = stringResource(R.string.session_mode_study),
                secondary = stringResource(R.string.session_mode_study_hint),
                onClick = { onChooseMode(SessionMode.STUDY) },
                tone = Tone.Learning,
            )
        }
        item {
            NavRow(
                icon = GlyphIcon.Desk,
                label = stringResource(R.string.session_mode_work),
                secondary = stringResource(R.string.session_mode_work_hint),
                onClick = { onChooseMode(SessionMode.WORK) },
                tone = Tone.Info,
            )
        }
    }
}

@Composable
private fun LiveSession(
    session: SessionState,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onFinish: () -> Unit,
) {
    // La pantalla se mantiene encendida mientras la sesión está a la vista.
    val view = LocalView.current
    DisposableEffect(view) {
        view.keepScreenOn = true
        onDispose { view.keepScreenOn = false }
    }

    val ui = postureUi(session.detection, session.paused)
    val statusText = stringResource(ui.label)
    val toggleLabel = stringResource(if (session.paused) R.string.action_resume else R.string.action_pause)
    val onToggle = if (session.paused) onResume else onPause

    val (badgeText, badgeTone) = when {
        session.simulated -> stringResource(R.string.source_simulated) to Tone.Learning
        session.notice == SessionNotice.OFFLINE -> stringResource(R.string.source_local) to Tone.Attention
        session.detection?.source == DetectionSource.CLOUD -> stringResource(R.string.source_cloud) to Tone.Info
        session.detection != null -> stringResource(R.string.source_local) to Tone.Neutral
        else -> null to Tone.Neutral
    }

    FaceTemplate(
        details = {
            SectionLabel(stringResource(R.string.session_details_heading))
            ui.detail?.let { TitleText(stringResource(it)) }
            BodyText(stringResource(ui.hint))
            StatPair(
                leftValue = session.alignedPercent?.let { stringResource(R.string.percent_value, it) }
                    ?: stringResource(R.string.value_none),
                leftLabel = stringResource(R.string.session_aligned_label),
                rightValue = session.alerts.toString(),
                rightLabel = stringResource(R.string.summary_alerts_label),
            )
            when (session.notice) {
                SessionNotice.OFFLINE -> MessageBlock(
                    what = stringResource(R.string.error_offline_what),
                    why = stringResource(R.string.error_offline_why),
                    next = stringResource(R.string.error_offline_next),
                    tone = Tone.Attention,
                )
                SessionNotice.SENSORS_MISSING -> MessageBlock(
                    what = stringResource(R.string.error_sensors_what),
                    why = stringResource(R.string.error_sensors_why),
                    next = stringResource(R.string.error_sensors_next),
                    tone = Tone.Error,
                )
                null -> Unit
            }
            PosturaButton(
                label = toggleLabel,
                onClick = onToggle,
                variant = ButtonVariant.Secondary,
                icon = if (session.paused) GlyphIcon.Play else GlyphIcon.Pause,
            )
            PosturaButton(
                label = stringResource(R.string.action_finish),
                onClick = onFinish,
                variant = ButtonVariant.Danger,
                icon = GlyphIcon.Stop,
            )
        },
    ) {
        SessionFace(
            statusText = statusText,
            dialStatus = ui.dial,
            deviation = if (session.paused) Orientation.Zero else session.detection?.deviation ?: Orientation.Zero,
            dialDescription = stringResource(R.string.a11y_dial, statusText),
            metric = formatClock(session.elapsedMs),
            ringProgress = (session.alignedPercent ?: 0) / 100f,
            ringColor = ui.tone.colors().accent,
            badgeText = badgeText,
            badgeTone = badgeTone,
            actionIcon = if (session.paused) GlyphIcon.Play else GlyphIcon.Pause,
            actionDescription = toggleLabel,
            onAction = onToggle,
        )
    }
}

@Composable
private fun SessionSummary(summary: SummaryState, onDone: () -> Unit) {
    val record = summary.record
    val percent = record.alignedPercent
    val tips = stringArrayResource(R.array.local_tips)
    ListTemplate {
        item {
            TitleText(
                stringResource(
                    when {
                        percent >= 80 -> R.string.summary_title_great
                        percent >= 50 -> R.string.summary_title_good
                        else -> R.string.summary_title_keep
                    },
                ),
            )
        }
        item { BodyText(stringResource(R.string.summary_aligned_percent, percent)) }
        item {
            StatPair(
                leftValue = formatDuration(record.durationMs),
                leftLabel = stringResource(R.string.summary_duration_label),
                rightValue = record.alerts.toString(),
                rightLabel = stringResource(R.string.summary_alerts_label),
            )
        }
        if (!summary.saved) {
            item { BodyText(stringResource(R.string.summary_too_short)) }
        }
        item { SectionLabel(stringResource(R.string.summary_tip_heading)) }
        when (val tip = summary.tip) {
            TipState.Loading -> item { BodyText(stringResource(R.string.summary_tip_loading)) }
            is TipState.Cloud -> item { BodyText(tip.text, color = PosturaTheme.colors.contentPrimary) }
            is TipState.Local -> {
                if (tip.failed) {
                    item {
                        MessageBlock(
                            what = stringResource(R.string.error_summary_what),
                            next = stringResource(R.string.error_summary_next),
                            tone = Tone.Info,
                        )
                    }
                }
                item { BodyText(tips[tip.index.mod(tips.size)], color = PosturaTheme.colors.contentPrimary) }
            }
        }
        item { PosturaButton(stringResource(R.string.action_done), onDone, icon = GlyphIcon.Check) }
    }
}
