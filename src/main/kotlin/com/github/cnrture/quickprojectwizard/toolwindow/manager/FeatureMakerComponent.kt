package com.github.cnrture.quickprojectwizard.toolwindow.manager

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.cnrture.quickprojectwizard.common.*
import com.github.cnrture.quickprojectwizard.toolwindow.components.*
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

    var showFileTreeDialog by remember { mutableStateOf(false) }

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
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                ),
            )
            Spacer(modifier = Modifier.size(24.dp))
            Row {
                AnimatedVisibility(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(0.3f),
                    visible = showFileTreeDialog,
                    enter = slideInHorizontally(initialOffsetX = { -it }),
                    exit = slideOutHorizontally(targetOffsetX = { -it }),
                ) {
                    FileTreePanel(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(0.3f),
                        project = project,
                        onSelectedSrc = { selectedSrc.value = it }
                    )
                }
                ConfigurationPanel(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(0.7f),
                    project = project,
                    fileWriter = fileWriter,
                    selectedSrc = selectedSrc.value,
                    featureName = featureName.value,
                    onFeatureNameChange = { featureName.value = it },
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
        titleColor = QPWTheme.colors.purple,
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ConfigurationPanel(
    modifier: Modifier = Modifier,
    project: Project,
    fileWriter: FileWriter,
    selectedSrc: String,
    featureName: String,
    onFeatureNameChange: (String) -> Unit,
    showFileTreeDialog: Boolean,
    onFileTreeDialogStateChange: () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        backgroundColor = QPWTheme.colors.black,
        bottomBar = {
            QPWDialogActions(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(QPWTheme.colors.black),
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
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
        ) {
            RootSelectionContent(
                modifier = Modifier.fillMaxWidth(),
                selectedSrc = selectedSrc,
                showFileTreeDialog = showFileTreeDialog,
                onChooseRootClick = { onFileTreeDialogStateChange() }
            )

            Spacer(modifier = Modifier.height(16.dp))

            QPWTextField(
                modifier = Modifier.fillMaxWidth(),
                label = "Feature Name",
                placeholder = "feature name",
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
            color = QPWTheme.colors.purple,
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
            backgroundColor = QPWTheme.colors.purple,
            onClick = onChooseRootClick,
        )
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