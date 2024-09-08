package com.github.cnrture.quickprojectwizard.general.data.source.remote

import com.github.cnrture.quickprojectwizard.general.NetworkLibrary

fun emptyMainService(packageName: String, selectedNetworkLibrary: NetworkLibrary): String {
    return when (selectedNetworkLibrary) {
        NetworkLibrary.Retrofit -> retrofit(packageName)
        NetworkLibrary.Ktor -> ktor(packageName)
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

