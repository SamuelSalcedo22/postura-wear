package com.posturawear.core.ui

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.posturawear.core.R
import com.posturawear.core.domain.model.Detection
import com.posturawear.core.domain.model.PostureClass
import com.posturawear.core.tokens.component.Tone
import com.posturawear.core.ui.organisms.DialStatus
import java.util.Locale

/**
 * Cómo se cuenta cada estado de postura: palabras, indicación, tono y aspecto
 * del nivel. Reúne en un solo lugar la relación entre dominio y contenido.
 */
data class PostureUi(
    @StringRes val label: Int,
    @StringRes val hint: Int,
    @StringRes val detail: Int?,
    val dial: DialStatus,
    val tone: Tone,
)

fun postureUi(detection: Detection?, paused: Boolean): PostureUi = when {
    paused -> PostureUi(R.string.posture_paused, R.string.hint_paused, null, DialStatus.Idle, Tone.Neutral)
    detection == null -> unknownUi
    else -> when (detection.posture) {
        PostureClass.ALIGNED -> if (detection.unusual) {
            PostureUi(R.string.posture_aligned, R.string.hint_side, R.string.posture_unusual_detail, DialStatus.Learning, Tone.Learning)
        } else {
            PostureUi(R.string.posture_aligned, R.string.hint_aligned, null, DialStatus.Aligned, Tone.Positive)
        }
        PostureClass.FORWARD_TILT ->
            PostureUi(R.string.posture_forward, R.string.hint_forward, R.string.posture_forward_detail, DialStatus.Attention, Tone.Attention)
        PostureClass.SIDE_TILT ->
            PostureUi(R.string.posture_side, R.string.hint_side, R.string.posture_side_detail, DialStatus.Attention, Tone.Attention)
        PostureClass.MOVING -> PostureUi(R.string.posture_moving, R.string.hint_moving, null, DialStatus.Moving, Tone.Info)
        PostureClass.UNKNOWN -> unknownUi
    }
}

private val unknownUi = PostureUi(R.string.posture_unknown, R.string.hint_unknown, null, DialStatus.Idle, Tone.Neutral)

/** Cronómetro `mm:ss` (o `h:mm:ss`). Son solo dígitos, así que no depende del idioma. */
fun formatClock(ms: Long): String {
    val total = ms / 1000
    val h = total / 3600
    val m = total % 3600 / 60
    val s = total % 60
    return if (h > 0) String.format(Locale.ROOT, "%d:%02d:%02d", h, m, s) else String.format(Locale.ROOT, "%02d:%02d", m, s)
}

/** Duración en palabras, con la unidad traducida. */
@Composable
fun formatDuration(ms: Long): String {
    val seconds = (ms / 1000).toInt()
    val minutes = seconds / 60
    return when {
        minutes >= 60 -> stringResource(R.string.duration_hours_minutes, minutes / 60, minutes % 60)
        minutes >= 1 -> stringResource(R.string.duration_minutes, minutes)
        else -> stringResource(R.string.duration_seconds, seconds)
    }
}
