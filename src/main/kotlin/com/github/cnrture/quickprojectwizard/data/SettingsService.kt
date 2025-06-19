package com.github.cnrture.quickprojectwizard.data

import com.github.cnrture.quickprojectwizard.common.Constants
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@State(name = "QuickProjectWizardSettings", storages = [Storage("quickProjectWizard.xml")])
@Service(Service.Level.APP)
class SettingsService : PersistentStateComponent<SettingsState> {
    private var myState = SettingsState()
    override fun getState(): SettingsState = myState
    override fun loadState(state: SettingsState) {
        myState = state
    }

    fun saveTemplate(template: ModuleTemplate) {
        val existingIndex = myState.moduleTemplates.indexOfFirst { it.id == template.id }
        if (existingIndex != -1) {
            myState.moduleTemplates[existingIndex] = template
        } else {
            myState.moduleTemplates.add(template)
        }
    }

    fun removeTemplate(template: ModuleTemplate) {
        myState.moduleTemplates.removeAll { it.id == template.id }
    }

    fun saveFeatureTemplate(template: FeatureTemplate) {
        val existingIndex = myState.featureTemplates.indexOfFirst { it.id == template.id }
        if (existingIndex != -1) {
            myState.featureTemplates[existingIndex] = template
        } else {
            myState.featureTemplates.add(template)
        }
    }

    fun removeFeatureTemplate(template: FeatureTemplate) {
        myState.featureTemplates.removeAll { it.id == template.id }
    }

    fun setDefaultModuleTemplate(templateId: String) {
        myState.defaultModuleTemplateId = templateId
    }

    fun setDefaultFeatureTemplate(templateId: String) {
        myState.defaultFeatureTemplateId = templateId
    }

    fun getDefaultModuleTemplate(): ModuleTemplate? {
        return myState.moduleTemplates.find { it.id == myState.defaultModuleTemplateId }
    }

    fun getDefaultFeatureTemplate(): FeatureTemplate? {
        return myState.featureTemplates.find { it.id == myState.defaultFeatureTemplateId }
    }

    fun exportSettings(): String {
        return Json.encodeToString(SettingsState.serializer(), myState)
    }

    fun importSettings(jsonString: String): Boolean {
        return try {
            val importedState = Json.decodeFromString(SettingsState.serializer(), jsonString)
            myState = importedState
            true
        } catch (e: Exception) {
            false
        }
    }

