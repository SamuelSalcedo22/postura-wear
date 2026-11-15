package com.posturawear.core.ui.organisms

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.wear.compose.material3.Text
import com.posturawear.core.domain.model.Orientation
import com.posturawear.core.tokens.component.Tone
import com.posturawear.core.tokens.semantic.SemanticMotion
import com.posturawear.core.tokens.semantic.SemanticSpacing
import com.posturawear.core.tokens.theme.PosturaTheme
import com.posturawear.core.ui.atoms.ArcRing
import com.posturawear.core.ui.atoms.ButtonVariant
import com.posturawear.core.ui.atoms.GlyphIcon
import com.posturawear.core.ui.atoms.PosturaIconButton
import com.posturawear.core.ui.molecules.Badge

/**
 * Organismo: cara de una pantalla redonda. Jerarquía de arriba abajo:
 * etiqueta de contexto → estado (lo más importante en palabras) → nivel de
 * burbuja (lo más importante de un vistazo) → cifra → acción.
 * El anillo exterior muestra el progreso y deja un hueco para la acción.
 */
@Composable
fun SessionFace(
    statusText: String,
    dialStatus: DialStatus,
    deviation: Orientation,
    dialDescription: String,
    metric: String,
    ringProgress: Float,
    ringColor: Color,
    modifier: Modifier = Modifier,
    badgeText: String? = null,
    badgeTone: Tone = Tone.Neutral,
    actionIcon: GlyphIcon? = null,
    actionDescription: String = "",
    actionVariant: ButtonVariant = ButtonVariant.Secondary,
    onAction: () -> Unit = {},
) {
    BoxWithConstraints(modifier.fillMaxSize()) {
        val width = maxWidth
        val height = maxHeight
        ArcRing(progress = ringProgress, color = ringColor)
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(height * 0.1f))
            if (badgeText != null) {
                Badge(badgeText, badgeTone, Modifier.widthIn(max = width * 0.6f))
            }
            AnimatedContent(
                targetState = statusText,
                transitionSpec = {
                    fadeIn(tween(SemanticMotion.enterMs)) togetherWith fadeOut(tween(SemanticMotion.stateChangeMs))
                },
                label = "status-text",
            ) { text ->
                Text(
                    text = text,
                    modifier = Modifier
                        .widthIn(max = width * 0.74f)
                        .padding(top = SemanticSpacing.inline),
                    style = PosturaTheme.type.label,
                    color = PosturaTheme.colors.contentPrimary,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Box(
                modifier = Modifier.weight(1f).padding(vertical = SemanticSpacing.inline),
                contentAlignment = Alignment.Center,
            ) {
                AlignmentDial(
                    status = dialStatus,
                    deviation = deviation,
                    contentDescription = dialDescription,
                    modifier = Modifier.fillMaxHeight().sizeIn(maxWidth = width * 0.4f, maxHeight = width * 0.4f),
                )
            }
            Text(
                text = metric,
                style = PosturaTheme.type.metricSmall,
                color = PosturaTheme.colors.contentPrimary,
                maxLines = 1,
            )
            if (actionIcon != null) {
                PosturaIconButton(actionIcon, actionDescription, onAction, variant = actionVariant)
            } else {
                Spacer(Modifier.height(height * 0.14f))
            }
            Spacer(Modifier.height(height * 0.02f))
        }
    }
}
