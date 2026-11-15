package com.posturawear.core.ui.pages

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.posturawear.core.R
import com.posturawear.core.state.AppData
import com.posturawear.core.tokens.component.Tone
import com.posturawear.core.tokens.semantic.SemanticSpacing
import com.posturawear.core.tokens.theme.PosturaTheme
import com.posturawear.core.ui.atoms.BodyText
import com.posturawear.core.ui.atoms.GlyphIcon
import com.posturawear.core.ui.atoms.PosturaButton
import com.posturawear.core.ui.molecules.NavRow
import com.posturawear.core.ui.organisms.DialStatus
import com.posturawear.core.ui.organisms.HeroHeader
import com.posturawear.core.ui.templates.ListTemplate

/**
 * Página: principal. Una sola acción protagonista (la que toca ahora) y, debajo,
 * los tres destinos secundarios, cada uno con un dato que evita tener que entrar.
 */
@Composable
fun HomePage(
    data: AppData,
    sessionRunning: Boolean,
    onStartSession: () -> Unit,
    onViewSession: () -> Unit,
    onOpenStats: () -> Unit,
    onOpenCalibration: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    val calibrated = data.calibration != null
    ListTemplate {
        item {
            when {
                sessionRunning -> HeroHeader(
                    stringResource(R.string.home_session_running_title),
                    stringResource(R.string.home_session_running_body),
                    DialStatus.Aligned,
                )
                !calibrated -> HeroHeader(
                    stringResource(R.string.home_needs_calibration_title),
                    stringResource(R.string.home_needs_calibration_body),
                    DialStatus.Learning,
                )
                else -> HeroHeader(
                    stringResource(R.string.home_ready_title),
                    stringResource(R.string.home_ready_body),
                    DialStatus.Idle,
                )
            }
        }
        item {
            when {
                sessionRunning -> PosturaButton(stringResource(R.string.action_view_session), onViewSession, icon = GlyphIcon.Level)
                !calibrated -> PosturaButton(stringResource(R.string.home_action_calibrate), onOpenCalibration, icon = GlyphIcon.Level)
                else -> PosturaButton(stringResource(R.string.action_start_session), onStartSession, icon = GlyphIcon.Play)
            }
        }
        item {
            val last = data.history.lastOrNull()
            NavRow(
                icon = GlyphIcon.Chart,
                label = stringResource(R.string.nav_stats),
                onClick = onOpenStats,
                secondary = if (last != null) {
                    stringResource(R.string.home_last_session, last.alignedPercent)
                } else {
                    stringResource(R.string.home_stats_secondary_empty)
                },
                tone = Tone.Positive,
            )
        }
        item {
            NavRow(
                icon = GlyphIcon.Level,
                label = stringResource(R.string.nav_calibration),
                onClick = onOpenCalibration,
                secondary = stringResource(
                    if (calibrated) R.string.home_calibration_secondary_done else R.string.home_calibration_secondary_pending,
                ),
                tone = Tone.Learning,
            )
        }
        item {
            NavRow(
                icon = GlyphIcon.Sliders,
                label = stringResource(R.string.nav_settings),
                onClick = onOpenSettings,
                secondary = stringResource(R.string.home_settings_secondary),
                tone = Tone.Info,
            )
        }
        item {
            BodyText(
                stringResource(R.string.disclaimer_short),
                Modifier.padding(top = SemanticSpacing.section),
                color = PosturaTheme.colors.contentMuted,
            )
        }
    }
}
