package com.posturawear.wear

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.posturawear.core.state.AppContainer
import com.posturawear.core.state.versionName
import com.posturawear.core.ui.PosturaApp

/** Entrada de la app del reloj. Toda la interfaz vive en `core` para compartirla con el celular. */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val engine = AppContainer.get(this).engine
        setContent { PosturaApp(engine, versionName()) }
    }
}
