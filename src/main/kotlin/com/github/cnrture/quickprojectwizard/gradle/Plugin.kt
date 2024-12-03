package com.github.cnrture.quickprojectwizard.gradle

sealed class Plugin(val name: String, val id: String, val verRef: String) {
    data object AndroidApplication : Plugin("android-application", "com.android.application", "agp")
    data object JetbrainsKotlinAndroid : Plugin("jetbrains-kotlin-android", "org.jetbrains.kotlin.android", "kotlin")
    data object ComposeCompiler : Plugin("compose-compiler", "org.jetbrains.kotlin.plugin.compose", "kotlin")
    data object Ksp : Plugin("ksp", "com.google.devtools.ksp", "ksp")
    data object Hilt : Plugin("hilt-plugin", "com.google.dagger.hilt.android", "hilt")
    data object KtLint : Plugin("ktlint", "org.jlleitschuh.gradle.ktlint", "ktlint")
    data object Detekt : Plugin("detekt-plugin", "io.gitlab.arturbosch.detekt", "detekt")
    data object GoogleServices : Plugin("google-services", "com.google.gms.google-services", "googleServices")
}