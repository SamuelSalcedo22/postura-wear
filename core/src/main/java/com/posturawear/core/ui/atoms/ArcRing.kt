package com.posturawear.core.ui.atoms

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import com.posturawear.core.tokens.component.DialTokens

/**
 * Átomo: anillo de progreso que abraza el borde de la pantalla redonda.
 * Deja un hueco abajo ([DialTokens.gapDegrees]) para la acción principal.
 */
@Composable
fun ArcRing(
    progress: Float,
    color: Color,
    modifier: Modifier = Modifier,
    animationMs: Int = DialTokens.shiftMs,
) {
    val animated by animateFloatAsState(progress.coerceIn(0f, 1f), tween(animationMs), label = "ring-progress")
    val animatedColor by animateColorAsState(color, tween(DialTokens.shiftMs), label = "ring-color")
    val track = DialTokens.track

    Canvas(modifier.fillMaxSize()) {
        val stroke = DialTokens.ringStroke.toPx()
        val inset = DialTokens.edgeInset.toPx() + stroke / 2
        val diameter = size.minDimension - inset * 2
        val topLeft = Offset((size.width - diameter) / 2, (size.height - diameter) / 2)
        val arcSize = Size(diameter, diameter)
        val start = 90f + DialTokens.gapDegrees / 2
        val sweep = 360f - DialTokens.gapDegrees

        drawArc(
            color = track,
            startAngle = start,
            sweepAngle = sweep,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(DialTokens.trackStroke.toPx(), cap = StrokeCap.Round),
        )
        if (animated > 0.005f) {
            drawArc(
                color = animatedColor,
                startAngle = start,
                sweepAngle = sweep * animated,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(stroke, cap = StrokeCap.Round),
            )
        }
    }
}
