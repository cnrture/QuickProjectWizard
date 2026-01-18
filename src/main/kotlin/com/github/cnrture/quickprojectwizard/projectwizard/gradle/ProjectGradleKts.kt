package com.github.cnrture.quickprojectwizard.projectwizard.gradle

import com.github.cnrture.quickprojectwizard.common.addGradlePlugin
import com.github.cnrture.quickprojectwizard.data.DILibrary
import com.github.cnrture.quickprojectwizard.data.ImageLibrary

fun getProjectGradleKts(
    isCompose: Boolean,
    selectedDILibrary: DILibrary,
    isRoomEnable: Boolean,
    isKtLintEnable: Boolean,
    isDetektEnable: Boolean,
    isFirebaseEnable: Boolean,
    isNavigationEnable: Boolean,
    selectedImageLibrary: ImageLibrary,
) = StringBuilder().apply {
    val isHiltEnable = selectedDILibrary == DILibrary.Hilt
    append("// Top-level build file where you can add configuration options common to all sub-projects/modules.\n")
    append("plugins {\n")
    addGradlePlugin(Plugin.AndroidApplication, true)
    addGradlePlugin(Plugin.JetbrainsKotlinAndroid, true)
    if (isCompose) addGradlePlugin(Plugin.ComposeCompiler, true)
    if (isHiltEnable || isRoomEnable || selectedImageLibrary == ImageLibrary.Glide) addGradlePlugin(Plugin.Ksp, true)
    if (isHiltEnable) addGradlePlugin(Plugin.Hilt, true)
    if (isKtLintEnable) addGradlePlugin(Plugin.KtLint, true)
    if (isDetektEnable) addGradlePlugin(Plugin.Detekt, true)
    if (isFirebaseEnable) addGradlePlugin(Plugin.GoogleServices, true)
    if (!isCompose && isNavigationEnable) addGradlePlugin(Plugin.NavigationSafeArgs, true)
    if (isNavigationEnable && isCompose) addGradlePlugin(Plugin.KotlinxSerialization, true)
    append("}")
}
