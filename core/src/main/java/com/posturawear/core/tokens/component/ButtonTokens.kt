package com.posturawear.core.tokens.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import com.posturawear.core.tokens.semantic.SemanticShape
import com.posturawear.core.tokens.semantic.SemanticSize
import com.posturawear.core.tokens.semantic.SemanticSpacing
import com.posturawear.core.tokens.theme.PosturaTheme

/**
 * Tokens de componente (nivel 3) del botón.
 * Convención: `button.<variante>.<parte>.<estado>` →
 * `ButtonTokens.primary.bgDefault` = `button.primary.bg.default`.
 */
@Immutable
data class ButtonColorTokens(
    val bgDefault: Color,
    val bgPressed: Color,
    val bgDisabled: Color,
    val contentDefault: Color,
    val contentDisabled: Color,
)

object ButtonTokens {
    val minHeight = SemanticSize.touchTargetMin
    val shape = SemanticShape.control
    val paddingHorizontal = SemanticSpacing.insetHorizontal
    val paddingVertical = SemanticSpacing.insetVertical
    val iconGap = SemanticSpacing.stack
    const val pressedScale = 0.96f
    const val maxLines = 2

    val primary: ButtonColorTokens
        @Composable @ReadOnlyComposable get() = with(PosturaTheme.colors) {
            ButtonColorTokens(
                bgDefault = accentBrand,
                bgPressed = accentBrand.copy(alpha = 0.82f),
                bgDisabled = backgroundRaised,
                contentDefault = contentOnAccent,
                contentDisabled = contentMuted,
            )
        }

    val secondary: ButtonColorTokens
        @Composable @ReadOnlyComposable get() = with(PosturaTheme.colors) {
            ButtonColorTokens(
                bgDefault = backgroundSurface,
                bgPressed = backgroundRaised,
                bgDisabled = backgroundSurface,
                contentDefault = contentPrimary,
                contentDisabled = contentMuted,
            )
        }

    val danger: ButtonColorTokens
        @Composable @ReadOnlyComposable get() = with(PosturaTheme.colors) {
            ButtonColorTokens(
                bgDefault = statusErrorContainer,
                bgPressed = statusErrorContainer.copy(alpha = 0.8f),
                bgDisabled = backgroundSurface,
                contentDefault = statusError,
                contentDisabled = contentMuted,
            )
        }
}
