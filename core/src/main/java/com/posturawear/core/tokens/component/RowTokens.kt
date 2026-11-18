package com.posturawear.core.tokens.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.posturawear.core.tokens.semantic.SemanticShape
import com.posturawear.core.tokens.semantic.SemanticSize
import com.posturawear.core.tokens.semantic.SemanticSpacing
import com.posturawear.core.tokens.theme.PosturaTheme

/** `row.*` — filas táctiles de lista (navegación, ajustes). */
object RowTokens {
    val minHeight = SemanticSize.touchTargetMin
    val shape = SemanticShape.surface
    val paddingHorizontal = SemanticSpacing.insetHorizontal
    val paddingVertical = SemanticSpacing.stack + SemanticSpacing.inline
    val gap = SemanticSpacing.section
    val iconSize = SemanticSize.iconMedium

    val bgDefault: Color @Composable @ReadOnlyComposable get() = PosturaTheme.colors.backgroundSurface
    val bgPressed: Color @Composable @ReadOnlyComposable get() = PosturaTheme.colors.backgroundRaised
    val labelColor: Color @Composable @ReadOnlyComposable get() = PosturaTheme.colors.contentPrimary
    val secondaryColor: Color @Composable @ReadOnlyComposable get() = PosturaTheme.colors.contentSecondary
}

/** `toggle.*` — interruptor dibujado a medida. */
object ToggleTokens {
    val trackWidth = 36.dp
    val trackHeight = 22.dp
    val thumbSize = 16.dp
    val thumbInset = 3.dp

    val trackOn: Color @Composable @ReadOnlyComposable get() = PosturaTheme.colors.accentBrand
    val trackOff: Color @Composable @ReadOnlyComposable get() = PosturaTheme.colors.borderSubtle
    val thumbOn: Color @Composable @ReadOnlyComposable get() = PosturaTheme.colors.contentOnAccent
    val thumbOff: Color @Composable @ReadOnlyComposable get() = PosturaTheme.colors.contentSecondary
}

/** `choice.*` — selector segmentado (sensibilidad, idioma, tema, filtro). */
object ChoiceTokens {
    val minHeight = 40.dp
    val shape = SemanticShape.tag
    val gap = SemanticSpacing.inline

    val bgSelected: Color @Composable @ReadOnlyComposable get() = PosturaTheme.colors.accentBrandContainer
    val bgDefault: Color @Composable @ReadOnlyComposable get() = PosturaTheme.colors.backgroundSurface
    val contentSelected: Color @Composable @ReadOnlyComposable get() = PosturaTheme.colors.accentBrand
    val contentDefault: Color @Composable @ReadOnlyComposable get() = PosturaTheme.colors.contentSecondary
}
