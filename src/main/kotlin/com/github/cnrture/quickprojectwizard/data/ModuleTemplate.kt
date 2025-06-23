package com.github.cnrture.quickprojectwizard.data

import com.github.cnrture.quickprojectwizard.common.Constants
import kotlinx.serialization.Serializable

@Serializable
data class ModuleTemplate(
    val id: String,
    val name: String,
    val fileTemplates: List<FileTemplate> = emptyList(),
    val isDefault: Boolean = false,
) {
    companion object {
        val EMPTY = ModuleTemplate(
            id = Constants.EMPTY,
            name = Constants.EMPTY,
            fileTemplates = emptyList(),
            isDefault = false
        )
    }
}