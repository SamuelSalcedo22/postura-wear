package com.posturawear.core.sensors

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.posturawear.core.tokens.global.HapticPattern

/** Salida háptica. Los patrones vienen de los tokens, no se escriben a mano. */
fun interface Haptics {
    fun play(pattern: HapticPattern)
}

class DeviceHaptics(context: Context) : Haptics {
    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager)?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    override fun play(pattern: HapticPattern) {
        val v = vibrator?.takeIf { it.hasVibrator() } ?: return
        val effect = if (v.hasAmplitudeControl()) {
            VibrationEffect.createWaveform(pattern.timings, pattern.amplitudes, -1)
        } else {
            VibrationEffect.createWaveform(pattern.timings, -1)
        }
        v.vibrate(effect)
    }
}
