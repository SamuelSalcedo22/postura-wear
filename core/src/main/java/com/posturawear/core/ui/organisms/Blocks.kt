package com.posturawear.core.ui.organisms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.posturawear.core.domain.model.Orientation
import com.posturawear.core.tokens.semantic.SemanticShape
import com.posturawear.core.tokens.semantic.SemanticSpacing
import com.posturawear.core.tokens.semantic.SemanticStroke
import com.posturawear.core.tokens.theme.PosturaTheme
import com.posturawear.core.ui.atoms.BodyText
import com.posturawear.core.ui.atoms.TitleText
import com.posturawear.core.ui.molecules.StatBlock

/**
 * Organismo: cabecera con la marca viva (el nivel de burbuja en pequeño),
 * un título y una línea de apoyo. Abre la pantalla principal y los estados vacíos.
 */
@Composable
fun HeroHeader(
    title: String,
    body: String,
    dialStatus: DialStatus,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(bottom = SemanticSpacing.stack),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(SemanticSpacing.inline),
    ) {
        // Decorativo: el título ya dice lo mismo con palabras.
        AlignmentDial(dialStatus, Orientation.Zero, contentDescription = "", modifier = Modifier.size(44.dp))
        TitleText(title, Modifier.padding(top = SemanticSpacing.inline))
        BodyText(body)
    }
}

/** Organismo: dos datos lado a lado sobre una superficie, separados por una línea. */
@Composable
fun StatPair(
    leftValue: String,
    leftLabel: String,
    rightValue: String,
    rightLabel: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clip(SemanticShape.surface)
            .background(PosturaTheme.colors.backgroundSurface)
            .padding(vertical = SemanticSpacing.section, horizontal = SemanticSpacing.stack),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StatBlock(leftValue, leftLabel, Modifier.weight(1f))
        Box(
            Modifier
                .width(SemanticStroke.divider)
                .fillMaxHeight()
                .background(PosturaTheme.colors.borderSubtle),
        )
        StatBlock(rightValue, rightLabel, Modifier.weight(1f))
    }
}
