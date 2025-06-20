package com.github.cnrture.quickprojectwizard.common.file

import com.github.cnrture.quickprojectwizard.common.Constants
import com.github.cnrture.quickprojectwizard.data.FeatureTemplate
import com.github.cnrture.quickprojectwizard.data.FileTemplate
import com.github.cnrture.quickprojectwizard.data.ModuleTemplate
import com.github.cnrture.quickprojectwizard.toolwindow.template.GitIgnoreTemplate
import com.github.cnrture.quickprojectwizard.toolwindow.template.ManifestTemplate
import com.github.cnrture.quickprojectwizard.toolwindow.template.TemplateWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.Writer
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.absolutePathString

class FileWriter() {
    private val templateWriter = TemplateWriter()

    fun createModule(
        packageName: String,
        settingsGradleFile: File,
        workingDirectory: File,
        modulePathAsString: String,
        moduleType: String,
        showErrorDialog: (String) -> Unit,
        showSuccessDialog: () -> Unit,
        dependencies: List<String> = emptyList(),
        libraryDependencies: String = Constants.EMPTY,
        pluginDependencies: String = Constants.EMPTY,
        template: ModuleTemplate? = null,
    ): List<File> {
        val filesCreated = mutableListOf<File>()

        val fileReady = modulePathAsString.replace(":", "/")

        val path = Paths.get(workingDirectory.toURI())
        val modulePath = Paths.get(path.toString(), fileReady)
        val moduleFile = File(modulePath.absolutePathString())

        if (modulePathAsString.isEmpty()) {
            showErrorDialog("Module name empty / not as expected (is it formatted as :module?)")
            return emptyList()
        }

        moduleFile.mkdirs()

        addToSettingsAtCorrectLocation(
            settingsGradleFile = settingsGradleFile,
            modulePathAsString = modulePathAsString,
        )

        filesCreated += createDefaultModuleStructure(
            packageName = packageName,
            moduleFile = moduleFile,
            moduleName = modulePathAsString,
            moduleType = moduleType,
            dependencies = dependencies,
            libraryDependencies = libraryDependencies,
            pluginDependencies = pluginDependencies,
            template = template,
        )

        showSuccessDialog()

        return filesCreated
    }

    private fun createDefaultModuleStructure(
        packageName: String,
        moduleFile: File,
        moduleName: String,
        moduleType: String,
        dependencies: List<String> = emptyList(),
        libraryDependencies: String = Constants.EMPTY,
        pluginDependencies: String = Constants.EMPTY,
        template: ModuleTemplate? = null,
    ): List<File> {
        val filesCreated = mutableListOf<File>()

        filesCreated += templateWriter.createGradleFile(
            packageName = packageName,
            moduleFile = moduleFile,
            moduleType = moduleType,
            dependencies = dependencies,
            libraryDependencies = libraryDependencies,
            pluginDependencies = pluginDependencies,
        )

        if (moduleType == Constants.ANDROID) {
            filesCreated += createAndroidManifest(moduleFile, packageName)
            filesCreated += createResourceDirectories(moduleFile)
        }

        filesCreated += templateWriter.createReadmeFile(moduleFile, moduleName)

        filesCreated += if (template != null) {
            createTemplateBasedStructure(moduleFile, packageName, template)
        } else {
            createDefaultPackages(moduleFile, packageName)
        }

        filesCreated += createGitIgnore(moduleFile)

        return filesCreated
    }

    private fun createAndroidManifest(moduleFile: File, packageName: String): List<File> {
        val manifestDir = Paths.get(moduleFile.absolutePath, "src", "main").toFile()
        manifestDir.mkdirs()

        val manifestFile = Paths.get(manifestDir.absolutePath, "AndroidManifest.xml").toFile()
        val writer: Writer = FileWriter(manifestFile)
        val manifestContent = ManifestTemplate.getManifestTemplate(packageName)

        writer.write(manifestContent)
        writer.flush()
        writer.close()

        return listOf(manifestFile)
    }

    private fun createResourceDirectories(moduleFile: File): List<File> {
        val createdDirs = mutableListOf<File>()

        val resDir = Paths.get(moduleFile.absolutePath, "src", "main", "res").toFile()
        resDir.mkdirs()
        createdDirs.add(resDir)

        val subDirs = listOf("drawable", "values")

        subDirs.forEach { dirName ->
            val dir = Paths.get(resDir.absolutePath, dirName).toFile()
            dir.mkdirs()
            createdDirs.add(dir)
        }

        return createdDirs
    }

    private fun createGitIgnore(moduleFile: File): List<File> {
        val gitignoreFile = Paths.get(moduleFile.absolutePath).toFile()
        val filePath = Paths.get(gitignoreFile.absolutePath, ".gitignore").toFile()
        val writer: Writer = FileWriter(filePath)
        val dataToWrite = GitIgnoreTemplate.data

        writer.write(dataToWrite)
        writer.flush()
        writer.close()

        return listOf(filePath)
    }

