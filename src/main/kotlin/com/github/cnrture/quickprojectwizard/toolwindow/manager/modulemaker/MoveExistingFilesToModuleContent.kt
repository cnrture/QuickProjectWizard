package com.github.cnrture.quickprojectwizard.toolwindow.manager.modulemaker

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.cnrture.quickprojectwizard.common.Constants
import com.github.cnrture.quickprojectwizard.toolwindow.components.QPWDialogActions
import com.github.cnrture.quickprojectwizard.toolwindow.dialog.MessageDialogWrapper
import com.github.cnrture.quickprojectwizard.toolwindow.file.FileWriter
import com.github.cnrture.quickprojectwizard.toolwindow.file.LibraryDependencyFinder
import com.github.cnrture.quickprojectwizard.toolwindow.manager.modulemaker.components.*
import com.github.cnrture.quickprojectwizard.toolwindow.theme.QPWTheme
import com.intellij.openapi.project.Project

@Composable
fun MoveExistingFilesToModuleContent(
    modifier: Modifier = Modifier,
    project: Project,
    fileWriter: FileWriter,
    isAnalyzingState: Boolean,
    analysisResultState: String?,
    selectedSrc: String,
    libraryDependencyFinder: LibraryDependencyFinder,
    onAnalysisResultChange: (String?) -> Unit,
    onAnalyzingChange: (Boolean) -> Unit,
    analyzeLibraries: Boolean,
    onDetectLibrariesLoaded: (List<String>) -> Unit,
    onDetectedModulesLoaded: (List<String>) -> Unit,
    onSelectedModulesLoaded: (List<String>) -> Unit,
    detectedModules: List<String>,
    isMoveFiles: Boolean,
    onMoveFilesChange: (Boolean) -> Unit,
    onAnalyzeLibrariesChange: (Boolean) -> Unit,
    moduleType: String,
    packageName: String,
    onPackageNameChanged: (String) -> Unit,
    moduleNameState: String,
    onModuleNameChanged: (String) -> Unit,
    onModuleTypeSelected: (String) -> Unit,
    existingModules: List<String>,
    selectedModules: List<String>,
    onCheckedModule: (String) -> Unit,
    availableLibraries: List<String>,
    selectedLibraries: List<String>,
    onLibrarySelected: (String) -> Unit,
    libraryGroups: Map<String, List<String>>,
    expandedGroups: Map<String, Boolean>,
    detectedLibraries: List<String>,
    onGroupExpandToggle: (String) -> Unit,
    showFileTreeDialog: Boolean,
    onFileTreeDialogStateChange: () -> Unit,
    onSelectedSrc: (String) -> Unit,
    availablePlugins: List<String>,
    selectedPlugins: List<String>,
    onPluginSelected: (String) -> Unit,
    pluginGroups: Map<String, List<String>>,
    expandedPluginGroups: Map<String, Boolean>,
    onPluginGroupExpandToggle: (String) -> Unit,
) {
    Row(
        modifier = modifier,
    ) {
        AnimatedVisibility(
            modifier = Modifier
                .fillMaxHeight()
                .weight(0.3f),
            visible = showFileTreeDialog,
            enter = slideInHorizontally(initialOffsetX = { -it }),
            exit = slideOutHorizontally(targetOffsetX = { -it }),
        ) {
            FileTreePanel(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.3f),
                project = project,
                onSelectedSrc = { onSelectedSrc(it) }
            )
        }
        val radioOptions = listOf(Constants.ANDROID, Constants.KOTLIN)

        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .weight(0.7f),
            backgroundColor = QPWTheme.colors.black,
            bottomBar = {
                QPWDialogActions(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(QPWTheme.colors.black),
                    onCreateClick = {
                        if (Utils.validateInput(packageName, moduleNameState) && selectedSrc.isNotEmpty()) {
                            Utils.createModule(
                                project = project,
                                fileWriter = fileWriter,
                                selectedSrc = selectedSrc,
                                packageName = packageName,
                                moduleName = moduleNameState,
                                moduleType = moduleType,
                                isMoveFiles = isMoveFiles,
                                analyzeLibraries = analyzeLibraries,
                                libraryDependencyFinder = libraryDependencyFinder,
                                selectedModules = selectedModules,
                                selectedLibraries = selectedLibraries,
                                detectedLibraries = detectedLibraries,
                                selectedPlugins = selectedPlugins,
                            )
                        } else {
                            MessageDialogWrapper("Please fill out required values").show()
                        }
                    },
                    color = QPWTheme.colors.red,
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                RootSelectionContent(
                    modifier = Modifier.fillMaxWidth(),
                    selectedSrc = selectedSrc,
                    showFileTreeDialog = showFileTreeDialog,
                    onChooseRootClick = { onFileTreeDialogStateChange() }
                )

                Spacer(modifier = Modifier.height(16.dp))

                DetectModulesContent(
                    project = project,
                    isAnalyzingState = isAnalyzingState,
                    analysisResultState = analysisResultState,
                    selectedSrc = selectedSrc,
                    libraryDependencyFinder = libraryDependencyFinder,
                    onAnalysisResultChange = onAnalysisResultChange,
                    onAnalyzingChange = onAnalyzingChange,
                    analyzeLibraries = analyzeLibraries,
                    onDetectLibrariesLoaded = onDetectLibrariesLoaded,
                    onDetectedModulesLoaded = onDetectedModulesLoaded,
                    onSelectedModulesLoaded = onSelectedModulesLoaded,
                    detectedModules = detectedModules,
                )

                Spacer(modifier = Modifier.height(16.dp))

                MoveFilesContent(
                    isChecked = isMoveFiles,
                    onCheckedChange = onMoveFilesChange,
                    analyzeLibraries = analyzeLibraries,
                    onAnalyzeLibrariesChange = onAnalyzeLibrariesChange,
                )

                Spacer(modifier = Modifier.height(16.dp))

                ModuleTypeNameContent(
                    moduleTypeSelectionState = moduleType,
                    packageName = packageName,
                    moduleNameState = moduleNameState,
                    radioOptions = radioOptions,
                    onPackageNameChanged = onPackageNameChanged,
                    onModuleTypeSelected = onModuleTypeSelected,
                    onModuleNameChanged = onModuleNameChanged,
                )

                ExistingModulesContent(
                    existingModules = existingModules,
                    selectedDependencies = selectedModules,
                    onCheckedModule = onCheckedModule,
                )

                Spacer(modifier = Modifier.height(16.dp))

                LibrarySelectionContent(
                    availableLibraries = availableLibraries,
                    selectedLibraries = selectedLibraries,
                    onLibrarySelected = onLibrarySelected,
                    libraryGroups = libraryGroups,
                    expandedGroups = expandedGroups,
                    onGroupExpandToggle = onGroupExpandToggle,
                )

                Spacer(modifier = Modifier.height(16.dp))

                PluginSelectionContent(
                    availablePlugins = availablePlugins,
                    selectedPlugins = selectedPlugins,
                    onPluginSelected = onPluginSelected,
                    pluginGroups = pluginGroups,
                    expandedPluginGroups = expandedPluginGroups,
                    onPluginGroupExpandToggle = onPluginGroupExpandToggle,
                )
            }
        }
    }
}