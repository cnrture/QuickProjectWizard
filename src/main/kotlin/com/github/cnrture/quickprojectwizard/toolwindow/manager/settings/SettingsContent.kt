package com.github.cnrture.quickprojectwizard.toolwindow.manager.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Save
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.cnrture.quickprojectwizard.service.AnalyticsService
import com.github.cnrture.quickprojectwizard.common.Constants
import com.github.cnrture.quickprojectwizard.common.Utils
import com.github.cnrture.quickprojectwizard.components.*
import com.github.cnrture.quickprojectwizard.data.FeatureTemplate
import com.github.cnrture.quickprojectwizard.data.ModuleTemplate
import com.github.cnrture.quickprojectwizard.service.SettingsService
import com.github.cnrture.quickprojectwizard.theme.QPWTheme
import com.github.cnrture.quickprojectwizard.toolwindow.manager.settings.dialog.FeatureTemplateCreatorDialog
import com.github.cnrture.quickprojectwizard.toolwindow.manager.settings.dialog.FeatureTemplateEditorDialog
import com.github.cnrture.quickprojectwizard.toolwindow.manager.settings.dialog.TemplateCreatorDialog
import com.github.cnrture.quickprojectwizard.toolwindow.manager.settings.dialog.TemplateEditorDialog

@Composable
fun SettingsContent() {
    val settings = SettingsService.getInstance()
    val analyticsService = AnalyticsService.getInstance()
    var currentSettings by mutableStateOf(settings.state.copy())
    var selectedTab by mutableStateOf("general")
    var selectedModuleType by mutableStateOf(currentSettings.preferredModuleType)
    var packageName by mutableStateOf(currentSettings.defaultPackageName)

    var refreshTrigger by remember { mutableStateOf(0) }
    val moduleTemplates by remember(refreshTrigger) {
        mutableStateOf(settings.getModuleTemplates())
    }
    val featureTemplates by remember(refreshTrigger) {
        mutableStateOf(settings.getFeatureTemplates())
    }

    val triggerRefresh = { refreshTrigger++ }
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(QPWTheme.colors.black)
            .padding(24.dp),
        backgroundColor = QPWTheme.colors.black,
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
                QPWTabRow(
                    text = "Feature Templates",
                    isSelected = selectedTab == "feature_templates",
                    color = QPWTheme.colors.lightGray,
                    onTabSelected = { selectedTab = "feature_templates" }
                )
            }

            Spacer(modifier = Modifier.size(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                when (selectedTab) {
                    "general" -> {
                        analyticsService.track("view_general_settings")
                        GeneralSettingsTab(
                            defaultPackageName = packageName,
                            preferredModuleType = selectedModuleType,
                            onSaveClick = {
                                currentSettings = currentSettings.copy(
                                    defaultPackageName = packageName,
                                    preferredModuleType = selectedModuleType
                                )
                                settings.loadState(currentSettings)
                                triggerRefresh()
                                analyticsService.track("settings_saved")
                                Utils.showInfo(
                                    title = "Quick Project Wizard",
                                    message = "Settings saved successfully!",
                                )
                            },
                            onPackageNameChange = { packageName = it },
                            onModuleTypeChange = { selectedModuleType = it }
                        )
                    }

                    "templates" -> {
                        analyticsService.track("view_module_templates")
                        ModuleTemplatesTab(
                            templates = moduleTemplates,
                            defaultTemplateId = currentSettings.defaultModuleTemplateId,
                            onTemplateDelete = { template ->
                                if (!template.isDefault) {
                                    settings.removeTemplate(template)
                                    triggerRefresh()
                                    analyticsService.track("module_template_deleted")
                                    Utils.showInfo(
                                        title = "Quick Project Wizard",
                                        message = "Template '${template.name}' deleted successfully!",
                                    )
                                }
                            },
                            onTemplateAdd = { newTemplate ->
                                settings.saveTemplate(newTemplate)
                                triggerRefresh()
                                analyticsService.track("module_template_added")
                                Utils.showInfo(
                                    title = "Quick Project Wizard",
                                    message = "Template '${newTemplate.name}' added successfully!",
                                )
                            },
                            onTemplateEdit = { oldTemplate, updatedTemplate ->
                                settings.saveTemplate(updatedTemplate)
                                triggerRefresh()
                                analyticsService.track("module_template_updated")
                                Utils.showInfo(
                                    title = "Quick Project Wizard",
                                    message = "Template '${updatedTemplate.name}' updated successfully!",
                                )
                            },
                            onSetDefault = { template ->
                                settings.setDefaultModuleTemplate(template.id)
                                triggerRefresh()
                                analyticsService.track("default_module_template_set")
                                Utils.showInfo(
                                    title = "Quick Project Wizard",
                                    message = "Default template set to '${template.name}' successfully!",
                                )
                            }
                        )
                    }

                    "feature_templates" -> {
                        analyticsService.track("view_feature_templates")
                        FeatureTemplatesTab(
                            templates = featureTemplates,
                            defaultTemplateId = currentSettings.defaultFeatureTemplateId,
                            onTemplateDelete = { template ->
                                if (!template.isDefault) {
                                    settings.removeFeatureTemplate(template)
                                    triggerRefresh()
                                    analyticsService.track("feature_template_deleted")
                                    Utils.showInfo(
                                        title = "Quick Project Wizard",
                                        message = "Feature template '${template.name}' deleted successfully!",
                                    )
                                }
                            },
                            onTemplateAdd = { newTemplate ->
                                settings.saveFeatureTemplate(newTemplate)
                                triggerRefresh()
                                analyticsService.track("feature_template_added")
                                Utils.showInfo(
                                    title = "Quick Project Wizard",
                                    message = "Feature template '${newTemplate.name}' added successfully!",
                                )
                            },
                            onTemplateEdit = { oldTemplate, updatedTemplate ->
                                settings.saveFeatureTemplate(updatedTemplate)
                                triggerRefresh()
                                analyticsService.track("feature_template_updated")
                                Utils.showInfo(
                                    title = "Quick Project Wizard",
                                    message = "Feature template '${updatedTemplate.name}' updated successfully!",
                                )
                            },
                            onSetDefault = { template ->
                                settings.setDefaultFeatureTemplate(template.id)
                                triggerRefresh()
                                analyticsService.track("default_feature_template_set")
                                Utils.showInfo(
                                    title = "Quick Project Wizard",
                                    message = "Default feature template set to '${template.name}' successfully!",
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ModuleTemplatesTab(
    templates: List<ModuleTemplate>,
    defaultTemplateId: String,
    onTemplateDelete: (ModuleTemplate) -> Unit,
    onTemplateAdd: (ModuleTemplate) -> Unit,
    onTemplateEdit: (ModuleTemplate, ModuleTemplate) -> Unit,
    onSetDefault: (ModuleTemplate) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
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
                icon = Icons.Rounded.Add,
                type = QPWActionCardType.SMALL,
                actionColor = QPWTheme.colors.green,
                onClick = {
                    TemplateCreatorDialog(onTemplateCreated = { newTemplate ->
                        onTemplateAdd(newTemplate)
                    }).show()
                }
            )
        }

        templates.forEach { template ->
            ModuleTemplateCard(
                template = template,
                defaultTemplateId = defaultTemplateId,
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
                },
                onSetDefault = {
                    onSetDefault(template)
                }
            )
        }
    }
}

@Composable
private fun ModuleTemplateCard(
    template: ModuleTemplate,
    defaultTemplateId: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onSetDefault: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        backgroundColor = QPWTheme.colors.gray,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
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
                    if (template.id == defaultTemplateId) {
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
                if (template.id != defaultTemplateId) {
                    QPWActionCard(
                        title = "Set Default",
                        icon = Icons.Rounded.Edit,
                        type = QPWActionCardType.SMALL,
                        actionColor = QPWTheme.colors.lightGray,
                        onClick = onSetDefault
                    )
                }
                QPWActionCard(
                    title = "Edit",
                    icon = Icons.Rounded.Edit,
                    type = QPWActionCardType.SMALL,
                    actionColor = QPWTheme.colors.lightGray,
                    onClick = {
                        onEdit()
                    }
                )
                if (!template.isDefault || template.id != "candroid_template") {
                    QPWActionCard(
                        title = "Delete",
                        icon = Icons.Rounded.Delete,
                        type = QPWActionCardType.SMALL,
                        actionColor = QPWTheme.colors.red,
                        onClick = onDelete
                    )
                }
            }
        }
    }
}

@Composable
private fun GeneralSettingsTab(
    defaultPackageName: String,
    preferredModuleType: String,
    onSaveClick: () -> Unit,
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
        QPWActionCard(
            modifier = Modifier.align(Alignment.End),
            title = "Save",
            icon = Icons.Rounded.Save,
            actionColor = QPWTheme.colors.lightGray,
            type = QPWActionCardType.MEDIUM,
            onClick = onSaveClick,
        )
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

@Composable
private fun FeatureTemplatesTab(
    templates: List<FeatureTemplate>,
    defaultTemplateId: String,
    onTemplateDelete: (FeatureTemplate) -> Unit,
    onTemplateAdd: (FeatureTemplate) -> Unit,
    onTemplateEdit: (FeatureTemplate, FeatureTemplate) -> Unit,
    onSetDefault: (FeatureTemplate) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            QPWText(
                text = "Feature Templates",
                color = QPWTheme.colors.white,
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            QPWActionCard(
                title = "Add Template",
                icon = Icons.Rounded.Add,
                type = QPWActionCardType.SMALL,
                actionColor = QPWTheme.colors.green,
                onClick = {
                    FeatureTemplateCreatorDialog(onTemplateCreated = { newTemplate ->
                        onTemplateAdd(newTemplate)
                    }).show()
                }
            )
        }

        templates.forEach { template ->
            FeatureTemplateCard(
                template = template,
                defaultTemplateId = defaultTemplateId,
                onEdit = {
                    FeatureTemplateEditorDialog(
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
                },
                onSetDefault = {
                    onSetDefault(template)
                }
            )
        }
    }
}

@Composable
private fun FeatureTemplateCard(
    template: FeatureTemplate,
    defaultTemplateId: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onSetDefault: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        backgroundColor = QPWTheme.colors.gray,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
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
                    if (template.id == defaultTemplateId) {
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
                if (template.id != defaultTemplateId) {
                    QPWActionCard(
                        title = "Set Default",
                        icon = Icons.Rounded.Edit,
                        type = QPWActionCardType.SMALL,
                        actionColor = QPWTheme.colors.lightGray,
                        onClick = onSetDefault
                    )
                }
                QPWActionCard(
                    title = "Edit",
                    icon = Icons.Rounded.Edit,
                    type = QPWActionCardType.SMALL,
                    actionColor = QPWTheme.colors.lightGray,
                    onClick = {
                        onEdit()
                    }
                )
                if (!template.isDefault || template.id != "candroid_template") {
                    QPWActionCard(
                        title = "Delete",
                        icon = Icons.Rounded.Delete,
                        type = QPWActionCardType.SMALL,
                        actionColor = QPWTheme.colors.red,
                        onClick = onDelete
                    )
                }
            }
        }
    }
}
