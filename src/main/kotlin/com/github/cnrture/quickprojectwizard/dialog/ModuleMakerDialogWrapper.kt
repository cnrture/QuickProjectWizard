package com.github.cnrture.quickprojectwizard.dialog

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.cnrture.quickprojectwizard.common.Constants
import com.github.cnrture.quickprojectwizard.common.getCurrentlySelectedFile
import com.github.cnrture.quickprojectwizard.common.rootDirectoryString
import com.github.cnrture.quickprojectwizard.common.rootDirectoryStringDropLast
import com.github.cnrture.quickprojectwizard.components.*
import com.github.cnrture.quickprojectwizard.data.SettingsService
import com.github.cnrture.quickprojectwizard.file.*
import com.github.cnrture.quickprojectwizard.theme.QPWTheme
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

class ModuleMakerDialogWrapper(
    private val project: Project,
    private val startingLocation: VirtualFile?,
) : QPWDialogWrapper(
    width = Constants.MODULE_MAKER_WINDOW_WIDTH,
    height = Constants.MODULE_MAKER_WINDOW_HEIGHT,
) {

    private val fileWriter = FileWriter(project)
    private val settings = project.getService(SettingsService::class.java)
    private val libraryDependencyFinder = LibraryDependencyFinder()

    private var existingModules = listOf<String>()
    private var selectedModules = mutableStateListOf<String>()
    private var detectedModules = mutableStateListOf<String>()
    private var detectedLibraries = mutableStateListOf<String>()

    // Library selection
    private var availableLibraries = mutableStateListOf<String>()
    private var selectedLibraries = mutableStateListOf<String>()
    private var libraryGroups = mutableStateMapOf<String, List<String>>()
    private var expandedGroups = mutableStateMapOf<String, Boolean>()

    private val isMoveFiles = mutableStateOf(false)
    private val analyzeLibraries = mutableStateOf(false)

    private val selectedSrc = mutableStateOf(Constants.DEFAULT_SRC_VALUE)
    private val moduleType = mutableStateOf(settings.state.preferredModuleType)
    private val packageName = mutableStateOf(settings.state.defaultPackageName)
    private val moduleName = mutableStateOf(Constants.EMPTY)

    private val isAnalyzing = mutableStateOf(false)
    private val analysisResult = mutableStateOf<String?>(null)

    init {
        loadExistingModules()
        loadAvailableLibraries()

        selectedSrc.value = if (startingLocation != null) {
            File(startingLocation.path).absolutePath.removePrefix(project.rootDirectoryStringDropLast())
                .removePrefix(File.separator)
        } else {
            File(project.rootDirectoryString()).absolutePath.removePrefix(project.rootDirectoryStringDropLast())
                .removePrefix(File.separator)
        }
    }

    private fun loadAvailableLibraries() {
        thread {
            try {
                val projectRoot = File(project.basePath.orEmpty())
                if (projectRoot.exists()) {
                    val libraries = libraryDependencyFinder.parseLibsVersionsToml(projectRoot)
                    val libraryAliases = libraries.map { it.alias }

                    // Group libraries by prefix (like room-, retrofit-, etc.)
                    val grouped = groupLibraries(libraryAliases)

                    SwingUtilities.invokeLater {
                        availableLibraries.clear()
                        availableLibraries.addAll(libraryAliases)

                        libraryGroups.clear()
                        libraryGroups.putAll(grouped)

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

    private fun analyzeSelectedDirectory(directory: File) {
        try {
            if (!directory.exists() || !directory.isDirectory) {
                analysisResult.value = "Directory does not exist or is not a directory"
                return
            }

            isAnalyzing.value = true
            analysisResult.value = null

            thread {
                try {
                    val analyzer = ImportAnalyzer()
                    val projectRoot = project.basePath?.let { File(it) }
                    if (projectRoot != null && projectRoot.exists()) {
                        analyzer.discoverProjectModules(projectRoot)
                    }
                    val findModules = analyzer.analyzeSourceDirectory(directory)

                    if (analyzeLibraries.value && projectRoot != null) {
                        val availableLibraries = libraryDependencyFinder.parseLibsVersionsToml(projectRoot)
                        val usedLibraries = libraryDependencyFinder.findImportedLibraries(directory, availableLibraries)

                        println("Available libraries: $availableLibraries")
                        println("Used libraries: $usedLibraries")

                        SwingUtilities.invokeLater {
                            detectedLibraries.clear()
                            detectedLibraries.addAll(usedLibraries)
                        }
                    }

                    SwingUtilities.invokeLater {
                        detectedModules.clear()
                        detectedModules.addAll(findModules)
                        selectedModules.clear()
                        selectedModules.addAll(findModules)

                        if (detectedModules.isEmpty()) {
                            analysisResult.value = "No dependencies detected"
                        } else {
                            analysisResult.value = "Detected ${detectedModules.size} dependencies"
                        }

                        isAnalyzing.value = false
                    }
                } catch (e: Exception) {
                    SwingUtilities.invokeLater {
                        analysisResult.value = "Error analyzing directory: ${e.message}"
                        isAnalyzing.value = false
                    }
                }
            }

        } catch (e: Exception) {
            analysisResult.value = "Error analyzing directory: ${e.message}"
            isAnalyzing.value = false
        }
    }

    private fun loadExistingModules() {
        val settingsFile = getSettingsGradleFile()
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

                existingModules = modulesSet.toList().sorted()
            } catch (e: Exception) {
                e.printStackTrace()
                existingModules = emptyList()
            }
        }
    }

    @Composable
    override fun createDesign() {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = QPWTheme.colors.gray,
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
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                QPWTheme.colors.blue,
                                QPWTheme.colors.purple,
                            ),
                            tileMode = TileMode.Mirror,
                        ),
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                )
                Spacer(modifier = Modifier.size(24.dp))
                Row(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    FileTreePanel(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(0.3f)
                            .padding(16.dp),
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(horizontal = 16.dp)
                            .background(QPWTheme.colors.white)
                            .width(2.dp)
                    )
                    ConfigurationPanel(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(0.7f),
                    )
                }
            }
        }
    }

    private fun validateInput(): Boolean {
        return packageName.value.isNotEmpty() && moduleName.value.isNotEmpty() && moduleName.value != Constants.DEFAULT_MODULE_NAME
    }

    @Composable
    private fun FileTreePanel(modifier: Modifier = Modifier) {
        QPWFileTree(
            modifier = modifier,
            model = FileTree(root = File(project.rootDirectoryString()).toProjectFile()),
            onClick = { fileTreeNode ->
                val absolutePathAtNode = fileTreeNode.file.absolutePath
                val relativePath = absolutePathAtNode.removePrefix(project.rootDirectoryStringDropLast())
                    .removePrefix(File.separator)
                if (fileTreeNode.file.isDirectory) {
                    selectedSrc.value = relativePath
                }
            }
        )
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    private fun ConfigurationPanel(modifier: Modifier = Modifier) {
        var selectedSrc by remember { selectedSrc }
        val radioOptions = listOf(Constants.ANDROID, Constants.KOTLIN)
        var moduleType by remember { moduleType }
        var packageName by remember { packageName }
        var moduleNameState by remember { moduleName }
        val selectedModules = remember { selectedModules }
        var isMoveFiles by remember { isMoveFiles }
        var analyzeLibraries by remember { analyzeLibraries }
        val isAnalyzingState by remember { isAnalyzing }
        val analysisResultState by remember { analysisResult }

        Scaffold(
            modifier = modifier,
            backgroundColor = QPWTheme.colors.gray,
            bottomBar = {
                QPWDialogActions(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(QPWTheme.colors.gray),
                    onCancelClick = { close(Constants.DEFAULT_EXIT_CODE) },
                    onCreateClick = {
                        if (validateInput()) {
                            createModule()
                        } else {
                            MessageDialogWrapper("Please fill out required values").show()
                        }
                    }
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
                QPWText(
                    text = "Selected root: $selectedSrc",
                    color = QPWTheme.colors.orange,
                    style = TextStyle(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                    ),
                    softWrap = true,
                )

                Spacer(modifier = Modifier.height(16.dp))

                DetectModulesContent(
                    isAnalyzingState = isAnalyzingState,
                    analysisResultState = analysisResultState,
                )

                Spacer(modifier = Modifier.height(16.dp))

                MoveFilesContent(
                    isChecked = isMoveFiles,
                    onCheckedChange = { isMoveFiles = it },
                    analyzeLibraries = analyzeLibraries,
                    onAnalyzeLibrariesChange = { analyzeLibraries = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                ModuleTypeNameContent(
                    moduleTypeSelectionState = moduleType,
                    packageName = packageName,
                    moduleNameState = moduleNameState,
                    radioOptions = radioOptions,
                    onPackageNameChanged = { packageName = it },
                    onModuleTypeSelected = { moduleType = it },
                    onModuleNameChanged = { moduleNameState = it },
                )

                ExistingModulesContent(
                    existingModules = existingModules,
                    selectedDependencies = selectedModules,
                    onCheckedModule = { module ->
                        if (selectedModules.contains(module)) {
                            selectedModules.remove(module)
                        } else {
                            selectedModules.add(module)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                LibrarySelectionContent(
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
                        val currentState = expandedGroups[groupName] ?: false
                        expandedGroups[groupName] = !currentState
                    }
                )
            }
        }
    }

    @Composable
    private fun DetectModulesContent(
        isAnalyzingState: Boolean,
        analysisResultState: String?,
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
                            color = QPWTheme.colors.orange,
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Icon(
                            modifier = Modifier
                                .size(24.dp)
                                .clickable {
                                    val selectedFile = project.getCurrentlySelectedFile(selectedSrc.value)
                                    if (selectedFile.exists()) {
                                        analyzeSelectedDirectory(selectedFile)
                                    }
                                },
                            imageVector = Icons.Rounded.PlayArrow,
                            tint = QPWTheme.colors.orange,
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
                    color = QPWTheme.colors.orange,
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
                                color = QPWTheme.colors.orange,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Icon(
                                imageVector = Icons.Rounded.ExpandMore,
                                contentDescription = null,
                                tint = QPWTheme.colors.orange,
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

    private fun moveFilesToNewModule(sourceDir: File, targetModulePath: String, packageName: String) {
        if (!isMoveFiles.value) return

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
                openNewModule(modulePath, movedFiles)
            }

            MessageDialogWrapper("Moved ${movedFiles.size} files to new module").show()
        } catch (e: Exception) {
            MessageDialogWrapper("Error moving files: ${e.message}").show()
            e.printStackTrace()
        }
    }

    private fun openNewModule(modulePath: File, filesToOpen: List<VirtualFile>) {
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

    private fun getSettingsGradleFile(): File? {
        val settingsGradleKtsPath = File(project.basePath, "settings.gradle.kts")
        val settingsGradlePath = File(project.basePath, "settings.gradle")

        return listOf(settingsGradleKtsPath, settingsGradlePath)
            .firstOrNull { it.exists() }
            ?: run {
                MessageDialogWrapper("Can't find settings.gradle(.kts) file")
                null
            }
    }

    private fun createModule(): List<File> {
        try {
            val settingsGradleFile = getSettingsGradleFile()
            val moduleType = moduleType.value

            val selectedSrcPath = selectedSrc.value
            val sourceFile = getSourceDirectoryFromSelected(selectedSrcPath)

            if (settingsGradleFile != null) {
                val moduleName = moduleName.value.trim()
                if (!moduleName.startsWith(":")) {
                    MessageDialogWrapper("Module name must start with ':' (e.g. ':home' or ':feature:home')").show()
                    return emptyList()
                }

                val moduleNameTrimmed = moduleName.removePrefix(":").replace(":", ".")
                val finalPackageName = "${packageName.value}.${moduleNameTrimmed.split(".").last()}"

                val libraryDependenciesString = if (isMoveFiles.value && analyzeLibraries.value) {
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

                        if (isMoveFiles.value) {
                            moveFilesToNewModule(sourceFile, moduleName, finalPackageName)
                        } else {
                            val modulePath = File(project.basePath, moduleName.replace(":", "/"))
                            ApplicationManager.getApplication().invokeLater {
                                openNewModule(modulePath, emptyList())
                            }
                        }

                        addDependencyToAppModule(moduleName)
                        syncProject()
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
            close(0)
        }
    }

    private fun syncProject() {
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

    private fun getSourceDirectoryFromSelected(selectedPath: String): File {
        if (selectedPath.isBlank()) {
            val projectRoot = File(project.basePath.orEmpty())
            return projectRoot
        }

        val projectBasePath = project.basePath.orEmpty()

        val pathOptions = mutableListOf<File>()

        pathOptions.add(File(projectBasePath, selectedPath))
        pathOptions.add(File(selectedPath))

        if (startingLocation != null) {
            pathOptions.add(File(startingLocation.path))
        }

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

    private fun addDependencyToAppModule(modulePathAsString: String) {
        try {
            val appGradleFile = findAppGradleFile()
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

    private fun findAppGradleFile(): File? {
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
}
