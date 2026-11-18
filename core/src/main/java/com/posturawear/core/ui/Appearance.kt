package com.posturawear.core.ui

import com.posturawear.core.domain.model.AppTheme
import com.posturawear.core.tokens.semantic.DarkSemanticColors
import com.posturawear.core.tokens.semantic.LightSemanticColors
import com.posturawear.core.tokens.semantic.SemanticColors

/** Une la preferencia del usuario con el juego de tokens semánticos de color. */
fun AppTheme.semanticColors(): SemanticColors = when (this) {
    AppTheme.DARK -> DarkSemanticColors
    AppTheme.LIGHT -> LightSemanticColors
}
