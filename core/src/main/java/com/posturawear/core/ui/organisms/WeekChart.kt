package com.posturawear.core.ui.organisms

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.wear.compose.material3.Text
import com.posturawear.core.domain.DayStat
import com.posturawear.core.tokens.component.ChartTokens
import com.posturawear.core.tokens.semantic.SemanticMotion
import com.posturawear.core.tokens.theme.PosturaTheme

/**
 * Organismo: barras de los últimos 7 días. Cada barra crece con un pequeño
 * escalonado; hoy se resalta. Los días sin sesiones muestran solo el carril.
 *
 * @param initials iniciales de lunes a domingo, ya traducidas.
 */
@Composable
fun WeekChart(
    days: List<DayStat>,
    initials: List<String>,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clearAndSetSemantics { this.contentDescription = contentDescription },
        horizontalArrangement = Arrangement.spacedBy(ChartTokens.barGap, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.Bottom,
    ) {
        days.forEachIndexed { index, day ->
            val target = (day.alignedPercent ?: 0) / 100f
            val grown = remember(day.date) { Animatable(0f) }
            LaunchedEffect(target) {
                grown.animateTo(
                    target,
                    tween(ChartTokens.growMs, delayMillis = index * ChartTokens.staggerMs, easing = SemanticMotion.standard),
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .width(ChartTokens.barWidth)
                        .height(ChartTokens.height)
                        .clip(CircleShape)
                        .background(ChartTokens.barTrack),
                    contentAlignment = Alignment.BottomCenter,
                ) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(grown.value)
                            .clip(CircleShape)
                            .background(if (day.isToday) ChartTokens.barFillToday else ChartTokens.barFill),
                    )
                }
                Spacer(Modifier.height(ChartTokens.labelGap))
                Text(
                    text = initials.getOrElse(day.date.dayOfWeek.value - 1) { "" },
                    style = PosturaTheme.type.overline,
                    color = if (day.isToday) ChartTokens.labelToday else ChartTokens.label,
                )
            }
        }
    }
}
