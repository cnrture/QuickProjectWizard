package com.github.cnrture.quickprojectwizard.data

import kotlinx.serialization.Serializable

@Serializable
data class FileTemplate(
    val fileName: String = "",
    val filePath: String = "",
    val fileContent: String = "",
)