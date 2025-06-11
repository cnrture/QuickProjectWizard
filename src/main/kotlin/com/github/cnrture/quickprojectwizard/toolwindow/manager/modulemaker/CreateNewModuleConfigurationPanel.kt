package com.github.cnrture.quickprojectwizard.toolwindow.manager.modulemaker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.cnrture.quickprojectwizard.common.Constants
import com.github.cnrture.quickprojectwizard.common.Utils
import com.github.cnrture.quickprojectwizard.common.file.FileWriter
import com.github.cnrture.quickprojectwizard.common.file.LibraryDependencyFinder
import com.github.cnrture.quickprojectwizard.components.QPWDialogActions
import com.github.cnrture.quickprojectwizard.dialog.MessageDialogWrapper
import com.github.cnrture.quickprojectwizard.theme.QPWTheme
import com.github.cnrture.quickprojectwizard.toolwindow.manager.modulemaker.components.LibrarySelectionContent
import com.github.cnrture.quickprojectwizard.toolwindow.manager.modulemaker.components.ModuleTypeNameContent
import com.github.cnrture.quickprojectwizard.toolwindow.manager.modulemaker.components.PluginSelectionContent
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
    availablePlugins: List<String>,
    selectedPlugins: List<String>,
    onPluginSelected: (String) -> Unit,
    pluginGroups: Map<String, List<String>>,
    expandedPluginGroups: Map<String, Boolean>,
    onPluginGroupExpandToggle: (String) -> Unit,
) {
    val radioOptions = listOf(Constants.ANDROID, Constants.KOTLIN)

    Scaffold(
        modifier = modifier,
        backgroundColor = QPWTheme.colors.black,
        bottomBar = {
            QPWDialogActions(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(QPWTheme.colors.black),
                onCreateClick = {
                    if (Utils.validateModuleInput(packageName, moduleNameState) && selectedSrc.isNotEmpty()) {
                        Utils.createModule(
                            project = project,
                            fileWriter = fileWriter,
                            selectedSrc = selectedSrc,
                            packageName = packageName,
                            moduleName = moduleNameState,
                            moduleType = moduleType,
                            isMoveFiles = false,
                            analyzeLibraries = false,
                            libraryDependencyFinder = libraryDependencyFinder,
                            selectedModules = emptyList(),
                            selectedLibraries = selectedLibraries,
                            detectedLibraries = emptyList(),
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
                .verticalScroll(rememberScrollState()),
        ) {
            ModuleTypeNameContent(
                moduleTypeSelectionState = moduleType,
                packageName = packageName,
                moduleNameState = moduleNameState,
                radioOptions = radioOptions,
                onPackageNameChanged = onPackageNameChanged,
                onModuleTypeSelected = onModuleTypeSelected,
                onModuleNameChanged = onModuleNameChanged,
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