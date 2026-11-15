package com.posturawear.core.ui.organisms

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.posturawear.core.domain.model.Orientation
import com.posturawear.core.tokens.component.DialTokens
import com.posturawear.core.tokens.semantic.SemanticMotion
import kotlin.math.hypot

enum class DialStatus { Idle, Aligned, Attention, Moving, Learning }

/**
 * Organismo: nivel de burbuja. Es la firma visual de la app.
 * La burbuja descansa en el centro cuando la postura coincide con la referencia
 * y se aleja en la dirección de la inclinación. "Respira" despacio para
 * transmitir calma; el color cambia sin sobresaltos.
 */
@Composable
fun AlignmentDial(
    status: DialStatus,
    deviation: Orientation,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    val targetColor = when (status) {
        DialStatus.Idle -> DialTokens.idle
        DialStatus.Aligned -> DialTokens.aligned
        DialStatus.Attention -> DialTokens.attention
        DialStatus.Moving -> DialTokens.neutral
        DialStatus.Learning -> DialTokens.learning
    }
    val color by animateColorAsState(targetColor, tween(DialTokens.shiftMs, easing = SemanticMotion.gentle), label = "dial-color")
    val guide = DialTokens.guide

    val drift = spring<Float>(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessVeryLow)
    val x by animateFloatAsState((deviation.rollDeg / DialTokens.fullScaleDegrees).coerceIn(-1.4f, 1.4f), drift, label = "dial-x")
    val y by animateFloatAsState((-deviation.pitchDeg / DialTokens.fullScaleDegrees).coerceIn(-1.4f, 1.4f), drift, label = "dial-y")

    val breath by rememberInfiniteTransition(label = "dial-breath").animateFloat(
        initialValue = DialTokens.breathScaleMin,
        targetValue = DialTokens.breathScaleMax,
        animationSpec = infiniteRepeatable(
            tween(if (status == DialStatus.Moving) DialTokens.breathMs / 3 else DialTokens.breathMs, easing = SemanticMotion.breath),
            RepeatMode.Reverse,
        ),
        label = "dial-breath-scale",
    )

    Canvas(
        modifier
            .aspectRatio(1f)
            .semantics { this.contentDescription = contentDescription },
    ) {
        val center = Offset(size.width / 2, size.height / 2)
        val hairline = DialTokens.guideStroke.toPx()
        val outer = size.minDimension / 2 - hairline
        val bubble = outer * DialTokens.bubbleRadiusFraction * 1.6f
        val travel = outer - bubble

        // Guías: aro exterior, casa de la burbuja y cuatro marcas cardinales.
        drawCircle(guide, radius = outer, center = center, style = Stroke(hairline))
        drawCircle(guide, radius = bubble * 1.22f, center = center, style = Stroke(hairline))
        for (i in 0 until 4) {
            val dx = floatArrayOf(0f, 1f, 0f, -1f)[i]
            val dy = floatArrayOf(-1f, 0f, 1f, 0f)[i]
            drawLine(
                color = guide,
                start = center + Offset(dx * outer, dy * outer),
                end = center + Offset(dx * (outer - bubble * 0.4f), dy * (outer - bubble * 0.4f)),
                strokeWidth = hairline * 2,
                cap = StrokeCap.Round,
            )
        }

        // La burbuja nunca sale del aro: se limita la longitud del desplazamiento.
        val length = hypot(x, y)
        val limit = if (length > 1f) 1f / length else 1f
        val position = center + Offset(x * limit * travel, y * limit * travel)

        drawCircle(color.copy(alpha = DialTokens.haloAlpha), radius = bubble * 1.45f * breath, center = position)
        drawCircle(color, radius = bubble * breath, center = position)
        drawCircle(
            Color.White.copy(alpha = 0.35f),
            radius = bubble * 0.22f,
            center = position - Offset(bubble * 0.34f, bubble * 0.34f),
        )
    }
}
