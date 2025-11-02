package com.github.cnrture.quickprojectwizard.projectwizard.gradle

import com.github.cnrture.quickprojectwizard.common.*
import com.github.cnrture.quickprojectwizard.data.ImageLibrary
import com.github.cnrture.quickprojectwizard.data.NetworkLibrary

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
    append("import org.jetbrains.kotlin.gradle.dsl.JvmTarget\n\n")
    append("plugins {\n")
    addGradlePlugin(Plugin.AndroidApplication)
    addGradlePlugin(Plugin.JetbrainsKotlinAndroid)
    if (isCompose) addGradlePlugin(Plugin.ComposeCompiler)
    if (isHiltEnable || isRoomEnable || selectedImageLibrary == ImageLibrary.Glide) addGradlePlugin(Plugin.Ksp)
    if (isHiltEnable) addGradlePlugin(Plugin.Hilt)
    if (isKtLintEnable) addGradlePlugin(Plugin.KtLint)
    if (isDetektEnable) addGradlePlugin(Plugin.Detekt)
    if (isFirebaseEnable) addGradlePlugin(Plugin.GoogleServices)
    if (!isCompose && isNavigationEnable) addGradlePlugin(Plugin.NavigationSafeArgs)
    if (isNavigationEnable && isCompose) addGradlePlugin(Plugin.KotlinxSerialization)
    append("}\n\n")

    addAndroidBlock(packagePath, minApi, javaJvmVersion, isCompose)

    append("dependencies {\n\n")
    addDefaultDependencies()

    if (isCompose) {
        addComposeDependencies()
    } else {
        addGradleImplementation(Library.Activity)
        addGradleImplementation(Library.ConstraintLayout)
        addGradleImplementation(Library.FragmentKtx)
    }

    if (isRoomEnable) {
        append("\n")
        append("    // Room\n")
        addKspImplementation(Library.RoomCompiler)
        addGradleImplementation(Library.RoomRuntime)
        addGradleImplementation(Library.RoomKtx)
    }

    when (selectedNetworkLibrary) {
        NetworkLibrary.Retrofit -> {
            append("\n")
            append("    // Retrofit\n")
            addGradleImplementation(Library.Retrofit)
            addGradleImplementation(Library.ConverterGson)
        }

        NetworkLibrary.Ktor -> {
            append("\n")
            append("    // Ktor\n")
            addGradleImplementation(Library.KtorClientCore)
            addGradleImplementation(Library.KtorClientOkHttp)
            addGradleImplementation(Library.KtorContentNegotiation)
            addGradleImplementation(Library.KtorSerialization)
        }

        else -> Unit
    }

    if (isHiltEnable) {
        append("\n")
        append("    // Hilt\n")
        addKspImplementation(Library.HiltCompiler)
        addGradleImplementation(Library.HiltAndroid)
        if (isCompose) addGradleImplementation(Library.HiltNavigationCompose)
    }


    if (isNavigationEnable) {
        append("\n")
        append("    // Navigation\n")
        if (isCompose) addGradleImplementation(Library.NavigationCompose)
        else {
            addGradleImplementation(Library.NavigationFragment)
            addGradleImplementation(Library.NavigationUi)
        }
        if (isCompose) {
            append("\n")
            append("    // Kotlinx Serialization\n")
            addGradleImplementation(Library.KotlinxSerialization)
        }
    }

    when {
        isCompose && selectedImageLibrary == ImageLibrary.Coil -> {
            append("\n")
            append("    // Coil\n")
            addGradleImplementation(Library.CoilCompose)
        }

        isCompose && selectedImageLibrary == ImageLibrary.Glide -> {
            append("\n")
            append("    // Glide\n")
            addGradleImplementation(Library.GlideCompose)
        }

        !isCompose && selectedImageLibrary == ImageLibrary.Coil -> {
            append("\n")
            append("    // Coil\n")
            addGradleImplementation(Library.Coil)
        }

        !isCompose && selectedImageLibrary == ImageLibrary.Glide -> {
            append("\n")
            append("    // Glide\n")
            addGradleImplementation(Library.Glide)
        }

        else -> Unit
    }

    if (isDetektEnable) {
        append("\n")
        append("    // Detekt\n")
        addGradleDetektImplementation(Library.Detekt)
    }
    if (isFirebaseEnable) {
        append("\n")
        append("    // Firebase\n")
        addGradlePlatformImplementation(Library.Firebase)
    }
    if (isWorkManagerEnable) {
        append("\n")
        append("    // WorkManager\n")
        addGradleImplementation(Library.WorkManager)
    }
    append("}\n")

    if (isDetektEnable) addDetektBlock()
}

private fun StringBuilder.addDefaultDependencies() {
    addGradleImplementation(Library.CoreKtx)
    addGradleImplementation(Library.AppCompat)
    addGradleImplementation(Library.Material)
    addGradleTestImplementation(Library.Junit)
    addGradleAndroidTestImplementation(Library.JunitExt)
    addGradleAndroidTestImplementation(Library.EspressoCore)
    addGradleImplementation(Library.LifecycleRuntimeKtx)
}

private fun StringBuilder.addComposeDependencies() {
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
