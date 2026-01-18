package com.github.cnrture.quickprojectwizard.projectwizard.general.data.source.remote

import com.github.cnrture.quickprojectwizard.data.NetworkLibrary

fun emptyMainService(packageName: String, selectedNetworkLibrary: NetworkLibrary): String {
    return when (selectedNetworkLibrary) {
        NetworkLibrary.Retrofit -> retrofit(packageName)
        NetworkLibrary.Ktor -> ktor(packageName)
        NetworkLibrary.Ktorfit -> ktorfit(packageName)
        else -> ""
    }
}

fun retrofit(packageName: String) = """
package $packageName.data.source.remote

interface MainService
"""

fun ktor(packageName: String) = """
package $packageName.data.source.remote

import $packageName.data.remote.KtorApi

class MainService : KtorApi()
"""

fun ktorfit(packageName: String) = """
package $packageName.data.source.remote

import de.jensklingenberg.ktorfit.http.GET

interface MainService {
    @GET("endpoint")
    suspend fun getData(): String
}
"""

