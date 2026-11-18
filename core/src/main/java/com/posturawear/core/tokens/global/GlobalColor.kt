package com.posturawear.core.tokens.global

import androidx.compose.ui.graphics.Color

/**
 * Tokens globales de color (nivel 1). Valores crudos, sin significado de uso.
 * Convención de nombre: `color.<familia>.<paso>` → [GlobalColor.Blue500] = `color.blue.500`.
 * Ningún componente debe leerlos directamente: se consumen a través de los semánticos.
 */
object GlobalColor {
    // color.ink.* — neutros fríos; 900 es negro puro para ahorrar batería en OLED.
    val Ink900 = Color(0xFF000000)
    val Ink800 = Color(0xFF0A0E13)
    val Ink700 = Color(0xFF131921)
    val Ink600 = Color(0xFF1D2530)
    val Ink500 = Color(0xFF2C3644)
    val Ink450 = Color(0xFF5E6978)
    val Ink400 = Color(0xFF7F8A99)
    val Ink200 = Color(0xFFB4BECB)
    val Ink150 = Color(0xFFC9D1DB)
    val Ink100 = Color(0xFFE6EBF1)
    val Ink50 = Color(0xFFF3F6F9)
    val Ink0 = Color(0xFFFFFFFF)

    // color.mint.* — alineación, progreso.
    val Mint100 = Color(0xFFD5F7EC)
    val Mint300 = Color(0xFF8FF7D4)
    val Mint500 = Color(0xFF3DE0AE)
    val Mint700 = Color(0xFF12936E)
    val Mint800 = Color(0xFF0B7457)
    val Mint900 = Color(0xFF07382B)

    // color.amber.* — atención amable.
    val Amber100 = Color(0xFFFFEBCC)
    val Amber300 = Color(0xFFFFD08A)
    val Amber500 = Color(0xFFFFAA3D)
    val Amber700 = Color(0xFFB36B0A)
    val Amber800 = Color(0xFF9A5B00)
    val Amber900 = Color(0xFF3D2503)

    // color.coral.* — errores y acciones destructivas.
    val Coral100 = Color(0xFFFFE0DB)
    val Coral300 = Color(0xFFFF9E8F)
    val Coral500 = Color(0xFFFF6B57)
    val Coral700 = Color(0xFFC2341F)
    val Coral900 = Color(0xFF40120C)

    // color.blue.* — información, nube.
    val Blue100 = Color(0xFFDCE8FF)
    val Blue300 = Color(0xFF8DB8FF)
    val Blue500 = Color(0xFF4D8DFF)
    val Blue700 = Color(0xFF1F56C2)
    val Blue900 = Color(0xFF0B1F47)

    // color.violet.* — calibración, aprendizaje.
    val Violet100 = Color(0xFFE8E3FF)
    val Violet300 = Color(0xFFC0B3FF)
    val Violet500 = Color(0xFF8E7CFF)
    val Violet700 = Color(0xFF5B45D6)
    val Violet900 = Color(0xFF211A52)
}
