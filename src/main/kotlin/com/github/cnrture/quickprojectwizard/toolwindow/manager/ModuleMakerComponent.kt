package com.github.cnrture.quickprojectwizard.toolwindow.manager

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.cnrture.quickprojectwizard.common.Constants
import com.github.cnrture.quickprojectwizard.common.getCurrentlySelectedFile
import com.github.cnrture.quickprojectwizard.common.rootDirectoryString
import com.github.cnrture.quickprojectwizard.common.rootDirectoryStringDropLast
import com.github.cnrture.quickprojectwizard.toolwindow.components.*
import com.github.cnrture.quickprojectwizard.toolwindow.data.SettingsService
import com.github.cnrture.quickprojectwizard.toolwindow.dialog.MessageDialogWrapper
import com.github.cnrture.quickprojectwizard.toolwindow.file.*
import com.github.cnrture.quickprojectwizard.toolwindow.theme.QPWTheme
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.externalSystem.model.ProjectSystemId
import com.intellij.openapi.externalSystem.service.execution.ProgressExecutionMode
import com.intellij.openapi.externalSystem.util.ExternalSystemUtil
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import java.io.File
import javax.swing.SwingUtilities
import kotlin.concurrent.thread

@Composable
fun ModuleMakerComponent(
    project: Project,
) {
    val fileWriter = FileWriter(project)
    val settings = project.getService(SettingsService::class.java)
    val libraryDependencyFinder = LibraryDependencyFinder()

    var existingModules = listOf<String>()
    val selectedModules = mutableStateListOf<String>()
    val detectedModules = mutableStateListOf<String>()
    val detectedLibraries = mutableStateListOf<String>()

    // Library selection
    val availableLibraries = mutableStateListOf<String>()
    val selectedLibraries = mutableStateListOf<String>()
    val libraryGroups = mutableStateMapOf<String, List<String>>()
    val expandedGroups = mutableStateMapOf<String, Boolean>()

    val isMoveFiles = mutableStateOf(false)
    val analyzeLibraries = mutableStateOf(false)

    val selectedSrc = mutableStateOf(Constants.DEFAULT_SRC_VALUE)
    val moduleType = mutableStateOf(settings.state.preferredModuleType)
    val packageName = mutableStateOf(settings.state.defaultPackageName)
    val moduleName = mutableStateOf(Constants.EMPTY)

    val isAnalyzing = mutableStateOf(false)
    val analysisResult = mutableStateOf<String?>(null)

    var showFileTreeDialog by remember { mutableStateOf(false) }

    loadExistingModules(
        project = project,
        onExistingModulesLoaded = { existingModules = it },
    )
    loadAvailableLibraries(
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

    selectedSrc.value =
        File(project.rootDirectoryString()).absolutePath.removePrefix(project.rootDirectoryStringDropLast())
            .removePrefix(File.separator)

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
                text = "Module Creator",
                style = TextStyle(
                    color = QPWTheme.colors.red,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
            )
            Spacer(modifier = Modifier.size(24.dp))
            Row {
                if (showFileTreeDialog) {
                    FileTreePanel(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(0.3f),
                        project = project,
                        onSelectedSrc = { selectedSrc.value = it }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                }
                ConfigurationPanel(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(0.7f),
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
                    onCheckedModule = { module ->
                        if (selectedModules.contains(module)) {
                            selectedModules.remove(module)
                        } else {
                            selectedModules.add(module)
                        }
                    },
                    availableLibraries = availableLibraries,
                    selectedLibraries = selectedLibraries,
                    onLibrarySelected = { library ->
                        if (selectedLibraries.contains(library)) {
                            selectedLibraries.remove(library)
                        } else {
                            selectedLibraries.add(library)
                        }
                    },
                    libraryGroups = libraryGroups,
                    expandedGroups = expandedGroups,
                    onGroupExpandToggle = { groupName ->
                        expandedGroups[groupName] = expandedGroups[groupName]?.not() ?: true
                    },
                    detectedLibraries = detectedLibraries,
                    showFileTreeDialog = showFileTreeDialog,
                    onFileTreeDialogStateChange = { showFileTreeDialog = !showFileTreeDialog },
                )
            }
        }
    }
}

