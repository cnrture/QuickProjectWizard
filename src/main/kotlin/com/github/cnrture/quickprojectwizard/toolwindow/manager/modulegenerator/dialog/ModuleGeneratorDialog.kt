package com.github.cnrture.quickprojectwizard.toolwindow.manager.modulegenerator.dialog

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.CreateNewFolder
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.cnrture.quickprojectwizard.analytics.AnalyticsService
import com.github.cnrture.quickprojectwizard.common.Constants
import com.github.cnrture.quickprojectwizard.common.Utils
import com.github.cnrture.quickprojectwizard.common.file.FileWriter
import com.github.cnrture.quickprojectwizard.common.file.LibraryDependencyFinder
import com.github.cnrture.quickprojectwizard.common.rootDirectoryString
import com.github.cnrture.quickprojectwizard.common.rootDirectoryStringDropLast
import com.github.cnrture.quickprojectwizard.components.*
import com.github.cnrture.quickprojectwizard.data.SettingsService
import com.github.cnrture.quickprojectwizard.theme.QPWTheme
import com.github.cnrture.quickprojectwizard.toolwindow.manager.modulegenerator.components.*
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import java.io.File

class ModuleGeneratorDialog(
    private val project: Project,
    startingLocation: VirtualFile?,
) : QPWDialogWrapper(
    width = 800,
    height = 700,
) {
    private val analyticsService = AnalyticsService.getInstance()
    private val settings = ApplicationManager.getApplication().service<SettingsService>()
    private val libraryDependencyFinder = LibraryDependencyFinder()
    private val fileWriter = FileWriter()
    private var selectedSrc = mutableStateOf(Constants.DEFAULT_SRC_VALUE)

    init {
        selectedSrc.value = if (startingLocation != null) {
            File(startingLocation.path).absolutePath
                .removePrefix(project.rootDirectoryStringDropLast())
                .removePrefix(File.separator)
        } else {
            File(project.rootDirectoryString()).absolutePath
                .removePrefix(project.rootDirectoryStringDropLast())
                .removePrefix(File.separator)
        }
    }

    @Composable
    override fun createDesign() {
        var existingModules = listOf<String>()
        val selectedModules = mutableStateListOf<String>()
        val detectedModules = mutableStateListOf<String>()

        val selectedSrc = remember { selectedSrc }

        val availableLibraries = mutableStateListOf<String>()
        val selectedLibraries = mutableStateListOf<String>()
        val libraryGroups = mutableStateMapOf<String, List<String>>()
        val expandedGroups = mutableStateMapOf<String, Boolean>()

        val availablePlugins = mutableStateListOf<String>()
        val selectedPlugins = mutableStateListOf<String>()

        val isMoveFiles = mutableStateOf(false)

        val moduleType = mutableStateOf(settings.state.preferredModuleType)
        val packageName = mutableStateOf(settings.state.defaultPackageName)
        val moduleName = mutableStateOf(Constants.EMPTY)

        val isAnalyzing = mutableStateOf(false)
        val analysisResult = mutableStateOf<String?>(null)

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
        analyticsService.track("view_module_generator_dialog")
        Surface(
            modifier = Modifier.Companion.fillMaxSize(),
            color = QPWTheme.colors.black,
        ) {
            Column(
                modifier = Modifier.Companion
                    .fillMaxSize()
                    .padding(24.dp),
            ) {
                QPWText(
                    modifier = Modifier.Companion.fillMaxWidth(),
                    text = "Feature Generator",
                    style = TextStyle(
                        color = QPWTheme.colors.red,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Companion.Bold,
                        textAlign = TextAlign.Companion.Center,
                    ),
                )
                Spacer(modifier = Modifier.Companion.size(24.dp))
                MoveExistingFilesToModuleContent(
                    modifier = Modifier.Companion
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
                    availablePlugins = availablePlugins,
                    selectedPlugins = selectedPlugins,
                    onPluginSelected = {
                        if (it in selectedPlugins) selectedPlugins.remove(it) else selectedPlugins.add(it)
                    },
                )
            }
        }
    }

    @Composable
    fun MoveExistingFilesToModuleContent(
        modifier: Modifier = Modifier.Companion,
        project: Project,
        fileWriter: FileWriter,
        isAnalyzingState: Boolean,
        analysisResultState: String?,
        selectedSrc: String,
        libraryDependencyFinder: LibraryDependencyFinder,
        onAnalysisResultChange: (String?) -> Unit,
        onAnalyzingChange: (Boolean) -> Unit,
        onDetectedModulesLoaded: (List<String>) -> Unit,
        onSelectedModulesLoaded: (List<String>) -> Unit,
        detectedModules: List<String>,
        isMoveFiles: Boolean,
        onMoveFilesChange: (Boolean) -> Unit,
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
        onGroupExpandToggle: (String) -> Unit,
        availablePlugins: List<String>,
        selectedPlugins: List<String>,
        onPluginSelected: (String) -> Unit,
    ) {
        val radioOptions = listOf(Constants.ANDROID, Constants.KOTLIN)
        Scaffold(
            modifier = modifier,
            backgroundColor = QPWTheme.colors.black,
            bottomBar = {
                Row(
                    modifier = Modifier.Companion
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.Companion.CenterVertically,
                ) {
                    QPWActionCard(
                        title = "Cancel",
                        icon = Icons.Rounded.Cancel,
                        actionColor = QPWTheme.colors.green,
                        type = QPWActionCardType.MEDIUM,
                        onClick = { close(Constants.DEFAULT_EXIT_CODE) },
                    )
                    Spacer(modifier = Modifier.Companion.size(16.dp))
                    QPWActionCard(
                        title = "Create",
                        icon = Icons.Rounded.CreateNewFolder,
                        actionColor = QPWTheme.colors.green,
                        type = QPWActionCardType.MEDIUM,
                        onClick = {
                            if (Utils.validateModuleInput(packageName, moduleNameState) && selectedSrc.isNotEmpty()) {
                                try {
                                    Utils.createModule(
                                        project = project,
                                        fileWriter = fileWriter,
                                        selectedSrc = selectedSrc,
                                        packageName = packageName,
                                        moduleName = moduleNameState,
                                        moduleType = moduleType,
                                        isMoveFiles = isMoveFiles,
                                        libraryDependencyFinder = libraryDependencyFinder,
                                        selectedModules = selectedModules,
                                        selectedLibraries = selectedLibraries,
                                        selectedPlugins = selectedPlugins,
                                        from = "action",
                                    )
                                    close(0)
                                } catch (_: Exception) {
                                }
                            } else {
                                QPWMessageDialog("Please fill out required values").show()
                            }
                        }
                    )
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier.Companion
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState()),
            ) {
                Spacer(modifier = Modifier.Companion.height(16.dp))
                ModuleTypeNameContent(
                    moduleTypeSelectionState = moduleType,
                    packageName = packageName,
                    moduleNameState = moduleNameState,
                    radioOptions = radioOptions,
                    onPackageNameChanged = onPackageNameChanged,
                    onModuleTypeSelected = onModuleTypeSelected,
                    onModuleNameChanged = onModuleNameChanged,
                )

                Spacer(modifier = Modifier.Companion.height(32.dp))

                RootSelectionContent(
                    selectedSrc = selectedSrc,
                    showFileTreeDialog = false,
                    isMoveFiles = isMoveFiles,
                    onMoveFilesChange = onMoveFilesChange,
                    isFileTreeButtonEnabled = false,
                )

                Spacer(modifier = Modifier.Companion.height(16.dp))

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

                Spacer(modifier = Modifier.Companion.height(16.dp))

                Column(
                    modifier = Modifier.Companion.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    PluginSelectionContent(
                        availablePlugins = availablePlugins,
                        selectedPlugins = selectedPlugins,
                        onPluginSelected = onPluginSelected,
                        plugins = availablePlugins,
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
}