package com.posturawear.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.posturawear.core.state.PosturaEngine
import com.posturawear.core.domain.model.AppLanguage
import com.posturawear.core.tokens.theme.PosturaTheme
import com.posturawear.core.ui.pages.CalibrationPage
import com.posturawear.core.ui.pages.HomePage
import com.posturawear.core.ui.pages.SessionPage
import com.posturawear.core.ui.pages.SettingsPage
import com.posturawear.core.ui.pages.StatsPage

/** Destinos de la app. Navegación plana: todo está a un toque de la pantalla principal. */
private object Routes {
    const val HOME = "home"
    const val SESSION = "session"
    const val STATS = "stats"
    const val CALIBRATION = "calibration"
    const val SETTINGS = "settings"
}

/**
 * Raíz de la interfaz, compartida por el reloj y por la app de pruebas del celular.
 * Volver siempre es deslizar hacia la derecha, el gesto estándar de Wear OS.
 */
@Composable
fun PosturaApp(engine: PosturaEngine, versionName: String) {
    val language by engine.language.collectAsStateWithLifecycle()
    LocalizedContent(language) {
      PosturaTheme {
        AppScaffold(Modifier.background(PosturaTheme.colors.backgroundPrimary)) {
            val data by engine.data.collectAsStateWithLifecycle()
            val session by engine.session.collectAsStateWithLifecycle()
            val summary by engine.summary.collectAsStateWithLifecycle()
            val calibrationState by engine.calibration.collectAsStateWithLifecycle()

            // Mientras se lee lo guardado solo se muestra el fondo: dura un instante.
            val loaded = data ?: run {
                Box(Modifier.fillMaxSize().background(PosturaTheme.colors.backgroundPrimary))
                return@AppScaffold
            }

            val nav = rememberSwipeDismissableNavController()
            fun open(route: String) = nav.navigate(route) { launchSingleTop = true }
            fun backHome() {
                nav.popBackStack(Routes.HOME, inclusive = false)
            }

            SwipeDismissableNavHost(navController = nav, startDestination = Routes.HOME) {
                composable(Routes.HOME) {
                    HomePage(
                        data = loaded,
                        sessionRunning = session != null,
                        onStartSession = {
                            engine.dismissSummary()
                            open(Routes.SESSION)
                        },
                        onViewSession = { open(Routes.SESSION) },
                        onOpenStats = { open(Routes.STATS) },
                        onOpenCalibration = { open(Routes.CALIBRATION) },
                        onOpenSettings = { open(Routes.SETTINGS) },
                    )
                }
                composable(Routes.SESSION) {
                    SessionPage(
                        session = session,
                        summary = summary,
                        onChooseMode = engine::startSession,
                        onPause = engine::pauseSession,
                        onResume = engine::resumeSession,
                        onFinish = engine::finishSession,
                        onDone = {
                            engine.dismissSummary()
                            backHome()
                        },
                    )
                }
                composable(Routes.STATS) {
                    StatsPage(
                        history = loaded.history,
                        habit = loaded.habit,
                        canStartSession = loaded.calibration != null && session == null,
                        onStartSession = {
                            engine.dismissSummary()
                            open(Routes.SESSION)
                        },
                    )
                }
                composable(Routes.CALIBRATION) {
                    DisposableEffect(Unit) { onDispose { engine.resetCalibrationState() } }
                    CalibrationPage(
                        calibration = loaded.calibration,
                        state = calibrationState,
                        onStart = engine::startCalibration,
                        onDone = ::backHome,
                        onOpenSettings = { open(Routes.SETTINGS) },
                    )
                }
                composable(Routes.SETTINGS) {
                    SettingsPage(
                        settings = loaded.settings,
                        versionName = versionName,
                        onChange = engine::updateSettings,
                        onClearHistory = engine::clearHistory,
                    )
                }
            }
        }
      }
    }
}
