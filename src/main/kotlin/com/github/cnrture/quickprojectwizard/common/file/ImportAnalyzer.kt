package com.github.cnrture.quickprojectwizard.common.file

import com.github.cnrture.quickprojectwizard.common.Constants
import java.io.File

class ImportAnalyzer() {

    private val modulePackageMapping = mutableMapOf<String, List<String>>()

    fun analyzeSourceDirectory(directory: File): List<String> {
        val requiredModules = mutableListOf<String>()
        val sourceFiles = findAllSourceFiles(directory)

        sourceFiles.forEach { file ->
            val imports = extractImports(file)
            val modules = mapImportsToModules(imports)
            requiredModules.addAll(modules)
        }

        return requiredModules.distinct()
    }

    private fun findAllSourceFiles(directory: File): List<File> {
        val sourceFiles = mutableListOf<File>()

        directory.walkTopDown().forEach { file ->
            if (file.isFile && (file.extension == "kt" || file.extension == "java")) {
                sourceFiles.add(file)
            }
        }

        return sourceFiles
    }

    private fun extractImports(file: File): List<String> {
        val imports = mutableListOf<String>()

        try {
            file.readLines().forEach { line ->
                val trimmedLine = line.trim()
                if (trimmedLine.startsWith("import ")) {
                    val importPath = trimmedLine.removePrefix("import ").removeSuffix(";").trim()
                    imports.add(importPath)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return imports
    }

    private fun mapImportsToModules(imports: List<String>): List<String> {
        val modules = mutableListOf<String>()

        imports.forEach { importPath ->
            modulePackageMapping.forEach { (module, packages) ->
                packages.forEach { packagePrefix ->
                    if (importPath.startsWith(packagePrefix)) {
                        modules.add(module)
                    }
                }
            }
        }

        return modules.distinct()
    }

    fun discoverProjectModules(projectRoot: File) {
        val moduleMap = mutableMapOf<String, MutableList<String>>()

        val gradleFiles = findGradleFiles(projectRoot)

        gradleFiles.forEach { gradleFile ->
            val modulePath = getModulePath(projectRoot, gradleFile.parentFile)
            val packageNames = findPackageNames(gradleFile.parentFile)

            if (modulePath.isNotEmpty() && packageNames.isNotEmpty()) {
                moduleMap[modulePath] = packageNames
            }
        }

        modulePackageMapping.clear()
        modulePackageMapping.putAll(moduleMap)
    }

    private fun findGradleFiles(root: File): List<File> {
        val gradleFiles = mutableListOf<File>()

        root.walkTopDown()
            .filter { it.isFile && (it.name == "build.gradle" || it.name == "build.gradle.kts") }
            .forEach { gradleFiles.add(it) }

        return gradleFiles
    }

    private fun getModulePath(projectRoot: File, moduleDir: File): String {
        val relativePath = moduleDir.relativeTo(projectRoot).path
        if (relativePath.isEmpty()) return Constants.EMPTY

        return ":${relativePath.replace(File.separator, ":")}"
    }

    private fun findPackageNames(moduleDir: File): MutableList<String> {
        val packageNames = mutableListOf<String>()
        val sourceRoots = listOf(
            File(moduleDir, "src/main/java"),
            File(moduleDir, "src/main/kotlin")
        )

        sourceRoots.filter { it.exists() }.forEach { srcRoot ->
            srcRoot.walkTopDown()
                .filter { it.isDirectory }
                .forEach { dir ->
                    val hasSourceFiles = dir.listFiles()?.any {
                        it.isFile && (it.extension == "kt" || it.extension == "java")
                    } ?: false

                    if (hasSourceFiles) {
                        val packagePath = dir.relativeTo(srcRoot).path.replace(File.separator, ".")
                        if (packagePath.isNotEmpty()) {
                            packageNames.add(packagePath)
                        }
                    }
                }
        }

        return packageNames
    }
}