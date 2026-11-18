package com.posturawear.core.sensors

import android.content.Context
import android.media.AudioAttributes
import android.os.Build
import android.os.VibrationAttributes
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.posturawear.core.tokens.global.HapticPattern
import com.posturawear.core.tokens.global.HapticUsage

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
        // Sin un tipo declarado, Android trata la vibración como "desconocida" y la
        // descarta si el usuario apagó la respuesta táctil (pasa en muchos Xiaomi).
        // Los avisos de postura se declaran como notificación para que siempre se sientan.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val usage = when (pattern.usage) {
                HapticUsage.Feedback -> VibrationAttributes.USAGE_TOUCH
                HapticUsage.Alert -> VibrationAttributes.USAGE_NOTIFICATION
            }
            v.vibrate(effect, VibrationAttributes.createForUsage(usage))
        } else {
            val usage = when (pattern.usage) {
                HapticUsage.Feedback -> AudioAttributes.USAGE_ASSISTANCE_SONIFICATION
                HapticUsage.Alert -> AudioAttributes.USAGE_NOTIFICATION
            }
            val attributes = AudioAttributes.Builder()
                .setUsage(usage)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            @Suppress("DEPRECATION")
            v.vibrate(effect, attributes)
        }
    }
}
