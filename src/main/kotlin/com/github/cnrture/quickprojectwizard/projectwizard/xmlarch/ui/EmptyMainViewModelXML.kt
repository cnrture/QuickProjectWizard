package com.github.cnrture.quickprojectwizard.projectwizard.xmlarch.ui

import com.github.cnrture.quickprojectwizard.data.DILibrary

fun emptyMainViewModelXML(packageName: String, screen: String, selectedDILibrary: DILibrary): String {
    return when (selectedDILibrary) {
        DILibrary.Hilt -> hilt(packageName, screen)
        DILibrary.Koin -> koin(packageName, screen)
        DILibrary.None -> withoutDI(packageName, screen)
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

private fun koin(packageName: String, screen: String) = """
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

private fun withoutDI(packageName: String, screen: String) = """
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
