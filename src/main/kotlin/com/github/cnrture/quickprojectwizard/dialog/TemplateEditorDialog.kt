package com.github.cnrture.quickprojectwizard.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.TabRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.cnrture.quickprojectwizard.common.Constants
import com.github.cnrture.quickprojectwizard.components.*
import com.github.cnrture.quickprojectwizard.data.FileTemplate
import com.github.cnrture.quickprojectwizard.data.ModuleTemplate
import com.github.cnrture.quickprojectwizard.data.SettingsService
import com.github.cnrture.quickprojectwizard.theme.QPWTheme
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service

class TemplateEditorDialog(
    private val template: ModuleTemplate,
    private val onTemplateUpdated: (ModuleTemplate) -> Unit,
) : QPWDialogWrapper(800, 600) {

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
    var templateDescription by remember { mutableStateOf(template.description) }
    var moduleType by remember { mutableStateOf(template.moduleType) }
    val packageStructure = remember { mutableStateListOf<String>().apply { addAll(template.packageStructure) } }
    val fileTemplates = remember { mutableStateListOf<FileTemplate>().apply { addAll(template.fileTemplates) } }

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Information", "Packages", "Files")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(QPWTheme.colors.black)
            .padding(24.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = null,
                tint = QPWTheme.colors.lightGray,
                modifier = Modifier.size(28.dp)
            )
            QPWText(
                text = "Edit Template: ${template.name}",
                color = QPWTheme.colors.white,
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            if (template.isDefault) {
                Card(
                    shape = RoundedCornerShape(6.dp),
                    backgroundColor = QPWTheme.colors.green.copy(alpha = 0.2f)
                ) {
                    QPWText(
                        text = "Default Template",
                        color = QPWTheme.colors.green,
                        style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Tab Row
        TabRow(
            selectedTabIndex = selectedTab,
            backgroundColor = QPWTheme.colors.gray,
            contentColor = QPWTheme.colors.white,
            divider = {},
            indicator = {}
        ) {
            tabs.forEachIndexed { index, tab ->
                QPWTabRow(
                    text = tab,
                    color = when (index) {
                        0 -> QPWTheme.colors.green
                        1 -> QPWTheme.colors.purple
                        2 -> QPWTheme.colors.red
                        else -> QPWTheme.colors.lightGray
                    },
                    isSelected = selectedTab == index,
                    onTabSelected = { selectedTab = index }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tab Content
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (selectedTab) {
                0 -> Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    TemplateSection("Basic Information") {
                        QPWTextField(
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = "Template Name",
                            color = if (template.isDefault) QPWTheme.colors.lightGray.copy(alpha = 0.5f) else QPWTheme.colors.white,
                            value = templateName,
                            onValueChange = { if (!template.isDefault) templateName = it }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        QPWTextField(
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = "Description",
                            color = if (template.isDefault) QPWTheme.colors.lightGray.copy(alpha = 0.5f) else QPWTheme.colors.white,
                            value = templateDescription,
                            onValueChange = { if (!template.isDefault) templateDescription = it }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        if (!template.isDefault) {
                            QPWText(
                                text = "Module Type:",
                                color = QPWTheme.colors.white,
                                style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                QPWRadioButton(
                                    text = Constants.ANDROID,
                                    selected = moduleType == Constants.ANDROID,
                                    color = QPWTheme.colors.green,
                                    onClick = { moduleType = Constants.ANDROID }
                                )
                                QPWRadioButton(
                                    text = Constants.KOTLIN,
                                    selected = moduleType == Constants.KOTLIN,
                                    color = QPWTheme.colors.green,
                                    onClick = { moduleType = Constants.KOTLIN }
                                )
                            }
                        } else {
                            QPWText(
                                text = "Module Type: ${moduleType}",
                                color = QPWTheme.colors.lightGray,
                                style = TextStyle(fontSize = 14.sp)
                            )
                        }
                    }
                }

                1 -> Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    TemplateSection("Package Structure") {
                        packageStructure.forEachIndexed { index, packagePath ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                QPWTextField(
                                    modifier = Modifier.weight(1f),
                                    placeholder = "e.g., data/repository",
                                    color = QPWTheme.colors.white,
                                    value = packagePath,
                                    onValueChange = { newValue ->
                                        packageStructure[index] = newValue
                                    }
                                )
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = QPWTheme.colors.red,
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clickable { packageStructure.removeAt(index) }
                                )
                            }
                        }

                        QPWActionCard(
                            title = "Add Package",
                            icon = Icons.Default.Add,
                            type = QPWActionCardType.SMALL,
                            actionColor = QPWTheme.colors.green,
                            onClick = { packageStructure.add("") }
                        )
                    }
                }

                2 -> Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    TemplateSection("File Templates") {
                        fileTemplates.forEachIndexed { index, fileTemplate ->
                            FileTemplateEditor(
                                fileTemplate = fileTemplate,
                                onUpdate = { updated ->
                                    fileTemplates[index] = updated
                                },
                                onDelete = {
                                    fileTemplates.removeAt(index)
                                }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        QPWActionCard(
                            title = "Add File Template",
                            icon = Icons.Default.Add,
                            type = QPWActionCardType.SMALL,
                            actionColor = QPWTheme.colors.green,
                            onClick = {
                                fileTemplates.add(
                                    FileTemplate("", "", "", "kt")
                                )
                            }
                        )
                    }
                }
            }
        }

        // Action Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End)
        ) {
            QPWActionCard(
                title = "Cancel",
                icon = Icons.Default.Delete,
                actionColor = QPWTheme.colors.lightGray,
                onClick = onCancel
            )

            QPWActionCard(
                title = "Save Changes",
                icon = Icons.Default.Edit,
                actionColor = QPWTheme.colors.green,
                onClick = {
                    val updatedTemplate = template.copy(
                        name = templateName,
                        description = templateDescription,
                        moduleType = moduleType,
                        packageStructure = packageStructure.filter { it.isNotBlank() },
                        fileTemplates = fileTemplates.filter { it.fileName.isNotBlank() }
                    )
                    onSave(updatedTemplate)
                }
            )
        }
    }
}

// Reuse helper components from TemplateCreatorDialog
@Composable
private fun TemplateSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        backgroundColor = QPWTheme.colors.gray,
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            QPWText(
                text = title,
                color = QPWTheme.colors.white,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = QPWTheme.colors.lightGray.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun FileTemplateEditor(
    fileTemplate: FileTemplate,
    onUpdate: (FileTemplate) -> Unit,
    onDelete: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        backgroundColor = QPWTheme.colors.black.copy(alpha = 0.3f),
        elevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                QPWText(
                    text = "File Template",
                    color = QPWTheme.colors.lightGray,
                    style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold)
                )
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = QPWTheme.colors.red,
                    modifier = Modifier
                        .size(16.dp)
                        .clickable { onDelete() }
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QPWTextField(
                    modifier = Modifier.weight(1f),
                    placeholder = "File Name (e.g., Repository.kt)",
                    color = QPWTheme.colors.white,
                    value = fileTemplate.fileName,
                    onValueChange = { onUpdate(fileTemplate.copy(fileName = it)) }
                )
                QPWTextField(
                    modifier = Modifier.weight(1f),
                    placeholder = "Path (e.g., domain/repository)",
                    color = QPWTheme.colors.white,
                    value = fileTemplate.filePath,
                    onValueChange = { onUpdate(fileTemplate.copy(filePath = it)) }
                )
            }

            QPWText(
                text = "File Content (use {{MODULE_NAME}}, {{PACKAGE_NAME}} placeholders):",
                color = QPWTheme.colors.lightGray,
                style = TextStyle(fontSize = 11.sp)
            )

            // Multiline text field for code content
            QPWTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                placeholder = "Enter file content here...\n\nExample:\ninterface {{MODULE_NAME}}Repository {\n    // Define methods here\n}",
                color = QPWTheme.colors.white,
                value = fileTemplate.fileContent,
                onValueChange = { onUpdate(fileTemplate.copy(fileContent = it)) },
                isSingleLine = false
            )
        }
    }
}
