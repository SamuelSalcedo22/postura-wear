package com.posturawear.core.domain.model

import kotlinx.serialization.Serializable

/** Qué tan pronto se considera inclinación. A mayor sensibilidad, menor umbral. */
enum class Sensitivity(val tiltThresholdDeg: Float) {
    LOW(26f),
    MEDIUM(18f),
    HIGH(12f),
}

/** Escenarios del simulador de sensores para probar sin hardware. */
enum class SimScenario { GOOD, BAD, MIXED }

/** Idioma de la interfaz. [tag] es `null` cuando se sigue el idioma del dispositivo. */
enum class AppLanguage(val tag: String?) { SYSTEM(null), ES("es"), EN("en") }

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
    val simulatorEnabled: Boolean = false,
    val scenario: SimScenario = SimScenario.MIXED,
    val language: AppLanguage = AppLanguage.SYSTEM,
) {
    companion object {
        val AlertDelayOptions = listOf(5, 10, 20, 30)
    }
}
