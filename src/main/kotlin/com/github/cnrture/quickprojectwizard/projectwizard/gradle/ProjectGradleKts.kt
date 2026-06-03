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
    isKts: Boolean = true
) = StringBuilder().apply {
    val isHiltEnable = selectedDILibrary == DILibrary.Hilt
    append("// Top-level build file where you can add configuration options common to all sub-projects/modules.\n")
    append("plugins {\n")
    addGradlePlugin(Plugin.AndroidApplication, isProject = true, isKts = isKts)
    addGradlePlugin(Plugin.JetbrainsKotlinAndroid, isProject = true, isKts = isKts)
    if (isCompose) addGradlePlugin(Plugin.ComposeCompiler, isProject = true, isKts = isKts)
    if (isHiltEnable || isRoomEnable || selectedImageLibrary == ImageLibrary.Glide) addGradlePlugin(Plugin.Ksp, isProject = true, isKts = isKts)
    if (isHiltEnable) addGradlePlugin(Plugin.Hilt, isProject = true, isKts = isKts)
    if (isKtLintEnable) addGradlePlugin(Plugin.KtLint, isProject = true, isKts = isKts)
    if (isDetektEnable) addGradlePlugin(Plugin.Detekt, isProject = true, isKts = isKts)
    if (isFirebaseEnable) addGradlePlugin(Plugin.GoogleServices, isProject = true, isKts = isKts)
    if (!isCompose && isNavigationEnable) addGradlePlugin(Plugin.NavigationSafeArgs, isProject = true, isKts = isKts)
    if (isNavigationEnable && isCompose) addGradlePlugin(Plugin.KotlinxSerialization, isProject = true, isKts = isKts)
    append("}")
}
