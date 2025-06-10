package com.github.cnrture.quickprojectwizard.template

object FeatureTemplate {
    fun getScreen(packageName: String, featureName: String) = """
        package $packageName
        
        import androidx.compose.foundation.layout.Box
        import androidx.compose.foundation.layout.fillMaxSize
        import androidx.compose.material3.Text
        import androidx.compose.runtime.Composable
        import androidx.compose.ui.Alignment
        import androidx.compose.ui.Modifier
        import androidx.compose.ui.tooling.preview.Preview
        import androidx.compose.ui.tooling.preview.PreviewParameter
        import androidx.compose.ui.unit.sp
        import $packageName.${featureName}Contract.UiState
        import $packageName.${featureName}Contract.UiEffect
        import $packageName.${featureName}Contract.UiAction
        import kotlinx.coroutines.flow.Flow
        import kotlinx.coroutines.flow.emptyFlow

        @Composable
        fun ${featureName}Screen(
            uiState: UiState,
            uiEffect: Flow<UiEffect>,
            onAction: (UiAction) -> Unit
        ) {
            ${featureName}Content(
                modifier = Modifier.fillMaxSize(),
                uiState = uiState,
                onAction = onAction,
            )
        }

        @Composable
        private fun ${featureName}Content(
            modifier: Modifier = Modifier,
            uiState: UiState,
            onAction: (UiAction) -> Unit,
        ) {
            Box(
                modifier = modifier,
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "$featureName Content",
                    fontSize = 24.sp,
                )
            }
        }
        
        @Preview(showBackground = true)
        @Composable
        fun ${featureName}ScreenPreview(
            @PreviewParameter(${featureName}ScreenPreviewProvider::class) uiState: UiState,
        ) {
            ${featureName}Screen(
                uiState = uiState,
                uiEffect = emptyFlow(),
                onAction = {},
            )
        }
    """.trimIndent()

    fun getViewModel(packageName: String, featureName: String, isHiltEnable: Boolean): String {
        return if (isHiltEnable) {
            viewModelWithHilt(packageName, featureName)
        } else {
            viewModelWithoutHilt(packageName, featureName)
        }
    }

    private fun viewModelWithHilt(packageName: String, featureName: String) = """
package $packageName

import androidx.lifecycle.ViewModel
import $packageName.${featureName}Contract.UiState
import $packageName.${featureName}Contract.UiEffect
import $packageName.${featureName}Contract.UiAction
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
class ${featureName}ViewModel @Inject constructor() : ViewModel() {

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

    private fun viewModelWithoutHilt(packageName: String, featureName: String) = """
package $packageName

import androidx.lifecycle.ViewModel
import $packageName.${featureName}Contract.UiState
import $packageName.${featureName}Contract.UiEffect
import $packageName.${featureName}Contract.UiAction
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

class ${featureName}ViewModel : ViewModel() {

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

    fun getContract(packageName: String, featureName: String) = """
        package $packageName

        object ${featureName}Contract {
            data class UiState(
                val isLoading: Boolean = false,
                val list: List<String> = emptyList(),
            )

            sealed interface UiAction
            
            sealed interface UiEffect
        }
    """.trimIndent()

    fun getPreviewProvider(packageName: String, featureName: String) = """
        package $packageName

        import androidx.compose.ui.tooling.preview.PreviewParameterProvider

        class ${featureName}ScreenPreviewProvider : PreviewParameterProvider<${featureName}Contract.UiState> {
            override val values: Sequence<${featureName}Contract.UiState>
                get() = sequenceOf(
                    ${featureName}Contract.UiState(
                        isLoading = true,
                        list = emptyList(),
                    ),
                    ${featureName}Contract.UiState(
                        isLoading = false,
                        list = emptyList(),
                    ),
                    ${featureName}Contract.UiState(
                        isLoading = false,
                        list = listOf("Item 1", "Item 2", "Item 3")
                    ),
                )
        }
    """.trimIndent()
}