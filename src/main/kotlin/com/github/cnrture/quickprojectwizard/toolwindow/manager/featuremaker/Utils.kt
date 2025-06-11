package com.github.cnrture.quickprojectwizard.toolwindow.manager.featuremaker

import com.github.cnrture.quickprojectwizard.common.Constants
import com.github.cnrture.quickprojectwizard.common.getCurrentlySelectedFile
import com.github.cnrture.quickprojectwizard.common.refreshFileSystem
import com.github.cnrture.quickprojectwizard.common.rootDirectoryString
import com.github.cnrture.quickprojectwizard.toolwindow.dialog.MessageDialogWrapper
import com.github.cnrture.quickprojectwizard.toolwindow.file.FileWriter
import com.intellij.openapi.project.Project
import java.io.File

object Utils {
    fun validateInput(featureName: String, selectedSrc: String): Boolean {
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
}