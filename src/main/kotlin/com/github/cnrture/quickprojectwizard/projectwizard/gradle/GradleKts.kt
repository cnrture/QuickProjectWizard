package com.github.cnrture.quickprojectwizard.projectwizard.gradle

import com.github.cnrture.quickprojectwizard.common.*
import com.github.cnrture.quickprojectwizard.data.DILibrary
import com.github.cnrture.quickprojectwizard.data.ImageLibrary
import com.github.cnrture.quickprojectwizard.data.NetworkLibrary

fun getGradleKts(
    isCompose: Boolean,
    selectedDILibrary: DILibrary,
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
    isKts: Boolean = true
) = StringBuilder().apply {
    val isHiltEnable = selectedDILibrary == DILibrary.Hilt
    val isKoinEnable = selectedDILibrary == DILibrary.Koin
    val isKtorfitEnable = selectedNetworkLibrary == NetworkLibrary.Ktorfit
    val isKtorEnable = selectedNetworkLibrary == NetworkLibrary.Ktor
    if (isKts) append("import org.jetbrains.kotlin.gradle.dsl.JvmTarget\n\n")
    append("plugins {\n")
    addGradlePlugin(Plugin.AndroidApplication, isKts = isKts)
    addGradlePlugin(Plugin.JetbrainsKotlinAndroid, isKts = isKts)
    if (isCompose) addGradlePlugin(Plugin.ComposeCompiler, isKts = isKts)
    if (isHiltEnable || isRoomEnable || selectedImageLibrary == ImageLibrary.Glide || isKtorfitEnable) addGradlePlugin(
        Plugin.Ksp, isKts = isKts
    )
    if (isHiltEnable) addGradlePlugin(Plugin.Hilt, isKts = isKts)
    if (isKtLintEnable) addGradlePlugin(Plugin.KtLint, isKts = isKts)
    if (isDetektEnable) addGradlePlugin(Plugin.Detekt, isKts = isKts)
    if (isFirebaseEnable) addGradlePlugin(Plugin.GoogleServices, isKts = isKts)
    if (!isCompose && isNavigationEnable) addGradlePlugin(Plugin.NavigationSafeArgs, isKts = isKts)
    if ((isNavigationEnable && isCompose) || isKtorEnable || isKtorfitEnable) addGradlePlugin(Plugin.KotlinxSerialization, isKts = isKts)
    append("}\n\n")

    addAndroidBlock(packagePath, minApi, javaJvmVersion, isCompose, isKts = isKts)

    append("dependencies {\n\n")
    addDefaultDependencies(isKts)

    if (isCompose) {
        addComposeDependencies(isKts)
    } else {
        addGradleImplementation(Library.Activity, isKts = isKts)
        addGradleImplementation(Library.ConstraintLayout, isKts = isKts)
        addGradleImplementation(Library.FragmentKtx, isKts = isKts)
    }

    if (isRoomEnable) {
        append("\n")
        append("    // Room\n")
        addKspImplementation(Library.RoomCompiler, isKts = isKts)
        addGradleImplementation(Library.RoomRuntime, isKts = isKts)
        addGradleImplementation(Library.RoomKtx, isKts = isKts)
    }

    when (selectedNetworkLibrary) {
        NetworkLibrary.Retrofit -> {
            append("\n")
            append("    // Retrofit\n")
            addGradleImplementation(Library.Retrofit, isKts = isKts)
            addGradleImplementation(Library.ConverterGson, isKts = isKts)
        }

        NetworkLibrary.Ktor -> {
            append("\n")
            append("    // Ktor\n")
            addGradleImplementation(Library.KtorClientCore, isKts = isKts)
            addGradleImplementation(Library.KtorClientOkHttp, isKts = isKts)
            addGradleImplementation(Library.KtorContentNegotiation, isKts = isKts)
            addGradleImplementation(Library.KtorSerialization, isKts = isKts)
        }

        NetworkLibrary.Ktorfit -> {
            append("\n")
            append("    // Ktorfit\n")
            addGradleImplementation(Library.Ktorfit, isKts = isKts)
            addKspImplementation(Library.KtorfitKsp, isKts = isKts)
            addGradleImplementation(Library.KtorClientCore, isKts = isKts)
            addGradleImplementation(Library.KtorClientOkHttp, isKts = isKts)
            addGradleImplementation(Library.KtorContentNegotiation, isKts = isKts)
            addGradleImplementation(Library.KtorSerialization, isKts = isKts)
        }

        else -> Unit
    }

    if (isHiltEnable) {
        append("\n")
        append("    // Hilt\n")
        addKspImplementation(Library.HiltCompiler, isKts = isKts)
        addGradleImplementation(Library.HiltAndroid, isKts = isKts)
        if (isCompose) addGradleImplementation(Library.HiltNavigationCompose, isKts = isKts)
    }

    if (isKoinEnable) {
        append("\n")
        append("    // Koin\n")
        addGradleImplementation(Library.KoinAndroid, isKts = isKts)
        if (isCompose) addGradleImplementation(Library.KoinCompose, isKts = isKts)
    }

    if (isNavigationEnable) {
        append("\n")
        append("    // Navigation\n")
        if (isCompose) addGradleImplementation(Library.NavigationCompose, isKts = isKts)
        else {
            addGradleImplementation(Library.NavigationFragment, isKts = isKts)
            addGradleImplementation(Library.NavigationUi, isKts = isKts)
        }
        if (isCompose) {
            append("\n")
            append("    // Kotlinx Serialization\n")
            addGradleImplementation(Library.KotlinxSerialization, isKts = isKts)
        }
    }

    when {
        isCompose && selectedImageLibrary == ImageLibrary.Coil -> {
            append("\n")
            append("    // Coil\n")
            addGradleImplementation(Library.CoilCompose, isKts = isKts)
        }

        isCompose && selectedImageLibrary == ImageLibrary.Glide -> {
            append("\n")
            append("    // Glide\n")
            addGradleImplementation(Library.GlideCompose, isKts = isKts)
        }

        !isCompose && selectedImageLibrary == ImageLibrary.Coil -> {
            append("\n")
            append("    // Coil\n")
            addGradleImplementation(Library.Coil, isKts = isKts)
        }

        !isCompose && selectedImageLibrary == ImageLibrary.Glide -> {
            append("\n")
            append("    // Glide\n")
            addGradleImplementation(Library.Glide, isKts = isKts)
        }

        else -> Unit
    }

    if (isDetektEnable) {
        append("\n")
        append("    // Detekt\n")
        addGradleDetektImplementation(Library.Detekt, isKts = isKts)
    }
    if (isFirebaseEnable) {
        append("\n")
        append("    // Firebase\n")
        addGradlePlatformImplementation(Library.Firebase, isKts = isKts)
    }
    if (isWorkManagerEnable) {
        append("\n")
        append("    // WorkManager\n")
        addGradleImplementation(Library.WorkManager, isKts = isKts)
    }
    append("}\n")

    if (isDetektEnable) addDetektBlock()
}

