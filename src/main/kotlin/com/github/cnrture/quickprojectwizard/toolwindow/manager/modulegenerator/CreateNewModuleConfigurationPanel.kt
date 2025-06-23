package com.github.cnrture.quickprojectwizard.toolwindow.manager.modulegenerator

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Create
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.cnrture.quickprojectwizard.common.Constants
import com.github.cnrture.quickprojectwizard.common.Utils
import com.github.cnrture.quickprojectwizard.common.file.FileWriter
import com.github.cnrture.quickprojectwizard.common.file.LibraryDependencyFinder
import com.github.cnrture.quickprojectwizard.components.QPWActionCard
import com.github.cnrture.quickprojectwizard.components.QPWActionCardType
import com.github.cnrture.quickprojectwizard.components.QPWMessageDialog
import com.github.cnrture.quickprojectwizard.data.ModuleTemplate
import com.github.cnrture.quickprojectwizard.data.PluginListItem
import com.github.cnrture.quickprojectwizard.service.SettingsService
import com.github.cnrture.quickprojectwizard.theme.QPWTheme
import com.github.cnrture.quickprojectwizard.toolwindow.manager.modulegenerator.components.*
import com.intellij.openapi.project.Project

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateNewModuleConfigurationPanel(
    modifier: Modifier = Modifier,
    project: Project,
    fileWriter: FileWriter,
    selectedSrc: String,
    libraryDependencyFinder: LibraryDependencyFinder,
    moduleType: String,
    packageName: String,
    nameState: String,
    onNameChanged: (String) -> Unit,
    onPackageNameChanged: (String) -> Unit,
    moduleNameState: String,
    onModuleNameChanged: (String) -> Unit,
    onModuleTypeSelected: (String) -> Unit,
    availableLibraries: List<String>,
    selectedLibraries: List<String>,
    onLibrarySelected: (String) -> Unit,
    libraryGroups: Map<String, List<String>>,
    expandedGroups: Map<String, Boolean>,
    onGroupExpandToggle: (String) -> Unit,
    availablePlugins: List<PluginListItem>,
    selectedPlugins: List<PluginListItem>,
    onPluginSelected: (PluginListItem) -> Unit,
    templates: List<ModuleTemplate>,
    selectedTemplate: ModuleTemplate?,
    onTemplateSelected: (ModuleTemplate?) -> Unit,
    isAnalyzingState: Boolean,
    analysisResultState: String?,
    onAnalysisResultChange: (String?) -> Unit,
    onAnalyzingChange: (Boolean) -> Unit,
    onDetectedModulesLoaded: (List<String>) -> Unit,
    onSelectedModulesLoaded: (List<String>) -> Unit,
    detectedModules: List<String>,
    existingModules: List<String>,
    selectedModules: List<String>,
    onCheckedModule: (String) -> Unit,
) {
    val radioOptions = listOf(Constants.ANDROID, Constants.KOTLIN)
    val settings = SettingsService.getInstance()

    Scaffold(
        modifier = modifier,
        backgroundColor = QPWTheme.colors.black,
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                contentAlignment = Alignment.CenterEnd,
            ) {
                QPWActionCard(
                    title = "Create",
                    icon = Icons.Rounded.Create,
                    actionColor = QPWTheme.colors.green,
                    type = QPWActionCardType.MEDIUM,
                    onClick = {
                        if (Utils.validateModuleInput(packageName, moduleNameState) && selectedSrc.isNotEmpty()) {
                            Utils.createModule(
                                project = project,
                                fileWriter = fileWriter,
                                selectedSrc = selectedSrc,
                                packageName = packageName,
                                moduleName = moduleNameState,
                                name = nameState,
                                moduleType = moduleType,
                                isMoveFiles = false,
                                libraryDependencyFinder = libraryDependencyFinder,
                                selectedModules = selectedModules,
                                selectedLibraries = selectedLibraries,
                                selectedPlugins = selectedPlugins,
                                template = selectedTemplate,
                                from = "new",
                            )
                        } else {
                            QPWMessageDialog("Please fill out required values").show()
                        }
                    },
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            ModuleTypeNameContent(
                moduleTypeSelectionState = moduleType,
                packageName = packageName,
                moduleNameState = moduleNameState,
                nameState = nameState,
                radioOptions = radioOptions,
                onPackageNameChanged = onPackageNameChanged,
                onModuleTypeSelected = onModuleTypeSelected,
                onModuleNameChanged = onModuleNameChanged,
                onNameChanged = onNameChanged,
            )
            Spacer(modifier = Modifier.height(32.dp))
            TemplateSelectionContent(
                templates = templates,
                selectedTemplate = selectedTemplate,
                defaultTemplateId = settings.state.defaultModuleTemplateId,
                onTemplateSelected = onTemplateSelected
            )
            Spacer(modifier = Modifier.height(16.dp))
            DetectedModulesContent(
                project = project,
                isAnalyzingState = isAnalyzingState,
                analysisResultState = analysisResultState,
                selectedSrc = selectedSrc,
                onAnalysisResultChange = onAnalysisResultChange,
                onAnalyzingChange = onAnalyzingChange,
                onDetectedModulesLoaded = onDetectedModulesLoaded,
                onSelectedModulesLoaded = onSelectedModulesLoaded,
                detectedModules = detectedModules,
                existingModules = existingModules,
                selectedModules = selectedModules,
                onCheckedModule = onCheckedModule,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                PluginSelectionContent(
                    availablePlugins = availablePlugins,
                    onPluginSelected = onPluginSelected,
                )
                LibrarySelectionContent(
                    availableLibraries = availableLibraries,
                    selectedLibraries = selectedLibraries,
                    onLibrarySelected = onLibrarySelected,
                    libraryGroups = libraryGroups,
                    expandedGroups = expandedGroups,
                    onGroupExpandToggle = onGroupExpandToggle,
                )
            }
        }
    }
}
