package com.github.cnrture.quickprojectwizard.gradle

import com.github.cnrture.quickprojectwizard.*

fun getGradleKts(
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
    packagePath: String,
    minApi: Int,
    javaJvmVersion: String,
) = StringBuilder().apply {
    append("plugins {\n")
    addGradlePlugin(Plugin.AndroidApplication)
    addGradlePlugin(Plugin.JetbrainsKotlinAndroid)
    if (isCompose) addGradlePlugin(Plugin.ComposeCompiler)
    if (isHiltEnable || isRoomEnable || selectedImageLibrary == ImageLibrary.Glide) addGradlePlugin(Plugin.Ksp)
    if (isHiltEnable) addGradlePlugin(Plugin.Hilt)
    if (isKtLintEnable) addGradlePlugin(Plugin.KtLint)
    if (isDetektEnable) addGradlePlugin(Plugin.Detekt)
    if (isFirebaseEnable) addGradlePlugin(Plugin.GoogleServices)
    append("}\n\n")

    addAndroidBlock(packagePath, minApi, javaJvmVersion, isCompose)

    append("dependencies {\n\n")
    addDefaultDependencies()
    if (isCompose) {
        addComposeDependencies()
    } else {
        addGradleImplementation(Library.Activity)
        addGradleImplementation(Library.ConstraintLayout)
    }
    addRoomLibrary(isRoomEnable)
    addNetworkLibrary(selectedNetworkLibrary)
    if (isHiltEnable) {
        addKspImplementation(Library.HiltCompiler)
        addGradleImplementation(Library.HiltAndroid)
        if (isCompose) addGradleImplementation(Library.HiltNavigationCompose)
    }
    addNavigationLibrary(isCompose, isNavigationEnable)
    addImageLibrary(isCompose, selectedImageLibrary)

    if (isDetektEnable) addGradleDetektImplementation(Library.Detekt)
    if (isFirebaseEnable) addGradlePlatformImplementation(Library.Firebase)
    if (isWorkManagerEnable) addGradleImplementation(Library.WorkManager)
    append("}\n")

    if (isDetektEnable) addDetektBlock()
}

private fun StringBuilder.addDefaultDependencies() {
    addGradleImplementation(Library.CoreKtx)
    addGradleImplementation(Library.AppCompat)
    addGradleImplementation(Library.Material)
    addGradleTestImplementation(Library.Junit)
    addGradleAndroidTestImplementation(Library.JunitVersion)
    addGradleAndroidTestImplementation(Library.EspressoCore)
}

private fun StringBuilder.addComposeDependencies() {
    addGradleImplementation(Library.LifecycleRuntimeKtx)
    addGradleImplementation(Library.LifecycleRuntimeCompose)
    addGradleImplementation(Library.ActivityCompose)
    addGradlePlatformImplementation(Library.ComposeBom)
    addGradleImplementation(Library.ComposeUi)
    addGradleImplementation(Library.ComposeUiGraphics)
    addGradleImplementation(Library.ComposeUiToolingPreview)
    addGradleImplementation(Library.Material3)
    addGradleAndroidTestPlatformImplementation(Library.ComposeBom)
    addGradleAndroidTestImplementation(Library.ComposeUiTestJunit4)
    addGradleDebugImplementation(Library.ComposeUiTooling)
    addGradleDebugImplementation(Library.ComposeUiTestManifest)
}

private fun StringBuilder.addNetworkLibrary(selectedNetworkLibrary: NetworkLibrary) {
    when (selectedNetworkLibrary) {
        NetworkLibrary.Retrofit -> {
            addGradleImplementation(Library.Retrofit)
            addGradleImplementation(Library.ConverterGson)
        }

        NetworkLibrary.Ktor -> {
            addGradleImplementation(Library.KtorClientCore)
            addGradleImplementation(Library.KtorClientOkHttp)
            addGradleImplementation(Library.KtorContentNegotiation)
            addGradleImplementation(Library.KtorSerialization)
        }

        else -> Unit
    }
}

private fun StringBuilder.addNavigationLibrary(isCompose: Boolean, isNavigationEnable: Boolean) {
    when {
        isCompose && isNavigationEnable -> addGradleImplementation(Library.NavigationCompose)
        !isCompose && isNavigationEnable -> {
            addGradleImplementation(Library.NavigationFragment)
            addGradleImplementation(Library.NavigationUi)
        }

        else -> Unit
    }
}

private fun StringBuilder.addImageLibrary(isCompose: Boolean, selectedImageLibrary: ImageLibrary) {
    when {
        isCompose && selectedImageLibrary == ImageLibrary.Coil -> addGradleImplementation(Library.CoilCompose)
        isCompose && selectedImageLibrary == ImageLibrary.Glide -> addGradleImplementation(Library.GlideCompose)
        !isCompose && selectedImageLibrary == ImageLibrary.Coil -> addGradleImplementation(Library.Coil)
        !isCompose && selectedImageLibrary == ImageLibrary.Glide -> addGradleImplementation(Library.Glide)
        else -> Unit
    }
}

private fun StringBuilder.addRoomLibrary(isRoomEnable: Boolean) {
    if (isRoomEnable) {
        addKspImplementation(Library.RoomCompiler)
        addGradleImplementation(Library.RoomRuntime)
        addGradleImplementation(Library.RoomKtx)
    }
}
