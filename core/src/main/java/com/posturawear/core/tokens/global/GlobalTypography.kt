package com.posturawear.core.tokens.global

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Tokens globales de tipografía (nivel 1).
 * `font.size.300` → [GlobalFontSize.S300]. El mínimo legible en pantalla redonda es 12 sp.
 */
object GlobalFontSize {
    val S100 = 12.sp
    val S200 = 13.sp
    val S300 = 15.sp
    val S400 = 18.sp
    val S500 = 22.sp
    val S700 = 36.sp
}

object GlobalLineHeight {
    val S100 = 15.sp
    val S200 = 17.sp
    val S300 = 19.sp
    val S400 = 22.sp
    val S500 = 26.sp
    val S700 = 38.sp
}

object GlobalFontWeight {
    val Regular = FontWeight.Normal
    val Medium = FontWeight.Medium
    val SemiBold = FontWeight.SemiBold
    val Bold = FontWeight.Bold
}

/** `font.tracking.*` → espaciado entre letras. */
object GlobalTracking {
    val Tight = (-0.5).sp
    val Normal = 0.sp
    val Wide = 0.8.sp
}
