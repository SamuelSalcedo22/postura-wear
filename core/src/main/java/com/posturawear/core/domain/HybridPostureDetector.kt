package com.posturawear.core.domain

import com.posturawear.core.domain.model.Calibration
import com.posturawear.core.domain.model.Detection
import com.posturawear.core.domain.model.DetectionSource
import com.posturawear.core.domain.model.PostureClass
import com.posturawear.core.domain.model.SensorWindow
import com.posturawear.core.domain.model.Sensitivity
import kotlinx.coroutines.CancellationException

/** Respuesta del clasificador remoto. */
data class RemoteClassification(val posture: PostureClass, val confidence: Float)

/** Clasificador en la nube. Lanza una excepción si no puede responder. */
fun interface RemoteClassifier {
    suspend fun classify(window: SensorWindow, calibration: Calibration, sensitivity: Sensitivity): RemoteClassification
}

/**
 * Enfoque combinado: intenta clasificar en la nube y, si falla, sigue con la
 * detección local por umbrales. Tras un fallo espera [retryAfterMs] antes de
 * volver a intentar, para no gastar batería ni datos con la red caída.
 */
class HybridPostureDetector(
    private val remote: RemoteClassifier?,
    private val retryAfterMs: Long = DEFAULT_RETRY_AFTER_MS,
    private val clockMs: () -> Long = System::currentTimeMillis,
) {
    private var remoteDownSinceMs: Long? = null

    /** Verdadero si el último intento en la nube falló y aún no toca reintentar. */
    val isRemoteDown: Boolean get() = remoteDownSinceMs != null

    suspend fun detect(
        window: SensorWindow,
        calibration: Calibration,
        sensitivity: Sensitivity,
        cloudEnabled: Boolean,
    ): Detection? {
        val features = FeatureExtractor.extract(window) ?: return null
        val local = ThresholdPostureDetector.detect(features, calibration, sensitivity)
        if (!cloudEnabled || remote == null) {
            remoteDownSinceMs = null
            return local
        }

        val downSince = remoteDownSinceMs
        if (downSince != null && clockMs() - downSince < retryAfterMs) return local

        return try {
            val result = remote.classify(window, calibration, sensitivity)
            remoteDownSinceMs = null
            if (result.posture == PostureClass.UNKNOWN) {
                local
            } else {
                local.copy(posture = result.posture, confidence = result.confidence, source = DetectionSource.CLOUD)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            remoteDownSinceMs = clockMs()
            local
        }
    }

    companion object {
        const val DEFAULT_RETRY_AFTER_MS = 30_000L
    }
}
