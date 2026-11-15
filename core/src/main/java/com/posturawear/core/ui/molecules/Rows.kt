package com.posturawear.core.ui.molecules

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.Text
import com.posturawear.core.tokens.component.RowTokens
import com.posturawear.core.tokens.component.Tone
import com.posturawear.core.tokens.component.colors
import com.posturawear.core.tokens.theme.PosturaTheme
import com.posturawear.core.ui.atoms.Glyph
import com.posturawear.core.ui.atoms.GlyphIcon
import com.posturawear.core.ui.atoms.ToggleSwitch

@Composable
private fun RowLabels(label: String, secondary: String?, modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(
            text = label,
            style = PosturaTheme.type.label,
            color = RowTokens.labelColor,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        if (secondary != null) {
            Text(
                text = secondary,
                style = PosturaTheme.type.caption,
                color = RowTokens.secondaryColor,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

/** Molécula: fila de navegación = icono con tono + etiqueta + dato secundario. */
@Composable
fun NavRow(
    icon: GlyphIcon,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    secondary: String? = null,
    tone: Tone = Tone.Neutral,
) {
    val toneColors = tone.colors()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = RowTokens.minHeight)
            .clip(RowTokens.shape)
            .background(RowTokens.bgDefault)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = RowTokens.gap, vertical = RowTokens.paddingVertical),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.size(34.dp).clip(CircleShape).background(toneColors.container),
            contentAlignment = Alignment.Center,
        ) {
            Glyph(icon, tint = toneColors.accent, size = RowTokens.iconSize)
        }
        Spacer(Modifier.width(RowTokens.gap))
        RowLabels(label, secondary, Modifier.weight(1f))
    }
}

/** Molécula: fila con interruptor. Toda la fila es el área táctil. */
@Composable
fun ToggleRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    secondary: String? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = RowTokens.minHeight)
            .clip(RowTokens.shape)
            .background(RowTokens.bgDefault)
            .toggleable(checked, role = Role.Switch, onValueChange = onCheckedChange)
            .padding(horizontal = RowTokens.paddingHorizontal, vertical = RowTokens.paddingVertical),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RowLabels(label, secondary, Modifier.weight(1f))
        Spacer(Modifier.width(RowTokens.gap))
        ToggleSwitch(checked)
    }
}
