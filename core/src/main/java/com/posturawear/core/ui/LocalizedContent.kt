package com.posturawear.core.ui

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.content.res.Resources
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import com.posturawear.core.domain.model.AppLanguage
import java.util.Locale

/**
 * Aplica el idioma elegido en Ajustes a todo el [content], al instante y sin
 * reiniciar la app. Con [AppLanguage.SYSTEM] no cambia nada: se usa el idioma
 * del dispositivo.
 *
 * Solo se sustituyen los recursos (textos, plurales, formatos); el contexto
 * sigue siendo el de la actividad, para no afectar al ciclo de vida.
 */
@Composable
fun LocalizedContent(language: AppLanguage, content: @Composable () -> Unit) {
    val tag = language.tag
    if (tag == null) {
        content()
        return
    }
    val baseContext = LocalContext.current
    val baseConfiguration = LocalConfiguration.current
    val configuration = remember(baseConfiguration, tag) {
        Configuration(baseConfiguration).apply { setLocale(Locale.forLanguageTag(tag)) }
    }
    val context = remember(baseContext, configuration) {
        val localized = baseContext.createConfigurationContext(configuration).resources
        object : ContextWrapper(baseContext) {
            override fun getResources(): Resources = localized
        } as Context
    }
    CompositionLocalProvider(
        LocalContext provides context,
        LocalConfiguration provides configuration,
        content = content,
    )
}
