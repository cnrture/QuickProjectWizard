package com.github.cnrture.quickprojectwizard.composearch.ui.main

fun emptyMainContract(packageName: String, screen: String) = """
package $packageName.ui.${screen.lowercase()}

object ${screen}Contract {
    data class UiState(
        val isLoading: Boolean = false,
        val list: List<String> = emptyList(),
    )

    sealed class UiAction

    sealed class UiEffect
}
"""
