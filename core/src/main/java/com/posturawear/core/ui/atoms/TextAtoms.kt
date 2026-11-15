package com.posturawear.core.ui.atoms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.wear.compose.material3.Text
import com.posturawear.core.tokens.component.BadgeTokens
import com.posturawear.core.tokens.semantic.SemanticSpacing
import com.posturawear.core.tokens.theme.PosturaTheme

/** Átomo: rótulo de sección. Marca la jerarquía dentro de una lista. */
@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    val locale = LocalConfiguration.current.locales[0]
    Text(
        text = text.uppercase(locale),
        modifier = modifier
            .fillMaxWidth()
            .padding(top = SemanticSpacing.section, bottom = SemanticSpacing.inline)
            .semantics { heading() },
        style = PosturaTheme.type.overline,
        color = PosturaTheme.colors.contentMuted,
        textAlign = TextAlign.Center,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
    )
}

/** Átomo: título de pantalla o de bloque. */
@Composable
fun TitleText(text: String, modifier: Modifier = Modifier, color: Color = PosturaTheme.colors.contentPrimary) {
    Text(
        text = text,
        modifier = modifier.semantics { heading() },
        style = PosturaTheme.type.title,
        color = color,
        textAlign = TextAlign.Center,
        maxLines = 3,
        overflow = TextOverflow.Ellipsis,
    )
}

/** Átomo: texto de apoyo. */
@Composable
fun BodyText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = PosturaTheme.colors.contentSecondary,
    textAlign: TextAlign = TextAlign.Center,
    maxLines: Int = Int.MAX_VALUE,
) {
    Text(
        text = text,
        modifier = modifier,
        style = PosturaTheme.type.caption,
        color = color,
        textAlign = textAlign,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
    )
}

/** Átomo: punto de estado. */
@Composable
fun StatusDot(color: Color, modifier: Modifier = Modifier, size: Dp = BadgeTokens.dotSize) {
    Box(modifier.size(size).clip(CircleShape).background(color))
}
