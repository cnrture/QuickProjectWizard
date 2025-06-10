pluginManagement {
    plugins {
        id("org.jetbrains.compose").version(extra["compose.version"] as String)
        id("org.gradle.toolchains.foojay-resolver-convention") version "0.10.0"
    }
}

rootProject.name = "QuickProjectWizard"
