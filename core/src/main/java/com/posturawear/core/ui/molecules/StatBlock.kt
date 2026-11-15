package com.posturawear.core.ui.molecules

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.wear.compose.material3.Text
import com.posturawear.core.tokens.theme.PosturaTheme

/**
 * Molécula: dato = cifra + rótulo. La cifra manda en la jerarquía; el rótulo la explica.
 * Se anuncia como un solo elemento a los lectores de pantalla.
 */
@Composable
fun StatBlock(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    valueColor: Color = PosturaTheme.colors.contentPrimary,
    large: Boolean = false,
) {
    Column(
        modifier.semantics(mergeDescendants = true) {},
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = value,
            style = if (large) PosturaTheme.type.metric else PosturaTheme.type.metricSmall,
            color = valueColor,
            maxLines = 1,
        )
        Text(
            text = label,
            style = PosturaTheme.type.caption,
            color = PosturaTheme.colors.contentSecondary,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
