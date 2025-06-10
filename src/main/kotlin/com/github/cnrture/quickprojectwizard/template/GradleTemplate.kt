package com.github.cnrture.quickprojectwizard.template

object GradleTemplate {
    fun getAndroidModuleGradleTemplate(packageName: String, dependencies: String): String {
        return """
plugins {
    id 'com.android.library'
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace '$packageName'
    compileSdkVersion 35

    defaultConfig {
        minSdkVersion 21
        targetSdkVersion 35
    }

    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    $dependencies
}""".trimIndent()
    }

    fun getKotlinModuleGradleTemplate() = """
plugins {
    id 'java-library'
    id 'org.jetbrains.kotlin.jvm'
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
}""".trimIndent()
}