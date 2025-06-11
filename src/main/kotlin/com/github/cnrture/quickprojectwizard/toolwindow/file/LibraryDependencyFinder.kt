package com.github.cnrture.quickprojectwizard.toolwindow.file

import com.github.cnrture.quickprojectwizard.common.Constants
import java.io.File

class LibraryDependencyFinder {

    data class LibraryInfo(
        val alias: String,
        val group: String,
        val artifact: String,
        val versionRef: String? = null,
        val version: String? = null,
    )

    data class PluginInfo(
        val alias: String,
        val id: String,
        val versionRef: String? = null,
        val version: String? = null,
    )

    fun parseLibsVersionsToml(projectRoot: File): List<LibraryInfo> {
        val libraries = mutableListOf<LibraryInfo>()

        val versionsTomlPath = "gradle/libs.versions.toml"
        var versionsToml = File(projectRoot, versionsTomlPath)

        if (!versionsToml.exists()) {
            versionsToml = File(projectRoot.parentFile, versionsTomlPath)
        }

        if (!versionsToml.exists()) {
            versionsToml = File(projectRoot, "libs.versions.toml")
        }

        if (!versionsToml.exists()) {
            println("LibraryDependencyFinder: Could not find libs.versions.toml file")
            return emptyList()
        }

        println("LibraryDependencyFinder: Found libs.versions.toml at ${versionsToml.absolutePath}")
        val content = versionsToml.readText()

        val librariesSection = content.split("[libraries]")
        if (librariesSection.size < 2) return emptyList()
        println("Parsing libraries section: $librariesSection")

        val librariesContent = librariesSection[1].split("[").first()

        val modulePattern =
            """(\w+(?:-\w+)*)\s*=\s*\{\s*module\s*=\s*["']([^:"']+):([^"']+)["']\s*(?:,\s*version\.ref\s*=\s*["']([^"']+)["'])?\s*(?:,\s*version\s*=\s*["']([^"']+)["'])?\s*}""".toRegex()

        val groupNamePattern =
            """(\w+(?:-\w+)*)\s*=\s*\{\s*group\s*=\s*["']([^"']+)["']\s*,\s*name\s*=\s*["']([^"']+)["']\s*(?:,\s*version\.ref\s*=\s*["']([^"']+)["'])?\s*(?:,\s*version\s*=\s*["']([^"']+)["'])?\s*}""".toRegex()

        val moduleMatches = modulePattern.findAll(librariesContent)
        moduleMatches.forEach { match ->
            val matchGroups = match.groupValues
            val alias = matchGroups[1]
            val group = matchGroups[2]
            val artifact = matchGroups[3]
            val versionRef = if (matchGroups.size > 4) matchGroups[4] else Constants.EMPTY
            val version = if (matchGroups.size > 5) matchGroups[5] else Constants.EMPTY

            libraries.add(LibraryInfo(alias, group, artifact, versionRef.ifEmpty { null }, version.ifEmpty { null }))
        }

        val groupNameMatches = groupNamePattern.findAll(librariesContent)
        groupNameMatches.forEach { match ->
            val matchGroups = match.groupValues
            val alias = matchGroups[1]
            val group = matchGroups[2]
            val artifact = matchGroups[3]
            val versionRef = if (matchGroups.size > 4) matchGroups[4] else Constants.EMPTY
            val version = if (matchGroups.size > 5) matchGroups[5] else Constants.EMPTY

            libraries.add(
                LibraryInfo(
                    alias,
                    group,
                    artifact,
                    versionRef.ifEmpty { null },
                    version.ifEmpty { null }
                )
            )
        }

        println("LibraryDependencyFinder: Found ${libraries.size} libraries in libs.versions.toml")
        libraries.forEach { library ->
            println("  - ${library.alias}: ${library.group}:${library.artifact}")
        }

        return libraries
    }

    fun parsePluginsFromToml(projectRoot: File): List<PluginInfo> {
        val plugins = mutableListOf<PluginInfo>()

        val versionsTomlPath = "gradle/libs.versions.toml"
        var versionsToml = File(projectRoot, versionsTomlPath)

        if (!versionsToml.exists()) {
            versionsToml = File(projectRoot.parentFile, versionsTomlPath)
        }

        if (!versionsToml.exists()) {
            versionsToml = File(projectRoot, "libs.versions.toml")
        }

        if (!versionsToml.exists()) {
            println("LibraryDependencyFinder: Could not find libs.versions.toml file")
            return emptyList()
        }

        println("LibraryDependencyFinder: Found libs.versions.toml at ${versionsToml.absolutePath}")
        val content = versionsToml.readText()

        val pluginsSection = content.split("[plugins]")
        if (pluginsSection.size < 2) return emptyList()
        println("Parsing plugins section: $pluginsSection")

        val pluginsContent = pluginsSection[1].split("[").firstOrNull() ?: return emptyList()

        // Parse plugin entries like:
        // kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
        // compose-compiler = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
        val pluginPattern =
            """(\w+(?:-\w+)*)\s*=\s*\{\s*id\s*=\s*["']([^"']+)["']\s*(?:,\s*version\.ref\s*=\s*["']([^"']+)["'])?\s*(?:,\s*version\s*=\s*["']([^"']+)["'])?\s*}""".toRegex()

        val pluginMatches = pluginPattern.findAll(pluginsContent)
        pluginMatches.forEach { match ->
            val matchGroups = match.groupValues
            val alias = matchGroups[1]
            val id = matchGroups[2]
            val versionRef = if (matchGroups.size > 3 && matchGroups[3].isNotEmpty()) matchGroups[3] else null
            val version = if (matchGroups.size > 4 && matchGroups[4].isNotEmpty()) matchGroups[4] else null

            plugins.add(PluginInfo(alias, id, versionRef, version))
        }

        println("LibraryDependencyFinder: Found ${plugins.size} plugins in libs.versions.toml")
        plugins.forEach { plugin ->
            println("  - ${plugin.alias}: ${plugin.id}")
        }

        return plugins
    }

