package com.github.cnrture.quickprojectwizard.toolwindow.manager.modulegenerator

import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.cnrture.quickprojectwizard.service.AnalyticsService
import com.github.cnrture.quickprojectwizard.common.Constants
import com.github.cnrture.quickprojectwizard.common.Utils
import com.github.cnrture.quickprojectwizard.common.file.FileWriter
import com.github.cnrture.quickprojectwizard.common.file.LibraryDependencyFinder
import com.github.cnrture.quickprojectwizard.common.rootDirectoryString
import com.github.cnrture.quickprojectwizard.common.rootDirectoryStringDropLast
import com.github.cnrture.quickprojectwizard.components.QPWTabRow
import com.github.cnrture.quickprojectwizard.components.QPWText
import com.github.cnrture.quickprojectwizard.data.PluginListItem
import com.github.cnrture.quickprojectwizard.service.SettingsService
import com.github.cnrture.quickprojectwizard.theme.QPWTheme
import com.intellij.openapi.project.Project
import java.io.File

@Composable
fun ModuleGeneratorContent(project: Project) {
    val fileWriter = FileWriter()
    val settings = SettingsService.getInstance()
    val analyticsService = AnalyticsService.getInstance()
    val libraryDependencyFinder = LibraryDependencyFinder()

    var existingModules = listOf<String>()
    val selectedModules = mutableStateListOf<String>()
    val detectedModules = mutableStateListOf<String>()

    val availableLibraries = mutableStateListOf<String>()
    val selectedLibraries = mutableStateListOf<String>()
    val libraryGroups = mutableStateMapOf<String, List<String>>()
    val expandedGroups = mutableStateMapOf<String, Boolean>()

    val availablePlugins = mutableStateListOf<PluginListItem>()

    var selectedTemplate by remember { mutableStateOf(settings.getDefaultModuleTemplate()) }
    val availableTemplates = remember { settings.getModuleTemplates() }

    val selectedSrc = mutableStateOf(Constants.DEFAULT_SRC_VALUE)
    val moduleType = mutableStateOf(settings.state.preferredModuleType)
    val packageName = mutableStateOf(settings.state.defaultPackageName)
    val moduleName = mutableStateOf(Constants.EMPTY)
    val name = mutableStateOf(Constants.EMPTY)

    val isAnalyzing = mutableStateOf(false)
    val analysisResult = mutableStateOf<String?>(null)

    var showFileTreeDialog by remember { mutableStateOf(false) }

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("New\nModule", "New Module with\nExisting Files")

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
            availablePlugins.addAll(it.map { PluginListItem(it) })
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
                text = "Module Generator",
                style = TextStyle(
                    color = QPWTheme.colors.green,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
            )
            Spacer(modifier = Modifier.size(24.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                QPWTabRow(
                    modifier = Modifier.weight(1f),
                    text = tabs[0],
                    color = QPWTheme.colors.green,
                    isSelected = selectedTab == 0,
                    onTabSelected = { selectedTab = 0 }
                )
                QPWTabRow(
                    modifier = Modifier.weight(1f),
                    text = tabs[1],
                    color = QPWTheme.colors.green,
                    isSelected = selectedTab == 1,
                    onTabSelected = { selectedTab = 1 }
                )
            }
            Spacer(modifier = Modifier.size(24.dp))
            when (selectedTab) {
                0 -> {
                    analyticsService.track("view_module_generator_new_module")
                    CreateNewModuleConfigurationPanel(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp),
                        project = project,
                        fileWriter = fileWriter,
                        selectedSrc = selectedSrc.value,
                        libraryDependencyFinder = libraryDependencyFinder,
                        moduleType = moduleType.value,
                        packageName = packageName.value,
                        nameState = name.value,
                        onNameChanged = { name.value = it },
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
                        selectedPlugins = availablePlugins,
                        onPluginSelected = { plugin ->
                            val index = availablePlugins.indexOfFirst { it.name == plugin.name }
                            if (index != -1) {
                                availablePlugins[index] = plugin.copy(isSelected = !plugin.isSelected)
                            }
                        },
                        templates = availableTemplates,
                        selectedTemplate = selectedTemplate,
                        onTemplateSelected = { selectedTemplate = it },
                        isAnalyzingState = isAnalyzing.value,
                        analysisResultState = analysisResult.value,
                        onAnalysisResultChange = { analysisResult.value = it },
                        onAnalyzingChange = { isAnalyzing.value = it },
                        onDetectedModulesLoaded = { detectedModules.clear(); detectedModules.addAll(it) },
                        onSelectedModulesLoaded = { selectedModules.clear(); selectedModules.addAll(it) },
                        detectedModules = detectedModules,
                        existingModules = existingModules,
                        selectedModules = selectedModules,
                        onCheckedModule = {
                            if (it in selectedModules) selectedModules.remove(it) else selectedModules.add(it)
                        },
                    )
                }

                1 -> {
                    analyticsService.track("view_module_generator_existing_files")
                    MoveExistingFilesToModuleContent(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp),
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
                        selectedPlugins = availablePlugins,
                        onPluginSelected = { plugin ->
                            val index = availablePlugins.indexOfFirst { it.name == plugin.name }
                            if (index != -1) {
                                availablePlugins[index] = plugin.copy(isSelected = !plugin.isSelected)
                            }
                        },
                    )
                }
            }
        }
    }
}
