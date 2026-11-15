package com.posturawear.core.state

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.posturawear.core.domain.HabitModel
import com.posturawear.core.domain.model.Calibration
import com.posturawear.core.domain.model.SessionRecord
import com.posturawear.core.domain.model.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

/** Todo lo que la app recuerda entre aperturas. Se guarda solo en el dispositivo. */
@Serializable
data class AppData(
    val settings: Settings = Settings(),
    val calibration: Calibration? = null,
    val habit: HabitModel = HabitModel(),
    val history: List<SessionRecord> = emptyList(),
) {
    companion object {
        const val MAX_HISTORY = 200
    }
}

interface AppStore {
    val data: Flow<AppData>
    suspend fun update(transform: (AppData) -> AppData)
}

private val Context.posturaDataStore: DataStore<Preferences> by preferencesDataStore(name = "postura")

class DataStoreAppStore(private val context: Context) : AppStore {
    private val key = stringPreferencesKey("app_data")
    private val json = Json { ignoreUnknownKeys = true }

    override val data: Flow<AppData> = context.posturaDataStore.data.map { decode(it[key]) }

    override suspend fun update(transform: (AppData) -> AppData) {
        context.posturaDataStore.edit { prefs ->
            prefs[key] = json.encodeToString(transform(decode(prefs[key])))
        }
    }

    /** Si lo guardado no se puede leer (por ejemplo, tras un cambio de versión), se empieza de cero. */
    private fun decode(raw: String?): AppData {
        if (raw == null) return AppData()
        return try {
            json.decodeFromString(raw)
        } catch (_: SerializationException) {
            AppData()
        } catch (_: IllegalArgumentException) {
            AppData()
        }
    }
}