    fun findImportedLibraries(sourceDir: File, libraries: List<LibraryInfo>): List<String> {
        if (!sourceDir.exists() || !sourceDir.isDirectory) {
            println("LibraryDependencyFinder: Source directory ${sourceDir.absolutePath} does not exist or is not a directory")
            return emptyList()
        }

        val usedLibraries = mutableSetOf<String>()
        val importRegex = """import\s+([\w.]+\*?)\s*""".toRegex()

        println("LibraryDependencyFinder: Scanning for imports in ${sourceDir.absolutePath}")

        val sourceFiles = sourceDir.walkTopDown()
            .filter { it.isFile && (it.extension == "kt" || it.extension == "java") }
            .toList()

        if (sourceFiles.isEmpty()) {
            println("LibraryDependencyFinder: No source files found")
            return emptyList()
        }

        println("LibraryDependencyFinder: Found ${sourceFiles.size} source files")

        val allImports = mutableSetOf<String>()
        sourceFiles.forEach { file ->
            val content = file.readText()
            importRegex.findAll(content).forEach { matchResult ->
                allImports.add(matchResult.groupValues[1])
            }
        }

        val fileContent = sourceFiles.joinToString("\n") { it.readText() }

        libraries.forEach { library ->
            val group = library.group
            val artifact = library.artifact.replace("-", Constants.EMPTY)

            val usageScore = calculateLibraryUsageScore(group, artifact, allImports, fileContent)

            if (usageScore >= LIBRARY_USAGE_THRESHOLD) {
                usedLibraries.add(library.alias)
                println("LibraryDependencyFinder: Detected usage of library ${library.alias} (${library.group}:${library.artifact}) with score $usageScore")
            } else {
                println("LibraryDependencyFinder: Library ${library.alias} (${library.group}:${library.artifact}) not detected (score: $usageScore)")
            }
        }

        println("LibraryDependencyFinder: Found ${usedLibraries.size} used libraries out of ${libraries.size} available")
        return usedLibraries.toList()
    }

    private fun calculateLibraryUsageScore(
        group: String,
        artifact: String,
        imports: Set<String>,
        fileContent: String,
    ): Int {
        val normalizedArtifact = artifact.replace("-", Constants.EMPTY)
        var score = 0

        val groupParts = group.split(".")
        val lastGroupPart = groupParts.lastOrNull()?.replace("-", Constants.EMPTY).orEmpty()

        if (imports.any { it == "$group.$artifact" || it == "$group.$normalizedArtifact" }) {
            println("  - Found exact import match for $group.$artifact")
            score += 3
        }

        if (imports.any { it == "$group.*" }) {
            println("  - Found wildcard import for $group.*")
            score += 1
        }

        if (imports.any { it.startsWith("$group.$artifact.") || it.startsWith("$group.$normalizedArtifact.") }) {
            println("  - Found subpackage imports for $group.$artifact")
            score += 2
        }

        val hasGroupImports = imports.any { it.startsWith("$group.") }
        if (hasGroupImports) {
            println("  - Found imports starting with $group")
            score += 1
        }

        if (lastGroupPart.isNotEmpty() && artifact.isNotEmpty() &&
            imports.any { it.contains(lastGroupPart) && it.contains(normalizedArtifact) }
        ) {
            println("  - Found imports containing both $lastGroupPart and $normalizedArtifact")
            score += 2
        }

        val capitalizedArtifact = normalizedArtifact.replaceFirstChar { it.uppercase() }
        if (capitalizedArtifact.length > 3 && fileContent.contains(capitalizedArtifact)) {
            println("  - Found class usage: $capitalizedArtifact")
            score += 1
        }

        if (lastGroupPart.length > 3 && imports.any { it.contains(lastGroupPart) }) {
            println("  - Found imports containing $lastGroupPart")
            score += 1
        }

        return score
    }

    fun formatLibraryDependencies(libraryAliases: List<String>): String {
        if (libraryAliases.isEmpty()) return Constants.EMPTY

        return StringBuilder().apply {
            append("    // Library Dependencies\n")
            libraryAliases.forEachIndexed { index, alias ->
                append("    implementation(libs.${alias.replace("-", ".")})")
                if (index != libraryAliases.lastIndex) append("\n")
            }
        }.toString()
    }

    fun formatPluginDependencies(pluginAliases: List<String>): String {
        if (pluginAliases.isEmpty()) return Constants.EMPTY

        return StringBuilder().apply {
            pluginAliases.forEachIndexed { index, alias ->
                append("    alias(libs.plugins.${alias.replace("-", ".")})")
                if (index != pluginAliases.lastIndex) append("\n")
            }
        }.toString()
    }

    companion object {
        private const val LIBRARY_USAGE_THRESHOLD = 2
    }
}
