package com.github.cnrture.quickprojectwizard.data

data class PluginInfo(
    val alias: String,
    val id: String,
    val versionRef: String? = null,
    val version: String? = null,
)