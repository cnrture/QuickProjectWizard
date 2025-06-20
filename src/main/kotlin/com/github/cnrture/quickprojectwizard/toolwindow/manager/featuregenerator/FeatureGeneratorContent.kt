package com.github.cnrture.quickprojectwizard.toolwindow.manager.featuregenerator

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.cnrture.quickprojectwizard.common.Constants
import com.github.cnrture.quickprojectwizard.common.file.FileWriter
import com.github.cnrture.quickprojectwizard.common.rootDirectoryString
import com.github.cnrture.quickprojectwizard.common.rootDirectoryStringDropLast
import com.github.cnrture.quickprojectwizard.components.QPWText
import com.github.cnrture.quickprojectwizard.theme.QPWTheme
import com.github.cnrture.quickprojectwizard.toolwindow.manager.featuregenerator.components.ConfigurationPanel
import com.github.cnrture.quickprojectwizard.toolwindow.manager.featuregenerator.components.FileTreePanel
import com.intellij.openapi.project.Project
import java.io.File

@Composable
fun FeatureGeneratorContent(project: Project) {
    val fileWriter = FileWriter()

    val selectedSrc = mutableStateOf(Constants.DEFAULT_SRC_VALUE)
    val featureName = mutableStateOf(Constants.EMPTY)

    var showFileTreeDialog by remember { mutableStateOf(false) }

    selectedSrc.value = File(project.rootDirectoryString()).absolutePath
        .removePrefix(project.rootDirectoryStringDropLast())
        .removePrefix(File.separator)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = QPWTheme.colors.black,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 24.dp),
        ) {
            QPWText(
                modifier = Modifier.fillMaxWidth(),
                text = "Feature Generator",
                style = TextStyle(
                    color = QPWTheme.colors.red,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                ),
            )
            Spacer(modifier = Modifier.size(24.dp))
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
            ) {
                AnimatedVisibility(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(0.4f),
                    visible = showFileTreeDialog,
                    enter = slideInHorizontally(initialOffsetX = { -it }),
                    exit = slideOutHorizontally(targetOffsetX = { -it }),
                ) {
                    FileTreePanel(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(0.4f),
                        project = project,
                        onSelectedSrc = { selectedSrc.value = it }
                    )
                }
                ConfigurationPanel(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(0.6f),
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