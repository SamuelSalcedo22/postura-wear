package com.posturawear.core.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

/** Lo que la app le pide al backend. Todas las llamadas lanzan [IOException] si fallan. */
interface PostureApi {
    suspend fun isHealthy(): Boolean
    suspend fun classify(request: ClassifyRequest): ClassifyResponse
    suspend fun sessionSummary(request: SummaryRequest): SummaryResponse
}

/**
 * Cliente HTTP mínimo, sin dependencias externas. No envía claves ni
 * credenciales: los secretos de Google Cloud viven solo en el backend.
 */
class HttpPostureApi(baseUrl: String) : PostureApi {
    private val baseUrl = baseUrl.trimEnd('/')
    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun isHealthy(): Boolean = try {
        request("GET", "/health", body = null, timeoutMs = FAST_TIMEOUT_MS)
        true
    } catch (_: IOException) {
        false
    }

    override suspend fun classify(request: ClassifyRequest): ClassifyResponse = json.decodeFromString(
        request("POST", "/v1/classify", json.encodeToString(request), FAST_TIMEOUT_MS),
    )

    override suspend fun sessionSummary(request: SummaryRequest): SummaryResponse = json.decodeFromString(
        request("POST", "/v1/sessions/summary", json.encodeToString(request), SLOW_TIMEOUT_MS),
    )

    private suspend fun request(method: String, path: String, body: String?, timeoutMs: Int): String =
        withContext(Dispatchers.IO) {
            val connection = URL(baseUrl + path).openConnection() as HttpURLConnection
            try {
                connection.requestMethod = method
                connection.connectTimeout = timeoutMs
                connection.readTimeout = timeoutMs
                connection.setRequestProperty("Accept", "application/json")
                if (body != null) {
                    connection.doOutput = true
                    connection.setRequestProperty("Content-Type", "application/json; charset=utf-8")
                    connection.outputStream.use { it.write(body.toByteArray()) }
                }
                if (connection.responseCode !in 200..299) {
                    throw IOException("Respuesta inesperada del backend")
                }
                connection.inputStream.bufferedReader().use { it.readText() }
            } catch (e: IOException) {
                throw e
            } catch (e: RuntimeException) {
                // Respuestas mal formadas o URL inválida se tratan como "sin conexión".
                throw IOException(e)
            } finally {
                connection.disconnect()
            }
        }

    private companion object {
        /** La clasificación ocurre cada pocos segundos: si tarda, es mejor seguir en local. */
        const val FAST_TIMEOUT_MS = 2500
        const val SLOW_TIMEOUT_MS = 12_000
    }
}
