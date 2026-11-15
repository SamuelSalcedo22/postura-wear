package com.posturawear.phone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
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
 * redondo y, debajo, un panel para elegir la fuente de datos y el escenario.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val engine = AppContainer.get(this).engine
        val version = versionName()
        setContent {
            PosturaTheme {
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
