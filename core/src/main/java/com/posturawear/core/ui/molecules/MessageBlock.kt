package com.posturawear.core.ui.molecules

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.wear.compose.material3.Text
import com.posturawear.core.tokens.component.MessageTokens
import com.posturawear.core.tokens.component.Tone
import com.posturawear.core.tokens.component.colors
import com.posturawear.core.tokens.theme.PosturaTheme

/**
 * Molécula: mensaje en tres partes.
 * 1. [what]: qué pasó, en lenguaje del usuario.
 * 2. [why]: por qué, solo si ayuda (opcional).
 * 3. [next]: qué hacer ahora, destacado con el color del tono.
 */
@Composable
fun MessageBlock(
    what: String,
    next: String,
    modifier: Modifier = Modifier,
    why: String? = null,
    tone: Tone = Tone.Attention,
) {
    val colors = tone.colors()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clip(MessageTokens.shape)
            .background(PosturaTheme.colors.backgroundSurface)
            .padding(MessageTokens.padding),
        horizontalArrangement = Arrangement.spacedBy(MessageTokens.padding),
    ) {
        Box(
            Modifier
                .width(MessageTokens.accentBarWidth)
                .fillMaxHeight()
                .clip(CircleShape)
                .background(colors.accent),
        )
        Column(verticalArrangement = Arrangement.spacedBy(MessageTokens.gap)) {
            Text(what, style = PosturaTheme.type.label, color = MessageTokens.titleColor)
            if (why != null) {
                Text(why, style = PosturaTheme.type.caption, color = MessageTokens.bodyColor)
            }
            Text(next, style = PosturaTheme.type.caption, color = colors.accent)
        }
    }
}
