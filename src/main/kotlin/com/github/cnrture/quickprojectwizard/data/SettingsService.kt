package com.github.cnrture.quickprojectwizard.data

import com.github.cnrture.quickprojectwizard.common.Constants
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

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
}

data class SettingsState(
    var moduleTemplates: MutableList<ModuleTemplate> = mutableListOf(),
    var featureTemplates: MutableList<FeatureTemplate> = mutableListOf(),
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

data class ModuleTemplate(
    val id: String,
    val name: String,
    val fileTemplates: List<FileTemplate>,
    val isDefault: Boolean = false,
)

data class FileTemplate(
    val fileName: String,
    val filePath: String,
    val fileContent: String,
    val fileType: String,
)

data class FeatureTemplate(
    val id: String,
    val name: String,
    val fileTemplates: List<FileTemplate>,
    val isDefault: Boolean = false,
)

fun getDefaultModuleTemplates(): List<ModuleTemplate> {
    return listOf(
        ModuleTemplate(
            id = "candroid_template",
            name = "Candroid's Template",
            fileTemplates = listOf(
                FileTemplate(
                    fileName = "Repository.kt",
                    filePath = "domain/repository",
                    fileContent = "interface {{MODULE_NAME}}Repository {\n    // Define methods here\n}",
                    fileType = "kt"
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
            name = "Compose MVVM Template",
            fileTemplates = listOf(
                FileTemplate(
                    fileName = "{FEATURE_NAME}Screen.kt",
                    filePath = "",
                    fileContent = """package {PACKAGE}

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun {FEATURE_NAME}Screen(
    viewModel: {FEATURE_NAME}ViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    {FEATURE_NAME}Content(
        uiState = uiState,
        onEvent = viewModel::onEvent
    )
}

@Composable
private fun {FEATURE_NAME}Content(
    uiState: {FEATURE_NAME}UiState,
    onEvent: ({FEATURE_NAME}Event) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "{FEATURE_NAME} Screen",
            style = MaterialTheme.typography.headlineMedium
        )
    }
}
""",
                    fileType = "kt"
                ),
                FileTemplate(
                    fileName = "{FEATURE_NAME}ViewModel.kt",
                    filePath = "",
                    fileContent = """package {PACKAGE}

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class {FEATURE_NAME}ViewModel @Inject constructor() : ViewModel() {
    
    private val _uiState = MutableStateFlow({FEATURE_NAME}UiState())
    val uiState: StateFlow<{FEATURE_NAME}UiState> = _uiState.asStateFlow()
    
    fun onEvent(event: {FEATURE_NAME}Event) {
        when (event) {
            // Handle events
        }
    }
}
""",
                    fileType = "kt"
                ),
                FileTemplate(
                    fileName = "{FEATURE_NAME}Contract.kt",
                    filePath = "",
                    fileContent = """package {PACKAGE}

data class {FEATURE_NAME}UiState(
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class {FEATURE_NAME}Event {
    // Define events here
}
""",
                    fileType = "kt"
                ),
            ),
            isDefault = true,
        ),
        FeatureTemplate(
            id = "simple_screen_template",
            name = "Simple Screen Template",
            fileTemplates = listOf(
                FileTemplate(
                    fileName = "{FEATURE_NAME}Screen.kt",
                    filePath = "",
                    fileContent = """package {PACKAGE}

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun {FEATURE_NAME}Screen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "{FEATURE_NAME} Screen",
            style = MaterialTheme.typography.headlineMedium
        )
    }
}
""",
                    fileType = "kt"
                ),
            ),
            isDefault = false,
        ),
    )
}