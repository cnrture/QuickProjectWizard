package com.github.cnrture.quickprojectwizard

fun getDependencies(
    isCompose: Boolean,
    isHiltEnable: Boolean,
    isKtLintEnable: Boolean,
    isDetektEnable: Boolean,
    isFirebaseEnable: Boolean,
    isRoomEnable: Boolean,
    isNavigationEnable: Boolean,
    isWorkManagerEnable: Boolean,
    selectedNetworkLibrary: NetworkLibrary,
    selectedImageLibrary: ImageLibrary,
) = StringBuilder().apply {
    append("[versions]\n")
    addDefaultVersions()
    if (isHiltEnable || isRoomEnable || selectedImageLibrary == ImageLibrary.Glide) addLibsVersion(
        "ksp",
        "2.0.20-1.0.24"
    )
    if (isCompose) {
        addLibsVersion("lifecycle-runtime-ktx", "2.8.4")
        addLibsVersion("activity-compose", "1.9.1")
        addLibsVersion("compose-bom", "2024.08.00")
    } else {
        addLibsVersion("activity", "1.9.1")
        addLibsVersion("constraintlayout", "2.1.4")
    }
    addImageLibraryVersions(isCompose, selectedImageLibrary)
    if (isRoomEnable) addLibsVersion("room", "2.6.1")
    addNetworkLibraryVersions(selectedNetworkLibrary)
    if (isHiltEnable) {
        addLibsVersion("hilt", "2.52")
        if (isCompose) addLibsVersion("hiltNavigationCompose", "1.2.0")
    }
    addNavigationVersions(isCompose, isNavigationEnable)
    if (isKtLintEnable) addLibsVersion("ktlint", "11.3.2")
    if (isDetektEnable) addLibsVersion("detekt", "1.23.5")
    if (isFirebaseEnable) {
        addLibsVersion("googleServices", "4.4.2")
        addLibsVersion("firebase", "33.2.0")
    }
    if (isWorkManagerEnable) addLibsVersion("workManagerVersion", "2.9.1")

    append("\n[libraries]\n")
    addDefaultDependencies()
    if (isCompose) {
        addComposeDependencies()
    } else {
        addLibsDependency("androidx-activity", "androidx.activity", "activity", "activity")
        addLibsDependency(
            "androidx-constraintlayout",
            "androidx.constraintlayout",
            "constraintlayout",
            "constraintlayout"
        )
    }
    if (isRoomEnable) {
        addLibsDependency("room-compiler", "androidx.room", "room-compiler", "room")
        addLibsDependency("room-runtime", "androidx.room", "room-runtime", "room")
        addLibsDependency("room-ktx", "androidx.room", "room-ktx", "room")
    }
    addNetworkLibraryDependencies(selectedNetworkLibrary)
    if (isHiltEnable) {
        addLibsDependency("hilt-compiler", "com.google.dagger", "hilt-compiler", "hilt")
        addLibsDependency("hilt-android", "com.google.dagger", "hilt-android", "hilt")
        if (isCompose) addLibsDependency(
            "hilt-navigation-compose",
            "androidx.hilt",
            "hilt-navigation-compose",
            "hiltNavigationCompose"
        )
    }
    addNavigationLibrary(isCompose, isNavigationEnable)
    addImageLibraryDependencies(isCompose, selectedImageLibrary)
    if (isDetektEnable) {
        addLibsDependency("detekt", "io.gitlab.arturbosch.detekt", "detekt-formatting", "detekt")
    }
    if (isFirebaseEnable) {
        addLibsDependency("firebase-bom", "com.google.firebase", "firebase-bom", "firebase")
    }
    if (isWorkManagerEnable) {
        addLibsDependency("workManager", "androidx.work", "work-runtime-ktx", "workManagerVersion")
    }

    append("\n[plugins]\n")
    addLibsPlugin("android-application", "com.android.application", "agp")
    addLibsPlugin("jetbrains-kotlin-android", "org.jetbrains.kotlin.android", "kotlin")
    if (isCompose) {
        addLibsPlugin("compose-compiler", "org.jetbrains.kotlin.plugin.compose", "kotlin")
    }
    if (isHiltEnable || isRoomEnable || selectedImageLibrary == ImageLibrary.Glide) {
        addLibsPlugin("ksp", "com.google.devtools.ksp", "ksp")
    }
    if (isHiltEnable) {
        addLibsPlugin("hilt-plugin", "com.google.dagger.hilt.android", "hilt")
    }
    if (isKtLintEnable) {
        addLibsPlugin("ktlint", "org.jlleitschuh.gradle.ktlint", "ktlint")
    }
    if (isDetektEnable) {
        addLibsPlugin("detekt", "io.gitlab.arturbosch.detekt", "detekt")
    }
    if (isFirebaseEnable) {
        addLibsPlugin("google-services", "com.google.gms.google-services", "googleServices")
    }
}

private fun StringBuilder.addDefaultVersions() {
    addLibsVersion("agp", "8.5.2")
    addLibsVersion("kotlin", "2.0.20")
    addLibsVersion("core-ktx", "1.13.1")
    addLibsVersion("junit", "4.13.2")
    addLibsVersion("junitVersion", "1.2.1")
    addLibsVersion("espresso-core", "3.6.1")
    addLibsVersion("appcompat", "1.7.0")
    addLibsVersion("material", "1.12.0")
}

