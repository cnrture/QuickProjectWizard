package com.github.cnrture.quickprojectwizard.projectwizard.composearch.ui.main

fun emptyMainContract(packageName: String, screen: String) = """
package $packageName.ui.${screen.lowercase()}

object ${screen}Contract {
    data class UiState(
        val isLoading: Boolean = false,
        val list: List<String> = emptyList(),
    )

    sealed interface UiAction

    sealed interface UiEffect
}
"""
