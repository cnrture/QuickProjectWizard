package com.github.cnrture.quickprojectwizard.projectwizard.composearch.ui.main

fun emptyMainViewModel(packageName: String, screen: String, isHiltEnable: Boolean): String {
    return if (isHiltEnable) {
        hilt(packageName, screen)
    } else {
        withoutHilt(packageName, screen)
    }
}

private fun hilt(packageName: String, screen: String) = """
package $packageName.ui.${screen.lowercase()}

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import $packageName.delegation.MVI
import $packageName.delegation.mvi
import $packageName.ui.${screen.lowercase()}.${screen}Contract.UiAction
import $packageName.ui.${screen.lowercase()}.${screen}Contract.UiEffect
import $packageName.ui.${screen.lowercase()}.${screen}Contract.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ${screen}ViewModel @Inject constructor() : ViewModel(), MVI<UiState, UiAction, UiEffect> by mvi(UiState()) {

    override fun onAction(uiAction: UiAction) {
        viewModelScope.launch {
        }
    }
}
"""

private fun withoutHilt(packageName: String, screen: String) = """
package $packageName.ui.${screen.lowercase()}

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import $packageName.delegation.MVI
import $packageName.delegation.mvi
import $packageName.ui.${screen.lowercase()}.${screen}Contract.UiAction
import $packageName.ui.${screen.lowercase()}.${screen}Contract.UiEffect
import $packageName.ui.${screen.lowercase()}.${screen}Contract.UiState

class ${screen}ViewModel : ViewModel(), MVI<UiState, UiAction, UiEffect> by mvi(UiState()) {

    override fun onAction(uiAction: UiAction) {
        viewModelScope.launch {
        }
    }
    
    // Update state example: updateUiState { UiState(isLoading = false) }
    // or // updateUiState { copy(isLoading = false) }
    
    // Update effect example: emitUiEffect(UiEffect.ShowError(it.message.orEmpty()))
    // Use within a coroutine scope, e.g., viewModelScope.launch { ... }
}
"""
