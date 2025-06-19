package com.github.cnrture.quickprojectwizard.toolwindow.template

object GradleTemplate {
    fun getAndroidModuleGradleTemplate(packageName: String, dependencies: String, plugins: String = ""): String {
        return """
plugins {
$plugins
}

android {
    namespace = "$packageName"
    compileSdk = 35

    defaultConfig {
        minSdk = 21
        targetSdk = 35
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
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
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
}""".trimIndent()
}
