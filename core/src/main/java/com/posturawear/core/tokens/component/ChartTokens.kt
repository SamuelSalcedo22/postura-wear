package com.posturawear.core.tokens.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.posturawear.core.tokens.semantic.SemanticMotion
import com.posturawear.core.tokens.semantic.SemanticSpacing
import com.posturawear.core.tokens.theme.PosturaTheme

/** `chart.*` — barras semanales de tiempo alineado. */
object ChartTokens {
    val height = 64.dp
    val barWidth = 10.dp
    val barGap = SemanticSpacing.stack
    val labelGap = SemanticSpacing.inline
    const val growMs = SemanticMotion.statusShiftMs
    const val staggerMs = 40

    val barTrack: Color @Composable @ReadOnlyComposable get() = PosturaTheme.colors.backgroundRaised
    val barFill: Color @Composable @ReadOnlyComposable get() = PosturaTheme.colors.statusAligned
    val barFillToday: Color @Composable @ReadOnlyComposable get() = PosturaTheme.colors.contentPrimary
    val label: Color @Composable @ReadOnlyComposable get() = PosturaTheme.colors.contentMuted
    val labelToday: Color @Composable @ReadOnlyComposable get() = PosturaTheme.colors.contentPrimary
}
