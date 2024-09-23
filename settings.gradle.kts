pluginManagement {
    plugins {
        id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
        id("org.jetbrains.compose").version(extra["compose.version"] as String)
    }
}

rootProject.name = "QuickProjectWizard"