@Composable
private fun FileTreePanel(
    modifier: Modifier = Modifier,
    project: Project,
    onSelectedSrc: (String) -> Unit = {},
) {
    QPWFileTree(
        modifier = modifier,
        model = FileTree(root = File(project.rootDirectoryString()).toProjectFile()),
        titleColor = QPWTheme.colors.red,
        containerColor = QPWTheme.colors.black,
        onClick = { fileTreeNode ->
            val absolutePathAtNode = fileTreeNode.file.absolutePath
            val relativePath = absolutePathAtNode.removePrefix(project.rootDirectoryStringDropLast())
                .removePrefix(File.separator)
            if (fileTreeNode.file.isDirectory) {
                onSelectedSrc(relativePath)
            }
        }
    )
}

private fun loadAvailableLibraries(
    project: Project,
    libraryDependencyFinder: LibraryDependencyFinder,
    onAvailableLibrariesLoaded: (List<String>) -> Unit,
    onLibraryGroupsLoaded: (Map<String, List<String>>) -> Unit,
    expandedGroups: MutableMap<String, Boolean>,
) {
    thread {
        try {
            val projectRoot = File(project.basePath.orEmpty())
            if (projectRoot.exists()) {
                val libraries = libraryDependencyFinder.parseLibsVersionsToml(projectRoot)
                val libraryAliases = libraries.map { it.alias }

                // Group libraries by prefix (like room-, retrofit-, etc.)
                val grouped = groupLibraries(libraryAliases)

                SwingUtilities.invokeLater {
                    onAvailableLibrariesLoaded(libraryAliases)
                    onLibraryGroupsLoaded(grouped)

                    expandedGroups.clear()
                    grouped.keys.forEach { expandedGroups[it] = false }
                }
            }
        } catch (e: Exception) {
            // Silently fail
        }
    }
}

private fun groupLibraries(libraries: List<String>): Map<String, List<String>> {
    val grouped = mutableMapOf<String, MutableList<String>>()
    val ungrouped = mutableListOf<String>()

    libraries.forEach { library ->
        val parts = library.split("-")
        if (parts.size > 1) {
            val prefix = parts[0]
            // Only group if there are multiple libraries with the same prefix
            val relatedLibs = libraries.filter { it.startsWith("$prefix-") }
            if (relatedLibs.size > 1) {
                grouped.getOrPut(prefix) { mutableListOf() }.add(library)
            } else {
                ungrouped.add(library)
            }
        } else {
            ungrouped.add(library)
        }
    }

    // Add ungrouped libraries as individual items
    if (ungrouped.isNotEmpty()) {
        grouped["Other"] = ungrouped.toMutableList()
    }

    return grouped.mapValues { it.value.sorted() }
}

