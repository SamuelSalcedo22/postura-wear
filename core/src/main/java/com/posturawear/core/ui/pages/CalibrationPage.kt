package com.posturawear.core.ui.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.posturawear.core.R
import com.posturawear.core.domain.model.Calibration
import com.posturawear.core.domain.model.Orientation
import com.posturawear.core.state.CalibrationState
import com.posturawear.core.tokens.component.DialTokens
import com.posturawear.core.tokens.component.Tone
import com.posturawear.core.tokens.theme.PosturaTheme
import com.posturawear.core.ui.atoms.BodyText
import com.posturawear.core.ui.atoms.ButtonVariant
import com.posturawear.core.ui.atoms.GlyphIcon
import com.posturawear.core.ui.atoms.PosturaButton
import com.posturawear.core.ui.molecules.MessageBlock
import com.posturawear.core.ui.organisms.DialStatus
import com.posturawear.core.ui.organisms.HeroHeader
import com.posturawear.core.ui.organisms.SessionFace
import com.posturawear.core.ui.templates.FaceTemplate
import com.posturawear.core.ui.templates.ListTemplate
import java.text.DateFormat
import java.util.Date

/**
 * Página: calibración. Un paso por pantalla: explicar → medir → confirmar.
 * Si algo falla, el mensaje dice qué pasó, por qué y qué hacer ahora.
 */
@Composable
fun CalibrationPage(
    calibration: Calibration?,
    state: CalibrationState,
    onStart: () -> Unit,
    onDone: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    when (state) {
        CalibrationState.Idle -> ListTemplate {
            item {
                HeroHeader(
                    stringResource(R.string.calibration_intro_title),
                    stringResource(R.string.calibration_intro_body),
                    DialStatus.Learning,
                )
            }
            item {
                PosturaButton(
                    label = stringResource(
                        if (calibration == null) R.string.calibration_action_start else R.string.calibration_action_repeat,
                    ),
                    onClick = onStart,
                    icon = GlyphIcon.Level,
                )
            }
            item {
                val status = if (calibration == null) {
                    stringResource(R.string.calibration_status_pending)
                } else {
                    val date = remember(calibration.createdAtEpochMs) {
                        DateFormat.getDateInstance(DateFormat.MEDIUM).format(Date(calibration.createdAtEpochMs))
                    }
                    stringResource(R.string.calibration_status_done, date)
                }
                BodyText(status, color = PosturaTheme.colors.contentMuted)
            }
        }

        is CalibrationState.Running -> FaceTemplate {
            val title = stringResource(R.string.calibration_running_title)
            SessionFace(
                statusText = title,
                dialStatus = DialStatus.Learning,
                deviation = Orientation.Zero,
                dialDescription = stringResource(R.string.a11y_dial, title),
                metric = stringResource(R.string.calibration_running_remaining, state.remainingSeconds),
                ringProgress = (state.totalSeconds - state.remainingSeconds + 1f) / state.totalSeconds,
                ringColor = DialTokens.learning,
            )
        }

        CalibrationState.Success -> ListTemplate {
            item {
                HeroHeader(
                    stringResource(R.string.calibration_success_title),
                    stringResource(R.string.calibration_success_body),
                    DialStatus.Aligned,
                )
            }
            item { PosturaButton(stringResource(R.string.action_done), onDone, icon = GlyphIcon.Check) }
        }

        is CalibrationState.Failed -> ListTemplate {
            when (state.reason) {
                CalibrationState.Reason.MOVING -> {
                    item {
                        MessageBlock(
                            what = stringResource(R.string.error_calibration_moving_what),
                            why = stringResource(R.string.error_calibration_moving_why),
                            next = stringResource(R.string.error_calibration_moving_next),
                            tone = Tone.Attention,
                        )
                    }
                    item { PosturaButton(stringResource(R.string.action_retry), onStart) }
                }
                CalibrationState.Reason.SENSORS_MISSING -> {
                    item {
                        MessageBlock(
                            what = stringResource(R.string.error_sensors_what),
                            why = stringResource(R.string.error_sensors_why),
                            next = stringResource(R.string.error_sensors_next),
                            tone = Tone.Error,
                        )
                    }
                    item { PosturaButton(stringResource(R.string.nav_settings), onOpenSettings, icon = GlyphIcon.Sliders) }
                    item {
                        PosturaButton(stringResource(R.string.action_retry), onStart, variant = ButtonVariant.Secondary)
                    }
                }
            }
        }
    }
}