    fun createFeatureFiles(
        file: File,
        featureName: String,
        packageName: String,
        showErrorDialog: (String) -> Unit,
        showSuccessDialog: () -> Unit,
        selectedTemplate: FeatureTemplate,
    ): List<File> {
        return createFeatureFilesFromTemplate(
            file = file,
            featureName = featureName,
            packageName = packageName,
            template = selectedTemplate,
            showErrorDialog = showErrorDialog,
            showSuccessDialog = showSuccessDialog,
        )
    }

    private fun createFeatureFilesFromTemplate(
        file: File,
        featureName: String,
        packageName: String,
        template: FeatureTemplate,
        showErrorDialog: (String) -> Unit,
        showSuccessDialog: () -> Unit,
    ): List<File> {
        val featureFile = Paths.get(file.absolutePath, featureName.lowercase()).toFile()
        featureFile.mkdirs()

        val filesCreated = mutableListOf<File>()

        template.fileTemplates.forEach { fileTemplate: FileTemplate ->
            val fileName = fileTemplate.fileName
                .replace("{NAME}", featureName.replaceFirstChar { it.uppercase() })

            val fileDir = if (fileTemplate.filePath.isNotEmpty()) {
                val subDirPath = fileTemplate.filePath.replace(".", File.separator)
                Paths.get(featureFile.absolutePath, subDirPath).toFile().apply { mkdirs() }
            } else {
                featureFile
            }

            val targetFile = File(fileDir, fileName)

            val content = fileTemplate.fileContent
                .replace("{NAME}", featureName.replaceFirstChar { it.uppercase() })
                .replace("{PACKAGE}", packageName)
                .replace(
                    "{FILE_PACKAGE}",
                    if (fileTemplate.filePath.isNotEmpty()) {
                        "$packageName.${fileTemplate.filePath}"
                    } else {
                        packageName
                    }
                )

            try {
                val writer: Writer = FileWriter(targetFile)
                writer.write(content)
                writer.flush()
                writer.close()
                filesCreated.add(targetFile)
            } catch (e: IOException) {
                showErrorDialog("Error creating file ${fileName}: ${e.message}")
            } catch (e: Exception) {
                showErrorDialog("Unexpected error: ${e.message}")
            }
        }

        showSuccessDialog()
        return filesCreated
    }

    private fun createDefaultPackages(moduleFile: File, packageName: String): List<File> {
        fun makePath(srcPath: File, packagePath: String): File {
            val packagePathFile = Paths.get(
                srcPath.path,
                packagePath.split(".").joinToString(File.separator)
            ).toFile()
            packagePathFile.mkdirs()
            return packagePathFile
        }

        val srcPath = Paths.get(moduleFile.absolutePath, "src/main/kotlin").toFile()
        val packagePath = makePath(srcPath, packageName)

        return listOf(packagePath)
    }

    private fun createTemplateBasedStructure(
        moduleFile: File,
        packageName: String,
        template: ModuleTemplate,
    ): List<File> {
        val filesCreated = mutableListOf<File>()

        val srcPath = Paths.get(moduleFile.absolutePath, "src/main/kotlin").toFile()
        srcPath.mkdirs()

        template.fileTemplates.forEach { fileTemplate ->
            val packagePath = if (fileTemplate.filePath.isNotEmpty()) {
                "$packageName.${fileTemplate.filePath}"
            } else {
                packageName
            }

            val fileDir = Paths.get(
                srcPath.absolutePath,
                packagePath.split(".").joinToString(File.separator)
            ).toFile()
            fileDir.mkdirs()

            val moduleNameFromPath = moduleFile.name.removePrefix(":")
                .split("-", "_", "/").joinToString("") { part ->
                    if (part.isNotEmpty()) part.replaceFirstChar { char -> char.uppercase() } else ""
                }

            val fileName = fileTemplate.fileName
                .replace("{NAME}", moduleNameFromPath.replaceFirstChar { it.uppercase() })

            val file = File(fileDir, fileName)

            val content = fileTemplate.fileContent
                .replace("{NAME}", moduleNameFromPath)
                .replace("{PACKAGE}", packageName)
                .replace("{FILE_PACKAGE}", packagePath)

            try {
                val writer: Writer = FileWriter(file)
                writer.write(content)
                writer.flush()
                writer.close()
                filesCreated.add(file)
            } catch (e: IOException) {
            }
        }

        return filesCreated
    }

    private fun addToSettingsAtCorrectLocation(
        settingsGradleFile: File,
        modulePathAsString: String,
    ) {
        val settingsFileContent = Files.readAllLines(Paths.get(settingsGradleFile.toURI()))

        val modulePath = modulePathAsString.removePrefix(":")
        val moduleCategory = determineModuleCategory(modulePath)

        val includeBlocks = findIncludeBlocksWithCategories(settingsFileContent)

        val updatedContent = insertModuleInCategory(
            settingsFileContent = settingsFileContent.toMutableList(),
            modulePathAsString = modulePathAsString,
            moduleCategory = moduleCategory,
            categoryBlocks = includeBlocks,
        )

        Files.write(Paths.get(settingsGradleFile.toURI()), updatedContent)
    }

