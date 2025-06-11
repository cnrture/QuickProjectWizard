package com.github.cnrture.quickprojectwizard.projectwizard.composearch.ui.main

fun emptyMainScreenPreviewProvider(packageName: String, screen: String) = """
package $packageName.ui.${screen.lowercase()}

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class ${screen}ScreenPreviewProvider : PreviewParameterProvider<${screen}Contract.UiState> {
    override val values: Sequence<${screen}Contract.UiState>
        get() = sequenceOf(
            ${screen}Contract.UiState(
                isLoading = true,
                list = emptyList(),
            ),
            ${screen}Contract.UiState(
                isLoading = false,
                list = emptyList(),
            ),
            ${screen}Contract.UiState(
                isLoading = false,
                list = listOf("Item 1", "Item 2", "Item 3")
            ),
        )
}
"""
