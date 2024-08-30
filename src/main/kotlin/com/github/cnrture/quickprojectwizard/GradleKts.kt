package com.github.cnrture.quickprojectwizard

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
    addGradlePlugin("android.application")
    addGradlePlugin("jetbrains.kotlin.android")
    if (isCompose) addGradlePlugin("compose.compiler")
    if (isHiltEnable || isRoomEnable || selectedImageLibrary == ImageLibrary.Glide) addGradlePlugin("ksp")
    if (isHiltEnable) addGradlePlugin("hilt.plugin")
    if (isKtLintEnable) addGradlePlugin("ktlint")
    if (isDetektEnable) addGradlePlugin("detekt")
    if (isFirebaseEnable) addGradlePlugin("google.services")
    append("}\n\n")

    addAndroidBlock(packagePath, minApi, javaJvmVersion, isCompose)

    append("dependencies {\n\n")
    addDefaultDependencies()
    if (isCompose) {
        addComposeDependencies()
    } else {
        addGradleImplementation("androidx.activity")
        addGradleImplementation("androidx.constraintlayout")
    }
    addRoomLibrary(isRoomEnable)
    addNetworkLibrary(selectedNetworkLibrary)
    if (isHiltEnable) {
        addKspImplementation("hilt.compiler")
        addGradleImplementation("hilt.android")
        if (isCompose) addGradleImplementation("hilt.navigation.compose")
    }
    addNavigationLibrary(isCompose, isNavigationEnable)
    addImageLibrary(isCompose, selectedImageLibrary)

    if (isDetektEnable) addGradleDetektImplementation("detekt")
    if (isFirebaseEnable) addGradlePlatformImplementation("firebase.bom")
    if (isWorkManagerEnable) addGradleImplementation("workManager")
    append("}\n")

    if (isDetektEnable) addDetektBlock()
}

private fun StringBuilder.addDefaultDependencies() {
    addGradleImplementation("core.ktx")
    addGradleImplementation("appcompat")
    addGradleImplementation("material")
    addGradleTestImplementation("junit")
    addGradleAndroidTestImplementation("androidx.test.ext.junit")
    addGradleAndroidTestImplementation("espresso.core")
}

private fun StringBuilder.addComposeDependencies() {
    addGradleImplementation("lifecycle.runtime.ktx")
    addGradleImplementation("lifecycle.runtime.compose")
    addGradleImplementation("activity.compose")
    addGradlePlatformImplementation("compose.bom")
    addGradleImplementation("ui")
    addGradleImplementation("ui.graphics")
    addGradleImplementation("ui.tooling.preview")
    addGradleImplementation("material3")
    addGradleAndroidTestPlatformImplementation("compose.bom")
    addGradleAndroidTestImplementation("ui.test.junit4")
    addGradleDebugImplementation("ui.tooling")
    addGradleDebugImplementation("ui.test.manifest")
}

private fun StringBuilder.addNetworkLibrary(selectedNetworkLibrary: NetworkLibrary) {
    when (selectedNetworkLibrary) {
        NetworkLibrary.Retrofit -> {
            addGradleImplementation("retrofit")
            addGradleImplementation("converter.gson")
        }

        NetworkLibrary.Ktor -> {
            addGradleImplementation("ktor.client.core")
            addGradleImplementation("ktor.client.okhttp")
            addGradleImplementation("ktor.client.content.negotiation")
            addGradleImplementation("ktor.serialization.kotlinx.json")
        }

        else -> Unit
    }
}

private fun StringBuilder.addNavigationLibrary(isCompose: Boolean, isNavigationEnable: Boolean) {
    when {
        isCompose && isNavigationEnable -> addGradleImplementation("navigation.compose")
        !isCompose && isNavigationEnable -> {
            addGradleImplementation("navigation.fragment")
            addGradleImplementation("navigation.ui")
        }
        else -> Unit
    }
}

private fun StringBuilder.addImageLibrary(isCompose: Boolean, selectedImageLibrary: ImageLibrary) {
    when {
        isCompose && selectedImageLibrary == ImageLibrary.Coil -> addGradleImplementation("coil")
        isCompose && selectedImageLibrary == ImageLibrary.Glide -> addGradleImplementation("glide")
        !isCompose && selectedImageLibrary == ImageLibrary.Coil -> addGradleImplementation("coil")
        !isCompose && selectedImageLibrary == ImageLibrary.Glide -> addGradleImplementation("glide")
        else -> Unit
    }
}

private fun StringBuilder.addRoomLibrary(isRoomEnable: Boolean) {
    if (isRoomEnable) {
        addKspImplementation("room.compiler")
        addGradleImplementation("room.runtime")
        addGradleImplementation("room.ktx")
    }
}
