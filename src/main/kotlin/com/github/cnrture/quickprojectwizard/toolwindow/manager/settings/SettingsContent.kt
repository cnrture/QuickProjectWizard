package com.github.cnrture.quickprojectwizard.toolwindow.manager.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.cnrture.quickprojectwizard.common.Constants
import com.github.cnrture.quickprojectwizard.components.*
import com.github.cnrture.quickprojectwizard.data.FileTemplate
import com.github.cnrture.quickprojectwizard.data.ModuleTemplate
import com.github.cnrture.quickprojectwizard.data.SettingsService
import com.github.cnrture.quickprojectwizard.dialog.template.TemplateCreatorDialog
import com.github.cnrture.quickprojectwizard.dialog.template.TemplateEditorDialog
import com.github.cnrture.quickprojectwizard.theme.QPWTheme
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service

@Composable
fun SettingsContent() {
    val settings = ApplicationManager.getApplication().service<SettingsService>()
    var currentSettings by mutableStateOf(settings.state.copy())
    var selectedTab by mutableStateOf("general")
    var selectedModuleType by mutableStateOf(currentSettings.preferredModuleType)
    var packageName by mutableStateOf(currentSettings.defaultPackageName)

    if (currentSettings.moduleTemplates.isEmpty()) {
        currentSettings.moduleTemplates.addAll(getDefaultTemplates())
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(QPWTheme.colors.black)
            .padding(24.dp),
        backgroundColor = QPWTheme.colors.black,
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.CenterEnd,
            ) {
                QPWActionCard(
                    title = "Save",
                    icon = Icons.Default.Save,
                    actionColor = QPWTheme.colors.lightGray,
                    type = QPWActionCardType.MEDIUM,
                    onClick = {
                        currentSettings = settings.state.copy(
                            defaultPackageName = packageName,
                            preferredModuleType = selectedModuleType,
                        )
                        settings.loadState(currentSettings)
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
            QPWText(
                modifier = Modifier.fillMaxWidth(),
                text = "Settings",
                style = TextStyle(
                    color = QPWTheme.colors.lightGray,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
            )

            Spacer(modifier = Modifier.size(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QPWTabRow(
                    text = "General",
                    isSelected = selectedTab == "general",
                    color = QPWTheme.colors.lightGray,
                    onTabSelected = { selectedTab = "general" }
                )
                QPWTabRow(
                    text = "Module Templates",
                    isSelected = selectedTab == "templates",
                    color = QPWTheme.colors.lightGray,
                    onTabSelected = { selectedTab = "templates" }
                )
            }

            Spacer(modifier = Modifier.size(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                when (selectedTab) {
                    "general" -> GeneralSettingsTab(
                        defaultPackageName = packageName,
                        preferredModuleType = selectedModuleType,
                        onPackageNameChange = { packageName = it },
                        onModuleTypeChange = { selectedModuleType = it }
                    )

                    "templates" -> ModuleTemplatesTab(
                        templates = currentSettings.moduleTemplates,
                        onTemplateDelete = { template ->
                            if (!template.isDefault) {
                                settings.removeTemplate(template)
                                currentSettings = settings.state.copy()
                                if (currentSettings.moduleTemplates.isEmpty()) {
                                    currentSettings.moduleTemplates.addAll(getDefaultTemplates())
                                }
                            }
                        },
                        onTemplateAdd = { newTemplate ->
                            settings.saveTemplate(newTemplate)
                            currentSettings = settings.state.copy()
                            if (currentSettings.moduleTemplates.isEmpty()) {
                                currentSettings.moduleTemplates.addAll(getDefaultTemplates())
                            }
                        },
                        onTemplateEdit = { oldTemplate, updatedTemplate ->
                            settings.saveTemplate(updatedTemplate)
                            currentSettings = settings.state.copy()
                            if (currentSettings.moduleTemplates.isEmpty()) {
                                currentSettings.moduleTemplates.addAll(getDefaultTemplates())
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ModuleTemplatesTab(
    templates: List<ModuleTemplate>,
    onTemplateDelete: (ModuleTemplate) -> Unit,
    onTemplateAdd: (ModuleTemplate) -> Unit,
    onTemplateEdit: (ModuleTemplate, ModuleTemplate) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            QPWText(
                text = "Module Templates",
                color = QPWTheme.colors.white,
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            QPWActionCard(
                title = "Add Template",
                icon = Icons.Default.Add,
                type = QPWActionCardType.SMALL,
                actionColor = QPWTheme.colors.green,
                onClick = {
                    TemplateCreatorDialog(onTemplateCreated = { newTemplate ->
                        onTemplateAdd(newTemplate)
                    }).show()
                }
            )
        }

        // Templates List
        templates.forEach { template ->
            ModuleTemplateCard(
                template = template,
                onEdit = {
                    TemplateEditorDialog(
                        template = template,
                        onTemplateUpdated = { updatedTemplate ->
                            onTemplateEdit(template, updatedTemplate)
                        }
                    ).show()
                },
                onDelete = {
                    if (!template.isDefault) {
                        onTemplateDelete(template)
                    }
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ModuleTemplateCard(
    template: ModuleTemplate,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        backgroundColor = QPWTheme.colors.gray,
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        QPWText(
                            text = template.name,
                            color = QPWTheme.colors.white,
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        if (template.isDefault) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Card(
                                shape = RoundedCornerShape(4.dp),
                                backgroundColor = QPWTheme.colors.green.copy(alpha = 0.2f)
                            ) {
                                QPWText(
                                    text = "Default",
                                    color = QPWTheme.colors.green,
                                    style = TextStyle(fontSize = 10.sp),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    QPWActionCard(
                        title = "Edit",
                        icon = Icons.Default.Edit,
                        type = QPWActionCardType.SMALL,
                        actionColor = QPWTheme.colors.lightGray,
                        onClick = {
                            onEdit()
                        }
                    )
                    if (!template.isDefault || template.id != "candroid_template") {
                        QPWActionCard(
                            title = "Delete",
                            icon = Icons.Default.Delete,
                            type = QPWActionCardType.SMALL,
                            actionColor = QPWTheme.colors.red,
                            onClick = onDelete
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GeneralSettingsTab(
    defaultPackageName: String,
    preferredModuleType: String,
    onPackageNameChange: (String) -> Unit,
    onModuleTypeChange: (String) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        SettingItem("Default Package Name") {
            QPWTextField(
                modifier = Modifier.fillMaxWidth(),
                placeholder = "com.example",
                color = QPWTheme.colors.lightGray,
                value = defaultPackageName,
                onValueChange = onPackageNameChange,
            )
        }

        SettingItem("Preferred Module Type") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                QPWRadioButton(
                    text = Constants.ANDROID,
                    selected = preferredModuleType == Constants.ANDROID,
                    color = QPWTheme.colors.lightGray,
                    onClick = { onModuleTypeChange(Constants.ANDROID) },
                )

                Spacer(modifier = Modifier.width(16.dp))

                QPWRadioButton(
                    text = Constants.KOTLIN,
                    selected = preferredModuleType == Constants.KOTLIN,
                    color = QPWTheme.colors.lightGray,
                    onClick = { onModuleTypeChange(Constants.KOTLIN) },
                )
            }
        }
    }
}

@Composable
private fun SettingItem(
    label: String,
    description: String? = null,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = QPWTheme.colors.gray,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(16.dp),
    ) {
        QPWText(
            text = label,
            color = QPWTheme.colors.white,
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            )
        )

        description?.let {
            Spacer(modifier = Modifier.size(8.dp))
            QPWText(
                text = it,
                color = QPWTheme.colors.lightGray,
                style = TextStyle(
                    fontSize = 14.sp,
                )
            )
        }

        Spacer(modifier = Modifier.size(8.dp))

        content()
    }
}

private fun getDefaultTemplates(): List<ModuleTemplate> {
    return listOf(
        ModuleTemplate(
            id = "candroid_template",
            name = "Candroid's Template",
            fileTemplates = listOf(
                FileTemplate(
                    fileName = "Repository.kt",
                    filePath = "domain/repository",
                    fileContent = "interface {{MODULE_NAME}}Repository {\n    // Define methods here\n}",
                    fileType = "kt"
                ),
            ),
            isDefault = true,
        ),
    )
}