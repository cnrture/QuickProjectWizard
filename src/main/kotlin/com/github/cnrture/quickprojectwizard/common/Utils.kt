package com.github.cnrture.quickprojectwizard.common

import com.github.cnrture.quickprojectwizard.dialog.MessageDialogWrapper
import com.github.cnrture.quickprojectwizard.toolwindow.file.FileWriter
import com.github.cnrture.quickprojectwizard.toolwindow.file.ImportAnalyzer
import com.github.cnrture.quickprojectwizard.toolwindow.file.LibraryDependencyFinder
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
import kotlin.io.readText
import kotlin.io.writeText

object Utils {
    fun validateFeatureInput(featureName: String, selectedSrc: String): Boolean {
        return featureName.isNotEmpty() && selectedSrc != Constants.DEFAULT_SRC_VALUE
    }

    fun createFeature(project: Project, selectedSrc: String, featureName: String, fileWriter: FileWriter) {
        try {
            val projectRoot = project.rootDirectoryString()

            val cleanSelectedPath = selectedSrc.let { path ->
                val projectName = projectRoot.split(File.separator).last()
                if (path.startsWith(projectName + File.separator)) {
                    path.substring(projectName.length + 1)
                } else {
                    path
                }
            }

            val packagePath = cleanSelectedPath
                .replace(
                    Regex("^.*?(/src/main/java/|/src/main/kotlin/)"),
                    Constants.EMPTY,
                )
                .replace("/", ".")

            fileWriter.createFeatureFiles(
                file = File(projectRoot, cleanSelectedPath),
                featureName = featureName,
                packageName = packagePath.plus(".${featureName.lowercase()}"),
                showErrorDialog = { MessageDialogWrapper("Error: $it").show() },
                showSuccessDialog = {
                    MessageDialogWrapper("Success").show()
                    val currentlySelectedFile = project.getCurrentlySelectedFile(selectedSrc)
                    listOf(currentlySelectedFile).refreshFileSystem()
                }
            )
        } catch (e: Exception) {
            MessageDialogWrapper("Error: ${e.message}").show()
        } finally {

        }
    }

    fun validateModuleInput(packageName: String, moduleName: String): Boolean {
        return packageName.isNotEmpty() && moduleName.isNotEmpty() && moduleName != Constants.DEFAULT_MODULE_NAME
    }

    fun createModule(
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
        selectedPlugins: List<String> = emptyList(),
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

                val pluginDependenciesString = libraryDependencyFinder.formatPluginDependencies(selectedPlugins)

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
                    libraryDependencies = combinedLibraryDependencies,
                    pluginDependencies = pluginDependenciesString,
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

    fun moveFilesToNewModule(
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

    fun openNewModule(
        project: Project,
        modulePath: File,
        filesToOpen: List<VirtualFile>,
    ) {
        try {
            val moduleRootDir = VfsUtil.findFileByIoFile(modulePath, true)
            if (moduleRootDir != null) {
                val buildGradleFile = moduleRootDir.findChild("build.gradle.kts")
                    ?: moduleRootDir.findChild("build.gradle")

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

    fun getSettingsGradleFile(
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

    fun syncProject(project: Project) {
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

    fun getRelativePath(sourceFile: File, sourceDir: File): String {
        val sourceFilePath = sourceFile.absolutePath
        val sourceDirPath = sourceDir.absolutePath

        if (sourceFilePath.startsWith(sourceDirPath)) {
            val relPath = sourceFilePath.substring(sourceDirPath.length)
            return if (relPath.startsWith(File.separator)) relPath.substring(1) else relPath
        }

        return sourceFile.name
    }

    fun getSourceDirectoryFromSelected(
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

    fun addDependencyToAppModule(
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

    fun findAppGradleFile(project: Project): File? {
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

    fun loadExistingModules(
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

    fun loadAvailableLibraries(
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

    fun groupLibraries(libraries: List<String>): Map<String, List<String>> {
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

    fun loadAvailablePlugins(
        project: Project,
        libraryDependencyFinder: LibraryDependencyFinder,
        onAvailablePluginsLoaded: (List<String>) -> Unit,
        onPluginGroupsLoaded: (Map<String, List<String>>) -> Unit,
        expandedPluginGroups: MutableMap<String, Boolean>,
    ) {
        thread {
            try {
                val projectRoot = File(project.basePath.orEmpty())
                if (projectRoot.exists()) {
                    val plugins = libraryDependencyFinder.parsePluginsFromToml(projectRoot)
                    val pluginAliases = plugins.map { it.alias }

                    // Group plugins by prefix (like kotlin-, compose-, etc.)
                    val grouped = groupPlugins(pluginAliases)

                    SwingUtilities.invokeLater {
                        onAvailablePluginsLoaded(pluginAliases)
                        onPluginGroupsLoaded(grouped)

                        expandedPluginGroups.clear()
                        grouped.keys.forEach { expandedPluginGroups[it] = false }
                    }
                }
            } catch (e: Exception) {
                // Silently fail
            }
        }
    }

    fun groupPlugins(plugins: List<String>): Map<String, List<String>> {
        val grouped = mutableMapOf<String, MutableList<String>>()
        val ungrouped = mutableListOf<String>()

        plugins.forEach { plugin ->
            val parts = plugin.split("-")
            if (parts.size > 1) {
                val prefix = parts[0]
                // Only group if there are multiple plugins with the same prefix
                val relatedPlugins = plugins.filter { it.startsWith("$prefix-") }
                if (relatedPlugins.size > 1) {
                    grouped.getOrPut(prefix) { mutableListOf() }.add(plugin)
                } else {
                    ungrouped.add(plugin)
                }
            } else {
                ungrouped.add(plugin)
            }
        }

        // Add ungrouped plugins as individual items
        if (ungrouped.isNotEmpty()) {
            grouped["Other"] = ungrouped.toMutableList()
        }

        return grouped.mapValues { it.value.sorted() }
    }

    fun analyzeSelectedDirectory(
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
}