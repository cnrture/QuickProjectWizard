package com.github.cnrture.quickprojectwizard.toolwindow.manager.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.cnrture.quickprojectwizard.common.Constants
import com.github.cnrture.quickprojectwizard.components.QPWActionCard
import com.github.cnrture.quickprojectwizard.components.QPWActionCardType
import com.github.cnrture.quickprojectwizard.components.QPWRadioButton
import com.github.cnrture.quickprojectwizard.components.QPWTextField
import com.github.cnrture.quickprojectwizard.components.QPWText
import com.github.cnrture.quickprojectwizard.data.ModuleTemplate
import com.github.cnrture.quickprojectwizard.data.FileTemplate
import com.github.cnrture.quickprojectwizard.data.SettingsService
import com.github.cnrture.quickprojectwizard.dialog.TemplateCreatorDialog
import com.github.cnrture.quickprojectwizard.dialog.TemplateEditorDialog
import com.github.cnrture.quickprojectwizard.theme.QPWTheme
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service

@Composable
fun SettingsContent() {
    val settings = ApplicationManager.getApplication().service<SettingsService>()
    var currentSettings by mutableStateOf(settings.state.copy())
    var selectedTab by mutableStateOf("general")
    var refreshTrigger by remember { mutableIntStateOf(0) }

    // Default template'ları ekle (ilk kez)
    if (currentSettings.moduleTemplates.isEmpty()) {
        currentSettings.moduleTemplates.addAll(getDefaultTemplates())
    }

    // Refresh settings when trigger changes
    LaunchedEffect(refreshTrigger) {
        currentSettings = settings.state.copy()
        if (currentSettings.moduleTemplates.isEmpty()) {
            currentSettings.moduleTemplates.addAll(getDefaultTemplates())
        }
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
                    onClick = {
                        settings.loadState(currentSettings)
                        refreshTrigger++ // UI'yi yenile
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
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
            )

            Spacer(modifier = Modifier.size(24.dp))

            // Tabs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SettingsTab(
                    title = "General",
                    isSelected = selectedTab == "general",
                    onClick = { selectedTab = "general" }
                )
                SettingsTab(
                    title = "Module Templates",
                    isSelected = selectedTab == "templates",
                    onClick = { selectedTab = "templates" }
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
                        defaultPackageName = currentSettings.defaultPackageName,
                        preferredModuleType = currentSettings.preferredModuleType,
                        onPackageNameChange = { newPackageName ->
                            currentSettings = currentSettings.copy(defaultPackageName = newPackageName)
                        },
                        onModuleTypeChange = { newModuleType ->
                            currentSettings = currentSettings.copy(preferredModuleType = newModuleType)
                        }
                    )

                    "templates" -> ModuleTemplatesTab(
                        templates = currentSettings.moduleTemplates,
                        onTemplateDelete = { template ->
                            if (!template.isDefault) {
                                settings.removeTemplate(template)
                                refreshTrigger++ // UI'yi hemen yenile
                            }
                        },
                        onTemplateAdd = { newTemplate ->
                            settings.saveTemplate(newTemplate)
                            refreshTrigger++ // UI'yi hemen yenile
                        },
                        onTemplateEdit = { oldTemplate, updatedTemplate ->
                            settings.saveTemplate(updatedTemplate)
                            refreshTrigger++ // UI'yi hemen yenile
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsTab(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        backgroundColor = if (isSelected) QPWTheme.colors.lightGray.copy(alpha = 0.2f) else QPWTheme.colors.gray,
        elevation = if (isSelected) 4.dp else 0.dp
    ) {
        QPWText(
            text = title,
            color = if (isSelected) QPWTheme.colors.white else QPWTheme.colors.lightGray,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            ),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )
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
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
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
                    Spacer(modifier = Modifier.height(4.dp))
                    QPWText(
                        text = template.description,
                        color = QPWTheme.colors.lightGray,
                        style = TextStyle(fontSize = 13.sp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    QPWText(
                        text = "Type: ${template.moduleType}",
                        color = QPWTheme.colors.lightGray,
                        style = TextStyle(fontSize = 11.sp)
                    )
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
                    if (!template.isDefault) {
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

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = QPWTheme.colors.lightGray.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(8.dp))

            // Package Structure Preview
            QPWText(
                text = "Package Structure:",
                color = QPWTheme.colors.lightGray,
                style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold)
            )
            template.packageStructure.take(3).forEach { packagePath ->
                QPWText(
                    text = "• $packagePath",
                    color = QPWTheme.colors.lightGray,
                    style = TextStyle(fontSize = 10.sp),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            if (template.packageStructure.size > 3) {
                QPWText(
                    text = "... and ${template.packageStructure.size - 3} more",
                    color = QPWTheme.colors.lightGray,
                    style = TextStyle(fontSize = 10.sp),
                    modifier = Modifier.padding(start = 8.dp)
                )
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
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
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
            id = "clean_architecture",
            name = "Clean Architecture",
            description = "MVVM + Clean Architecture with Repository, UseCase, ViewModel",
            moduleType = Constants.ANDROID,
            packageStructure = listOf(
                "data/local",
                "data/remote",
                "data/repository",
                "domain/model",
                "domain/repository",
                "domain/usecase",
                "presentation/viewmodel",
                "presentation/ui"
            ),
            fileTemplates = listOf(
                FileTemplate(
                    fileName = "Repository.kt",
                    filePath = "domain/repository",
                    fileContent = "interface {{MODULE_NAME}}Repository {\n    // Define methods here\n}",
                    fileType = "kt"
                ),
                FileTemplate(
                    fileName = "ViewModel.kt",
                    filePath = "presentation/viewmodel",
                    fileContent = "@HiltViewModel\nclass {{MODULE_NAME}}ViewModel @Inject constructor() : ViewModel() {\n    // ViewModel implementation\n}",
                    fileType = "kt"
                )
            ),
            isDefault = true
        ),
        ModuleTemplate(
            id = "simple_mvvm",
            name = "Simple MVVM",
            description = "Basic MVVM pattern with ViewModel and Repository",
            moduleType = Constants.ANDROID,
            packageStructure = listOf(
                "data/repository",
                "presentation/viewmodel",
                "presentation/ui"
            ),
            fileTemplates = listOf(
                FileTemplate(
                    fileName = "Repository.kt",
                    filePath = "data/repository",
                    fileContent = "@Singleton\nclass {{MODULE_NAME}}Repository @Inject constructor() {\n    // Repository implementation\n}",
                    fileType = "kt"
                )
            ),
            isDefault = true
        )
    )
}
