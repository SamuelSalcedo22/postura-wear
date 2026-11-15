package com.posturawear.core.ui.molecules

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.wear.compose.material3.Text
import com.posturawear.core.tokens.component.BadgeTokens
import com.posturawear.core.tokens.component.Tone
import com.posturawear.core.tokens.component.colors
import com.posturawear.core.tokens.theme.PosturaTheme
import com.posturawear.core.ui.atoms.StatusDot

/** Molécula: etiqueta compacta = punto de estado + texto. */
@Composable
fun Badge(text: String, tone: Tone, modifier: Modifier = Modifier) {
    val colors = tone.colors()
    Row(
        modifier = modifier
            .clip(BadgeTokens.shape)
            .background(colors.container)
            .padding(horizontal = BadgeTokens.paddingHorizontal, vertical = BadgeTokens.paddingVertical),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StatusDot(colors.accent)
        Spacer(Modifier.width(BadgeTokens.gap))
        Text(
            text = text,
            modifier = Modifier.weight(1f, fill = false),
            style = PosturaTheme.type.overline,
            color = colors.accent,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
