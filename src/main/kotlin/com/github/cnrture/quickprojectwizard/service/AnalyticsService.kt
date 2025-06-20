package com.github.cnrture.quickprojectwizard.service

import com.github.cnrture.quickprojectwizard.data.QPWEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import kotlinx.coroutines.*
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class AnalyticsService {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val measurementId = System.getenv("QPW_MEASUREMENT_ID").orEmpty()
    private val apiSecret = System.getenv("QPW_API_SECRET").orEmpty()

    private val isAnalyticsEnabled = measurementId.isNotEmpty() && apiSecret.isNotEmpty()

    fun track(event: String) {
        scope.launch {
            try {
                val eventWithTimestamp = QPWEvent(eventName = event, timestamp = getCurrentTimestamp())
                if (isAnalyticsEnabled) sendToFirebase(eventWithTimestamp)
            } catch (_: Exception) {
            }
        }
    }

    private suspend fun sendToFirebase(event: QPWEvent) {
        withContext(Dispatchers.IO) {
            try {
                val baseUrl = "https://www.google-analytics.com/mp/collect"
                val url = URL("$baseUrl?measurement_id=$measurementId&api_secret=$apiSecret")
                val connection = url.openConnection() as HttpURLConnection

                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true
                connection.connectTimeout = 5000
                connection.readTimeout = 5000

                val payload = createFirebasePayload(event)
                connection.outputStream.use { os -> os.write(payload.toByteArray()) }
                connection.responseCode
                connection.disconnect()
            } catch (_: Exception) {
            }
        }
    }

    private fun createFirebasePayload(event: QPWEvent): String {
        val clientId = generateClientId()
        val jsonPayload = """
            {
                "client_id": "$clientId",
                "events": [
                    {
                        "name": "${event.eventName}"
                    }
                ]
            }
        """.trimIndent()

        return jsonPayload
    }

    private fun generateClientId() = "qpw_" + System.currentTimeMillis().toString() + "_" + (1000..9999).random()

    private fun getCurrentTimestamp() = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

    companion object {
        fun getInstance(): AnalyticsService {
            return ApplicationManager.getApplication().getService(AnalyticsService::class.java)
        }
    }
}
