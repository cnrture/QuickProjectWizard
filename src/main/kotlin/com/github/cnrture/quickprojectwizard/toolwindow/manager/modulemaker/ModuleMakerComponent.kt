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
import com.github.cnrture.quickprojectwizard.common.Constants
import com.github.cnrture.quickprojectwizard.common.Utils
import com.github.cnrture.quickprojectwizard.common.file.FileWriter
import com.github.cnrture.quickprojectwizard.common.file.LibraryDependencyFinder
import com.github.cnrture.quickprojectwizard.common.rootDirectoryString
import com.github.cnrture.quickprojectwizard.common.rootDirectoryStringDropLast
import com.github.cnrture.quickprojectwizard.components.QPWTabRow
import com.github.cnrture.quickprojectwizard.components.QPWText
import com.github.cnrture.quickprojectwizard.data.SettingsService
import com.github.cnrture.quickprojectwizard.theme.QPWTheme
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import java.io.File

@Composable
fun ModuleMakerComponent(project: Project) {
    val fileWriter = FileWriter()
    val settings = ApplicationManager.getApplication().service<SettingsService>()
    val libraryDependencyFinder = LibraryDependencyFinder()

    var existingModules = listOf<String>()
    val selectedModules = mutableStateListOf<String>()
    val detectedModules = mutableStateListOf<String>()
    val detectedLibraries = mutableStateListOf<String>()

    val availableLibraries = mutableStateListOf<String>()
    val selectedLibraries = mutableStateListOf<String>()
    val libraryGroups = mutableStateMapOf<String, List<String>>()
    val expandedGroups = mutableStateMapOf<String, Boolean>()

    val availablePlugins = mutableStateListOf<String>()
    val selectedPlugins = mutableStateListOf<String>()
    val pluginGroups = mutableStateMapOf<String, List<String>>()
    val expandedPluginGroups = mutableStateMapOf<String, Boolean>()

    val isMoveFiles = mutableStateOf(false)
    val analyzeLibraries = mutableStateOf(false)

    val selectedSrc = mutableStateOf(Constants.DEFAULT_SRC_VALUE)
    val moduleType = mutableStateOf(settings.state.preferredModuleType)
    val packageName = mutableStateOf(settings.state.defaultPackageName)
    val moduleName = mutableStateOf(Constants.EMPTY)

    val isAnalyzing = mutableStateOf(false)
    val analysisResult = mutableStateOf<String?>(null)

    var showFileTreeDialog by remember { mutableStateOf(false) }

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Create New Module", "Move Existing Files to Module")

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
        onPluginGroupsLoaded = {
            pluginGroups.clear()
            pluginGroups.putAll(it)
        },
        expandedPluginGroups = expandedPluginGroups,
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
                    pluginGroups = pluginGroups,
                    expandedPluginGroups = expandedPluginGroups,
                    onPluginGroupExpandToggle = { expandedPluginGroups[it] = !(expandedPluginGroups[it] ?: false) },
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
                    analyzeLibraries = analyzeLibraries.value,
                    onDetectLibrariesLoaded = { detectedLibraries.clear(); detectedLibraries.addAll(it) },
                    onDetectedModulesLoaded = { detectedModules.clear(); detectedModules.addAll(it) },
                    onSelectedModulesLoaded = { selectedModules.clear(); selectedModules.addAll(it) },
                    detectedModules = detectedModules,
                    isMoveFiles = isMoveFiles.value,
                    onMoveFilesChange = { isMoveFiles.value = it },
                    onAnalyzeLibrariesChange = { analyzeLibraries.value = it },
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
                    detectedLibraries = detectedLibraries,
                    onGroupExpandToggle = { expandedGroups[it] = !(expandedGroups[it] ?: false) },
                    showFileTreeDialog = showFileTreeDialog,
                    onFileTreeDialogStateChange = { showFileTreeDialog = !showFileTreeDialog },
                    onSelectedSrc = { selectedSrc.value = it },
                    availablePlugins = availablePlugins,
                    selectedPlugins = selectedPlugins,
                    onPluginSelected = {
                        if (it in selectedPlugins) selectedPlugins.remove(it) else selectedPlugins.add(it)
                    },
                    pluginGroups = pluginGroups,
                    expandedPluginGroups = expandedPluginGroups,
                    onPluginGroupExpandToggle = { group ->
                        expandedPluginGroups[group] = !(expandedPluginGroups[group] ?: false)
                    },
                )
            }
        }
    }
}