private fun StringBuilder.addImageLibraryVersions(isCompose: Boolean, selectedImageLibrary: ImageLibrary) {
    when {
        isCompose && selectedImageLibrary == ImageLibrary.Coil -> addLibsVersion("coilVersion", "2.7.0")
        isCompose && selectedImageLibrary == ImageLibrary.Glide -> addLibsVersion("glideVersion", "1.0.0-beta01")
        !isCompose && selectedImageLibrary == ImageLibrary.Coil -> addLibsVersion("coilVersion", "2.7.0")
        !isCompose && selectedImageLibrary == ImageLibrary.Glide -> addLibsVersion("glideVersion", "4.16.0")
    }
}

private fun StringBuilder.addNetworkLibraryVersions(selectedNetworkLibrary: NetworkLibrary) {
    when (selectedNetworkLibrary) {
        NetworkLibrary.Retrofit -> addLibsVersion("retrofit", "2.11.0")
        NetworkLibrary.Ktor -> addLibsVersion("ktor", "2.3.12")
        else -> Unit
    }
}

private fun StringBuilder.addNavigationVersions(isCompose: Boolean, isNavigationEnable: Boolean) {
    when {
        isCompose && isNavigationEnable -> addLibsVersion("navigationCompose", "2.7.7")
        !isCompose && isNavigationEnable -> addLibsVersion("navigation", "2.7.7")
    }
}

private fun StringBuilder.addDefaultDependencies() {
    addLibsDependency("core-ktx", "androidx.core", "core-ktx", "core-ktx")
    addLibsDependency("junit", "junit", "junit", "junit")
    addLibsDependency("androidx-test-ext-junit", "androidx.test.ext", "junit", "junitVersion")
    addLibsDependency("espresso-core", "androidx.test.espresso", "espresso-core", "espresso-core")
    addLibsDependency("appcompat", "androidx.appcompat", "appcompat", "appcompat")
    addLibsDependency("material", "com.google.android.material", "material", "material")
}

private fun StringBuilder.addComposeDependencies() {
    addLibsDependency(
        "lifecycle-runtime-ktx",
        "androidx.lifecycle",
        "lifecycle-runtime-ktx",
        "lifecycle-runtime-ktx"
    )
    addLibsDependency(
        "lifecycle-runtime-compose",
        "androidx.lifecycle",
        "lifecycle-runtime-compose",
        "lifecycle-runtime-ktx"
    )
    addLibsDependency("activity-compose", "androidx.activity", "activity-compose", "activity-compose")
    addLibsDependency("compose-bom", "androidx.compose", "compose-bom", "compose-bom")
    addLibsDependency("ui", "androidx.compose.ui", "ui")
    addLibsDependency("ui-graphics", "androidx.compose.ui", "ui-graphics")
    addLibsDependency("ui-tooling-preview", "androidx.compose.ui", "ui-tooling-preview")
    addLibsDependency("material3", "androidx.compose.material3", "material3")
    addLibsDependency("ui-tooling", "androidx.compose.ui", "ui-tooling")
    addLibsDependency("ui-test-manifest", "androidx.compose.ui", "ui-test-manifest")
    addLibsDependency("ui-test-junit4", "androidx.compose.ui", "ui-test-junit4")
}

private fun StringBuilder.addNetworkLibraryDependencies(selectedNetworkLibrary: NetworkLibrary) {
    when (selectedNetworkLibrary) {
        NetworkLibrary.Retrofit -> {
            addLibsDependency("retrofit", "com.squareup.retrofit2", "retrofit", "retrofit")
            addLibsDependency("converter-gson", "com.squareup.retrofit2", "converter-gson", "retrofit")
        }

        NetworkLibrary.Ktor -> {
            addLibsDependency("ktor-client-core", "io.ktor", "ktor-client-core", "ktor")
            addLibsDependency("ktor-client-okhttp", "io.ktor", "ktor-client-okhttp", "ktor")
            addLibsDependency("ktor-client-content-negotiation", "io.ktor", "ktor-client-content-negotiation", "ktor")
            addLibsDependency("ktor-serialization-kotlinx-json", "io.ktor", "ktor-serialization-kotlinx-json", "ktor")
        }

        else -> Unit
    }
}

private fun StringBuilder.addNavigationLibrary(isCompose: Boolean, isNavigationEnable: Boolean) {
    when {
        isCompose && isNavigationEnable -> {
            addLibsDependency("navigation-compose", "androidx.navigation", "navigation-compose", "navigationCompose")
        }

        !isCompose && isNavigationEnable -> {
            addLibsDependency("navigation-fragment", "androidx.navigation", "navigation-fragment-ktx", "navigation")
            addLibsDependency("navigation-ui", "androidx.navigation", "navigation-ui-ktx", "navigation")
        }
    }
}

private fun StringBuilder.addImageLibraryDependencies(isCompose: Boolean, selectedImageLibrary: ImageLibrary) {
    when {
        isCompose && selectedImageLibrary == ImageLibrary.Coil -> {
            addLibsDependency("coil", "io.coil-kt", "coil-compose", "coilVersion")
        }

        isCompose && selectedImageLibrary == ImageLibrary.Glide -> {
            addLibsDependency("glide", "com.github.bumptech.glide", "compose", "glideVersion")
        }

        !isCompose && selectedImageLibrary == ImageLibrary.Coil -> {
            addLibsDependency("coil", "io.coil-kt", "coil", "coilVersion")
        }

        !isCompose && selectedImageLibrary == ImageLibrary.Glide -> {
            addLibsDependency("glide", "com.github.bumptech.glide", "glide", "glideVersion")
        }
    }
}