private fun analyzeSelectedDirectory(
    directory: File,
    project: Project,
    libraryDependencyFinder: LibraryDependencyFinder,
    onAnalysisResultChange: (String?) -> Unit,
    onAnalyzingChange: (Boolean) -> Unit,
    analyzeLibraries: Boolean,
    onDetectLibrariesLoaded: (List<String>) -> Unit,
    onDetectedModulesLoaded: (List<String>) -> Unit,
    onSelectedModulesLoaded: (List<String>) -> Unit,
    detectedModules: List<String>,
) {
    try {
        if (!directory.exists() || !directory.isDirectory) {
            onAnalysisResultChange("Directory does not exist or is not a directory")
            return
        }

        onAnalyzingChange(true)
        onAnalysisResultChange(null)

        thread {
            try {
                val analyzer = ImportAnalyzer()
                val projectRoot = project.basePath?.let { File(it) }
                if (projectRoot != null && projectRoot.exists()) {
                    analyzer.discoverProjectModules(projectRoot)
                }
                val findModules = analyzer.analyzeSourceDirectory(directory)

                if (analyzeLibraries && projectRoot != null) {
                    val availableLibraries = libraryDependencyFinder.parseLibsVersionsToml(projectRoot)
                    val usedLibraries = libraryDependencyFinder.findImportedLibraries(directory, availableLibraries)

                    println("Available libraries: $availableLibraries")
                    println("Used libraries: $usedLibraries")

                    SwingUtilities.invokeLater {
                        onDetectLibrariesLoaded(usedLibraries)
                    }
                }

                SwingUtilities.invokeLater {
                    onDetectedModulesLoaded(findModules)
                    onSelectedModulesLoaded(findModules)

                    if (detectedModules.isEmpty()) {
                        onAnalysisResultChange("No dependencies detected")
                    } else {
                        onAnalysisResultChange("Detected ${detectedModules.size} dependencies")
                    }
                    onAnalyzingChange(false)
                }
            } catch (e: Exception) {
                SwingUtilities.invokeLater {
                    onAnalysisResultChange("Error analyzing directory: ${e.message}")
                    onAnalyzingChange(false)
                }
            }
        }

    } catch (e: Exception) {
        onAnalysisResultChange("Error analyzing directory: ${e.message}")
        onAnalyzingChange(false)
    }
}

private fun loadExistingModules(
    project: Project,
    onExistingModulesLoaded: (List<String>) -> Unit,
) {
    val settingsFile = getSettingsGradleFile(project)
    if (settingsFile != null) {
        try {
            val content = settingsFile.readText()

            val patterns = listOf(
                """include\s*\(\s*["']([^"']+)["']\s*\)""".toRegex(),
                """include\s+['"]([^"']+)["']""".toRegex(),
                """include\s+['"]([^"']+)["'](?:\s*,\s*['"]([^"']+)["'])*""".toRegex(),
                """include\s+['"]([^"']+)["'](?:\s*,\s*\n\s*['"]([^"']+)["'])*""".toRegex()
            )

            val modulesSet = mutableSetOf<String>()

            patterns.forEach { pattern ->
                val matches = pattern.findAll(content)
                matches.forEach { matchResult ->
                    matchResult.groupValues.drop(1).forEach { moduleValue ->
                        if (moduleValue.isNotEmpty()) {
                            modulesSet.add(moduleValue)
                        }
                    }
                }
            }

            val multiLinePattern =
                """include\s*(?:'[^']*'|"[^"]*")\s*(?:,\s*\n\s*(?:'[^']*'|"[^"]*")\s*)*""".toRegex()
            val multiLineMatches = multiLinePattern.findAll(content)

            multiLineMatches.forEach { match ->
                val modulePattern = """['"]([^"']+)["']""".toRegex()
                val moduleMatches = modulePattern.findAll(match.value)
                moduleMatches.forEach { moduleMatch ->
                    val moduleValue = moduleMatch.groupValues[1]
                    if (moduleValue.isNotEmpty()) {
                        modulesSet.add(moduleValue)
                    }
                }
            }
            onExistingModulesLoaded(modulesSet.toList().sorted())
        } catch (e: Exception) {
            e.printStackTrace()
            onExistingModulesLoaded(emptyList())
        }
    }
}

private fun validateInput(packageName: String, moduleName: String): Boolean {
    return packageName.isNotEmpty() && moduleName.isNotEmpty() && moduleName != Constants.DEFAULT_MODULE_NAME
}

