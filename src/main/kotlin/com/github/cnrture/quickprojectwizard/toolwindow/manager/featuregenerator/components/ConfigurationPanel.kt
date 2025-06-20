package com.github.cnrture.quickprojectwizard.toolwindow.manager.featuregenerator.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Create
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.cnrture.quickprojectwizard.common.Utils
import com.github.cnrture.quickprojectwizard.common.file.FileWriter
import com.github.cnrture.quickprojectwizard.components.*
import com.github.cnrture.quickprojectwizard.data.FeatureTemplate
import com.github.cnrture.quickprojectwizard.data.SettingsService
import com.github.cnrture.quickprojectwizard.theme.QPWTheme
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
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
    val settings = ApplicationManager.getApplication().service<SettingsService>()
    var selectedTemplate by remember { mutableStateOf(settings.getDefaultFeatureTemplate()) }
    val availableTemplates = remember { settings.getFeatureTemplates() }
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
                            selectedTemplate?.let { selectedTemplate ->
                                Utils.createFeature(
                                    project = project,
                                    selectedSrc = selectedSrc,
                                    featureName = featureName,
                                    fileWriter = fileWriter,
                                    selectedTemplate = selectedTemplate,
                                )
                            } ?: run { QPWMessageDialog("Please select a feature template").show() }
                        } else {
                            QPWMessageDialog("Please fill out required values").show()
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
                style = TextStyle(fontWeight = FontWeight.SemiBold),
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (availableTemplates.isNotEmpty()) {
                TemplateSelectionContent(
                    templates = availableTemplates,
                    selectedTemplate = selectedTemplate,
                    defaultTemplateId = settings.getDefaultFeatureTemplate()?.id.orEmpty(),
                    onTemplateSelected = { template ->
                        selectedTemplate = template ?: settings.getDefaultFeatureTemplate()
                    }
                )
            }

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

@Composable
fun TemplateSelectionContent(
    templates: List<FeatureTemplate>,
    selectedTemplate: FeatureTemplate?,
    defaultTemplateId: String,
    onTemplateSelected: (FeatureTemplate?) -> Unit,
) {
    Column(
        modifier = Modifier
            .background(
                color = QPWTheme.colors.gray,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(16.dp)
    ) {
        QPWText(
            text = "Feature Templates",
            color = QPWTheme.colors.white,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        QPWText(
            text = "Choose a template to auto-configure your module",
            color = QPWTheme.colors.lightGray,
            style = TextStyle(fontSize = 12.sp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        templates.forEach { template ->
            TemplateOption(
                title = template.name,
                isSelected = selectedTemplate?.id == template.id,
                onClick = {
                    onTemplateSelected(template)
                },
                badge = if (template.id == defaultTemplateId) "Default" else "",
                badgeColor = if (template.id == defaultTemplateId) QPWTheme.colors.red else QPWTheme.colors.purple
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun TemplateOption(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    badge: String,
    badgeColor: Color,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 0.dp,
            color = if (isSelected) QPWTheme.colors.red else Color.Transparent
        ),
        backgroundColor = QPWTheme.colors.gray,
        elevation = 0.dp
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    QPWText(
                        text = title,
                        color = QPWTheme.colors.white,
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    if (badge.isNotEmpty()) {
                        Card(
                            shape = RoundedCornerShape(4.dp),
                            backgroundColor = badgeColor.copy(alpha = 0.2f)
                        ) {
                            QPWText(
                                text = badge,
                                color = badgeColor,
                                style = TextStyle(fontSize = 9.sp),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = "Selected",
                    tint = QPWTheme.colors.red,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}