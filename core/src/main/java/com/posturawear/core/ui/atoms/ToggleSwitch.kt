package com.posturawear.core.ui.atoms

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.posturawear.core.tokens.component.ToggleTokens
import com.posturawear.core.tokens.semantic.SemanticMotion

/**
 * Átomo: interruptor. Solo dibuja el estado; quien lo contiene (la fila) recibe
 * el toque, para que toda la fila sea el área táctil.
 */
@Composable
fun ToggleSwitch(checked: Boolean, modifier: Modifier = Modifier) {
    val spec = tween<Color>(SemanticMotion.stateChangeMs, easing = SemanticMotion.standard)
    val track by animateColorAsState(if (checked) ToggleTokens.trackOn else ToggleTokens.trackOff, spec, label = "track")
    val thumb by animateColorAsState(if (checked) ToggleTokens.thumbOn else ToggleTokens.thumbOff, spec, label = "thumb")
    val travel = ToggleTokens.trackWidth - ToggleTokens.thumbSize - ToggleTokens.thumbInset * 2
    val offset by animateDpAsState(
        targetValue = if (checked) ToggleTokens.thumbInset + travel else ToggleTokens.thumbInset,
        animationSpec = tween(SemanticMotion.stateChangeMs, easing = SemanticMotion.standard),
        label = "thumb-offset",
    )
    Box(
        modifier = modifier
            .size(ToggleTokens.trackWidth, ToggleTokens.trackHeight)
            .clip(CircleShape)
            .background(track),
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            Modifier
                .offset(x = offset)
                .size(ToggleTokens.thumbSize)
                .clip(CircleShape)
                .background(thumb),
        )
    }
}
