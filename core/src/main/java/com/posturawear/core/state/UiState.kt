package com.posturawear.core.state

import com.posturawear.core.domain.model.Detection
import com.posturawear.core.domain.model.SessionMode
import com.posturawear.core.domain.model.SessionRecord

/** Problema que el usuario debe conocer durante una sesión. La UI lo traduce a un mensaje en 3 partes. */
enum class SessionNotice { OFFLINE, SENSORS_MISSING }

/** Sesión en curso. */
data class SessionState(
    val mode: SessionMode,
    val paused: Boolean = false,
    val elapsedMs: Long = 0,
    val alignedPercent: Int? = null,
    val alerts: Int = 0,
    val detection: Detection? = null,
    val simulated: Boolean = false,
    val notice: SessionNotice? = null,
)

/** Recomendación que acompaña al resumen. */
sealed interface TipState {
    data object Loading : TipState

    /** Redactada en la nube por el modelo generativo. */
    data class Cloud(val text: String) : TipState

    /** Recomendación general guardada en la app; [failed] indica que se intentó la nube sin éxito. */
    data class Local(val index: Int, val failed: Boolean) : TipState
}

/** Resumen que se muestra al terminar. */
data class SummaryState(
    val record: SessionRecord,
    val saved: Boolean,
    val tip: TipState,
)

sealed interface CalibrationState {
    data object Idle : CalibrationState
    data class Running(val remainingSeconds: Int, val totalSeconds: Int) : CalibrationState
    data object Success : CalibrationState
    data class Failed(val reason: Reason) : CalibrationState

    enum class Reason { MOVING, SENSORS_MISSING }
}

enum class BackendStatus { UNKNOWN, CHECKING, OK, DOWN }
