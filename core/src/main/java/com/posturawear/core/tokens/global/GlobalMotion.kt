package com.posturawear.core.tokens.global

import androidx.compose.animation.core.CubicBezierEasing

/** Tokens globales de movimiento (nivel 1). `motion.duration.300` → [GlobalDuration.D300] (ms). */
object GlobalDuration {
    const val D100 = 120
    const val D200 = 220
    const val D300 = 360
    const val D500 = 600
    const val D900 = 2400
}

/** `motion.easing.*` → curvas. Preferimos salidas suaves: nada rebota ni sobresalta. */
object GlobalEasing {
    val Standard = CubicBezierEasing(0.2f, 0f, 0f, 1f)
    val Gentle = CubicBezierEasing(0.4f, 0f, 0.2f, 1f)
    val Breath = CubicBezierEasing(0.37f, 0f, 0.63f, 1f)
}
