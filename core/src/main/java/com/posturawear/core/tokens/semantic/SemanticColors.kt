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

/** Único tema: oscuro. El fondo negro puro apaga píxeles en OLED y ahorra batería. */
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
