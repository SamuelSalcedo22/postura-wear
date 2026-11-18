package com.posturawear.core.domain.model

import kotlinx.serialization.Serializable

/** Qué tan pronto se considera inclinación. A mayor sensibilidad, menor umbral. */
enum class Sensitivity(val tiltThresholdDeg: Float) {
    LOW(26f),
    MEDIUM(18f),
    HIGH(12f),
}

/** Idioma de la interfaz. [tag] es `null` cuando se sigue el idioma del dispositivo. */
enum class AppLanguage(val tag: String?) { SYSTEM(null), ES("es"), EN("en") }

/** Apariencia de la interfaz. El oscuro es el predeterminado porque ahorra batería en pantallas OLED. */
enum class AppTheme { DARK, LIGHT }

/** Configuración personal del usuario. */
@Serializable
data class Settings(
    val vibrationEnabled: Boolean = true,
    val sensitivity: Sensitivity = Sensitivity.MEDIUM,
    /** Segundos que debe sostenerse el patrón antes de avisar. */
    val alertDelaySeconds: Int = 10,
    val alertOnForward: Boolean = true,
    val alertOnSide: Boolean = true,
    val alertOnUnusual: Boolean = false,
    val cloudEnabled: Boolean = true,
    val language: AppLanguage = AppLanguage.SYSTEM,
    val theme: AppTheme = AppTheme.DARK,
) {
    companion object {
        val AlertDelayOptions = listOf(5, 10, 20, 30)
    }
}