private fun StringBuilder.addDefaultDependencies(isKts: Boolean) {
    addGradleImplementation(Library.CoreKtx, isKts = isKts)
    addGradleImplementation(Library.AppCompat, isKts = isKts)
    addGradleImplementation(Library.Material, isKts = isKts)
    addGradleTestImplementation(Library.Junit, isKts = isKts)
    addGradleAndroidTestImplementation(Library.JunitExt, isKts = isKts)
    addGradleAndroidTestImplementation(Library.EspressoCore, isKts = isKts)
    addGradleImplementation(Library.LifecycleRuntimeKtx, isKts = isKts)
}

private fun StringBuilder.addComposeDependencies(isKts: Boolean) {
    addGradleImplementation(Library.LifecycleRuntimeCompose, isKts = isKts)
    addGradleImplementation(Library.ActivityCompose, isKts = isKts)
    addGradlePlatformImplementation(Library.ComposeBom, isKts = isKts)
    addGradleImplementation(Library.ComposeUi, isKts = isKts)
    addGradleImplementation(Library.ComposeUiGraphics, isKts = isKts)
    addGradleImplementation(Library.ComposeUiToolingPreview, isKts = isKts)
    addGradleImplementation(Library.Material3, isKts = isKts)
    addGradleAndroidTestPlatformImplementation(Library.ComposeBom, isKts = isKts)
    addGradleAndroidTestImplementation(Library.ComposeUiTestJunit4, isKts = isKts)
    addGradleDebugImplementation(Library.ComposeUiTooling, isKts = isKts)
    addGradleDebugImplementation(Library.ComposeUiTestManifest, isKts = isKts)
}