@Composable
private fun RootSelectionContent(
    modifier: Modifier = Modifier,
    selectedSrc: String,
    showFileTreeDialog: Boolean,
    onChooseRootClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(2.dp, QPWTheme.colors.white, RoundedCornerShape(8.dp))
            .padding(16.dp),
    ) {
        QPWText(
            text = "Selected: $selectedSrc",
            color = QPWTheme.colors.red,
            softWrap = true,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
            ),
        )
        Spacer(modifier = Modifier.size(4.dp))
        Text(
            text = "Choose the root directory for your new module.",
            color = QPWTheme.colors.lightGray,
        )
        Spacer(modifier = Modifier.size(8.dp))
        QPWButton(
            text = if (showFileTreeDialog) "Close File Tree" else "Open File Tree",
            backgroundColor = QPWTheme.colors.red,
            onClick = onChooseRootClick,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ConfigurationPanel(
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
                    if (validateInput(packageName, moduleNameState) && selectedSrc.isNotEmpty()) {
                        createModule(
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
        }
    }
}

@Composable
private fun DetectModulesContent(
    project: Project,
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
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = 2.dp,
                color = QPWTheme.colors.white,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            QPWText(
                text = "Detect Modules",
                color = QPWTheme.colors.white,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                ),
            )
            Spacer(modifier = Modifier.size(8.dp))
            Box {
                if (isAnalyzingState) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = QPWTheme.colors.red,
                        strokeWidth = 2.dp,
                    )
                } else {
                    Icon(
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                val selectedFile = project.getCurrentlySelectedFile(selectedSrc)
                                if (selectedFile.exists()) {
                                    analyzeSelectedDirectory(
                                        directory = selectedFile,
                                        project = project,
                                        libraryDependencyFinder = libraryDependencyFinder,
                                        onAnalysisResultChange = onAnalysisResultChange,
                                        onAnalyzingChange = onAnalyzingChange,
                                        analyzeLibraries = analyzeLibraries,
                                        onDetectLibrariesLoaded = onDetectLibrariesLoaded,
                                        onDetectedModulesLoaded = onDetectedModulesLoaded,
                                        onSelectedModulesLoaded = onSelectedModulesLoaded,
                                        detectedModules = detectedModules,
                                    )
                                }
                            },
                        imageVector = Icons.Rounded.PlayArrow,
                        tint = QPWTheme.colors.red,
                        contentDescription = null,
                    )
                }
            }
        }
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = "These modules will be added to the new module's build.gradle file.",
            color = QPWTheme.colors.lightGray,
        )
        Spacer(modifier = Modifier.size(8.dp))
        analysisResultState?.let { result ->
            Text(
                text = result,
                color = QPWTheme.colors.red,
            )
        }
    }
}

@Composable
private fun MoveFilesContent(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    analyzeLibraries: Boolean = false,
    onAnalyzeLibrariesChange: (Boolean) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = 2.dp,
                color = QPWTheme.colors.white,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(8.dp),
    ) {
        QPWCheckbox(
            label = "Move selected files to new module",
            checked = isChecked,
            color = QPWTheme.colors.red,
            onCheckedChange = { onCheckedChange(it) },
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp),
            text = "This will move files from the selected directory to the new module.",
            color = QPWTheme.colors.lightGray,
        )

        if (isChecked) {
            Spacer(modifier = Modifier.height(8.dp))
            QPWCheckbox(
                label = "Analyze and include library dependencies",
                checked = analyzeLibraries,
                color = QPWTheme.colors.red,
                onCheckedChange = { onAnalyzeLibrariesChange(it) },
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp),
                text = "Parse libs.versions.toml and include relevant library dependencies.",
                color = QPWTheme.colors.lightGray,
            )
        }
    }
}

