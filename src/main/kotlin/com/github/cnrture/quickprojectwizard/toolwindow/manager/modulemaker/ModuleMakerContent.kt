package com.github.cnrture.quickprojectwizard.toolwindow.manager.modulemaker

import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.material.TabRow
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.LaunchedEffect
import com.github.cnrture.quickprojectwizard.common.Constants
import com.github.cnrture.quickprojectwizard.common.Utils
import com.github.cnrture.quickprojectwizard.common.file.FileWriter
import com.github.cnrture.quickprojectwizard.common.file.LibraryDependencyFinder
import com.github.cnrture.quickprojectwizard.common.rootDirectoryString
import com.github.cnrture.quickprojectwizard.common.rootDirectoryStringDropLast
import com.github.cnrture.quickprojectwizard.components.QPWTabRow
import com.github.cnrture.quickprojectwizard.components.QPWText
import com.github.cnrture.quickprojectwizard.data.ModuleTemplate
import com.github.cnrture.quickprojectwizard.data.FileTemplate
import com.github.cnrture.quickprojectwizard.data.SettingsService
import com.github.cnrture.quickprojectwizard.theme.QPWTheme
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import java.io.File

@Composable
fun ModuleMakerContent(project: Project) {
    val fileWriter = FileWriter()
    val settings = ApplicationManager.getApplication().service<SettingsService>()
    val libraryDependencyFinder = LibraryDependencyFinder()

    var existingModules = listOf<String>()
    val selectedModules = mutableStateListOf<String>()
    val detectedModules = mutableStateListOf<String>()

    val availableLibraries = mutableStateListOf<String>()
    val selectedLibraries = mutableStateListOf<String>()
    val libraryGroups = mutableStateMapOf<String, List<String>>()
    val expandedGroups = mutableStateMapOf<String, Boolean>()

    val availablePlugins = mutableStateListOf<String>()
    val selectedPlugins = mutableStateListOf<String>()

    // Template states
    var selectedTemplate by remember { mutableStateOf<ModuleTemplate?>(null) }
    val availableTemplates = remember {
        val currentTemplates = settings.state.moduleTemplates.toMutableList()
        if (currentTemplates.isEmpty()) {
            // Add default templates if not present
            currentTemplates.addAll(getDefaultTemplates())
        }
        currentTemplates
    }

    val isMoveFiles = mutableStateOf(false)

    val selectedSrc = mutableStateOf(Constants.DEFAULT_SRC_VALUE)
    val moduleType = mutableStateOf(settings.state.preferredModuleType)
    val packageName = mutableStateOf(settings.state.defaultPackageName)
    val moduleName = mutableStateOf(Constants.EMPTY)

    val isAnalyzing = mutableStateOf(false)
    val analysisResult = mutableStateOf<String?>(null)

    var showFileTreeDialog by remember { mutableStateOf(false) }

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("New Module", "New Module with Existing Files")

    // Auto-apply template when selected
    LaunchedEffect(selectedTemplate) {
        selectedTemplate?.let { template ->
            // Set module type from template
            moduleType.value = template.moduleType
        }
    }

    Utils.loadExistingModules(
        project = project,
        onExistingModulesLoaded = { existingModules = it },
    )

    Utils.loadAvailableLibraries(
        project = project,
        libraryDependencyFinder = libraryDependencyFinder,
        onAvailableLibrariesLoaded = {
            availableLibraries.clear()
            availableLibraries.addAll(it)
        },
        onLibraryGroupsLoaded = {
            libraryGroups.clear()
            libraryGroups.putAll(it)
        },
        expandedGroups = expandedGroups,
    )

    Utils.loadAvailablePlugins(
        project = project,
        libraryDependencyFinder = libraryDependencyFinder,
        onAvailablePluginsLoaded = {
            availablePlugins.clear()
            availablePlugins.addAll(it)
        },
    )

    selectedSrc.value = File(project.rootDirectoryString()).absolutePath
        .removePrefix(project.rootDirectoryStringDropLast())
        .removePrefix(File.separator)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = QPWTheme.colors.black,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 24.dp),
        ) {
            QPWText(
                modifier = Modifier.fillMaxWidth(),
                text = "Module Creator",
                style = TextStyle(
                    color = QPWTheme.colors.green,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
            )
            Spacer(modifier = Modifier.size(24.dp))
            TabRow(
                selectedTabIndex = selectedTab,
                backgroundColor = QPWTheme.colors.black,
                contentColor = QPWTheme.colors.white,
                divider = {},
                indicator = {},
            ) {
                QPWTabRow(
                    text = tabs[0],
                    color = QPWTheme.colors.green,
                    isSelected = selectedTab == 0,
                    onTabSelected = { selectedTab = 0 }
                )
                QPWTabRow(
                    text = tabs[1],
                    color = QPWTheme.colors.green,
                    isSelected = selectedTab == 1,
                    onTabSelected = { selectedTab = 1 }
                )
            }
            Spacer(modifier = Modifier.size(24.dp))
            when (selectedTab) {
                0 -> CreateNewModuleConfigurationPanel(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 40.dp, vertical = 16.dp),
                    project = project,
                    fileWriter = fileWriter,
                    selectedSrc = selectedSrc.value,
                    libraryDependencyFinder = libraryDependencyFinder,
                    moduleType = moduleType.value,
                    packageName = packageName.value,
                    onPackageNameChanged = { packageName.value = it },
                    moduleNameState = moduleName.value,
                    onModuleNameChanged = { moduleName.value = it },
                    onModuleTypeSelected = { moduleType.value = it },
                    availableLibraries = availableLibraries,
                    selectedLibraries = selectedLibraries,
                    onLibrarySelected = {
                        if (it in selectedLibraries) selectedLibraries.remove(it) else selectedLibraries.add(it)
                    },
                    libraryGroups = libraryGroups,
                    expandedGroups = expandedGroups,
                    onGroupExpandToggle = { expandedGroups[it] = !(expandedGroups[it] ?: false) },
                    availablePlugins = availablePlugins,
                    selectedPlugins = selectedPlugins,
                    onPluginSelected = {
                        if (it in selectedPlugins) selectedPlugins.remove(it) else selectedPlugins.add(it)
                    },
                    templates = availableTemplates,
                    selectedTemplate = selectedTemplate,
                    onTemplateSelected = { selectedTemplate = it },
                )

                1 -> MoveExistingFilesToModuleContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 40.dp, vertical = 16.dp),
                    project = project,
                    fileWriter = fileWriter,
                    isAnalyzingState = isAnalyzing.value,
                    analysisResultState = analysisResult.value,
                    selectedSrc = selectedSrc.value,
                    libraryDependencyFinder = libraryDependencyFinder,
                    onAnalysisResultChange = { analysisResult.value = it },
                    onAnalyzingChange = { isAnalyzing.value = it },
                    onDetectedModulesLoaded = { detectedModules.clear(); detectedModules.addAll(it) },
                    onSelectedModulesLoaded = { selectedModules.clear(); selectedModules.addAll(it) },
                    detectedModules = detectedModules,
                    isMoveFiles = isMoveFiles.value,
                    onMoveFilesChange = { isMoveFiles.value = it },
                    moduleType = moduleType.value,
                    packageName = packageName.value,
                    onPackageNameChanged = { packageName.value = it },
                    moduleNameState = moduleName.value,
                    onModuleNameChanged = { moduleName.value = it },
                    onModuleTypeSelected = { moduleType.value = it },
                    existingModules = existingModules,
                    selectedModules = selectedModules,
                    onCheckedModule = {
                        if (it in selectedModules) selectedModules.remove(it) else selectedModules.add(it)
                    },
                    availableLibraries = availableLibraries,
                    selectedLibraries = selectedLibraries,
                    onLibrarySelected = {
                        if (it in selectedLibraries) selectedLibraries.remove(it) else selectedLibraries.add(it)
                    },
                    libraryGroups = libraryGroups,
                    expandedGroups = expandedGroups,
                    onGroupExpandToggle = { expandedGroups[it] = !(expandedGroups[it] ?: false) },
                    showFileTreeDialog = showFileTreeDialog,
                    onFileTreeDialogStateChange = { showFileTreeDialog = !showFileTreeDialog },
                    onSelectedSrc = { selectedSrc.value = it },
                    availablePlugins = availablePlugins,
                    selectedPlugins = selectedPlugins,
                    onPluginSelected = {
                        if (it in selectedPlugins) selectedPlugins.remove(it) else selectedPlugins.add(it)
                    },
                )
            }
        }
    }
}

