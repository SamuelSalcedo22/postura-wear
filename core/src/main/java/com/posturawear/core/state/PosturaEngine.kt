package com.posturawear.core.state

import com.posturawear.core.domain.AlertPolicy
import com.posturawear.core.domain.CalibrationResult
import com.posturawear.core.domain.Calibrator
import com.posturawear.core.domain.HabitModel
import com.posturawear.core.domain.HybridPostureDetector
import com.posturawear.core.domain.SessionTracker
import com.posturawear.core.domain.model.AppLanguage
import com.posturawear.core.domain.model.PostureClass
import com.posturawear.core.domain.model.SensorSample
import com.posturawear.core.domain.model.SensorWindow
import com.posturawear.core.domain.model.SessionMode
import com.posturawear.core.domain.model.SessionRecord
import com.posturawear.core.domain.model.Settings
import com.posturawear.core.network.ApiRemoteClassifier
import com.posturawear.core.network.PostureApi
import com.posturawear.core.network.SummaryRequest
import com.posturawear.core.sensors.Haptics
import com.posturawear.core.sensors.SensorSource
import com.posturawear.core.sensors.windowed
import com.posturawear.core.tokens.semantic.SemanticHaptics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import java.io.IOException
import java.util.Locale

/**
 * Orquesta sensores, detección, avisos y persistencia, y expone el estado que
 * dibuja la interfaz. No conoce Compose ni textos: solo datos.
 */
