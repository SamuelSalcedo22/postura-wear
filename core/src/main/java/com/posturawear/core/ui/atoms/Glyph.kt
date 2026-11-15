package com.posturawear.core.ui.atoms

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import com.posturawear.core.tokens.semantic.SemanticSize

/** Iconos propios de la app, dibujados con trazos redondeados sobre una cuadrícula 0-1. */
enum class GlyphIcon { Play, Pause, Stop, Chart, Level, Sliders, Check, Book, Desk }

/**
 * Átomo: icono. Es decorativo: el texto que lo acompaña es el que se anuncia
 * a los lectores de pantalla.
 */
@Composable
fun Glyph(
    icon: GlyphIcon,
    tint: Color,
    modifier: Modifier = Modifier,
    size: Dp = SemanticSize.iconMedium,
) {
    Canvas(modifier.size(size)) {
        val s = this.size.minDimension
        val line = Stroke(width = s * 0.1f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        fun p(x: Float, y: Float) = Offset(x * s, y * s)
        fun stroke(vararg points: Pair<Float, Float>) = drawPath(
            Path().apply {
                moveTo(points[0].first * s, points[0].second * s)
                points.drop(1).forEach { lineTo(it.first * s, it.second * s) }
            },
            tint,
            style = line,
        )

        when (icon) {
            GlyphIcon.Play -> {
                val triangle = Path().apply {
                    moveTo(0.34f * s, 0.24f * s)
                    lineTo(0.78f * s, 0.5f * s)
                    lineTo(0.34f * s, 0.76f * s)
                    close()
                }
                drawPath(triangle, tint)
                drawPath(triangle, tint, style = line)
            }
            GlyphIcon.Pause -> {
                stroke(0.36f to 0.26f, 0.36f to 0.74f)
                stroke(0.64f to 0.26f, 0.64f to 0.74f)
            }
            GlyphIcon.Stop -> roundRect(tint, 0.28f, 0.28f, 0.72f, 0.72f, filled = true, line)
            GlyphIcon.Chart -> {
                stroke(0.24f to 0.56f, 0.24f to 0.8f)
                stroke(0.5f to 0.22f, 0.5f to 0.8f)
                stroke(0.76f to 0.42f, 0.76f to 0.8f)
            }
            GlyphIcon.Level -> {
                drawCircle(tint, radius = 0.36f * s, center = p(0.5f, 0.5f), style = line)
                drawCircle(tint, radius = 0.13f * s, center = p(0.5f, 0.5f))
            }
            GlyphIcon.Sliders -> {
                stroke(0.18f to 0.34f, 0.82f to 0.34f)
                stroke(0.18f to 0.66f, 0.82f to 0.66f)
                drawCircle(tint, radius = 0.12f * s, center = p(0.64f, 0.34f))
                drawCircle(tint, radius = 0.12f * s, center = p(0.36f, 0.66f))
            }
            GlyphIcon.Check -> stroke(0.22f to 0.52f, 0.42f to 0.72f, 0.78f to 0.3f)
            GlyphIcon.Book -> {
                roundRect(tint, 0.18f, 0.26f, 0.82f, 0.74f, filled = false, line)
                stroke(0.5f to 0.28f, 0.5f to 0.72f)
            }
            GlyphIcon.Desk -> {
                roundRect(tint, 0.26f, 0.24f, 0.74f, 0.6f, filled = false, line)
                stroke(0.14f to 0.76f, 0.86f to 0.76f)
            }
        }
    }
}

private fun DrawScope.roundRect(
    tint: Color,
    left: Float,
    top: Float,
    right: Float,
    bottom: Float,
    filled: Boolean,
    line: Stroke,
) {
    val s = size.minDimension
    val topLeft = Offset(left * s, top * s)
    val rectSize = Size((right - left) * s, (bottom - top) * s)
    val corner = CornerRadius(0.08f * s)
    if (filled) drawRoundRect(tint, topLeft, rectSize, corner)
    drawRoundRect(tint, topLeft, rectSize, corner, style = line)
}
