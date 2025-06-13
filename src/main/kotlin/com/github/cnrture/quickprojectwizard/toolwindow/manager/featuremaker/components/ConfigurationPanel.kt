package com.github.cnrture.quickprojectwizard.toolwindow.manager.featuremaker.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Create
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.cnrture.quickprojectwizard.common.Utils
import com.github.cnrture.quickprojectwizard.common.file.FileWriter
import com.github.cnrture.quickprojectwizard.components.QPWActionCard
import com.github.cnrture.quickprojectwizard.components.QPWActionCardType
import com.github.cnrture.quickprojectwizard.components.QPWText
import com.github.cnrture.quickprojectwizard.components.QPWTextField
import com.github.cnrture.quickprojectwizard.dialog.MessageDialog
import com.github.cnrture.quickprojectwizard.theme.QPWTheme
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                contentAlignment = Alignment.CenterEnd,
            ) {
                QPWActionCard(
                    title = "Create",
                    icon = Icons.Rounded.Create,
                    actionColor = QPWTheme.colors.red,
                    type = QPWActionCardType.MEDIUM,
                    onClick = {
                        if (Utils.validateFeatureInput(featureName, selectedSrc)) {
                            Utils.createFeature(
                                project = project,
                                selectedSrc = selectedSrc,
                                featureName = featureName,
                                fileWriter = fileWriter,
                            )
                        } else {
                            MessageDialog("Please fill out required values").show()
                        }
                    },
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            QPWTextField(
                modifier = Modifier.fillMaxWidth(),
                placeholder = "Feature Name",
                color = QPWTheme.colors.red,
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
            )

            Spacer(modifier = Modifier.height(16.dp))

            RootSelectionContent(
                modifier = Modifier.fillMaxWidth(),
                selectedSrc = selectedSrc,
                showFileTreeDialog = showFileTreeDialog,
                onChooseRootClick = { onFileTreeDialogStateChange() }
            )
        }
    }
}