package com.posturawear.core.ui.atoms

import androidx.compose.runtime.Composable
import androidx.wear.compose.material3.TimeText
import androidx.wear.compose.material3.TimeTextDefaults
import androidx.wear.compose.material3.timeTextCurvedText
import com.posturawear.core.tokens.theme.PosturaTheme

/**
 * Átomo: hora curva en el borde superior. La de la librería fija un gris claro
 * pensado solo para fondo negro; esta toma el color del tema activo para que
 * también se lea en el tema claro.
 */
@Composable
fun ClockText() {
    val style = TimeTextDefaults.timeTextStyle(color = PosturaTheme.colors.contentSecondary)
    TimeText { time -> timeTextCurvedText(time, style) }
}
