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
import $packageName.ui.${screen.lowercase()}.${screen}Contract.UiAction
import $packageName.ui.${screen.lowercase()}.${screen}Contract.UiEffect
import $packageName.ui.${screen.lowercase()}.${screen}Contract.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ${screen}ViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _uiEffect by lazy { Channel<UiEffect>() }
    val uiEffect: Flow<UiEffect> by lazy { _uiEffect.receiveAsFlow() }

    fun onAction(uiAction: UiAction) {
    }

    private fun updateUiState(block: UiState.() -> UiState) {
        _uiState.update(block)
    }

    private suspend fun emitUiEffect(uiEffect: UiEffect) {
        _uiEffect.send(uiEffect)
    }
}
"""

private fun withoutHilt(packageName: String, screen: String) = """
package $packageName.ui.${screen.lowercase()}

import androidx.lifecycle.ViewModel
import $packageName.ui.${screen.lowercase()}.${screen}Contract.UiAction
import $packageName.ui.${screen.lowercase()}.${screen}Contract.UiEffect
import $packageName.ui.${screen.lowercase()}.${screen}Contract.UiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

class ${screen}ViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _uiEffect by lazy { Channel<UiEffect>() }
    val uiEffect: Flow<UiEffect> by lazy { _uiEffect.receiveAsFlow() }

    fun onAction(uiAction: UiAction) {
    }

    private fun updateUiState(block: UiState.() -> UiState) {
        _uiState.update(block)
    }

    private suspend fun emitUiEffect(uiEffect: UiEffect) {
        _uiEffect.send(uiEffect)
    }
}
"""
