package com.github.cnrture.quickprojectwizard.gradle

import com.github.cnrture.quickprojectwizard.addGradlePlugin
import com.github.cnrture.quickprojectwizard.general.ImageLibrary

fun getProjectGradleKts(
    isCompose: Boolean,
    isHiltEnable: Boolean,
    isRoomEnable: Boolean,
    isKtLintEnable: Boolean,
    isDetektEnable: Boolean,
    isFirebaseEnable: Boolean,
    selectedImageLibrary: ImageLibrary,
) = StringBuilder().apply {
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
    append("}\n\n")
}
