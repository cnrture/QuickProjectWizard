package com.github.cnrture.quickprojectwizard.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontFamily
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
import java.util.*

class TemplateCreatorDialog(
    private val onTemplateCreated: (ModuleTemplate) -> Unit,
) : QPWDialogWrapper(800, 600) {

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
    var templateDescription by remember { mutableStateOf("") }
    var moduleType by remember { mutableStateOf(Constants.ANDROID) }
    val packageStructure = remember { mutableStateListOf<String>() }
    val fileTemplates = remember { mutableStateListOf<FileTemplate>() }

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Information", "Packages", "Files")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(QPWTheme.colors.black)
            .padding(24.dp)
    ) {
        // Header
        QPWText(
            text = "Create New Module Template",
            color = QPWTheme.colors.white,
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        )

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
                0 -> InformationTab(
                    templateName = templateName,
                    templateDescription = templateDescription,
                    moduleType = moduleType,
                    onTemplateNameChange = { templateName = it },
                    onTemplateDescriptionChange = { templateDescription = it },
                    onModuleTypeChange = { moduleType = it }
                )
                1 -> PackagesTab(
                    packageStructure = packageStructure
                )
                2 -> FilesTab(
                    fileTemplates = fileTemplates
                )
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
                title = "Create Template",
                icon = Icons.Default.Add,
                actionColor = QPWTheme.colors.green,
                onClick = {
                    if (templateName.isNotBlank()) {
                        val template = ModuleTemplate(
                            id = UUID.randomUUID().toString(),
                            name = templateName,
                            description = templateDescription,
                            moduleType = moduleType,
                            packageStructure = packageStructure.filter { it.isNotBlank() },
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

@Composable
private fun InformationTab(
    templateName: String,
    templateDescription: String,
    moduleType: String,
    onTemplateNameChange: (String) -> Unit,
    onTemplateDescriptionChange: (String) -> Unit,
    onModuleTypeChange: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        TemplateSection("Basic Information") {
            QPWTextField(
                modifier = Modifier.fillMaxWidth(),
                placeholder = "Template Name (e.g., Clean Architecture)",
                color = QPWTheme.colors.white,
                value = templateName,
                onValueChange = onTemplateNameChange
            )

            Spacer(modifier = Modifier.height(12.dp))

            QPWTextField(
                modifier = Modifier.fillMaxWidth(),
                placeholder = "Description",
                color = QPWTheme.colors.white,
                value = templateDescription,
                onValueChange = onTemplateDescriptionChange
            )

            Spacer(modifier = Modifier.height(12.dp))

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
                    onClick = { onModuleTypeChange(Constants.ANDROID) }
                )
                QPWRadioButton(
                    text = Constants.KOTLIN,
                    selected = moduleType == Constants.KOTLIN,
                    color = QPWTheme.colors.green,
                    onClick = { onModuleTypeChange(Constants.KOTLIN) }
                )
            }
        }
    }
}

@Composable
private fun PackagesTab(
    packageStructure: MutableList<String>,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TemplateSection("Package Structure") {
            QPWText(
                text = "Define the package hierarchy for your module:",
                color = QPWTheme.colors.lightGray,
                style = TextStyle(fontSize = 12.sp)
            )

            Spacer(modifier = Modifier.height(12.dp))

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
                        onValueChange = { packageStructure[index] = it }
                    )
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = QPWTheme.colors.red,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable {
                                packageStructure.removeAt(index)
                            }
                    )
                }
            }

            QPWActionCard(
                title = "Add Package",
                icon = Icons.Default.Add,
                type = QPWActionCardType.SMALL,
                actionColor = QPWTheme.colors.green,
                onClick = {
                    packageStructure.add("")
                }
            )
        }
    }
}

@Composable
private fun FilesTab(
    fileTemplates: MutableList<FileTemplate>,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TemplateSection("File Templates") {
            QPWText(
                text = "Create file templates with placeholder support ({{MODULE_NAME}}, {{PACKAGE_NAME}}):",
                color = QPWTheme.colors.lightGray,
                style = TextStyle(fontSize = 12.sp)
            )

            Spacer(modifier = Modifier.height(12.dp))

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