    private fun determineModuleCategory(modulePath: String): String {
        return when {
            modulePath.startsWith("library:") -> "Libraries"
            modulePath.startsWith("plugin:") -> "Plugins"
            modulePath.startsWith("feature:") -> "Features"
            modulePath.startsWith("launcher:") -> "Launchers"
            else -> "Root"
        }
    }

    private fun findIncludeBlocksWithCategories(settingsFileContent: List<String>): Map<String, Pair<Int, Int>> {
        val categoryBlocks = mutableMapOf<String, Pair<Int, Int>>()
        var currentCategory = "Root"
        var categoryStart = -1
        var inIncludeBlock = false

        settingsFileContent.forEachIndexed { index, line ->
            if (line.trim().startsWith("//") && !inIncludeBlock) {
                val potentialCategory = line.trim().removePrefix("//").trim()
                if (potentialCategory in listOf("Libraries", "Plugins", "Features", "Launchers")) {
                    currentCategory = potentialCategory
                    categoryStart = index
                }
            }

            if (line.contains("include ") || line.contains("include(")) {
                if (!inIncludeBlock) {
                    inIncludeBlock = true
                    if (!categoryBlocks.containsKey(currentCategory)) {
                        categoryBlocks[currentCategory] = Pair(index, index)
                    }
                }

                if (!line.endsWith(",")) {
                    inIncludeBlock = false
                    categoryBlocks[currentCategory] = Pair(
                        categoryBlocks[currentCategory]?.first ?: index,
                        index
                    )
                }
            } else if (inIncludeBlock) {
                if (!line.trim().endsWith(",")) {
                    inIncludeBlock = false
                    categoryBlocks[currentCategory] = Pair(
                        categoryBlocks[currentCategory]?.first ?: categoryStart,
                        index
                    )
                }
            }
        }

        return categoryBlocks
    }

    private fun insertModuleInCategory(
        settingsFileContent: MutableList<String>,
        modulePathAsString: String,
        moduleCategory: String,
        categoryBlocks: Map<String, Pair<Int, Int>>,
    ): MutableList<String> {
        if (categoryBlocks.containsKey(moduleCategory)) {
            val (blockStart, blockEnd) = categoryBlocks[moduleCategory]!!

            val insertPosition = blockEnd
            val lastLine = settingsFileContent[insertPosition]

            val baseIndentation = lastLine.takeWhile { it.isWhitespace() }

            var continuationIndentation = Constants.EMPTY
            if (insertPosition > blockStart) {
                for (i in blockStart + 1..insertPosition) {
                    val line = settingsFileContent[i].trim()
                    if (line.startsWith("':") && !line.startsWith("include")) {
                        continuationIndentation = settingsFileContent[i].takeWhile { it.isWhitespace() }
                        break
                    }
                }

                if (continuationIndentation.isEmpty()) {
                    continuationIndentation = baseIndentation + " ".repeat(8)
                }
            } else {
                continuationIndentation = baseIndentation + " ".repeat(8)
            }

            val trimmedLastLine = lastLine.trim()

            if (trimmedLastLine.endsWith(",")) {
                settingsFileContent.add(insertPosition + 1, "$continuationIndentation'$modulePathAsString'")
            } else if (trimmedLastLine.contains("include") &&
                (trimmedLastLine.endsWith("'") || trimmedLastLine.endsWith("\""))
            ) {
                settingsFileContent[insertPosition] = "${lastLine},"
                settingsFileContent.add(insertPosition + 1, "$continuationIndentation'$modulePathAsString'")
            } else if (trimmedLastLine.endsWith("'") || trimmedLastLine.endsWith("\"")) {
                settingsFileContent[insertPosition] = "${lastLine},"
                settingsFileContent.add(insertPosition + 1, "$continuationIndentation'$modulePathAsString'")
            } else {
                val includeStatement = constructIncludeStatement(modulePathAsString, settingsFileContent)
                settingsFileContent.add(insertPosition + 1, "$baseIndentation$includeStatement")
            }

            return settingsFileContent
        } else {
            settingsFileContent.add(Constants.EMPTY)
            settingsFileContent.add("// $moduleCategory")

            val includeStatement = constructIncludeStatement(modulePathAsString, settingsFileContent)
            settingsFileContent.add(includeStatement)

            return settingsFileContent
        }
    }

    private fun constructIncludeStatement(modulePathAsString: String, settingsFileContent: List<String>): String {
        val firstIncludeStatement = settingsFileContent.firstOrNull { it.contains("include") }
        return if (firstIncludeStatement != null && firstIncludeStatement.contains("'")) {
            "include '$modulePathAsString'"
        } else {
            "include(\"$modulePathAsString\")"
        }
    }
}
