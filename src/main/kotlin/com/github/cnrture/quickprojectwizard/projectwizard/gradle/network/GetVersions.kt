package com.github.cnrture.quickprojectwizard.projectwizard.gradle.network

import com.github.cnrture.quickprojectwizard.data.VersionModel
import com.github.cnrture.quickprojectwizard.projectwizard.gradle.Versions
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.json.Json

suspend fun getVersions() {
    try {
        val client = HttpClient(CIO) {
            this.engine {
                requestTimeout = 5000
            }
        }
        val response: HttpResponse = client.get("https://api.canerture.com/qpwizard/versions")
        val versions = Json.decodeFromString<List<VersionModel>>(response.bodyAsText())
        val newVersionsMap = versions.associate { it.name to it.value }
        Versions.updateVersions(newVersionsMap)
        client.close()
    } catch (e: Exception) {
        println("Failed to fetch versions: ${e.message}")
    }
}