@Composable
private fun ModuleTypeNameContent(
    moduleTypeSelectionState: String,
    packageName: String,
    moduleNameState: String,
    radioOptions: List<String>,
    onPackageNameChanged: (String) -> Unit,
    onModuleTypeSelected: (String) -> Unit,
    onModuleNameChanged: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = 2.dp,
                color = QPWTheme.colors.white,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            QPWText(
                text = "Module Type",
                color = QPWTheme.colors.white,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                ),
            )
            Spacer(modifier = Modifier.size(16.dp))
            Column {
                radioOptions.forEach { text ->
                    QPWRadioButton(
                        text = text,
                        selected = text == moduleTypeSelectionState,
                        isBackgroundEnable = true,
                        color = QPWTheme.colors.red,
                        onClick = { onModuleTypeSelected(text) },
                    )
                    if (text != radioOptions.last()) {
                        Spacer(modifier = Modifier.size(8.dp))
                    }
                }
            }
        }
        Spacer(modifier = Modifier.size(24.dp))
        Column {
            QPWTextField(
                modifier = Modifier.fillMaxWidth(),
                label = "Package Name",
                placeholder = "Package Name",
                value = packageName,
                onValueChange = { onPackageNameChanged(it) },
            )
            Spacer(modifier = Modifier.size(16.dp))
            QPWTextField(
                modifier = Modifier.fillMaxWidth(),
                label = "Module Name",
                placeholder = Constants.DEFAULT_MODULE_NAME,
                value = moduleNameState,
                onValueChange = { onModuleNameChanged(it) },
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ExistingModulesContent(
    existingModules: List<String>,
    selectedDependencies: List<String>,
    onCheckedModule: (String) -> Unit,
) {
    if (existingModules.isNotEmpty()) {
        Spacer(modifier = Modifier.height(16.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(
                    shape = RoundedCornerShape(8.dp)
                )
                .border(
                    width = 2.dp,
                    color = QPWTheme.colors.white,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(16.dp)
        ) {
            Text(
                text = "Module Dependencies",
                color = QPWTheme.colors.white,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = "Select modules that your new module will depend on:",
                color = QPWTheme.colors.lightGray,
                fontSize = 14.sp,
            )
            Divider(
                color = QPWTheme.colors.lightGray,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                existingModules.forEachIndexed { index, module ->
                    val isChecked = module in selectedDependencies
                    QPWCheckbox(
                        checked = isChecked,
                        label = module,
                        isBackgroundEnable = true,
                        color = QPWTheme.colors.red,
                        onCheckedChange = { onCheckedModule(module) },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LibrarySelectionContent(
    availableLibraries: List<String>,
    selectedLibraries: List<String>,
    onLibrarySelected: (String) -> Unit,
    libraryGroups: Map<String, List<String>>,
    expandedGroups: Map<String, Boolean>,
    onGroupExpandToggle: (String) -> Unit,
) {
    if (availableLibraries.isNotEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(
                    shape = RoundedCornerShape(8.dp)
                )
                .border(
                    width = 2.dp,
                    color = QPWTheme.colors.white,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(16.dp)
        ) {
            Text(
                text = "Library Dependencies",
                color = QPWTheme.colors.white,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = "Select libraries that your new module will depend on:",
                color = QPWTheme.colors.lightGray,
                fontSize = 14.sp,
            )
            Divider(
                color = QPWTheme.colors.lightGray,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                libraryGroups.forEach { (groupName, groupLibraries) ->
                    val isExpanded = expandedGroups[groupName] ?: false
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onGroupExpandToggle(groupName) }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = groupName,
                            color = QPWTheme.colors.red,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Icon(
                            imageVector = Icons.Rounded.ExpandMore,
                            contentDescription = null,
                            tint = QPWTheme.colors.red,
                            modifier = Modifier
                                .size(24.dp)
                                .rotate(if (isExpanded) 180f else 0f)
                        )
                    }
                    if (isExpanded) {
                        FlowRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            groupLibraries.forEach { library ->
                                val isChecked = library in selectedLibraries
                                QPWCheckbox(
                                    checked = isChecked,
                                    label = library,
                                    isBackgroundEnable = true,
                                    color = QPWTheme.colors.red,
                                    onCheckedChange = { onLibrarySelected(library) },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun moveFilesToNewModule(
    project: Project,
    sourceDir: File,
    targetModulePath: String,
    packageName: String,
    isMoveFiles: Boolean,
) {
    if (!isMoveFiles) return

    try {
        if (!sourceDir.exists() || !sourceDir.isDirectory) {
            MessageDialogWrapper("Source directory does not exist or is not a directory").show()
            return
        }

        val modulePath = File(project.basePath, targetModulePath.replace(":", "/"))
        val targetSrcDir = File(modulePath, "src/main/kotlin")
        targetSrcDir.mkdirs()

        val packagePath = packageName.split(".").joinToString(File.separator)
        val targetPackageDir = File(targetSrcDir, packagePath)
        targetPackageDir.mkdirs()

        val sourceFiles = sourceDir.walkTopDown()
            .filter { it.isFile && (it.extension == "kt" || it.extension == "java") }
            .toList()

        if (sourceFiles.isEmpty()) {
            MessageDialogWrapper("No source files found to move in ${sourceDir.absolutePath}").show()
            return
        }

        val movedFiles = mutableListOf<VirtualFile>()
        val packageMappings = mutableMapOf<String, String>()
        val filePathMappings = mutableMapOf<String, File>()

        sourceFiles.forEach { sourceFile ->
            try {
                val relativePath = getRelativePath(sourceFile, sourceDir)

                val targetFile = File(targetPackageDir, relativePath)
                targetFile.parentFile.mkdirs()

                sourceFile.copyTo(targetFile, overwrite = true)

                val relativeDir = targetFile.parentFile.absolutePath
                    .removePrefix(targetPackageDir.absolutePath)
                    .trim(File.separatorChar)

                val subPackage = if (relativeDir.isNotEmpty()) {
                    "." + relativeDir.replace(File.separator, ".")
                } else {
                    Constants.EMPTY
                }

                val fullPackageName = packageName + subPackage

                val content = targetFile.readText()
                val packagePattern = """package\s+([a-zA-Z0-9_.]+)""".toRegex()
                val packageMatch = packagePattern.find(content)
                val originalPackage = packageMatch?.groupValues?.get(1).orEmpty()

                if (originalPackage.isNotEmpty()) {
                    packageMappings[originalPackage] = fullPackageName
                }

                val updatedContent = packagePattern.replace(content, "package $fullPackageName")

                if (content != updatedContent) {
                    targetFile.writeText(updatedContent)
                }

                filePathMappings[sourceFile.absolutePath] = targetFile

                VfsUtil.findFileByIoFile(targetFile, true)?.let { vFile ->
                    movedFiles.add(vFile)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        filePathMappings.values.forEach { targetFile ->
            try {
                val content = targetFile.readText()
                var updatedContent = content

                packageMappings.forEach { (oldPackage, newPackage) ->
                    val importPattern = """import\s+$oldPackage\.([a-zA-Z0-9_.]+)""".toRegex()
                    updatedContent = updatedContent.replace(importPattern) { matchResult ->
                        val subpath = matchResult.groupValues[1]
                        "import $newPackage.$subpath"
                    }
                }

                if (content != updatedContent) {
                    targetFile.writeText(updatedContent)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        val projectDir = File(project.basePath.orEmpty())
        VfsUtil.markDirtyAndRefresh(false, true, true, VfsUtil.findFileByIoFile(projectDir, true))

        ApplicationManager.getApplication().invokeLater {
            openNewModule(project, modulePath, movedFiles)
        }

        MessageDialogWrapper("Moved ${movedFiles.size} files to new module").show()
    } catch (e: Exception) {
        MessageDialogWrapper("Error moving files: ${e.message}").show()
        e.printStackTrace()
    }
}

private fun openNewModule(
    project: Project,
    modulePath: File,
    filesToOpen: List<VirtualFile>,
) {
    try {
        val moduleRootDir = VfsUtil.findFileByIoFile(modulePath, true)
        if (moduleRootDir != null) {
            val buildGradleFile = moduleRootDir.findChild("build.gradle")
                ?: moduleRootDir.findChild("build.gradle.kts")

            if (buildGradleFile != null) {
                FileEditorManager.getInstance(project).openFile(buildGradleFile, true)
            }

            filesToOpen.take(5).forEach { file ->
                FileEditorManager.getInstance(project).openFile(file, true)
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private fun getSettingsGradleFile(
    project: Project,
): File? {
    val settingsGradleKtsPath = File(project.basePath, "settings.gradle.kts")
    val settingsGradlePath = File(project.basePath, "settings.gradle")

    return listOf(settingsGradleKtsPath, settingsGradlePath)
        .firstOrNull { it.exists() }
        ?: run {
            MessageDialogWrapper("Can't find settings.gradle(.kts) file")
            null
        }
}

private fun createModule(
    project: Project,
    fileWriter: FileWriter,
    selectedSrc: String,
    packageName: String,
    moduleName: String,
    moduleType: String,
    isMoveFiles: Boolean,
    analyzeLibraries: Boolean,
    libraryDependencyFinder: LibraryDependencyFinder,
    detectedLibraries: List<String>,
    selectedLibraries: List<String>,
    selectedModules: List<String>,
): List<File> {
    try {
        val settingsGradleFile = getSettingsGradleFile(project)
        val moduleType = moduleType

        val selectedSrcPath = selectedSrc
        val sourceFile = getSourceDirectoryFromSelected(project, selectedSrcPath)

        if (settingsGradleFile != null) {
            val moduleName = moduleName.trim()
            if (!moduleName.startsWith(":")) {
                MessageDialogWrapper("Module name must start with ':' (e.g. ':home' or ':feature:home')").show()
                return emptyList()
            }

            val moduleNameTrimmed = moduleName.removePrefix(":").replace(":", ".")
            val finalPackageName = "${packageName}.${moduleNameTrimmed.split(".").last()}"

            val libraryDependenciesString = if (isMoveFiles && analyzeLibraries) {
                libraryDependencyFinder.formatLibraryDependencies(detectedLibraries)
            } else {
                Constants.EMPTY
            }

            val manualLibraryDependenciesString =
                libraryDependencyFinder.formatLibraryDependencies(selectedLibraries)

            val combinedLibraryDependencies = listOf(libraryDependenciesString, manualLibraryDependenciesString)
                .filter { it.isNotEmpty() }
                .joinToString("\n")

            val filesCreated = fileWriter.createModule(
                packageName = finalPackageName,
                settingsGradleFile = settingsGradleFile,
                modulePathAsString = moduleName,
                moduleType = moduleType,
                showErrorDialog = { MessageDialogWrapper(it).show() },
                showSuccessDialog = {
                    MessageDialogWrapper("Module '$moduleName' created successfully").show()

                    val projectDir = File(project.basePath.orEmpty())
                    VfsUtil.markDirtyAndRefresh(false, true, true, VfsUtil.findFileByIoFile(projectDir, true))

                    if (isMoveFiles) {
                        moveFilesToNewModule(
                            project = project,
                            sourceDir = sourceFile,
                            targetModulePath = moduleName,
                            packageName = finalPackageName,
                            isMoveFiles = isMoveFiles
                        )
                    } else {
                        val modulePath = File(project.basePath, moduleName.replace(":", "/"))
                        ApplicationManager.getApplication().invokeLater {
                            openNewModule(project, modulePath, emptyList())
                        }
                    }

                    addDependencyToAppModule(
                        project = project,
                        modulePathAsString = moduleName,
                    )
                    syncProject(project)
                },
                workingDirectory = File(project.basePath.orEmpty()),
                dependencies = selectedModules,
                libraryDependencies = combinedLibraryDependencies
            )
            return filesCreated
        } else {
            MessageDialogWrapper("Couldn't find settings.gradle(.kts) file").show()
            return emptyList()
        }
    } catch (e: Exception) {
        MessageDialogWrapper("Error: ${e.message}").show()
        return emptyList()
    } finally {

    }
}

private fun syncProject(project: Project) {
    val projectDir = File(project.basePath.orEmpty())
    VfsUtil.markDirtyAndRefresh(false, true, true, VfsUtil.findFileByIoFile(projectDir, true))
    ExternalSystemUtil.refreshProject(
        project,
        ProjectSystemId("GRADLE"),
        project.rootDirectoryString(),
        false,
        ProgressExecutionMode.IN_BACKGROUND_ASYNC
    )
}

private fun getRelativePath(sourceFile: File, sourceDir: File): String {
    val sourceFilePath = sourceFile.absolutePath
    val sourceDirPath = sourceDir.absolutePath

    if (sourceFilePath.startsWith(sourceDirPath)) {
        val relPath = sourceFilePath.substring(sourceDirPath.length)
        return if (relPath.startsWith(File.separator)) relPath.substring(1) else relPath
    }

    return sourceFile.name
}

private fun getSourceDirectoryFromSelected(
    project: Project,
    selectedPath: String,
): File {
    if (selectedPath.isBlank()) {
        val projectRoot = File(project.basePath.orEmpty())
        return projectRoot
    }

    val projectBasePath = project.basePath.orEmpty()

    val pathOptions = mutableListOf<File>()

    pathOptions.add(File(projectBasePath, selectedPath))
    pathOptions.add(File(selectedPath))

    val pathParts = selectedPath.split(File.separator)
    if (pathParts.size > 1) {
        val reducedPath = pathParts.drop(1).joinToString(File.separator)
        pathOptions.add(File(projectBasePath, reducedPath))
    }

    for (option in pathOptions) {
        if (option.exists() && option.isDirectory) {
            return option
        }
    }
    return pathOptions.first()
}

private fun addDependencyToAppModule(
    project: Project,
    modulePathAsString: String,
) {
    try {
        val appGradleFile = findAppGradleFile(project)
        if (appGradleFile == null || !appGradleFile.exists()) {
            return
        }

        val content = appGradleFile.readText()

        val dependenciesPattern = """dependencies\s*\{([^}]*)}""".toRegex(RegexOption.DOT_MATCHES_ALL)
        val dependenciesMatch = dependenciesPattern.find(content)

        if (dependenciesMatch != null) {
            val dependenciesBlock = dependenciesMatch.groupValues[1]
            val moduleName = modulePathAsString.removePrefix(":").replace(":", ".")
            val dependencyLine = "    implementation(projects.$moduleName)"

            if (dependenciesBlock.contains(dependencyLine)) {
                return
            }

            val newDependenciesBlock = "$dependenciesBlock\n$dependencyLine\n"
            val newContent =
                content.replace(dependenciesMatch.groupValues[0], "dependencies {$newDependenciesBlock}")

            appGradleFile.writeText(newContent)
        }

    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private fun findAppGradleFile(project: Project): File? {
    val projectBasePath = project.basePath ?: return null

    val possibleAppLocations = listOf(
        "app/build.gradle",
        "app/build.gradle.kts",
        "mobile/build.gradle",
        "mobile/build.gradle.kts",
        "androidApp/build.gradle",
        "androidApp/build.gradle.kts"
    )

    for (location in possibleAppLocations) {
        val file = File(projectBasePath, location)
        if (file.exists()) {
            return file
        }
    }

    val rootDir = File(projectBasePath)
    val appDir = rootDir.listFiles()?.firstOrNull {
        it.isDirectory && (it.name == "app" || it.name == "mobile" || it.name == "androidApp")
    }

    if (appDir != null) {
        val gradleFile = File(appDir, "build.gradle")
        val ktsFile = File(appDir, "build.gradle.kts")

        if (gradleFile.exists()) return gradleFile
        if (ktsFile.exists()) return ktsFile
    }

    return null
}