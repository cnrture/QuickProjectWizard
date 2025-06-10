package com.github.cnrture.quickprojectwizard.toolwindow.manager

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.cnrture.quickprojectwizard.common.*
import com.github.cnrture.quickprojectwizard.toolwindow.components.QPWDialogActions
import com.github.cnrture.quickprojectwizard.toolwindow.components.QPWFileTree
import com.github.cnrture.quickprojectwizard.toolwindow.components.QPWText
import com.github.cnrture.quickprojectwizard.toolwindow.components.QPWTextField
import com.github.cnrture.quickprojectwizard.toolwindow.dialog.MessageDialogWrapper
import com.github.cnrture.quickprojectwizard.toolwindow.file.FileTree
import com.github.cnrture.quickprojectwizard.toolwindow.file.FileWriter
import com.github.cnrture.quickprojectwizard.toolwindow.file.toProjectFile
import com.github.cnrture.quickprojectwizard.toolwindow.theme.QPWTheme
import com.intellij.openapi.project.Project
import java.io.File

@Composable
fun FeatureMakerComponent(project: Project) {
    val fileWriter = FileWriter(project)

    val selectedSrc = mutableStateOf(Constants.DEFAULT_SRC_VALUE)
    val featureName = mutableStateOf(Constants.EMPTY)

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
                text = "Feature Creator",
                style = TextStyle(
                    color = QPWTheme.colors.purple,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                ),
            )
            Spacer(modifier = Modifier.size(24.dp))
            Row {
                FileTreePanel(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(0.5f),
                    project = project,
                    onSelectedSrc = { selectedSrc.value = it }
                )
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(end = 32.dp)
                        .background(QPWTheme.colors.white)
                        .width(2.dp)
                )
                ConfigurationPanel(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(0.5f),
                    project = project,
                    fileWriter = fileWriter,
                    selectedSrc = selectedSrc.value,
                    featureName = featureName.value,
                    onFeatureNameChange = { featureName.value = it }
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
        titleColor = QPWTheme.colors.purple,
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ConfigurationPanel(
    modifier: Modifier = Modifier,
    project: Project,
    fileWriter: FileWriter,
    selectedSrc: String,
    featureName: String,
    onFeatureNameChange: (String) -> Unit,
) {
    Scaffold(
        modifier = modifier,
        backgroundColor = QPWTheme.colors.black,
        bottomBar = {
            QPWDialogActions(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(QPWTheme.colors.gray),
                onCreateClick = {
                    if (validateInput(featureName, selectedSrc)) {
                        createFeature(
                            project = project,
                            selectedSrc = selectedSrc,
                            featureName = featureName,
                            fileWriter = fileWriter,
                        )
                    } else {
                        MessageDialogWrapper("Please fill out required values").show()
                    }
                },
                color = QPWTheme.colors.purple,
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            QPWText(
                text = "Selected root: $selectedSrc",
                color = QPWTheme.colors.purple,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                ),
                softWrap = true,
            )

            Spacer(modifier = Modifier.height(16.dp))

            QPWTextField(
                modifier = Modifier.fillMaxWidth(),
                placeholder = "Enter feature name",
                value = featureName,
                onValueChange = onFeatureNameChange,
            )

            Spacer(modifier = Modifier.height(8.dp))

            QPWText(
                text = "Be sure to use camel case for the feature name (e.g. MyFeature)",
                color = QPWTheme.colors.lightGray,
                style = TextStyle(
                    fontWeight = FontWeight.SemiBold,
                ),
                softWrap = true,
            )
        }
    }
}

private fun validateInput(featureName: String, selectedSrc: String): Boolean {
    return featureName.isNotEmpty() && selectedSrc != Constants.DEFAULT_SRC_VALUE
}

private fun createFeature(project: Project, selectedSrc: String, featureName: String, fileWriter: FileWriter) {
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