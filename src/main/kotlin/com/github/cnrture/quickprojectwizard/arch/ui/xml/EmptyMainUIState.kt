package com.github.cnrture.quickprojectwizard.arch.ui.xml

fun emptyMainUIState(packageName: String, screen: String) = """
package $packageName.ui.${screen.lowercase()}

data class ${screen}UiState(
    val isLoading: Boolean = false,
    val list: List<String> = emptyList(),
)
""".trimIndent()
