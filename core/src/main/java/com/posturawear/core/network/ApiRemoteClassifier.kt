package com.posturawear.core.network

import com.posturawear.core.domain.RemoteClassification
import com.posturawear.core.domain.RemoteClassifier
import com.posturawear.core.domain.model.Calibration
import com.posturawear.core.domain.model.PostureClass
import com.posturawear.core.domain.model.SensorWindow
import com.posturawear.core.domain.model.Sensitivity
import com.posturawear.core.sensors.SensorSource

/** Adapta el [PostureApi] al contrato [RemoteClassifier] del dominio. */
class ApiRemoteClassifier(private val api: PostureApi) : RemoteClassifier {
    override suspend fun classify(
        window: SensorWindow,
        calibration: Calibration,
        sensitivity: Sensitivity,
    ): RemoteClassification {
        val response = api.classify(
            ClassifyRequest(
                samples = window.samples.map { listOf(it.ax, it.ay, it.az, it.gx, it.gy, it.gz) },
                sampleRateHz = SensorSource.SAMPLE_RATE_HZ,
                baselinePitch = calibration.baseline.pitchDeg,
                baselineRoll = calibration.baseline.rollDeg,
                tiltThreshold = sensitivity.tiltThresholdDeg,
            ),
        )
        return RemoteClassification(response.posture.toPostureClass(), response.confidence.coerceIn(0f, 1f))
    }

    private fun String.toPostureClass(): PostureClass = when (lowercase()) {
        "aligned" -> PostureClass.ALIGNED
        "forward_tilt" -> PostureClass.FORWARD_TILT
        "side_tilt" -> PostureClass.SIDE_TILT
        "moving" -> PostureClass.MOVING
        else -> PostureClass.UNKNOWN
    }
}
