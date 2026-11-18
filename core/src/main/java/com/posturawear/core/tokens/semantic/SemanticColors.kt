package com.posturawear.core.tokens.semantic

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.posturawear.core.tokens.global.GlobalColor

/**
 * Tokens semánticos de color (nivel 2). Nombran la intención, no el valor.
 * Convención: `color.<rol>.<variante>` → [backgroundPrimary] = `color.background.primary`.
 */
@Immutable
data class SemanticColors(
    // color.background.*
    val backgroundPrimary: Color,
    val backgroundSurface: Color,
    val backgroundRaised: Color,
    // color.content.*
    val contentPrimary: Color,
    val contentSecondary: Color,
    val contentMuted: Color,
    val contentOnAccent: Color,
    // color.border.*
    val borderSubtle: Color,
    // color.accent.*
    val accentBrand: Color,
    val accentBrandContainer: Color,
    val accentInfo: Color,
    val accentInfoContainer: Color,
    val accentLearning: Color,
    val accentLearningContainer: Color,
    // color.status.*
    val statusAligned: Color,
    val statusAlignedContainer: Color,
    val statusAttention: Color,
    val statusAttentionContainer: Color,
    val statusNeutral: Color,
    val statusError: Color,
    val statusErrorContainer: Color,
)

/** Tema oscuro (predeterminado). El fondo negro puro apaga píxeles en OLED y ahorra batería. */
val DarkSemanticColors = SemanticColors(
    backgroundPrimary = GlobalColor.Ink900,
    backgroundSurface = GlobalColor.Ink700,
    backgroundRaised = GlobalColor.Ink600,
    contentPrimary = GlobalColor.Ink50,
    contentSecondary = GlobalColor.Ink200,
    contentMuted = GlobalColor.Ink400,
    contentOnAccent = GlobalColor.Ink900,
    borderSubtle = GlobalColor.Ink500,
    accentBrand = GlobalColor.Mint500,
    accentBrandContainer = GlobalColor.Mint900,
    accentInfo = GlobalColor.Blue300,
    accentInfoContainer = GlobalColor.Blue900,
    accentLearning = GlobalColor.Violet300,
    accentLearningContainer = GlobalColor.Violet900,
    statusAligned = GlobalColor.Mint500,
    statusAlignedContainer = GlobalColor.Mint900,
    statusAttention = GlobalColor.Amber500,
    statusAttentionContainer = GlobalColor.Amber900,
    statusNeutral = GlobalColor.Blue300,
    statusError = GlobalColor.Coral500,
    statusErrorContainer = GlobalColor.Coral900,
)

/**
 * Tema claro en tonos beige, para leer a plena luz del sol sin el brillo de un
 * blanco puro. Los acentos usan los pasos más oscuros de cada familia para
 * mantener un contraste de al menos 4.5:1; el significado de cada color no cambia
 * entre temas.
 */
val LightSemanticColors = SemanticColors(
    backgroundPrimary = GlobalColor.Sand50,
    backgroundSurface = GlobalColor.Sand0,
    backgroundRaised = GlobalColor.Sand100,
    contentPrimary = GlobalColor.Sand900,
    contentSecondary = GlobalColor.Sand700,
    contentMuted = GlobalColor.Sand600,
    contentOnAccent = GlobalColor.Ink0,
    borderSubtle = GlobalColor.Sand200,
    accentBrand = GlobalColor.Mint800,
    accentBrandContainer = GlobalColor.Mint100,
    accentInfo = GlobalColor.Blue700,
    accentInfoContainer = GlobalColor.Blue100,
    accentLearning = GlobalColor.Violet700,
    accentLearningContainer = GlobalColor.Violet100,
    statusAligned = GlobalColor.Mint800,
    statusAlignedContainer = GlobalColor.Mint100,
    statusAttention = GlobalColor.Amber800,
    statusAttentionContainer = GlobalColor.Amber100,
    statusNeutral = GlobalColor.Blue700,
    statusError = GlobalColor.Coral700,
    statusErrorContainer = GlobalColor.Coral100,
)
