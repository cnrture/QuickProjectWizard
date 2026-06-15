package com.github.cnrture.quickprojectwizard.projectwizard.gradle.network

import com.github.cnrture.quickprojectwizard.projectwizard.gradle.Versions
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.json.Json

suspend fun getVersions() {
    val client = HttpClient(CIO) {
        this.engine {
            requestTimeout = 5000
        }
    }
    val response: HttpResponse = client.get("https://api.candroid.dev/qpwizard/versions")
    val versions = Json.decodeFromString<Map<String, String>>(response.bodyAsText())
    Versions.versionList = versions.toMutableMap()
    client.close()
}