package com.github.cnrture.quickprojectwizard.data

data class LibraryInfo(
    val alias: String,
    val group: String,
    val artifact: String,
    val versionRef: String? = null,
    val version: String? = null,
)