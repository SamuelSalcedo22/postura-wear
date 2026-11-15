package com.posturawear.core.ui.molecules

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.wear.compose.material3.Text
import com.posturawear.core.tokens.component.ChoiceTokens
import com.posturawear.core.tokens.semantic.SemanticMotion
import com.posturawear.core.tokens.semantic.SemanticSpacing
import com.posturawear.core.tokens.theme.PosturaTheme

/**
 * Molécula: selector de una opción entre pocas.
 * [stacked] apila las opciones cuando las etiquetas son largas; en fila, cada
 * opción recibe el mismo ancho y su texto se trunca si no cabe.
 */
@Composable
fun <T> ChoiceGroup(
    options: List<Pair<T, String>>,
    selected: T,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
    stacked: Boolean = false,
) {
    @Composable
    fun Option(value: T, label: String, optionModifier: Modifier) {
        val isSelected = value == selected
        val bg by animateColorAsState(
            if (isSelected) ChoiceTokens.bgSelected else ChoiceTokens.bgDefault,
            tween(SemanticMotion.stateChangeMs),
            label = "choice-bg",
        )
        val fg by animateColorAsState(
            if (isSelected) ChoiceTokens.contentSelected else ChoiceTokens.contentDefault,
            tween(SemanticMotion.stateChangeMs),
            label = "choice-fg",
        )
        Box(
            modifier = optionModifier
                .defaultMinSize(minHeight = ChoiceTokens.minHeight)
                .clip(ChoiceTokens.shape)
                .background(bg)
                .selectable(isSelected, role = Role.RadioButton, onClick = { onSelect(value) })
                .padding(horizontal = SemanticSpacing.stack, vertical = SemanticSpacing.inline),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = label,
                style = PosturaTheme.type.label,
                color = fg,
                textAlign = TextAlign.Center,
                maxLines = if (stacked) 2 else 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }

    if (stacked) {
        Column(
            modifier.fillMaxWidth().selectableGroup(),
            verticalArrangement = Arrangement.spacedBy(ChoiceTokens.gap),
        ) {
            options.forEach { (value, label) -> Option(value, label, Modifier.fillMaxWidth()) }
        }
    } else {
        Row(
            modifier.fillMaxWidth().selectableGroup(),
            horizontalArrangement = Arrangement.spacedBy(ChoiceTokens.gap),
        ) {
            options.forEach { (value, label) -> Option(value, label, Modifier.weight(1f)) }
        }
    }
}
