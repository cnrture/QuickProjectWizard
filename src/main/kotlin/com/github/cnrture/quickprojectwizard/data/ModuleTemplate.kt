package com.github.cnrture.quickprojectwizard.data

import kotlinx.serialization.Serializable

@Serializable
data class ModuleTemplate(
    val id: String,
    val name: String,
    val fileTemplates: List<FileTemplate> = emptyList(),
    val isDefault: Boolean = false,
)