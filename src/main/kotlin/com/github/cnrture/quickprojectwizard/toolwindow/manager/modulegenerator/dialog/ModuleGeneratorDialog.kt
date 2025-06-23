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
import com.github.cnrture.quickprojectwizard.common.Constants
import com.github.cnrture.quickprojectwizard.common.Utils
import com.github.cnrture.quickprojectwizard.common.file.FileWriter
import com.github.cnrture.quickprojectwizard.common.file.LibraryDependencyFinder
import com.github.cnrture.quickprojectwizard.common.rootDirectoryString
import com.github.cnrture.quickprojectwizard.common.rootDirectoryStringDropLast
import com.github.cnrture.quickprojectwizard.components.*
import com.github.cnrture.quickprojectwizard.data.PluginListItem
import com.github.cnrture.quickprojectwizard.service.AnalyticsService
import com.github.cnrture.quickprojectwizard.service.SettingsService
import com.github.cnrture.quickprojectwizard.theme.QPWTheme
import com.github.cnrture.quickprojectwizard.toolwindow.manager.modulegenerator.components.*
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
    private val settings = SettingsService.getInstance()
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

        val availablePlugins = mutableStateListOf<PluginListItem>()

        val moduleType = mutableStateOf(settings.state.preferredModuleType)
        val packageName = mutableStateOf(settings.state.defaultPackageName)
        val moduleName = mutableStateOf(Constants.EMPTY)
        val name = mutableStateOf(Constants.EMPTY)

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
            onAvailablePluginsLoaded = { list ->
                availablePlugins.clear()
                availablePlugins.addAll(list.map { PluginListItem(it) })
            },
        )
        analyticsService.track("view_module_generator_dialog")
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = QPWTheme.colors.black,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
            ) {
                QPWText(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Module Generator",
                    style = TextStyle(
                        color = QPWTheme.colors.green,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    ),
                )
                Spacer(modifier = Modifier.size(24.dp))

                val state = ModuleGeneratorState(
                    project = project,
                    fileWriter = fileWriter,
                    isAnalyzingState = isAnalyzing.value,
                    analysisResultState = analysisResult.value,
                    selectedSrc = selectedSrc.value,
                    libraryDependencyFinder = libraryDependencyFinder,
                    detectedModules = detectedModules,
                    moduleType = moduleType.value,
                    packageName = packageName.value,
                    nameState = name.value,
                    moduleNameState = moduleName.value,
                    existingModules = existingModules,
                    selectedModules = selectedModules,
                    availableLibraries = availableLibraries,
                    selectedLibraries = selectedLibraries,
                    libraryGroups = libraryGroups,
                    expandedGroups = expandedGroups,
                    availablePlugins = availablePlugins,
                )

                val callbacks = ModuleGeneratorCallbacks(
                    onAnalysisResultChange = { analysisResult.value = it },
                    onAnalyzingChange = { isAnalyzing.value = it },
                    onDetectedModulesLoaded = { detectedModules.clear(); detectedModules.addAll(it) },
                    onSelectedModulesLoaded = { selectedModules.clear(); selectedModules.addAll(it) },
                    onNameChanged = { name.value = it },
                    onPackageNameChanged = { packageName.value = it },
                    onModuleNameChanged = { moduleName.value = it },
                    onModuleTypeSelected = { moduleType.value = it },
                    onCheckedModule = {
                        if (it in selectedModules) selectedModules.remove(it) else selectedModules.add(it)
                    },
                    onLibrarySelected = {
                        if (it in selectedLibraries) selectedLibraries.remove(it) else selectedLibraries.add(it)
                    },
                    onGroupExpandToggle = { expandedGroups[it] = !(expandedGroups[it] ?: false) },
                    onPluginSelected = { plugin ->
                        val index = availablePlugins.indexOfFirst { it.name == plugin.name }
                        if (index != -1) {
                            availablePlugins[index] = plugin.copy(isSelected = !plugin.isSelected)
                        }
                    },
                )

                MoveExistingFilesToModuleContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    state = state,
                    callbacks = callbacks,
                )
            }
        }
    }

    @Composable
    fun MoveExistingFilesToModuleContent(
        modifier: Modifier = Modifier,
        state: ModuleGeneratorState,
        callbacks: ModuleGeneratorCallbacks,
    ) {
        val radioOptions = listOf(Constants.ANDROID, Constants.KOTLIN)
        Scaffold(
            modifier = modifier,
            backgroundColor = QPWTheme.colors.black,
            bottomBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    QPWActionCard(
                        title = "Cancel",
                        icon = Icons.Rounded.Cancel,
                        actionColor = QPWTheme.colors.green,
                        type = QPWActionCardType.MEDIUM,
                        onClick = { close(Constants.DEFAULT_EXIT_CODE) },
                    )
                    Spacer(modifier = Modifier.size(16.dp))
                    QPWActionCard(
                        title = "Create",
                        icon = Icons.Rounded.CreateNewFolder,
                        actionColor = QPWTheme.colors.green,
                        type = QPWActionCardType.MEDIUM,
                        onClick = {
                            if (Utils.validateModuleInput(
                                    state.packageName,
                                    state.moduleNameState
                                ) && state.selectedSrc.isNotEmpty()
                            ) {
                                try {
                                    Utils.createModule(
                                        project = state.project,
                                        fileWriter = state.fileWriter,
                                        selectedSrc = state.selectedSrc,
                                        packageName = state.packageName,
                                        moduleName = state.moduleNameState,
                                        name = state.nameState,
                                        moduleType = state.moduleType,
                                        isMoveFiles = true,
                                        libraryDependencyFinder = state.libraryDependencyFinder,
                                        selectedModules = state.selectedModules,
                                        selectedLibraries = state.selectedLibraries,
                                        selectedPlugins = state.availablePlugins,
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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState()),
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                ModuleTypeNameContent(
                    moduleTypeSelectionState = state.moduleType,
                    packageName = state.packageName,
                    moduleNameState = state.moduleNameState,
                    radioOptions = radioOptions,
                    onPackageNameChanged = callbacks.onPackageNameChanged,
                    onModuleTypeSelected = callbacks.onModuleTypeSelected,
                    onModuleNameChanged = callbacks.onModuleNameChanged,
                )

                Spacer(modifier = Modifier.height(32.dp))

                RootSelectionContent(
                    selectedSrc = state.selectedSrc,
                    showFileTreeDialog = false,
                    isFileTreeButtonEnabled = false,
                )

                Spacer(modifier = Modifier.height(16.dp))

                DetectedModulesContent(
                    project = state.project,
                    isAnalyzingState = state.isAnalyzingState,
                    analysisResultState = state.analysisResultState,
                    selectedSrc = state.selectedSrc,
                    onAnalysisResultChange = callbacks.onAnalysisResultChange,
                    onAnalyzingChange = callbacks.onAnalyzingChange,
                    onDetectedModulesLoaded = callbacks.onDetectedModulesLoaded,
                    onSelectedModulesLoaded = callbacks.onSelectedModulesLoaded,
                    detectedModules = state.detectedModules,
                    existingModules = state.existingModules,
                    selectedModules = state.selectedModules,
                    onCheckedModule = callbacks.onCheckedModule,
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    PluginSelectionContent(
                        availablePlugins = state.availablePlugins,
                        onPluginSelected = callbacks.onPluginSelected,
                    )
                    LibrarySelectionContent(
                        availableLibraries = state.availableLibraries,
                        selectedLibraries = state.selectedLibraries,
                        onLibrarySelected = callbacks.onLibrarySelected,
                        libraryGroups = state.libraryGroups,
                        expandedGroups = state.expandedGroups,
                        onGroupExpandToggle = callbacks.onGroupExpandToggle,
                    )
                }
            }
        }
    }

    data class ModuleGeneratorState(
        val project: Project,
        val fileWriter: FileWriter,
        val isAnalyzingState: Boolean,
        val analysisResultState: String?,
        val selectedSrc: String,
        val libraryDependencyFinder: LibraryDependencyFinder,
        val detectedModules: List<String>,
        val moduleType: String,
        val packageName: String,
        val nameState: String,
        val moduleNameState: String,
        val existingModules: List<String>,
        val selectedModules: List<String>,
        val availableLibraries: List<String>,
        val selectedLibraries: List<String>,
        val libraryGroups: Map<String, List<String>>,
        val expandedGroups: Map<String, Boolean>,
        val availablePlugins: List<PluginListItem>,
    )

    data class ModuleGeneratorCallbacks(
        val onAnalysisResultChange: (String?) -> Unit,
        val onAnalyzingChange: (Boolean) -> Unit,
        val onDetectedModulesLoaded: (List<String>) -> Unit,
        val onSelectedModulesLoaded: (List<String>) -> Unit,
        val onNameChanged: (String) -> Unit,
        val onPackageNameChanged: (String) -> Unit,
        val onModuleNameChanged: (String) -> Unit,
        val onModuleTypeSelected: (String) -> Unit,
        val onCheckedModule: (String) -> Unit,
        val onLibrarySelected: (String) -> Unit,
        val onGroupExpandToggle: (String) -> Unit,
        val onPluginSelected: (PluginListItem) -> Unit,
    )
}