private fun getDefaultTemplates(): List<ModuleTemplate> {
    return listOf(
        ModuleTemplate(
            id = "clean_architecture",
            name = "Clean Architecture",
            description = "MVVM + Clean Architecture with Repository, UseCase, ViewModel",
            moduleType = Constants.ANDROID,
            packageStructure = listOf(
                "data/local",
                "data/remote",
                "data/repository",
                "domain/model",
                "domain/repository",
                "domain/usecase",
                "presentation/viewmodel",
                "presentation/ui"
            ),
            fileTemplates = listOf(
                FileTemplate(
                    fileName = "Repository.kt",
                    filePath = "domain/repository",
                    fileContent = "interface {{MODULE_NAME}}Repository {\n    // Define methods here\n}",
                    fileType = "kt"
                ),
                FileTemplate(
                    fileName = "ViewModel.kt",
                    filePath = "presentation/viewmodel",
                    fileContent = "@HiltViewModel\nclass {{MODULE_NAME}}ViewModel @Inject constructor() : ViewModel() {\n    // ViewModel implementation\n}",
                    fileType = "kt"
                )
            ),
            isDefault = true
        ),
        ModuleTemplate(
            id = "simple_mvvm",
            name = "Simple MVVM",
            description = "Basic MVVM pattern with ViewModel and Repository",
            moduleType = Constants.ANDROID,
            packageStructure = listOf(
                "data/repository",
                "presentation/viewmodel",
                "presentation/ui"
            ),
            fileTemplates = listOf(
                FileTemplate(
                    fileName = "Repository.kt",
                    filePath = "data/repository",
                    fileContent = "@Singleton\nclass {{MODULE_NAME}}Repository @Inject constructor() {\n    // Repository implementation\n}",
                    fileType = "kt"
                )
            ),
            isDefault = true
        )
    )
}
