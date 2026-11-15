package com.posturawear.core.tokens.semantic

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import com.posturawear.core.tokens.global.GlobalFontSize
import com.posturawear.core.tokens.global.GlobalFontWeight
import com.posturawear.core.tokens.global.GlobalLineHeight
import com.posturawear.core.tokens.global.GlobalTracking

/**
 * Tokens semánticos de tipografía (nivel 2): `type.<rol>`.
 * La jerarquía visual sale de aquí: metric > title > body > label > caption.
 */
@Immutable
data class SemanticTypography(
    /** Cifra protagonista (tiempo, porcentaje). Números tabulares para que no "bailen". */
    val metric: TextStyle,
    val metricSmall: TextStyle,
    val title: TextStyle,
    val body: TextStyle,
    val label: TextStyle,
    /** Rótulos de sección en versalitas visuales. */
    val overline: TextStyle,
    val caption: TextStyle,
)

val DefaultSemanticTypography = SemanticTypography(
    metric = TextStyle(
        fontSize = GlobalFontSize.S700,
        lineHeight = GlobalLineHeight.S700,
        fontWeight = GlobalFontWeight.SemiBold,
        letterSpacing = GlobalTracking.Tight,
        fontFeatureSettings = "tnum",
    ),
    metricSmall = TextStyle(
        fontSize = GlobalFontSize.S500,
        lineHeight = GlobalLineHeight.S500,
        fontWeight = GlobalFontWeight.SemiBold,
        fontFeatureSettings = "tnum",
    ),
    title = TextStyle(
        fontSize = GlobalFontSize.S400,
        lineHeight = GlobalLineHeight.S400,
        fontWeight = GlobalFontWeight.SemiBold,
    ),
    body = TextStyle(
        fontSize = GlobalFontSize.S300,
        lineHeight = GlobalLineHeight.S300,
        fontWeight = GlobalFontWeight.Regular,
    ),
    label = TextStyle(
        fontSize = GlobalFontSize.S300,
        lineHeight = GlobalLineHeight.S300,
        fontWeight = GlobalFontWeight.Medium,
    ),
    overline = TextStyle(
        fontSize = GlobalFontSize.S100,
        lineHeight = GlobalLineHeight.S100,
        fontWeight = GlobalFontWeight.Bold,
        letterSpacing = GlobalTracking.Wide,
    ),
    caption = TextStyle(
        fontSize = GlobalFontSize.S200,
        lineHeight = GlobalLineHeight.S200,
        fontWeight = GlobalFontWeight.Regular,
    ),
)
