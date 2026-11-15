package com.posturawear.core.ui.templates

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.ScalingLazyColumnDefaults
import androidx.wear.compose.foundation.lazy.ScalingLazyListScope
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material3.ScreenScaffold
import com.posturawear.core.tokens.semantic.SemanticSpacing
import com.posturawear.core.tokens.theme.PosturaTheme

/**
 * Template: lista desplazable. Estructura de casi todas las pantallas:
 * hora arriba, contenido en una sola columna, indicador de scroll al borde.
 * Añade margen lateral para que el borde redondo no recorte el contenido.
 */
@Composable
fun ListTemplate(
    modifier: Modifier = Modifier,
    content: ScalingLazyListScope.() -> Unit,
) {
    val listState = rememberScalingLazyListState()
    ScreenScaffold(scrollState = listState, modifier = modifier) { scaffoldPadding ->
        val direction = LocalLayoutDirection.current
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize().background(PosturaTheme.colors.backgroundPrimary),
            state = listState,
            contentPadding = PaddingValues(
                start = scaffoldPadding.calculateStartPadding(direction) + SemanticSpacing.roundSafeHorizontal,
                end = scaffoldPadding.calculateEndPadding(direction) + SemanticSpacing.roundSafeHorizontal,
                top = scaffoldPadding.calculateTopPadding(),
                bottom = scaffoldPadding.calculateBottomPadding(),
            ),
            verticalArrangement = Arrangement.spacedBy(SemanticSpacing.stack),
            horizontalAlignment = Alignment.CenterHorizontally,
            content = content,
        )
    }
}

/**
 * Template: cara a pantalla completa con detalles opcionales debajo.
 * La cara ocupa exactamente una pantalla (lo que se entiende de un vistazo);
 * al deslizar aparecen los [details] (lo que se lee con calma).
 * No muestra la hora: la cara usa todo el círculo.
 */
@Composable
fun FaceTemplate(
    modifier: Modifier = Modifier,
    details: (@Composable ColumnScope.() -> Unit)? = null,
    face: @Composable () -> Unit,
) {
    val listState = rememberScalingLazyListState(initialCenterItemIndex = 0)
    ScreenScaffold(scrollState = listState, modifier = modifier, timeText = {}) {
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize().background(PosturaTheme.colors.backgroundPrimary),
            state = listState,
            contentPadding = PaddingValues(0.dp),
            autoCentering = null,
            // Sin escalado en los bordes: la cara debe verse entera y estable.
            scalingParams = ScalingLazyColumnDefaults.scalingParams(edgeScale = 1f, edgeAlpha = 1f),
            userScrollEnabled = details != null,
        ) {
            item { Box(Modifier.fillParentMaxSize()) { face() } }
            if (details != null) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = SemanticSpacing.section + SemanticSpacing.roundSafeHorizontal,
                                end = SemanticSpacing.section + SemanticSpacing.roundSafeHorizontal,
                                top = SemanticSpacing.section,
                                bottom = 48.dp,
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(SemanticSpacing.stack),
                        content = details,
                    )
                }
            }
        }
    }
}
