package com.github.cnrture.quickprojectwizard.toolwindow.manager.featuremaker.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.cnrture.quickprojectwizard.toolwindow.components.QPWDialogActions
import com.github.cnrture.quickprojectwizard.toolwindow.components.QPWText
import com.github.cnrture.quickprojectwizard.toolwindow.components.QPWTextField
import com.github.cnrture.quickprojectwizard.toolwindow.dialog.MessageDialogWrapper
import com.github.cnrture.quickprojectwizard.toolwindow.file.FileWriter
import com.github.cnrture.quickprojectwizard.toolwindow.manager.featuremaker.Utils
import com.github.cnrture.quickprojectwizard.toolwindow.theme.QPWTheme
import com.intellij.openapi.project.Project

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ConfigurationPanel(
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
                    if (Utils.validateInput(featureName, selectedSrc)) {
                        Utils.createFeature(
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