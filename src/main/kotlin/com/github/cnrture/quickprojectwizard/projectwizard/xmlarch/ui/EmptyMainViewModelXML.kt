package com.github.cnrture.quickprojectwizard.projectwizard.xmlarch.ui

fun emptyMainViewModelXML(packageName: String, screen: String, isHiltEnable: Boolean): String {
    return if (isHiltEnable) {
        hilt(packageName, screen)
    } else {
        withoutHilt(packageName, screen)
    }
}

private fun hilt(packageName: String, screen: String) = """
package $packageName.ui.${screen.lowercase()}

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ${screen}ViewModel @Inject constructor(): ViewModel() {

    private var _uiState = MutableStateFlow(${screen}UiState())
    val uiState: StateFlow<${screen}UiState> = _uiState.asStateFlow()

}
""".trimIndent()

private fun withoutHilt(packageName: String, screen: String) = """
package $packageName.ui.${screen.lowercase()}

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ${screen}ViewModel : ViewModel() {

    private var _uiState = MutableStateFlow(${screen}UiState())
    val uiState: StateFlow<${screen}UiState> = _uiState.asStateFlow()

}
""".trimIndent()
