package com.github.cnrture.quickprojectwizard.gradle

import com.github.cnrture.quickprojectwizard.addLibsDependency
import com.github.cnrture.quickprojectwizard.addLibsPlugin
import com.github.cnrture.quickprojectwizard.addLibsVersion
import com.github.cnrture.quickprojectwizard.general.ImageLibrary
import com.github.cnrture.quickprojectwizard.general.NetworkLibrary

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
    if (isHiltEnable || isRoomEnable || selectedImageLibrary == ImageLibrary.Glide) addLibsVersion(Version.Ksp)
    if (isCompose) {
        addLibsVersion(Version.ActivityCompose)
        addLibsVersion(Version.ComposeBom)
    } else {
        addLibsVersion(Version.Activity)
        addLibsVersion(Version.ConstraintLayout)
        addLibsVersion(Version.FragmentKtx)
    }
    addImageLibraryVersions(isCompose, selectedImageLibrary)
    if (isRoomEnable) addLibsVersion(Version.Room)
    addNetworkLibraryVersions(selectedNetworkLibrary)
    if (isHiltEnable) {
        addLibsVersion(Version.Hilt)
        if (isCompose) addLibsVersion(Version.HiltNavigationCompose)
    }
    if (isNavigationEnable) addLibsVersion(Version.Navigation)
    if (isKtLintEnable) addLibsVersion(Version.KtLint)
    if (isDetektEnable) addLibsVersion(Version.Detekt)
    if (isFirebaseEnable) {
        addLibsVersion(Version.GoogleServices)
        addLibsVersion(Version.Firebase)
    }
    if (isWorkManagerEnable) addLibsVersion(Version.WorkManager)
    if (isNavigationEnable && isCompose) addLibsVersion(Version.KotlinxSerialization)

    append("\n[libraries]\n")
    addDefaultDependencies()
    if (isCompose) {
        addComposeDependencies()
    } else {
        addLibsDependency(Library.Activity)
        addLibsDependency(Library.ConstraintLayout)
        addLibsDependency(Library.FragmentKtx)
    }
    if (isRoomEnable) {
        addLibsDependency(Library.RoomCompiler)
        addLibsDependency(Library.RoomRuntime)
        addLibsDependency(Library.RoomKtx)
    }
    addNetworkLibraryDependencies(selectedNetworkLibrary)
    if (isHiltEnable) {
        addLibsDependency(Library.HiltCompiler)
        addLibsDependency(Library.HiltAndroid)
        if (isCompose) addLibsDependency(Library.HiltNavigationCompose)
    }
    addNavigationLibrary(isCompose, isNavigationEnable)
    addImageLibraryDependencies(isCompose, selectedImageLibrary)
    if (isDetektEnable) addLibsDependency(Library.Detekt)
    if (isFirebaseEnable) addLibsDependency(Library.Firebase)
    if (isWorkManagerEnable) addLibsDependency(Library.WorkManager)

    append("\n[plugins]\n")
    addLibsPlugin(Plugin.AndroidApplication)
    addLibsPlugin(Plugin.JetbrainsKotlinAndroid)
    if (isCompose) addLibsPlugin(Plugin.ComposeCompiler)
    if (isHiltEnable || isRoomEnable || selectedImageLibrary == ImageLibrary.Glide) addLibsPlugin(Plugin.Ksp)
    if (isHiltEnable) addLibsPlugin(Plugin.Hilt)
    if (isKtLintEnable) addLibsPlugin(Plugin.KtLint)
    if (isDetektEnable) addLibsPlugin(Plugin.Detekt)
    if (isFirebaseEnable) addLibsPlugin(Plugin.GoogleServices)
    if (!isCompose && isNavigationEnable) addLibsPlugin(Plugin.NavigationSafeArgs)
    if (isNavigationEnable && isCompose) addLibsPlugin(Plugin.KotlinxSerialization)
}

