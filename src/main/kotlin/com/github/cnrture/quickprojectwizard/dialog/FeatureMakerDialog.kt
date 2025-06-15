package com.github.cnrture.quickprojectwizard.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.cnrture.quickprojectwizard.common.*
import com.github.cnrture.quickprojectwizard.common.file.FileTree
import com.github.cnrture.quickprojectwizard.common.file.FileWriter
import com.github.cnrture.quickprojectwizard.components.*
import com.github.cnrture.quickprojectwizard.data.FeatureTemplate
import com.github.cnrture.quickprojectwizard.data.SettingsService
import com.github.cnrture.quickprojectwizard.data.getDefaultFeatureTemplates
import com.github.cnrture.quickprojectwizard.theme.QPWTheme
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import java.io.File

class FeatureMakerDialog(
    private val project: Project,
    startingLocation: VirtualFile?,
) : QPWDialogWrapper(
    width = Constants.FEATURE_MAKER_WINDOW_WIDTH,
    height = Constants.FEATURE_MAKER_WINDOW_HEIGHT,
) {

    private val fileWriter = FileWriter()

    private var selectedSrc = mutableStateOf(Constants.DEFAULT_SRC_VALUE)
    private var featureName = mutableStateOf(Constants.EMPTY)

    init {
        selectedSrc.value = if (startingLocation != null) {
            File(startingLocation.path).absolutePath
                .removePrefix(project.rootDirectoryStringDropLast())
                .removePrefix(File.separator)
        } else {
            File(project.rootDirectoryString()).absolutePath
                .removePrefix(project.rootDirectoryStringDropLast())
                .removePrefix(File.separator)
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
                    )
                }
            }
        }
    }

    @Composable
    private fun FileTreePanel(modifier: Modifier = Modifier) {
        QPWFileTree(
            modifier = modifier,
            model = FileTree(root = File(project.rootDirectoryString()).toProjectFile()),
            titleColor = QPWTheme.colors.purple,
            onClick = { fileTreeNode ->
                val absolutePathAtNode = fileTreeNode.file.absolutePath
                val relativePath = absolutePathAtNode.removePrefix(project.rootDirectoryStringDropLast())
                    .removePrefix(File.separator)
                if (fileTreeNode.file.isDirectory) selectedSrc.value = relativePath
            }
        )
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    private fun ConfigurationPanel(modifier: Modifier = Modifier) {
        val selectedSrc = remember { selectedSrc }
        val featureName = remember { featureName }
        val settings = ApplicationManager.getApplication().service<SettingsService>()
        var selectedTemplate by remember {
            mutableStateOf(
                settings.state.featureTemplates.find { it.isDefault } ?: settings.state.featureTemplates.first(),
            )
        }
        val availableTemplates = remember {
            val currentTemplates = settings.state.featureTemplates.toMutableList()
            if (currentTemplates.isEmpty()) {
                currentTemplates.addAll(getDefaultFeatureTemplates())
            }
            currentTemplates
        }

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
                            createFeature(selectedTemplate)
                        } else {
                            MessageDialog("Please fill out required values").show()
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
                    text = "Selected root: ${selectedSrc.value}",
                    color = QPWTheme.colors.purple,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                    ),
                )

                Spacer(modifier = Modifier.height(16.dp))

                QPWTextField(
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = "Enter feature name",
                    value = featureName.value,
                    onValueChange = { featureName.value = it },
                )

                Spacer(modifier = Modifier.height(8.dp))

                QPWText(
                    text = "Be sure to use camel case for the feature name (e.g. MyFeature)",
                    color = QPWTheme.colors.lightGray,
                    style = TextStyle(
                        fontWeight = FontWeight.SemiBold,
                    ),
                )

                if (availableTemplates.isNotEmpty()) {
                    QPWText(
                        text = "Feature Template",
                        color = QPWTheme.colors.white,
                        style = TextStyle(
                            fontWeight = FontWeight.SemiBold,
                        ),
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        availableTemplates.forEach { template ->
                            QPWRadioButton(
                                text = template.name,
                                selected = selectedTemplate?.id == template.id,
                                color = QPWTheme.colors.red,
                                onClick = { selectedTemplate = template }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun validateInput(): Boolean {
        return featureName.value.isNotEmpty() && selectedSrc.value != Constants.DEFAULT_SRC_VALUE
    }

    private fun createFeature(selectedTemplate: FeatureTemplate) {
        try {
            Utils.createFeature(
                project = project,
                selectedSrc = selectedSrc.value,
                featureName = featureName.value,
                fileWriter = fileWriter,
                selectedTemplate = selectedTemplate,
            )
        } catch (e: Exception) {
            MessageDialog("Error: ${e.message}").show()
        } finally {
            close(0)
        }
    }
}