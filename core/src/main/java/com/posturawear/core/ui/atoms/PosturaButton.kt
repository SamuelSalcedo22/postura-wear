package com.posturawear.core.ui.atoms

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.wear.compose.material3.Text
import com.posturawear.core.tokens.component.ButtonColorTokens
import com.posturawear.core.tokens.component.ButtonTokens
import com.posturawear.core.tokens.semantic.SemanticMotion
import com.posturawear.core.tokens.semantic.SemanticSize
import com.posturawear.core.tokens.theme.PosturaTheme

enum class ButtonVariant { Primary, Secondary, Danger }

@Composable
private fun ButtonVariant.tokens(): ButtonColorTokens = when (this) {
    ButtonVariant.Primary -> ButtonTokens.primary
    ButtonVariant.Secondary -> ButtonTokens.secondary
    ButtonVariant.Danger -> ButtonTokens.danger
}

/**
 * Átomo: botón. Ocupa el ancho disponible y nunca fija su ancho al texto:
 * las etiquetas largas (otros idiomas) pasan a dos líneas y luego se truncan.
 */
@Composable
fun PosturaButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ButtonVariant = ButtonVariant.Primary,
    icon: GlyphIcon? = null,
    enabled: Boolean = true,
) {
    val colors = variant.tokens()
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) ButtonTokens.pressedScale else 1f,
        animationSpec = tween(SemanticMotion.feedbackMs, easing = SemanticMotion.standard),
        label = "button-scale",
    )
    val background by animateColorAsState(
        targetValue = when {
            !enabled -> colors.bgDisabled
            pressed -> colors.bgPressed
            else -> colors.bgDefault
        },
        animationSpec = tween(SemanticMotion.feedbackMs),
        label = "button-bg",
    )
    val content = if (enabled) colors.contentDefault else colors.contentDisabled

    Row(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = ButtonTokens.minHeight)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(ButtonTokens.shape)
            .background(background)
            .clickable(interaction, indication = null, enabled = enabled, role = Role.Button, onClick = onClick)
            .padding(horizontal = ButtonTokens.paddingHorizontal, vertical = ButtonTokens.paddingVertical),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Glyph(icon, tint = content, size = SemanticSize.iconSmall)
            Spacer(Modifier.width(ButtonTokens.iconGap))
        }
        Text(
            text = label,
            modifier = Modifier.weight(1f, fill = false),
            style = PosturaTheme.type.label,
            color = content,
            textAlign = TextAlign.Center,
            maxLines = ButtonTokens.maxLines,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/** Átomo: botón circular solo con icono. Exige descripción para lectores de pantalla. */
@Composable
fun PosturaIconButton(
    icon: GlyphIcon,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ButtonVariant = ButtonVariant.Secondary,
) {
    val colors = variant.tokens()
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) ButtonTokens.pressedScale else 1f,
        animationSpec = tween(SemanticMotion.feedbackMs),
        label = "icon-button-scale",
    )
    Box(
        modifier = modifier
            .size(SemanticSize.touchTargetMin)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(CircleShape)
            .background(if (pressed) colors.bgPressed else colors.bgDefault)
            .clickable(interaction, indication = null, role = Role.Button, onClick = onClick)
            .semantics { this.contentDescription = contentDescription },
        contentAlignment = Alignment.Center,
    ) {
        Glyph(icon, tint = colors.contentDefault)
    }
}
