package com.posturawear.core.tokens.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.posturawear.core.tokens.semantic.SemanticMotion
import com.posturawear.core.tokens.semantic.SemanticStroke
import com.posturawear.core.tokens.theme.PosturaTheme

/**
 * `dial.*` — el "nivel de burbuja", pieza de identidad de la app: una burbuja que
 * se centra cuando la postura está alineada y se desplaza al inclinarse.
 */
object DialTokens {
    val ringStroke = SemanticStroke.ringProgress
    val trackStroke = SemanticStroke.ringTrack
    val guideStroke = SemanticStroke.divider
    val edgeInset = 5.dp

    /** Hueco inferior del anillo, en grados, donde vive la acción principal. */
    const val gapDegrees = 70f

    /** Tamaño de la burbuja como fracción del radio del dial. */
    const val bubbleRadiusFraction = 0.2f
    const val guideRadiusFraction = 0.34f
    const val haloAlpha = 0.22f

    /** Desviación (en grados) que lleva la burbuja al borde de su zona. */
    const val fullScaleDegrees = 35f

    const val breathMs = SemanticMotion.breathMs
    const val shiftMs = SemanticMotion.statusShiftMs
    const val breathScaleMin = 0.94f
    const val breathScaleMax = 1.06f

    val track: Color @Composable @ReadOnlyComposable get() = PosturaTheme.colors.backgroundRaised
    val guide: Color @Composable @ReadOnlyComposable get() = PosturaTheme.colors.borderSubtle
    val aligned: Color @Composable @ReadOnlyComposable get() = PosturaTheme.colors.statusAligned
    val attention: Color @Composable @ReadOnlyComposable get() = PosturaTheme.colors.statusAttention
    val neutral: Color @Composable @ReadOnlyComposable get() = PosturaTheme.colors.statusNeutral
    val idle: Color @Composable @ReadOnlyComposable get() = PosturaTheme.colors.contentMuted
    val learning: Color @Composable @ReadOnlyComposable get() = PosturaTheme.colors.accentLearning
}
