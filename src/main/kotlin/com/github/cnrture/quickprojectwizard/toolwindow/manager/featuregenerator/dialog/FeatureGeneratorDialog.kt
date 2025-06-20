package com.github.cnrture.quickprojectwizard.toolwindow.manager.featuregenerator.dialog

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.CreateNewFolder
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.cnrture.quickprojectwizard.service.AnalyticsService
import com.github.cnrture.quickprojectwizard.common.Constants
import com.github.cnrture.quickprojectwizard.common.Utils
import com.github.cnrture.quickprojectwizard.common.file.FileWriter
import com.github.cnrture.quickprojectwizard.common.rootDirectoryString
import com.github.cnrture.quickprojectwizard.common.rootDirectoryStringDropLast
import com.github.cnrture.quickprojectwizard.components.*
import com.github.cnrture.quickprojectwizard.data.FeatureTemplate
import com.github.cnrture.quickprojectwizard.service.SettingsService
import com.github.cnrture.quickprojectwizard.theme.QPWTheme
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import java.io.File

class FeatureGeneratorDialog(
    private val project: Project,
    startingLocation: VirtualFile?,
) : QPWDialogWrapper(
    width = 600,
    height = 540,
) {
    private val analyticsService = AnalyticsService.getInstance()
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
        analyticsService.track("view_feature_generator_dialog")
        Surface(
            modifier = Modifier.Companion.fillMaxSize(),
            color = QPWTheme.colors.black,
        ) {
            Column(
                modifier = Modifier.Companion
                    .fillMaxSize()
                    .padding(24.dp),
            ) {
                QPWText(
                    modifier = Modifier.Companion.fillMaxWidth(),
                    text = "Feature Generator",
                    style = TextStyle(
                        color = QPWTheme.colors.red,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Companion.Bold,
                        textAlign = TextAlign.Companion.Center,
                    ),
                )
                Spacer(modifier = Modifier.Companion.size(24.dp))
                ConfigurationPanel(
                    modifier = Modifier.Companion
                        .fillMaxHeight()
                        .weight(0.6f),
                )
            }
        }
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    private fun ConfigurationPanel(modifier: Modifier = Modifier.Companion) {
        val selectedSrc = remember { selectedSrc }
        val featureName = remember { featureName }
        val settings = SettingsService.getInstance()
        var selectedTemplate by remember { mutableStateOf(settings.getDefaultFeatureTemplate()) }
        val availableTemplates = remember { settings.getFeatureTemplates() }

        Scaffold(
            modifier = modifier,
            backgroundColor = QPWTheme.colors.black,
            bottomBar = {
                Row(
                    modifier = Modifier.Companion.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.Companion.CenterVertically,
                ) {
                    QPWActionCard(
                        title = "Cancel",
                        icon = Icons.Rounded.Cancel,
                        actionColor = QPWTheme.colors.red,
                        type = QPWActionCardType.MEDIUM,
                        onClick = { close(Constants.DEFAULT_EXIT_CODE) },
                    )
                    Spacer(modifier = Modifier.Companion.size(16.dp))
                    QPWActionCard(
                        title = "Create",
                        icon = Icons.Rounded.CreateNewFolder,
                        actionColor = QPWTheme.colors.red,
                        type = QPWActionCardType.MEDIUM,
                        onClick = {
                            if (validateInput()) {
                                selectedTemplate?.let {
                                    createFeature(it)
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
                modifier = Modifier.Companion
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
            ) {
                QPWText(
                    text = "Selected root: ${selectedSrc.value}",
                    color = QPWTheme.colors.red,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Companion.SemiBold,
                    ),
                )

                Spacer(modifier = Modifier.Companion.height(16.dp))

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

                Spacer(modifier = Modifier.Companion.height(16.dp))

                Column(
                    modifier = Modifier.Companion
                        .background(
                            color = QPWTheme.colors.gray,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(16.dp)
                ) {
                    QPWTextField(
                        modifier = Modifier.Companion
                            .fillMaxWidth(),
                        placeholder = "Enter feature name",
                        value = featureName.value,
                        onValueChange = { featureName.value = it },
                    )

                    Spacer(modifier = Modifier.Companion.height(8.dp))

                    QPWText(
                        text = "Be sure to use camel case for the feature name (e.g. MyFeature)",
                        color = QPWTheme.colors.lightGray,
                        style = TextStyle(
                            fontWeight = FontWeight.Companion.SemiBold,
                        ),
                    )
                }
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
            modifier = Modifier.Companion
                .background(
                    color = QPWTheme.colors.gray,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                )
                .padding(16.dp)
        ) {
            QPWText(
                text = "Feature Templates",
                color = QPWTheme.colors.white,
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Companion.Bold,
                )
            )

            Spacer(modifier = Modifier.Companion.height(8.dp))

            QPWText(
                text = "Choose a template to auto-configure your module",
                color = QPWTheme.colors.lightGray,
                style = TextStyle(fontSize = 12.sp)
            )

            Spacer(modifier = Modifier.Companion.height(12.dp))

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
                Spacer(modifier = Modifier.Companion.height(8.dp))
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
            modifier = Modifier.Companion
                .fillMaxWidth()
                .clickable { onClick() },
            shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
            border = BorderStroke(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) QPWTheme.colors.red else Color.Companion.Transparent
            ),
            backgroundColor = QPWTheme.colors.gray,
            elevation = 0.dp
        ) {
            Row(
                modifier = Modifier.Companion.padding(12.dp),
                verticalAlignment = Alignment.Companion.CenterVertically
            ) {
                Column(
                    modifier = Modifier.Companion.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.Companion.CenterVertically
                    ) {
                        QPWText(
                            text = title,
                            color = QPWTheme.colors.white,
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Companion.Bold
                            )
                        )
                        Spacer(modifier = Modifier.Companion.width(8.dp))

                        if (badge.isNotEmpty()) {
                            Card(
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
                                backgroundColor = badgeColor.copy(alpha = 0.2f)
                            ) {
                                QPWText(
                                    text = badge,
                                    color = badgeColor,
                                    style = TextStyle(fontSize = 9.sp),
                                    modifier = Modifier.Companion.padding(horizontal = 6.dp, vertical = 2.dp)
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
                        modifier = Modifier.Companion.size(20.dp)
                    )
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
                from = "action",
            )
        } catch (e: Exception) {
            QPWMessageDialog("Error: ${e.message}").show()
        } finally {
            close(0)
        }
    }
}