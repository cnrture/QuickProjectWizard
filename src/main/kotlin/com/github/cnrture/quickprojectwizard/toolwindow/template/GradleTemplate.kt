package com.github.cnrture.quickprojectwizard.toolwindow.template

object GradleTemplate {
    fun getAndroidModuleGradleTemplate(
        packageName: String,
        dependencies: String,
        plugins: String = "",
        isKts: Boolean = true
    ): String {
        val assignment = if (isKts) " = " else " "
        return """
plugins {
$plugins
}

android {
    namespace${assignment}"$packageName"
    compileSdk${assignment}36
    
    buildFeatures {
        buildConfig${assignment}true
        viewBinding${assignment}true
    }
    
    defaultConfig {
        minSdk${assignment}21
        targetSdk${assignment}36
    }

    compileOptions {
        sourceCompatibility${assignment}JavaVersion.VERSION_17
        targetCompatibility${assignment}JavaVersion.VERSION_17
    }
}

dependencies {
    $dependencies
}""".trimIndent()
    }

    fun getKotlinModuleGradleTemplate(plugins: String = "", isKts: Boolean = true): String {
        return if (isKts) {
            """
plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")${if (plugins.isNotEmpty()) "\n$plugins" else ""}
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
}""".trimIndent()
        } else {
            """
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
    }
}
