package com.posturawear.phone

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.posturawear.core.tokens.global.GlobalColor

/**
 * Simula la pantalla redonda del reloj en el celular. Además de recortar en
 * círculo, le dice al contenido que la pantalla es redonda y mide [screenSize],
 * para que los componentes de Wear (hora curva, márgenes, listas) se comporten
 * igual que en un reloj real.
 */
@Composable
fun WatchFrame(
    modifier: Modifier = Modifier,
    screenSize: Dp = 225.dp,
    content: @Composable () -> Unit,
) {
    val bezel = 14.dp
    Box(modifier, contentAlignment = Alignment.Center) {
        // Corona.
        Box(
            Modifier
                .align(Alignment.CenterEnd)
                .offset(x = 7.dp)
                .size(width = 12.dp, height = 30.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(GlobalColor.Ink500),
        )
        // Caja y bisel.
        Box(
            Modifier
                .size(screenSize + bezel * 2)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(GlobalColor.Ink500, GlobalColor.Ink700)))
                .border(1.dp, GlobalColor.Ink400.copy(alpha = 0.4f), CircleShape)
                .padding(bezel),
        ) {
            val base = LocalConfiguration.current
            val watchConfiguration = remember(base, screenSize) {
                Configuration(base).apply {
                    screenWidthDp = screenSize.value.toInt()
                    screenHeightDp = screenSize.value.toInt()
                    screenLayout = (screenLayout and Configuration.SCREENLAYOUT_ROUND_MASK.inv()) or
                        Configuration.SCREENLAYOUT_ROUND_YES
                }
            }
            CompositionLocalProvider(LocalConfiguration provides watchConfiguration) {
                Box(Modifier.size(screenSize).clip(CircleShape).background(GlobalColor.Ink900)) {
                    content()
                }
            }
        }
    }
}
