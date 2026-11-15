package com.posturawear.core.tokens.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.wear.compose.material3.ColorScheme
import androidx.wear.compose.material3.MaterialTheme
import com.posturawear.core.tokens.semantic.DarkSemanticColors
import com.posturawear.core.tokens.semantic.DefaultSemanticTypography
import com.posturawear.core.tokens.semantic.SemanticColors
import com.posturawear.core.tokens.semantic.SemanticTypography

private val LocalColors = staticCompositionLocalOf { DarkSemanticColors }
private val LocalTypography = staticCompositionLocalOf { DefaultSemanticTypography }

/**
 * Tema de la app. Expone los tokens semánticos por CompositionLocal y alinea
 * Material 3 para Wear con ellos, para que los componentes de la librería
 * (scaffold, indicador de scroll, hora) compartan la misma identidad.
 */
@Composable
fun PosturaTheme(
    colors: SemanticColors = DarkSemanticColors,
    typography: SemanticTypography = DefaultSemanticTypography,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalColors provides colors,
        LocalTypography provides typography,
    ) {
        MaterialTheme(
            colorScheme = ColorScheme(
                primary = colors.accentBrand,
                onPrimary = colors.contentOnAccent,
                primaryContainer = colors.accentBrandContainer,
                onPrimaryContainer = colors.contentPrimary,
                secondary = colors.accentInfo,
                onSecondary = colors.contentOnAccent,
                tertiary = colors.accentLearning,
                onTertiary = colors.contentOnAccent,
                surfaceContainerLow = colors.backgroundSurface,
                surfaceContainer = colors.backgroundSurface,
                surfaceContainerHigh = colors.backgroundRaised,
                onSurface = colors.contentPrimary,
                onSurfaceVariant = colors.contentSecondary,
                outline = colors.contentMuted,
                outlineVariant = colors.borderSubtle,
                background = colors.backgroundPrimary,
                onBackground = colors.contentPrimary,
                error = colors.statusError,
                onError = colors.contentOnAccent,
            ),
            content = content,
        )
    }
}

object PosturaTheme {
    val colors: SemanticColors
        @Composable @ReadOnlyComposable get() = LocalColors.current

    val type: SemanticTypography
        @Composable @ReadOnlyComposable get() = LocalTypography.current
}
