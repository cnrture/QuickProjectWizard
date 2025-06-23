package com.github.cnrture.quickprojectwizard.service

import com.github.cnrture.quickprojectwizard.data.*
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import kotlinx.serialization.json.Json
import java.io.File

@State(name = "QuickProjectWizardSettings", storages = [Storage("quickProjectWizard.xml")])
@Service(Service.Level.APP)
class SettingsService : PersistentStateComponent<SettingsState> {
    private var myState = SettingsState()

    companion object {
        private fun getAutoBackupPath(): String {
            val userHome = System.getProperty("user.home")
            val separator = File.separator
            return "$userHome${separator}.quickprojectwizard${separator}settings.json"
        }

        fun getInstance(): SettingsService {
            return ApplicationManager.getApplication().getService(SettingsService::class.java)
        }
    }

    init {
        loadFromAutoBackup()
        setDefaultTemplatesIfEmpty()
    }

    override fun getState(): SettingsState {
        setDefaultTemplatesIfEmpty()
        return myState
    }

    override fun loadState(state: SettingsState) {
        myState = state
        setDefaultTemplatesIfEmpty()
        saveToAutoBackup()
    }

    private fun loadFromAutoBackup() {
        try {
            val backupFile = File(getAutoBackupPath())
            if (backupFile.exists()) {
                val jsonContent = backupFile.readText()
                val importedState = Json.decodeFromString(SettingsState.serializer(), jsonContent)
                myState = importedState
            }
        } catch (_: Exception) {
        }
    }

    private fun saveToAutoBackup() {
        try {
            val backupFile = File(getAutoBackupPath())
            backupFile.parentFile?.mkdirs()
            backupFile.writeText(exportSettings())
        } catch (_: Exception) {
        }
    }

    fun saveTemplate(template: ModuleTemplate) {
        val existingIndex = myState.moduleTemplates.indexOfFirst { it.id == template.id }
        if (existingIndex != -1) myState.moduleTemplates[existingIndex] = template
        else myState.moduleTemplates.add(template)
        saveToAutoBackup()
    }

    fun saveFeatureTemplate(template: FeatureTemplate) {
        val existingIndex = myState.featureTemplates.indexOfFirst { it.id == template.id }
        if (existingIndex != -1) myState.featureTemplates[existingIndex] = template
        else myState.featureTemplates.add(template)
        saveToAutoBackup()
    }

    fun setDefaultModuleTemplate(templateId: String) {
        myState.defaultModuleTemplateId = templateId
        saveToAutoBackup()
    }

    fun setDefaultFeatureTemplate(templateId: String) {
        myState.defaultFeatureTemplateId = templateId
        saveToAutoBackup()
    }

    fun getDefaultModuleTemplate() = myState.moduleTemplates.find { it.id == myState.defaultModuleTemplateId }

    fun getDefaultFeatureTemplate() = myState.featureTemplates.find { it.id == myState.defaultFeatureTemplateId }

    fun exportSettings(): String = Json.encodeToString(SettingsState.serializer(), myState)

    fun importSettings(jsonString: String): Boolean {
        return try {
            myState = Json.decodeFromString(SettingsState.serializer(), jsonString)
            setDefaultTemplatesIfEmpty()
            true
        } catch (_: Exception) {
            false
        }
    }

    fun exportToFile(filePath: String): Boolean {
        return try {
            File(filePath).writeText(exportSettings())
            true
        } catch (_: Exception) {
            false
        }
    }

    fun importFromFile(filePath: String): Boolean {
        return try {
            importSettings(File(filePath).readText())
        } catch (_: Exception) {
            false
        }
    }

    fun getModuleTemplates(): List<ModuleTemplate> {
        if (myState.moduleTemplates.isEmpty()) myState.moduleTemplates.addAll(getDefaultModuleTemplates())
        return myState.moduleTemplates
    }

    fun getFeatureTemplates(): List<FeatureTemplate> {
        if (myState.featureTemplates.isEmpty()) myState.featureTemplates.addAll(getDefaultFeatureTemplates())
        return myState.featureTemplates
    }

    fun addColorToHistory(colorInfo: ColorInfo) {
        if (!myState.colorHistory.any { it.hex == colorInfo.hex }) {
            myState.colorHistory.add(0, colorInfo)
            if (myState.colorHistory.size > 10) myState.colorHistory.removeAt(myState.colorHistory.size - 1)
        }
        saveToAutoBackup()
    }

    fun saveFormatterState(selectedFormat: String, inputText: String, errorMessage: String) {
        myState.formatterSelectedFormat = selectedFormat
        myState.formatterInputText = inputText
        myState.formatterErrorMessage = errorMessage
        saveToAutoBackup()
    }

    fun saveApiTesterState(
        method: String,
        url: String,
        requestBody: String,
        headers: Map<String, String>,
        queryParams: Map<String, String>,
        selectedTab: String,
    ) {
        myState.apiSelectedMethod = method
        myState.apiUrl = url
        myState.apiRequestBody = requestBody
        myState.apiHeaders.clear()
        myState.apiHeaders.putAll(headers)
        myState.apiQueryParams.clear()
        myState.apiQueryParams.putAll(queryParams)
        myState.apiSelectedTab = selectedTab
        saveToAutoBackup()
    }

    fun getApiSelectedMethod(): String = myState.apiSelectedMethod
    fun getApiUrl(): String = myState.apiUrl
    fun getApiRequestBody(): String = myState.apiRequestBody
    fun getApiHeaders(): Map<String, String> = myState.apiHeaders.toMap()
    fun getApiQueryParams(): Map<String, String> = myState.apiQueryParams.toMap()
    fun getApiSelectedTab(): String = myState.apiSelectedTab
    fun getFormatterSelectedFormat(): String = myState.formatterSelectedFormat
    fun getFormatterInputText(): String = myState.formatterInputText
    fun getFormatterErrorMessage(): String = myState.formatterErrorMessage
    fun getColorHistory(): List<ColorInfo> = myState.colorHistory.toList()
    fun removeFeatureTemplate(template: FeatureTemplate) {
        myState.featureTemplates.removeAll { it.id == template.id }
        saveToAutoBackup()
    }

    fun removeTemplate(template: ModuleTemplate) {
        myState.moduleTemplates.removeAll { it.id == template.id }
        saveToAutoBackup()
    }

    fun addFeatureTemplate(template: FeatureTemplate) {
        val existingIndex = myState.featureTemplates.indexOfFirst { it.id == template.id }
        if (existingIndex != -1) {
            myState.featureTemplates[existingIndex] = template
        } else {
            myState.featureTemplates.add(template)
        }
        saveToAutoBackup()
    }

    fun addModuleTemplate(template: ModuleTemplate) {
        val existingIndex = myState.moduleTemplates.indexOfFirst { it.id == template.id }
        if (existingIndex != -1) {
            myState.moduleTemplates[existingIndex] = template
        } else {
            myState.moduleTemplates.add(template)
        }
        saveToAutoBackup()
    }

    private fun setDefaultTemplatesIfEmpty() {
        if (myState.moduleTemplates.isEmpty()) myState.moduleTemplates.addAll(getDefaultModuleTemplates())
        if (myState.featureTemplates.isEmpty()) myState.featureTemplates.addAll(getDefaultFeatureTemplates())
    }
}

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
                    fileName = "{NAME}PreviewParameterProvider.kt",
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
                    fileName = "{NAME}PreviewParameterProvider.kt",
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
