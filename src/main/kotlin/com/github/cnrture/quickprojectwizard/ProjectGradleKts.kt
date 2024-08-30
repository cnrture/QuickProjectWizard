package com.github.cnrture.quickprojectwizard

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
    addGradlePlugin("android.application", true)
    addGradlePlugin("jetbrains.kotlin.android", true)
    if (isCompose) addGradlePlugin("compose.compiler", true)
    if (isHiltEnable || isRoomEnable || selectedImageLibrary == ImageLibrary.Glide) addGradlePlugin("ksp", true)
    if (isHiltEnable) addGradlePlugin("hilt.plugin", true)
    if (isKtLintEnable) addGradlePlugin("ktlint", true)
    if (isDetektEnable) addGradlePlugin("detekt", true)
    if (isFirebaseEnable) addGradlePlugin("google.services", true)
    append("}\n\n")
}
