package com.github.cnrture.quickprojectwizard.gradle

object Versions {
    val versionList = mutableMapOf(
        "agp" to "8.5.2",
        "kotlin" to "2.0.20",
        "core-ktx" to "1.13.1",
        "junit" to "4.13.2",
        "junit-ext" to "1.2.1",
        "espresso-core" to "3.6.1",
        "appcompat" to "1.7.0",
        "material" to "1.12.0",
        "ksp" to "2.0.20-1.0.24",
        "lifecycle-runtime-ktx" to "2.8.4",
        "activity-compose" to "1.9.1",
        "compose-bom" to "2024.08.00",
        "activity" to "1.9.1",
        "constraintlayout" to "2.1.4",
        "coilVersion" to "2.7.0",
        "glideVersion" to "4.16.0",
        "glideCompose" to "1.0.0-beta01",
        "room" to "2.6.1",
        "retrofit" to "2.11.0",
        "ktor" to "2.3.12",
        "hilt" to "2.52",
        "hiltNavigationCompose" to "1.2.0",
        "navigation" to "2.7.7",
        "ktlint" to "11.3.2",
        "detekt" to "1.23.5",
        "googleServices" to "4.4.2",
        "firebase" to "33.2.0",
        "workManagerVersion" to "2.9.1",
        "fragment-ktx" to "1.8.3",
        "cmp-agp" to "8.5.2",
        "cmp-kotlin" to "2.0.20",
        "cmp-activity-compose" to "1.9.2",
        "cmp-ui-tooling" to "1.7.2",
        "cmp-multiplatform" to "1.6.11",
        "cmp-koin" to "4.0.0",
        "cmp-ktor" to "2.3.12",
        "cmp-navigation" to "2.7.0-alpha07",
    )
}

sealed class Version(val name: String, val value: String) {
    data object Agp : Version("agp", Versions.versionList["agp"] ?: "8.5.2")
    data object Kotlin : Version("kotlin", Versions.versionList["kotlin"] ?: "2.0.20")
    data object CoreKtx : Version("core-ktx", Versions.versionList["core-ktx"] ?: "1.13.1")
    data object Junit : Version("junit", Versions.versionList["junit"] ?: "4.13.2")
    data object JunitExt : Version("junit-ext", Versions.versionList["junit-ext"] ?: "1.2.1")
    data object EspressoCore : Version("espresso-core", Versions.versionList["espresso-core"] ?: "3.6.1")
    data object AppCompat : Version("appcompat", Versions.versionList["appcompat"] ?: "1.7.0")
    data object Material : Version("material", Versions.versionList["material"] ?: "1.12.0")
    data object Ksp : Version("ksp", Versions.versionList["ksp"] ?: "2.0.20-1.0.24")
    data object LifecycleRuntimeKtx :
        Version("lifecycle-runtime-ktx", Versions.versionList["lifecycle-runtime-ktx"] ?: "2.8.4")

    data object FragmentKtx : Version("fragment-ktx", Versions.versionList["fragment-ktx"] ?: "1.4.0")
    data object ActivityCompose : Version("activity-compose", Versions.versionList["activity-compose"] ?: "1.9.1")
    data object ComposeBom : Version("compose-bom", Versions.versionList["compose-bom"] ?: "2024.08.00")
    data object Activity : Version("activity", Versions.versionList["activity"] ?: "1.9.1")
    data object ConstraintLayout : Version("constraintlayout", Versions.versionList["constraintlayout"] ?: "2.1.4")
    data object Coil : Version("coilVersion", Versions.versionList["coilVersion"] ?: "2.7.0")
    data object GlideXml : Version("glideVersion", Versions.versionList["glideVersion"] ?: "4.16.0")
    data object GlideCompose : Version("glideCompose", Versions.versionList["glideCompose"] ?: "1.0.0-beta01")
    data object Room : Version("room", Versions.versionList["room"] ?: "2.6.1")
    data object Retrofit : Version("retrofit", Versions.versionList["retrofit"] ?: "2.11.0")
    data object Ktor : Version("ktor", Versions.versionList["ktor"] ?: "2.3.12")
    data object Hilt : Version("hilt", Versions.versionList["hilt"] ?: "2.52")
    data object HiltNavigationCompose :
        Version("hiltNavigationCompose", Versions.versionList["hiltNavigationCompose"] ?: "1.2.0")

    data object Navigation : Version("navigation", Versions.versionList["navigation"] ?: "2.7.7")
    data object KtLint : Version("ktlint", Versions.versionList["ktlint"] ?: "11.3.2")
    data object Detekt : Version("detekt", Versions.versionList["detekt"] ?: "1.23.5")
    data object GoogleServices : Version("googleServices", Versions.versionList["googleServices"] ?: "4.4.2")
    data object Firebase : Version("firebase", Versions.versionList["firebase"] ?: "33.2.0")
    data object WorkManager : Version("workManagerVersion", Versions.versionList["workManagerVersion"] ?: "2.9.1")
}