class PosturaEngine(
    private val scope: CoroutineScope,
    private val store: AppStore,
    private val api: PostureApi,
    private val deviceSensors: SensorSource,
    private val haptics: Haptics,
    private val clockMs: () -> Long = System::currentTimeMillis,
) {
    /** `null` mientras se lee lo guardado. */
    val data: StateFlow<AppData?> = store.data.stateIn(scope, SharingStarted.Eagerly, null)

    /** Idioma elegido; cambia la interfaz al instante, sin reiniciar la app. */
    val language: StateFlow<AppLanguage> =
        store.data.map { it.settings.language }.stateIn(scope, SharingStarted.Eagerly, AppLanguage.SYSTEM)

    private val _session = MutableStateFlow<SessionState?>(null)
    val session: StateFlow<SessionState?> = _session.asStateFlow()

    private val _summary = MutableStateFlow<SummaryState?>(null)
    val summary: StateFlow<SummaryState?> = _summary.asStateFlow()

    private val _calibration = MutableStateFlow<CalibrationState>(CalibrationState.Idle)
    val calibration: StateFlow<CalibrationState> = _calibration.asStateFlow()

    private val _backend = MutableStateFlow(BackendStatus.UNKNOWN)
    val backend: StateFlow<BackendStatus> = _backend.asStateFlow()

    private val settings: Settings get() = data.value?.settings ?: Settings()

    private val detector = HybridPostureDetector(ApiRemoteClassifier(api), clockMs = clockMs)
    private val alertPolicy = AlertPolicy()

    private var tracker: SessionTracker? = null

    /** Patrón habitual en memoria durante la sesión; se guarda al pausar o terminar para cuidar la batería. */
    private var habit: HabitModel? = null
    private var analysisJob: Job? = null
    private var tickerJob: Job? = null
    private var calibrationJob: Job? = null

    // ---------- Configuración ----------

    fun updateSettings(transform: (Settings) -> Settings) {
        scope.launch { store.update { it.copy(settings = transform(it.settings)) } }
    }

    fun clearHistory() {
        scope.launch { store.update { it.copy(history = emptyList()) } }
    }

    fun testVibration() = haptics.play(SemanticHaptics.postureNudge)

    fun checkBackend() {
        _backend.value = BackendStatus.CHECKING
        scope.launch { _backend.value = if (api.isHealthy()) BackendStatus.OK else BackendStatus.DOWN }
    }

    // ---------- Calibración ----------

    fun startCalibration() {
        if (calibrationJob?.isActive == true) return
        val source = deviceSensors
        if (!source.isAvailable) {
            _calibration.value = CalibrationState.Failed(CalibrationState.Reason.SENSORS_MISSING)
            return
        }
        calibrationJob = scope.launch {
            val total = Calibrator.DURATION_SECONDS
            val samples = ArrayList<SensorSample>()
            val collector = launch { source.samples().collect { samples.add(it) } }
            try {
                for (remaining in total downTo 1) {
                    _calibration.value = CalibrationState.Running(remaining, total)
                    delay(1000)
                }
            } finally {
                collector.cancel()
            }
            when (val result = Calibrator.calibrate(SensorWindow(samples.toList()), clockMs())) {
                is CalibrationResult.Success -> {
                    // Una referencia nueva invalida el patrón aprendido con la anterior.
                    store.update { it.copy(calibration = result.calibration, habit = it.habit.copy(windows = 0)) }
                    haptics.play(SemanticHaptics.success)
                    _calibration.value = CalibrationState.Success
                }
                CalibrationResult.TooMuchMovement -> failCalibration(CalibrationState.Reason.MOVING)
                CalibrationResult.NotEnoughData -> failCalibration(CalibrationState.Reason.SENSORS_MISSING)
            }
        }
    }

    private fun failCalibration(reason: CalibrationState.Reason) {
        haptics.play(SemanticHaptics.error)
        _calibration.value = CalibrationState.Failed(reason)
    }

    fun resetCalibrationState() {
        calibrationJob?.cancel()
        _calibration.value = CalibrationState.Idle
    }

    // ---------- Sesión ----------

    fun startSession(mode: SessionMode) {
        if (_session.value != null) return
        _summary.value = null
        tracker = SessionTracker(mode, clockMs())
        habit = data.value?.habit
        alertPolicy.reset()
        _session.value = SessionState(
            mode = mode,
            notice = if (deviceSensors.isAvailable) null else SessionNotice.SENSORS_MISSING,
        )
        haptics.play(SemanticHaptics.confirm)
        startJobs()
    }

    fun pauseSession() {
        if (_session.value?.paused != false) return
        stopJobs()
        alertPolicy.reset()
        val learned = habit
        if (learned != null) scope.launch { store.update { it.copy(habit = learned) } }
        _session.update { it?.copy(paused = true) }
    }

    fun resumeSession() {
        if (_session.value?.paused != true) return
        _session.update { it?.copy(paused = false) }
        startJobs()
    }

    fun finishSession() {
        val state = _session.value ?: return
        val current = tracker ?: return
        stopJobs()
        val record = current.toRecord().copy(durationMs = state.elapsedMs)
        val saved = record.durationMs >= SessionTracker.MIN_SAVED_DURATION_MS
        val learned = habit
        tracker = null
        habit = null
        _session.value = null
        _summary.value = SummaryState(record, saved, TipState.Loading)
        haptics.play(SemanticHaptics.success)
        scope.launch {
            store.update {
                it.copy(
                    habit = learned ?: it.habit,
                    history = if (saved) (it.history + record).takeLast(AppData.MAX_HISTORY) else it.history,
                )
            }
            _summary.update { it?.copy(tip = loadTip(record)) }
        }
    }

    fun dismissSummary() {
        _summary.value = null
    }

    private suspend fun loadTip(record: SessionRecord): TipState {
        val localIndex = (record.startedAtEpochMs / 1000 % Int.MAX_VALUE).toInt()
        if (!settings.cloudEnabled) return TipState.Local(localIndex, failed = false)
        return try {
            val response = api.sessionSummary(
                SummaryRequest(
                    mode = record.mode.name.lowercase(),
                    durationSeconds = record.durationMs / 1000,
                    alignedPercent = record.alignedPercent,
                    alerts = record.alerts,
                    language = settings.language.tag ?: Locale.getDefault().language,
                ),
            )
            if (response.text.isBlank()) TipState.Local(localIndex, failed = true) else TipState.Cloud(response.text.trim())
        } catch (_: IOException) {
            TipState.Local(localIndex, failed = true)
        }
    }

    private fun startJobs() {
        val source = deviceSensors
        tickerJob = scope.launch {
            while (isActive) {
                delay(TICK_MS)
                _session.update { it?.copy(elapsedMs = it.elapsedMs + TICK_MS) }
            }
        }
        if (!source.isAvailable) return
        analysisJob = scope.launch {
            source.samples().windowed(WINDOW_MS).collect { analyze(it) }
        }
    }

    private fun stopJobs() {
        analysisJob?.cancel()
        tickerJob?.cancel()
        analysisJob = null
        tickerJob = null
    }

    private suspend fun analyze(window: SensorWindow) {
        val snapshot = data.value ?: return
        val calibration = snapshot.calibration ?: return
        val current = tracker ?: return
        val config = snapshot.settings

        // Si la nube tarda más que la ventana, se descarta el intento y se sigue en local.
        val raw = withTimeoutOrNull(WINDOW_MS) {
            detector.detect(window, calibration, config.sensitivity, config.cloudEnabled)
        } ?: detector.detect(window, calibration, config.sensitivity, cloudEnabled = false) ?: return

        val model = habit ?: snapshot.habit
        val detection = raw.copy(unusual = raw.posture == PostureClass.ALIGNED && model.isUnusual(raw.deviation))
        current.onWindow(detection.posture, WINDOW_MS)

        if (alertPolicy.onDetection(detection, clockMs(), config)) {
            current.onAlert()
            if (config.vibrationEnabled) haptics.play(SemanticHaptics.postureNudge)
        }
        if (detection.posture == PostureClass.ALIGNED && !detection.unusual) {
            habit = model.learn(detection.deviation)
        }

        val record = current.toRecord()
        _session.update {
            it?.copy(
                detection = detection,
                alerts = current.alerts,
                alignedPercent = if (record.analyzedMs > 0) record.alignedPercent else null,
                notice = if (config.cloudEnabled && detector.isRemoteDown) SessionNotice.OFFLINE else null,
            )
        }
    }

    companion object {
        /** Duración de cada ventana de análisis. */
        const val WINDOW_MS = 3000L
        const val WINDOW_SECONDS = (WINDOW_MS / 1000).toInt()
        private const val TICK_MS = 1000L
    }
}
