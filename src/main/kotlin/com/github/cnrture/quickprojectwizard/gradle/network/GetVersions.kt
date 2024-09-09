package com.github.cnrture.quickprojectwizard.gradle.network

import com.github.cnrture.quickprojectwizard.gradle.Versions
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.json.Json

suspend fun getVersions() {
    val client = HttpClient(CIO) {
        this.engine {
            requestTimeout = 2000
        }
    }
    val response: HttpResponse = client.get("https://api.canerture.com/qpwizard/versions")
    val versions = Json.decodeFromString<List<VersionModel>>(response.bodyAsText())
    versions.forEach {
        Versions.versionList[it.name] = it.value
    }
    client.close()
}