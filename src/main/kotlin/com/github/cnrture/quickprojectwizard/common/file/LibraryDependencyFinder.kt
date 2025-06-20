package com.github.cnrture.quickprojectwizard.common.file

import com.github.cnrture.quickprojectwizard.common.Constants
import com.github.cnrture.quickprojectwizard.data.LibraryInfo
import com.github.cnrture.quickprojectwizard.data.PluginInfo
import java.io.File

class LibraryDependencyFinder {

    fun parseLibsVersionsToml(projectRoot: File): List<LibraryInfo> {
        val libraries = mutableListOf<LibraryInfo>()

        val versionsTomlPath = "gradle/libs.versions.toml"
        var versionsToml = File(projectRoot, versionsTomlPath)

        if (!versionsToml.exists()) versionsToml = File(projectRoot.parentFile, versionsTomlPath)
        if (!versionsToml.exists()) versionsToml = File(projectRoot, "libs.versions.toml")
        if (!versionsToml.exists()) return emptyList()

        val content = versionsToml.readText()

        val librariesSection = content.split("[libraries]")
        if (librariesSection.size < 2) return emptyList()

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
            libraries.add(LibraryInfo(alias, group, artifact, versionRef.ifEmpty { null }, version.ifEmpty { null }))
        }

        return libraries
    }

    fun parsePluginsFromToml(projectRoot: File): List<PluginInfo> {
        val plugins = mutableListOf<PluginInfo>()

        val versionsTomlPath = "gradle/libs.versions.toml"
        var versionsToml = File(projectRoot, versionsTomlPath)

        if (!versionsToml.exists()) versionsToml = File(projectRoot.parentFile, versionsTomlPath)
        if (!versionsToml.exists()) versionsToml = File(projectRoot, "libs.versions.toml")
        if (!versionsToml.exists()) return emptyList()

        val content = versionsToml.readText()

        val pluginsSection = content.split("[plugins]")
        if (pluginsSection.size < 2) return emptyList()

        val pluginsContent = pluginsSection[1].split("[").firstOrNull() ?: return emptyList()

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

        return plugins
    }

    fun formatLibraryDependencies(libraryAliases: List<String>): String {
        if (libraryAliases.isEmpty()) return Constants.EMPTY
        val bomLibraries = setOf("compose-bom", "firebase", "firebase-bom")
        val annotationProcessors = setOf("room-compiler", "hilt-compiler")
        return StringBuilder().apply {
            append("    // Library Dependencies\n")
            libraryAliases.forEachIndexed { index, alias ->
                val formattedAlias = alias.replace("-", ".")
                val implementationType = when {
                    bomLibraries.contains(alias) -> "implementation(platform(libs.$formattedAlias))"
                    annotationProcessors.contains(alias) -> "ksp(libs.$formattedAlias)"
                    else -> "implementation(libs.$formattedAlias)"
                }
                append("    $implementationType")
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
}
