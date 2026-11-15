package com.posturawear.core.tokens.semantic

import androidx.compose.foundation.shape.RoundedCornerShape
import com.posturawear.core.tokens.global.GlobalDuration
import com.posturawear.core.tokens.global.GlobalEasing
import com.posturawear.core.tokens.global.GlobalHaptics
import com.posturawear.core.tokens.global.GlobalRadius
import com.posturawear.core.tokens.global.GlobalSpacing
import com.posturawear.core.tokens.global.GlobalStroke
import androidx.compose.ui.unit.dp

/** Tokens semánticos de espaciado (nivel 2): `space.<uso>`. */
object SemanticSpacing {
    /** Entre un icono y su texto, o entre líneas de un mismo bloque. */
    val inline = GlobalSpacing.S2
    /** Entre elementos hermanos de una lista. */
    val stack = GlobalSpacing.S3
    /** Entre grupos distintos de contenido. */
    val section = GlobalSpacing.S5
    /** Relleno interior de superficies táctiles. */
    val insetHorizontal = GlobalSpacing.S6
    val insetVertical = GlobalSpacing.S5
    /** Margen lateral extra en pantallas redondas, para que el borde no recorte texto. */
    val roundSafeHorizontal = GlobalSpacing.S5
}

/** `size.<uso>`: medidas mínimas de accesibilidad. */
object SemanticSize {
    val touchTargetMin = 48.dp
    val iconSmall = 16.dp
    val iconMedium = 22.dp
}

/** `shape.<uso>`. */
object SemanticShape {
    val control = RoundedCornerShape(GlobalRadius.Lg)
    val surface = RoundedCornerShape(GlobalRadius.Md)
    val tag = RoundedCornerShape(GlobalRadius.Full)
}

/** `stroke.<uso>`. */
object SemanticStroke {
    val divider = GlobalStroke.Hairline
    val ringTrack = GlobalStroke.Regular
    val ringProgress = GlobalStroke.Bold
}

/** `motion.<uso>`: toda animación de la app usa uno de estos papeles. */
object SemanticMotion {
    /** Respuesta inmediata al toque. */
    const val feedbackMs = GlobalDuration.D100
    /** Cambio de estado de un control. */
    const val stateChangeMs = GlobalDuration.D200
    /** Cambio de estado de postura: lento a propósito, para no alarmar. */
    const val statusShiftMs = GlobalDuration.D500
    /** Entrada de contenido. */
    const val enterMs = GlobalDuration.D300
    /** Ciclo de "respiración" del indicador. */
    const val breathMs = GlobalDuration.D900

    val standard = GlobalEasing.Standard
    val gentle = GlobalEasing.Gentle
    val breath = GlobalEasing.Breath
}

/** `haptic.<uso>`. */
object SemanticHaptics {
    val confirm = GlobalHaptics.Tap
    val postureNudge = GlobalHaptics.Nudge
    val success = GlobalHaptics.Success
    val error = GlobalHaptics.Warning
}
