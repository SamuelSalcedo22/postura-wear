package com.posturawear.phone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.posturawear.core.domain.model.AppTheme
import com.posturawear.core.ui.LocalizedContent
import com.posturawear.core.ui.semanticColors
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.posturawear.core.R
import com.posturawear.core.state.AppContainer
import com.posturawear.core.state.versionName
import com.posturawear.core.tokens.semantic.SemanticSpacing
import com.posturawear.core.tokens.theme.PosturaTheme
import com.posturawear.core.ui.PosturaApp
import com.posturawear.core.ui.atoms.BodyText
import com.posturawear.core.ui.atoms.TitleText

/**
 * App de pruebas para celular: muestra la interfaz del reloj dentro de un marco
 * redondo y, debajo, un panel con el idioma, el tema, la conexión y la vibración.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val engine = AppContainer.get(this).engine
        val version = versionName()
        setContent {
            val language by engine.language.collectAsStateWithLifecycle()
            val theme by engine.theme.collectAsStateWithLifecycle()
            // Iconos de la barra de estado legibles sobre el fondo de cada tema.
            LaunchedEffect(theme) {
                val transparent = android.graphics.Color.TRANSPARENT
                val style = if (theme == AppTheme.LIGHT) {
                    SystemBarStyle.light(transparent, transparent)
                } else {
                    SystemBarStyle.dark(transparent)
                }
                enableEdgeToEdge(statusBarStyle = style, navigationBarStyle = style)
            }
            LocalizedContent(language) {
            PosturaTheme(colors = theme.semanticColors()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(PosturaTheme.colors.backgroundPrimary)
                        .safeDrawingPadding()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = SemanticSpacing.insetHorizontal, vertical = SemanticSpacing.section),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(SemanticSpacing.stack),
                ) {
                    TitleText(stringResource(R.string.phone_title))
                    BodyText(stringResource(R.string.phone_subtitle))
                    WatchFrame(Modifier.padding(vertical = SemanticSpacing.section)) {
                        PosturaApp(engine, version)
                    }
                    TestPanel(engine)
                }
            }
            }
        }
    }
}
