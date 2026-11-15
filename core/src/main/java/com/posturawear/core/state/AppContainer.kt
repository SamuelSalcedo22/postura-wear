package com.posturawear.core.state

import android.content.Context
import com.posturawear.core.BuildConfig
import com.posturawear.core.network.HttpPostureApi
import com.posturawear.core.sensors.DeviceHaptics
import com.posturawear.core.sensors.DeviceSensorSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * Composición manual de dependencias. Un único motor por proceso, compartido
 * por la app del reloj y la de pruebas del celular.
 */
class AppContainer private constructor(context: Context) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    val engine = PosturaEngine(
        scope = scope,
        store = DataStoreAppStore(context),
        api = HttpPostureApi(BuildConfig.BACKEND_URL),
        deviceSensors = DeviceSensorSource(context),
        haptics = DeviceHaptics(context),
    )

    companion object {
        @Volatile
        private var instance: AppContainer? = null

        fun get(context: Context): AppContainer = instance ?: synchronized(this) {
            instance ?: AppContainer(context.applicationContext).also { instance = it }
        }
    }
}
