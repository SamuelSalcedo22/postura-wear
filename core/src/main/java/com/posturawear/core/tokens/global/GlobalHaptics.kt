package com.posturawear.core.tokens.global

/**
 * Qué tipo de vibración es para el sistema. Importa porque Android permite
 * apagar cada tipo por separado: mucha gente apaga la vibración al tocar
 * (respuesta táctil) pero deja la de notificaciones.
 */
enum class HapticUsage {
    /** Respuesta a un toque. Respeta el ajuste "vibrar al tocar" del sistema. */
    Feedback,

    /** Aviso que la persona debe sentir aunque no esté mirando la pantalla. */
    Alert,
}

/**
 * Tokens globales hápticos (nivel 1). En un wearable la vibración es parte del lenguaje
 * de diseño, así que también se tokeniza. Cada patrón alterna espera/vibración en ms
 * y lleva una amplitud (0-255) por tramo.
 */
class HapticPattern(
    val timings: LongArray,
    val amplitudes: IntArray,
    val usage: HapticUsage = HapticUsage.Alert,
) {
    init {
        require(timings.size == amplitudes.size) { "timings y amplitudes deben tener el mismo tamaño" }
    }
}

object GlobalHaptics {
    /** `haptic.tap` — confirmación breve. */
    val Tap = HapticPattern(longArrayOf(0, 30), intArrayOf(0, 120), HapticUsage.Feedback)

    /** `haptic.nudge` — dos pulsos suaves: "revisa tu postura". */
    val Nudge = HapticPattern(longArrayOf(0, 90, 120, 90), intArrayOf(0, 140, 0, 200))

    /** `haptic.success` — pulso ascendente. */
    val Success = HapticPattern(longArrayOf(0, 50, 60, 120), intArrayOf(0, 90, 0, 220))

    /** `haptic.warning` — pulso largo y único. */
    val Warning = HapticPattern(longArrayOf(0, 260), intArrayOf(0, 180))
}
