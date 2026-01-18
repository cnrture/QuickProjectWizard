package com.github.cnrture.quickprojectwizard.projectwizard.general.di

fun emptyAppModule(
    packageName: String,
    hasNetworkModule: Boolean,
    hasLocalModule: Boolean,
    hasRepositoryModule: Boolean,
    hasViewModel: Boolean = false,
) = """
package $packageName.di

val appModule = listOf(
    ${
    buildList {
        if (hasRepositoryModule) add("repositoryModule")
        if (hasNetworkModule) add("networkModule")
        if (hasLocalModule) add("localModule")
        if (hasViewModel) add("viewModelModule")
    }.joinToString(",\n    ")
}
)
""".trimIndent()
