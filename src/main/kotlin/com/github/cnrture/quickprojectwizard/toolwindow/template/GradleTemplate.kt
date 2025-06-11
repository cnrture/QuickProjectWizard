package com.github.cnrture.quickprojectwizard.toolwindow.template

object GradleTemplate {
    fun getAndroidModuleGradleTemplate(packageName: String, dependencies: String, plugins: String = ""): String {
        return """
plugins {
$plugins
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

    fun getKotlinModuleGradleTemplate(plugins: String = "") = """
plugins {
    id 'java-library'
    id 'org.jetbrains.kotlin.jvm'${if (plugins.isNotEmpty()) "\n$plugins" else ""}
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
}""".trimIndent()
}
