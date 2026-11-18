package com.posturawear.core.sensors

import com.posturawear.core.domain.model.SensorSample
import com.posturawear.core.domain.model.SensorWindow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/** Origen de lecturas de movimiento (acelerómetro y giroscopio). */
interface SensorSource {
    /** Falso si el dispositivo no ofrece los sensores necesarios. */
    val isAvailable: Boolean

    /** Flujo frío de lecturas; los sensores se liberan al cancelar la recolección. */
    fun samples(): Flow<SensorSample>

    companion object {
        /** Lecturas por segundo. El backend lo usa para interpretar cada ventana. */
        const val SAMPLE_RATE_HZ = 25
    }
}

/** Agrupa las lecturas en ventanas consecutivas de [windowMs], sin solaparse. */
fun Flow<SensorSample>.windowed(windowMs: Long): Flow<SensorWindow> = flow {
    var buffer = ArrayList<SensorSample>()
    collect { sample ->
        buffer.add(sample)
        if (sample.timestampMs - buffer.first().timestampMs >= windowMs) {
            emit(SensorWindow(buffer))
            buffer = ArrayList()
        }
    }
}
