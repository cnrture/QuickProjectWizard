package com.github.cnrture.quickprojectwizard.dialog.template

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.cnrture.quickprojectwizard.components.*
import com.github.cnrture.quickprojectwizard.data.FeatureTemplate
import com.github.cnrture.quickprojectwizard.data.FileTemplate
import com.github.cnrture.quickprojectwizard.data.ModuleTemplate
import com.github.cnrture.quickprojectwizard.data.SettingsService
import com.github.cnrture.quickprojectwizard.dialog.template.component.FileTemplateEditor
import com.github.cnrture.quickprojectwizard.theme.QPWTheme
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import java.util.*

class TemplateCreatorDialog(
    private val onTemplateCreated: (ModuleTemplate) -> Unit,
) : QPWDialogWrapper(800, 1200) {

    @Composable
    override fun createDesign() {
        TemplateCreatorContent(
            onSave = { template ->
                onTemplateCreated(template)
                saveTemplateToSettings(template)
                close(0)
            },
            onCancel = {
                close(1)
            }
        )
    }

    private fun saveTemplateToSettings(template: ModuleTemplate) {
        val settingsService = service<SettingsService>()
        ApplicationManager.getApplication().invokeLater {
            settingsService.saveTemplate(template)
        }
    }
}

@Composable
private fun TemplateCreatorContent(
    onSave: (ModuleTemplate) -> Unit,
    onCancel: () -> Unit,
) {
    var templateName by remember { mutableStateOf("") }
    val fileTemplates = remember { mutableStateListOf<FileTemplate>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(QPWTheme.colors.black)
            .padding(24.dp)
    ) {
        QPWText(
            text = "Create New Module Template",
            color = QPWTheme.colors.white,
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                QPWTextField(
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = "Template Name",
                    color = QPWTheme.colors.white,
                    value = templateName,
                    onValueChange = { templateName = it }
                )
                Spacer(modifier = Modifier.height(16.dp))
                QPWText(
                    text = "File Content (use {NAME}, {PACKAGE}, {FILE_PACKAGE} placeholders):",
                    color = QPWTheme.colors.lightGray,
                    style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))
                QPWText(
                    text = "{NAME} -> Name of the file without extension",
                    color = QPWTheme.colors.lightGray,
                    style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                )
                QPWText(
                    text = "{PACKAGE} -> Package structure (ex., com.example.app)",
                    color = QPWTheme.colors.lightGray,
                    style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                )
                QPWText(
                    text = "{FILE_PACKAGE} -> Package structure without dots (ex., com.example.app.repository)",
                    color = QPWTheme.colors.lightGray,
                    style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                )
                Spacer(modifier = Modifier.height(24.dp))
                fileTemplates.forEachIndexed { index, fileTemplate ->
                    FileTemplateEditor(
                        fileTemplate = fileTemplate,
                        isModuleEdit = true,
                        onUpdate = { fileTemplates[index] = it },
                        onDelete = { fileTemplates.removeAt(index) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
                Spacer(modifier = Modifier.height(12.dp))
                QPWActionCard(
                    title = "Add File Template",
                    icon = Icons.Rounded.Add,
                    type = QPWActionCardType.MEDIUM,
                    actionColor = QPWTheme.colors.green,
                    onClick = {
                        fileTemplates.add(
                            FileTemplate("", "", "")
                        )
                    }
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End)
        ) {
            QPWActionCard(
                title = "Cancel",
                icon = Icons.Rounded.Delete,
                actionColor = QPWTheme.colors.lightGray,
                onClick = onCancel
            )

            QPWActionCard(
                title = "Create Template",
                icon = Icons.Rounded.Add,
                actionColor = QPWTheme.colors.green,
                onClick = {
                    if (templateName.isNotBlank()) {
                        val template = ModuleTemplate(
                            id = UUID.randomUUID().toString(),
                            name = templateName,
                            fileTemplates = fileTemplates.filter { it.fileName.isNotBlank() },
                            isDefault = false
                        )
                        onSave(template)
                    }
                }
            )
        }
    }
}

class FeatureTemplateCreatorDialog(
    private val onTemplateCreated: (FeatureTemplate) -> Unit,
) : QPWDialogWrapper(800, 1200) {

    @Composable
    override fun createDesign() {
        FeatureTemplateCreatorContent(
            onSave = { template ->
                onTemplateCreated(template)
                saveTemplateToSettings(template)
                close(0)
            },
            onCancel = {
                close(1)
            }
        )
    }

    private fun saveTemplateToSettings(template: FeatureTemplate) {
        val settingsService = service<SettingsService>()
        ApplicationManager.getApplication().invokeLater {
            settingsService.saveFeatureTemplate(template)
        }
    }
}

@Composable
private fun FeatureTemplateCreatorContent(
    onSave: (FeatureTemplate) -> Unit,
    onCancel: () -> Unit,
) {
    var templateName by remember { mutableStateOf("") }
    val fileTemplates = remember { mutableStateListOf<FileTemplate>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(QPWTheme.colors.black)
            .padding(24.dp)
    ) {
        QPWText(
            text = "Create New Feature Template",
            color = QPWTheme.colors.white,
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                QPWTextField(
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = "Template Name",
                    color = QPWTheme.colors.white,
                    value = templateName,
                    onValueChange = { templateName = it }
                )
                Spacer(modifier = Modifier.height(16.dp))
                QPWText(
                    text = "File Content (use {NAME}, {PACKAGE}, {FILE_PACKAGE} placeholders):",
                    color = QPWTheme.colors.lightGray,
                    style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))
                QPWText(
                    text = "{NAME} -> Name of the file without extension",
                    color = QPWTheme.colors.lightGray,
                    style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                )
                QPWText(
                    text = "{PACKAGE} -> Package structure (ex., com.example.app)",
                    color = QPWTheme.colors.lightGray,
                    style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                )
                QPWText(
                    text = "{FILE_PACKAGE} -> Package structure without dots (ex., com.example.app.repository)",
                    color = QPWTheme.colors.lightGray,
                    style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                )
                Spacer(modifier = Modifier.height(24.dp))
                fileTemplates.forEachIndexed { index, fileTemplate ->
                    FileTemplateEditor(
                        fileTemplate = fileTemplate,
                        isModuleEdit = false,
                        onUpdate = { fileTemplates[index] = it },
                        onDelete = { fileTemplates.removeAt(index) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
                Spacer(modifier = Modifier.height(12.dp))
                QPWActionCard(
                    title = "Add File Template",
                    icon = Icons.Rounded.Add,
                    type = QPWActionCardType.MEDIUM,
                    actionColor = QPWTheme.colors.green,
                    onClick = {
                        fileTemplates.add(
                            FileTemplate("", "", "")
                        )
                    }
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End)
        ) {
            QPWActionCard(
                title = "Cancel",
                icon = Icons.Rounded.Delete,
                actionColor = QPWTheme.colors.lightGray,
                onClick = onCancel
            )

            QPWActionCard(
                title = "Create Template",
                icon = Icons.Rounded.Add,
                actionColor = QPWTheme.colors.green,
                onClick = {
                    if (templateName.isNotBlank()) {
                        val template = FeatureTemplate(
                            id = UUID.randomUUID().toString(),
                            name = templateName,
                            fileTemplates = fileTemplates.filter { it.fileName.isNotBlank() },
                            isDefault = false
                        )
                        onSave(template)
                    }
                }
            )
        }
    }
}
