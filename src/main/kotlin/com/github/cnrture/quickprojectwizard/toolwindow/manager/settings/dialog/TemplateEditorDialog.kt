package com.github.cnrture.quickprojectwizard.toolwindow.manager.settings.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Edit
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
import com.github.cnrture.quickprojectwizard.service.SettingsService
import com.github.cnrture.quickprojectwizard.theme.QPWTheme
import com.github.cnrture.quickprojectwizard.toolwindow.manager.settings.component.FileTemplateEditor
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service

class TemplateEditorDialog(
    private val template: ModuleTemplate,
    private val onTemplateUpdated: (ModuleTemplate) -> Unit,
) : QPWDialogWrapper(800, 1200, modal = false) {

    @Composable
    override fun createDesign() {
        TemplateEditorContent(
            template = template,
            onSave = { updatedTemplate ->
                onTemplateUpdated(updatedTemplate)
                saveToSettingsService(updatedTemplate)
                close(0)
            },
            onCancel = {
                close(1)
            }
        )
    }

    private fun saveToSettingsService(updatedTemplate: ModuleTemplate) {
        val settingsService = service<SettingsService>()
        ApplicationManager.getApplication().invokeLater {
            settingsService.saveTemplate(updatedTemplate)
        }
    }
}

@Composable
private fun TemplateEditorContent(
    template: ModuleTemplate,
    onSave: (ModuleTemplate) -> Unit,
    onCancel: () -> Unit,
) {
    var templateName by remember { mutableStateOf(template.name) }
    val fileTemplates = remember { mutableStateListOf<FileTemplate>().apply { addAll(template.fileTemplates) } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(QPWTheme.colors.black)
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Edit,
                contentDescription = null,
                tint = QPWTheme.colors.lightGray,
                modifier = Modifier.size(28.dp)
            )
            QPWText(
                text = template.name,
                color = QPWTheme.colors.white,
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }

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
                    color = if (template.isDefault) QPWTheme.colors.lightGray.copy(alpha = 0.5f) else QPWTheme.colors.white,
                    value = templateName,
                    onValueChange = { if (!template.isDefault) templateName = it }
                )
                Spacer(modifier = Modifier.height(16.dp))
                QPWText(
                    text = "File Content (use {NAME}, {PACKAGE}, {FILE_PACKAGE} placeholders):",
                    color = QPWTheme.colors.lightGray,
                    style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))
                QPWText(
                    text = "{NAME} -> Name of the file without extension",
                    color = QPWTheme.colors.lightGray,
                    style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                )
                QPWText(
                    text = "{PACKAGE} -> Package structure (ex., com.example.app)",
                    color = QPWTheme.colors.lightGray,
                    style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                )
                QPWText(
                    text = "{FILE_PACKAGE} -> Package structure without dots (ex., com.example.app.repository)",
                    color = QPWTheme.colors.lightGray,
                    style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
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
                type = QPWActionCardType.MEDIUM,
                actionColor = QPWTheme.colors.lightGray,
                onClick = onCancel
            )

            QPWActionCard(
                title = "Save Changes",
                icon = Icons.Rounded.Edit,
                type = QPWActionCardType.MEDIUM,
                actionColor = QPWTheme.colors.green,
                onClick = {
                    val updatedTemplate = template.copy(
                        name = templateName,
                        fileTemplates = fileTemplates.filter { it.fileName.isNotBlank() }
                    )
                    onSave(updatedTemplate)
                }
            )
        }
    }
}

class FeatureTemplateEditorDialog(
    private val template: FeatureTemplate,
    private val onTemplateUpdated: (FeatureTemplate) -> Unit,
) : QPWDialogWrapper(800, 1200, modal = false) {

    @Composable
    override fun createDesign() {
        FeatureTemplateEditorContent(
            template = template,
            onSave = { updatedTemplate ->
                onTemplateUpdated(updatedTemplate)
                saveToSettingsService(updatedTemplate)
                close(0)
            },
            onCancel = {
                close(1)
            }
        )
    }

    private fun saveToSettingsService(updatedTemplate: FeatureTemplate) {
        val settingsService = service<SettingsService>()
        ApplicationManager.getApplication().invokeLater {
            settingsService.saveFeatureTemplate(updatedTemplate)
        }
    }
}

@Composable
private fun FeatureTemplateEditorContent(
    template: FeatureTemplate,
    onSave: (FeatureTemplate) -> Unit,
    onCancel: () -> Unit,
) {
    var templateName by remember { mutableStateOf(template.name) }
    val fileTemplates = remember { mutableStateListOf<FileTemplate>().apply { addAll(template.fileTemplates) } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(QPWTheme.colors.black)
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Edit,
                contentDescription = null,
                tint = QPWTheme.colors.lightGray,
                modifier = Modifier.size(28.dp)
            )
            QPWText(
                text = template.name,
                color = QPWTheme.colors.white,
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }

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
                    color = if (template.isDefault) QPWTheme.colors.lightGray.copy(alpha = 0.5f) else QPWTheme.colors.white,
                    value = templateName,
                    onValueChange = { if (!template.isDefault) templateName = it }
                )
                Spacer(modifier = Modifier.height(16.dp))
                QPWText(
                    text = "File Content (use {NAME}, {PACKAGE}, {FILE_PACKAGE} placeholders):",
                    color = QPWTheme.colors.lightGray,
                    style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))
                QPWText(
                    text = "{NAME} -> Name of the file without extension",
                    color = QPWTheme.colors.lightGray,
                    style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                )
                QPWText(
                    text = "{PACKAGE} -> Package structure (ex., com.example.app)",
                    color = QPWTheme.colors.lightGray,
                    style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                )
                QPWText(
                    text = "{FILE_PACKAGE} -> Package structure without dots (ex., com.example.app.repository)",
                    color = QPWTheme.colors.lightGray,
                    style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
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
                type = QPWActionCardType.MEDIUM,
                actionColor = QPWTheme.colors.lightGray,
                onClick = onCancel
            )

            QPWActionCard(
                title = "Save Changes",
                icon = Icons.Rounded.Edit,
                type = QPWActionCardType.MEDIUM,
                actionColor = QPWTheme.colors.green,
                onClick = {
                    val updatedTemplate = template.copy(
                        name = templateName,
                        fileTemplates = fileTemplates.filter { it.fileName.isNotBlank() }
                    )
                    onSave(updatedTemplate)
                }
            )
        }
    }
}
