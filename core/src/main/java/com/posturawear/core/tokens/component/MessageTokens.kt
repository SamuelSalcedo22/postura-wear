package com.posturawear.core.tokens.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.posturawear.core.tokens.semantic.SemanticShape
import com.posturawear.core.tokens.semantic.SemanticSpacing
import com.posturawear.core.tokens.theme.PosturaTheme

/** Tono visual de un mensaje o etiqueta. El tono cambia; la voz no. */
enum class Tone { Positive, Attention, Info, Learning, Error, Neutral }

@Immutable
data class ToneColors(val container: Color, val accent: Color)

/** `tone.<tono>.*` — par contenedor/acento con contraste suficiente sobre negro. */
@Composable
@ReadOnlyComposable
fun Tone.colors(): ToneColors = with(PosturaTheme.colors) {
    when (this@colors) {
        Tone.Positive -> ToneColors(statusAlignedContainer, statusAligned)
        Tone.Attention -> ToneColors(statusAttentionContainer, statusAttention)
        Tone.Info -> ToneColors(accentInfoContainer, accentInfo)
        Tone.Learning -> ToneColors(accentLearningContainer, accentLearning)
        Tone.Error -> ToneColors(statusErrorContainer, statusError)
        Tone.Neutral -> ToneColors(backgroundSurface, contentSecondary)
    }
}

/** `message.*` — bloque de mensaje en tres partes (qué pasó, por qué, qué hacer). */
object MessageTokens {
    val shape = SemanticShape.surface
    val padding = SemanticSpacing.section
    val gap = SemanticSpacing.inline
    val accentBarWidth = 3.dp
    val bodyColor: Color @Composable @ReadOnlyComposable get() = PosturaTheme.colors.contentSecondary
    val titleColor: Color @Composable @ReadOnlyComposable get() = PosturaTheme.colors.contentPrimary
}

/** `badge.*` — etiqueta compacta de estado. */
object BadgeTokens {
    val shape = SemanticShape.tag
    val paddingHorizontal = SemanticSpacing.stack + SemanticSpacing.inline
    val paddingVertical = SemanticSpacing.inline
    val dotSize = 6.dp
    val gap = SemanticSpacing.inline
}