private fun StringBuilder.addDefaultVersions() {
    addLibsVersion(Version.Agp)
    addLibsVersion(Version.Kotlin)
    addLibsVersion(Version.CoreKtx)
    addLibsVersion(Version.Junit)
    addLibsVersion(Version.JunitExt)
    addLibsVersion(Version.EspressoCore)
    addLibsVersion(Version.AppCompat)
    addLibsVersion(Version.Material)
    addLibsVersion(Version.LifecycleRuntimeKtx)
}

private fun StringBuilder.addImageLibraryVersions(isCompose: Boolean, selectedImageLibrary: ImageLibrary) {
    when {
        isCompose && selectedImageLibrary == ImageLibrary.Coil -> addLibsVersion(Version.Coil)
        isCompose && selectedImageLibrary == ImageLibrary.Glide -> addLibsVersion(Version.GlideCompose)
        !isCompose && selectedImageLibrary == ImageLibrary.Coil -> addLibsVersion(Version.Coil)
        !isCompose && selectedImageLibrary == ImageLibrary.Glide -> addLibsVersion(Version.GlideXml)
    }
}

private fun StringBuilder.addNetworkLibraryVersions(selectedNetworkLibrary: NetworkLibrary) {
    when (selectedNetworkLibrary) {
        NetworkLibrary.Retrofit -> addLibsVersion(Version.Retrofit)
        NetworkLibrary.Ktor -> addLibsVersion(Version.Ktor)
        else -> Unit
    }
}

private fun StringBuilder.addDefaultDependencies() {
    addLibsDependency(Library.CoreKtx)
    addLibsDependency(Library.Junit)
    addLibsDependency(Library.JunitExt)
    addLibsDependency(Library.EspressoCore)
    addLibsDependency(Library.AppCompat)
    addLibsDependency(Library.Material)
    addLibsDependency(Library.LifecycleRuntimeKtx)
}

private fun StringBuilder.addComposeDependencies() {
    addLibsDependency(Library.LifecycleRuntimeCompose)
    addLibsDependency(Library.ActivityCompose)
    addLibsDependency(Library.ComposeBom)
    addLibsDependency(Library.ComposeUi)
    addLibsDependency(Library.ComposeUiGraphics)
    addLibsDependency(Library.ComposeUiToolingPreview)
    addLibsDependency(Library.Material3)
    addLibsDependency(Library.ComposeUiTooling)
    addLibsDependency(Library.ComposeUiTestManifest)
    addLibsDependency(Library.ComposeUiTestJunit4)
}

private fun StringBuilder.addNetworkLibraryDependencies(selectedNetworkLibrary: NetworkLibrary) {
    when (selectedNetworkLibrary) {
        NetworkLibrary.Retrofit -> {
            addLibsDependency(Library.Retrofit)
            addLibsDependency(Library.ConverterGson)
        }

        NetworkLibrary.Ktor -> {
            addLibsDependency(Library.KtorClientCore)
            addLibsDependency(Library.KtorClientOkHttp)
            addLibsDependency(Library.KtorContentNegotiation)
            addLibsDependency(Library.KtorSerialization)
        }

        else -> Unit
    }
}

private fun StringBuilder.addNavigationLibrary(isCompose: Boolean, isNavigationEnable: Boolean) {
    when {
        isCompose && isNavigationEnable -> {
            addLibsDependency(Library.NavigationCompose)
            addLibsDependency(Library.KotlinxSerialization)
        }

        !isCompose && isNavigationEnable -> {
            addLibsDependency(Library.NavigationFragment)
            addLibsDependency(Library.NavigationUi)
        }
    }
}

private fun StringBuilder.addImageLibraryDependencies(isCompose: Boolean, selectedImageLibrary: ImageLibrary) {
    when {
        isCompose && selectedImageLibrary == ImageLibrary.Coil -> addLibsDependency(Library.CoilCompose)
        isCompose && selectedImageLibrary == ImageLibrary.Glide -> addLibsDependency(Library.GlideCompose)
        !isCompose && selectedImageLibrary == ImageLibrary.Coil -> addLibsDependency(Library.Coil)
        !isCompose && selectedImageLibrary == ImageLibrary.Glide -> addLibsDependency(Library.Glide)
    }
}
