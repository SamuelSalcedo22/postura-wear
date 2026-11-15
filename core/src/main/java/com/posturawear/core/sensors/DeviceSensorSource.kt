package com.posturawear.core.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.posturawear.core.domain.model.SensorSample
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow

/**
 * Acelerómetro y giroscopio reales del reloj o del celular.
 * Emite una lectura por cada evento del acelerómetro, con el último valor del
 * giroscopio. Si no hay giroscopio, la app sigue funcionando solo con inclinación.
 */
class DeviceSensorSource(context: Context) : SensorSource {
    private val manager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer: Sensor? = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val gyroscope: Sensor? = manager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

    override val isAvailable: Boolean get() = accelerometer != null

    override fun samples(): Flow<SensorSample> = callbackFlow {
        val accel = accelerometer
        if (accel == null) {
            close()
            return@callbackFlow
        }
        val gyro = FloatArray(3)
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                when (event.sensor.type) {
                    Sensor.TYPE_GYROSCOPE -> event.values.copyInto(gyro, endIndex = 3)
                    Sensor.TYPE_ACCELEROMETER -> trySend(
                        SensorSample(
                            timestampMs = event.timestamp / 1_000_000,
                            ax = event.values[0],
                            ay = event.values[1],
                            az = event.values[2],
                            gx = gyro[0],
                            gy = gyro[1],
                            gz = gyro[2],
                        ),
                    )
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
        }
        manager.registerListener(listener, accel, SAMPLING_PERIOD_US)
        gyroscope?.let { manager.registerListener(listener, it, SAMPLING_PERIOD_US) }
        awaitClose { manager.unregisterListener(listener) }
    }.buffer(capacity = 256, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    private companion object {
        const val SAMPLING_PERIOD_US = 1_000_000 / SyntheticMotion.SAMPLE_RATE_HZ
    }
}