    fun exportToFile(filePath: String): Boolean {
        return try {
            val jsonString = exportSettings()
            java.io.File(filePath).writeText(jsonString)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun importFromFile(filePath: String): Boolean {
        return try {
            val jsonString = java.io.File(filePath).readText()
            importSettings(jsonString)
        } catch (e: Exception) {
            false
        }
    }
}

@Serializable
data class SettingsState(
    var moduleTemplates: MutableList<ModuleTemplate> = mutableListOf(),
    var featureTemplates: MutableList<FeatureTemplate> = mutableListOf(),
    var defaultModuleTemplateId: String = "candroid_template",
    var defaultFeatureTemplateId: String = "candroid_template",
    var defaultPackageName: String = Constants.DEFAULT_BASE_PACKAGE_NAME,
    var preferredModuleType: String = Constants.ANDROID,
    var featureScreenTemplate: String = Constants.EMPTY,
    var featureViewModelTemplate: String = Constants.EMPTY,
    var featureContractTemplate: String = Constants.EMPTY,
    var featureComponentKeyTemplate: String = Constants.EMPTY,
    var featurePreviewProviderTemplate: String = Constants.EMPTY,
    var moduleReadmeTemplate: String = Constants.EMPTY,
    var manifestTemplate: String = Constants.EMPTY,
    var gradleAndroidTemplate: String = Constants.EMPTY,
    var gradleKotlinTemplate: String = Constants.EMPTY,

    var isCompose: Boolean = true,
    var isHiltEnable: Boolean = true,
    var isActionsExpanded: Boolean = true,
)

@Serializable
data class ModuleTemplate(
    val id: String = "",
    val name: String = "",
    val fileTemplates: List<FileTemplate> = emptyList(),
    val isDefault: Boolean = false,
)

@Serializable
data class FileTemplate(
    val fileName: String = "",
    val filePath: String = "",
    val fileContent: String = "",
)

@Serializable
data class FeatureTemplate(
    val id: String = "",
    val name: String = "",
    val fileTemplates: List<FileTemplate> = emptyList(),
    val isDefault: Boolean = false,
)

fun getDefaultModuleTemplates(): List<ModuleTemplate> {
    return listOf(
        ModuleTemplate(
            id = "candroid_template",
            name = "Candroid's Module",
            fileTemplates = listOf(
                FileTemplate(
                    fileName = "{NAME}Screen.kt",
                    filePath = "ui",
                    fileContent = """
package {FILE_PACKAGE}

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.sp
import {FILE_PACKAGE}.{NAME}Contract.UiState
import {FILE_PACKAGE}.{NAME}Contract.UiEffect
import {FILE_PACKAGE}.{NAME}Contract.UiAction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@Composable
fun {NAME}Screen(
    uiState: UiState,
    uiEffect: Flow<UiEffect>,
    onAction: (UiAction) -> Unit
) {
    {NAME}Content(
        modifier = Modifier.fillMaxSize(),
        uiState = uiState,
        onAction = onAction,
    )
}

@Composable
private fun {NAME}Content(
    modifier: Modifier = Modifier,
    uiState: UiState,
    onAction: (UiAction) -> Unit,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "{NAME} Content",
            fontSize = 24.sp,
        )
    }
}
        
@Preview(showBackground = true)
@Composable
fun {NAME}ScreenPreview(
    @PreviewParameter({NAME}ScreenPreviewProvider::class) uiState: UiState,
) {
    {NAME}Screen(
        uiState = uiState,
        uiEffect = emptyFlow(),
        onAction = {},
    )
}
""".trimIndent(),
                ),
                FileTemplate(
                    fileName = "{NAME}ViewModel.kt",
                    filePath = "ui",
                    fileContent = """
package {FILE_PACKAGE}

import androidx.lifecycle.ViewModel
import {FILE_PACKAGE}.{NAME}Contract.UiState
import {FILE_PACKAGE}.{NAME}Contract.UiEffect
import {FILE_PACKAGE}.{NAME}Contract.UiAction
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
class {NAME}ViewModel @Inject constructor() : ViewModel() {

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
""".trimIndent(),
                ),
                FileTemplate(
                    fileName = "{NAME}Contract.kt",
                    filePath = "ui",
                    fileContent = """
package {FILE_PACKAGE}

object {NAME}Contract {
    data class UiState(
        val isLoading: Boolean = false,
        val list: List<String> = emptyList(),
    )

    sealed interface UiAction
            
    sealed interface UiEffect
}
""".trimIndent(),
                ),
                FileTemplate(
                    fileName = "{NAME}ComponentKey.kt",
                    filePath = "ui",
                    fileContent = """
package {FILE_PACKAGE}
                       
import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class {NAME}ScreenPreviewProvider : PreviewParameterProvider<{NAME}Contract.UiState> {
    override val values: Sequence<{NAME}Contract.UiState>
        get() = sequenceOf(
            {NAME}Contract.UiState(
                isLoading = true,
                list = emptyList(),
            ),
            {NAME}Contract.UiState(
                isLoading = false,
                list = emptyList(),
            ),
            {NAME}Contract.UiState(
                isLoading = false,
                list = listOf("Item 1", "Item 2", "Item 3")
            ),
        )
}
""".trimIndent(),
                ),
            ),
            isDefault = true,
        ),
    )
}

fun getDefaultFeatureTemplates(): List<FeatureTemplate> {
    return listOf(
        FeatureTemplate(
            id = "candroid_template",
            name = "Candroid's Feature",
            fileTemplates = listOf(
                FileTemplate(
                    fileName = "{NAME}Screen.kt",
                    filePath = "",
                    fileContent = """
package {FILE_PACKAGE}

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.sp
import {FILE_PACKAGE}.{NAME}Contract.UiState
import {FILE_PACKAGE}.{NAME}Contract.UiEffect
import {FILE_PACKAGE}.{NAME}Contract.UiAction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@Composable
fun {NAME}Screen(
    uiState: UiState,
    uiEffect: Flow<UiEffect>,
    onAction: (UiAction) -> Unit
) {
    {NAME}Content(
        modifier = Modifier.fillMaxSize(),
        uiState = uiState,
        onAction = onAction,
    )
}

@Composable
private fun {NAME}Content(
    modifier: Modifier = Modifier,
    uiState: UiState,
    onAction: (UiAction) -> Unit,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "{NAME} Content",
            fontSize = 24.sp,
        )
    }
}
        
@Preview(showBackground = true)
@Composable
fun {NAME}ScreenPreview(
    @PreviewParameter({NAME}ScreenPreviewProvider::class) uiState: UiState,
) {
    {NAME}Screen(
        uiState = uiState,
        uiEffect = emptyFlow(),
        onAction = {},
    )
}
""".trimIndent(),
                ),
                FileTemplate(
                    fileName = "{NAME}ViewModel.kt",
                    filePath = "",
                    fileContent = """
package {FILE_PACKAGE}

import androidx.lifecycle.ViewModel
import {FILE_PACKAGE}.{NAME}Contract.UiState
import {FILE_PACKAGE}.{NAME}Contract.UiEffect
import {FILE_PACKAGE}.{NAME}Contract.UiAction
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
class {NAME}ViewModel @Inject constructor() : ViewModel() {

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
""".trimIndent(),
                ),
                FileTemplate(
                    fileName = "{NAME}Contract.kt",
                    filePath = "",
                    fileContent = """
package {FILE_PACKAGE}

object {NAME}Contract {
    data class UiState(
        val isLoading: Boolean = false,
        val list: List<String> = emptyList(),
    )

    sealed interface UiAction
            
    sealed interface UiEffect
}
""".trimIndent(),
                ),
                FileTemplate(
                    fileName = "{NAME}ComponentKey.kt",
                    filePath = "",
                    fileContent = """
package {FILE_PACKAGE}
                       
import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class {NAME}ScreenPreviewProvider : PreviewParameterProvider<{NAME}Contract.UiState> {
    override val values: Sequence<{NAME}Contract.UiState>
        get() = sequenceOf(
            {NAME}Contract.UiState(
                isLoading = true,
                list = emptyList(),
            ),
            {NAME}Contract.UiState(
                isLoading = false,
                list = emptyList(),
            ),
            {NAME}Contract.UiState(
                isLoading = false,
                list = listOf("Item 1", "Item 2", "Item 3")
            ),
        )
}
""".trimIndent(),
                ),
            ),
            isDefault = true,
        ),
    )
}
