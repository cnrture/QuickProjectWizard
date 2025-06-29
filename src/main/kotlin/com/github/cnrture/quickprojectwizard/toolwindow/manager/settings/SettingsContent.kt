package com.github.cnrture.quickprojectwizard.toolwindow.manager.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.PopupProperties
import com.github.cnrture.quickprojectwizard.common.Constants
import com.github.cnrture.quickprojectwizard.common.Utils
import com.github.cnrture.quickprojectwizard.components.*
import com.github.cnrture.quickprojectwizard.components.QPWDropdownItem
import com.github.cnrture.quickprojectwizard.data.FeatureTemplate
import com.github.cnrture.quickprojectwizard.data.ModuleTemplate
import com.github.cnrture.quickprojectwizard.service.AnalyticsService
import com.github.cnrture.quickprojectwizard.service.SettingsService
import com.github.cnrture.quickprojectwizard.theme.QPWTheme
import com.github.cnrture.quickprojectwizard.toolwindow.manager.settings.dialog.*
import com.intellij.openapi.project.Project

@Composable
fun SettingsContent(project: Project) {
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
                    text = "Module",
                    isSelected = selectedTab == "templates",
                    color = QPWTheme.colors.lightGray,
                    onTabSelected = { selectedTab = "templates" }
                )
                QPWTabRow(
                    text = "Feature",
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
                            project = project,
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
                            onRefreshTriggered = { triggerRefresh() },
                            onSetDefault = { template ->
                                settings.setDefaultModuleTemplate(template.id)
                                triggerRefresh()
                                analyticsService.track("default_module_template_set")
                                Utils.showInfo(
                                    title = "Quick Project Wizard",
                                    message = "Default template set to '${template.name}' successfully!",
                                )
                            },
                            onImport = {
                                Utils.importModuleTemplate(project) { template, message ->
                                    if (template != null) {
                                        val updatedTemplate = template.copy(
                                            id = java.util.UUID.randomUUID().toString(),
                                            isDefault = false
                                        )
                                        settings.addModuleTemplate(updatedTemplate)
                                        triggerRefresh()
                                        analyticsService.track("module_template_imported")
                                    }
                                    Utils.showInfo(
                                        title = "Quick Project Wizard",
                                        message = message,
                                    )
                                }
                            },
                        )
                    }

                    "feature_templates" -> {
                        analyticsService.track("view_feature_templates")
                        FeatureTemplatesTab(
                            project = project,
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
                            onRefreshTriggered = { triggerRefresh() },
                            onSetDefault = { template ->
                                settings.setDefaultFeatureTemplate(template.id)
                                triggerRefresh()
                                analyticsService.track("default_feature_template_set")
                                Utils.showInfo(
                                    title = "Quick Project Wizard",
                                    message = "Default template set to '${template.name}' successfully!",
                                )
                            },
                            onImport = {
                                Utils.importFeatureTemplate(project) { template, message ->
                                    if (template != null) {
                                        val updatedTemplate = template.copy(
                                            id = java.util.UUID.randomUUID().toString(),
                                            name = "${template.name} (Copy)",
                                            isDefault = false
                                        )
                                        settings.addFeatureTemplate(updatedTemplate)
                                        triggerRefresh()
                                        analyticsService.track("feature_template_imported")
                                    }
                                    Utils.showInfo(
                                        title = "Quick Project Wizard",
                                        message = message,
                                    )
                                }
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ModuleTemplatesTab(
    project: Project,
    templates: List<ModuleTemplate>,
    defaultTemplateId: String,
    onTemplateDelete: (ModuleTemplate) -> Unit,
    onRefreshTriggered: () -> Unit,
    onSetDefault: (ModuleTemplate) -> Unit,
    onImport: () -> Unit,
) {
    var isReviewDialogVisible by remember { mutableStateOf(Pair(false, ModuleTemplate.EMPTY)) }
    var isCreateDialogVisible by remember { mutableStateOf(Pair(false, ModuleTemplate.EMPTY)) }
    var isEditDialogVisible by remember { mutableStateOf(Pair(false, ModuleTemplate.EMPTY)) }
    val settings = SettingsService.getInstance()
    val analyticsService = AnalyticsService.getInstance()

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
                text = "Module",
                color = QPWTheme.colors.white,
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QPWActionCard(
                    title = "Add Template",
                    icon = Icons.Rounded.Add,
                    type = QPWActionCardType.SMALL,
                    actionColor = QPWTheme.colors.green,
                    onClick = { isCreateDialogVisible = Pair(true, ModuleTemplate.EMPTY) }
                )
                QPWActionCard(
                    title = "Import",
                    icon = Icons.Rounded.FileDownload,
                    type = QPWActionCardType.SMALL,
                    actionColor = QPWTheme.colors.lightGray,
                    onClick = onImport,
                )
            }
        }

        templates.forEach { template ->
            ModuleTemplateCard(
                template = template,
                defaultTemplateId = defaultTemplateId,
                onEdit = { isEditDialogVisible = Pair(true, template) },
                onDelete = { if (!template.isDefault) onTemplateDelete(template) },
                onSetDefault = { onSetDefault(template) },
                onExport = {
                    Utils.exportModuleTemplate(project, template) { success, message ->
                        Utils.showInfo("Quick Project Wizard", message)
                    }
                },
                onReview = { isReviewDialogVisible = Pair(true, template) },
                onDuplicate = { templateToDuplicate ->
                    val duplicatedTemplate = templateToDuplicate.copy(
                        id = java.util.UUID.randomUUID().toString(),
                        name = "${templateToDuplicate.name} (Copy)",
                        isDefault = false
                    )
                    settings.addModuleTemplate(duplicatedTemplate)
                    onRefreshTriggered()
                    analyticsService.track("module_template_duplicated")
                    Utils.showInfo(
                        title = "Quick Project Wizard",
                        message = "Template duplicated as '${duplicatedTemplate.name}' successfully!",
                    )
                }
            )
        }

        if (isEditDialogVisible.first) {
            Dialog(
                onDismissRequest = { isEditDialogVisible = Pair(false, ModuleTemplate.EMPTY) },
                properties = DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false,
                    usePlatformDefaultWidth = false,
                )
            ) {
                TemplateEditorContent(
                    template = isEditDialogVisible.second,
                    onCancelClick = {
                        onRefreshTriggered()
                        isEditDialogVisible = Pair(false, ModuleTemplate.EMPTY)
                    },
                    onApplyClick = { updatedTemplate ->
                        settings.saveTemplate(updatedTemplate)
                    },
                    onOkayClick = { updatedTemplate ->
                        settings.saveTemplate(updatedTemplate)
                        analyticsService.track("module_template_updated")
                        Utils.showInfo(
                            title = "Quick Project Wizard",
                            message = "Module template '${updatedTemplate.name}' updated successfully!",
                        )
                        onRefreshTriggered()
                        isEditDialogVisible = Pair(false, ModuleTemplate.EMPTY)
                    },
                )
            }
        }

        if (isCreateDialogVisible.first) {
            Dialog(
                onDismissRequest = { isCreateDialogVisible = Pair(false, ModuleTemplate.EMPTY) },
                properties = DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false,
                    usePlatformDefaultWidth = false,
                )
            ) {
                TemplateCreatorContent(
                    onCancelClick = {
                        onRefreshTriggered()
                        isCreateDialogVisible = Pair(false, ModuleTemplate.EMPTY)
                    },
                    onApplyClick = { template ->
                        settings.saveTemplate(template)
                    },
                    onOkayClick = { template ->
                        settings.saveTemplate(template)
                        analyticsService.track("module_template_added")
                        Utils.showInfo(
                            title = "Quick Project Wizard",
                            message = "Module template '${template.name}' added successfully!",
                        )
                        onRefreshTriggered()
                        isCreateDialogVisible = Pair(false, ModuleTemplate.EMPTY)
                    }
                )
            }
        }

        if (isReviewDialogVisible.first) {
            Dialog(
                onDismissRequest = { isReviewDialogVisible = Pair(false, ModuleTemplate.EMPTY) },
                properties = DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false,
                    usePlatformDefaultWidth = false,
                )
            ) {
                ModuleTemplateReviewContent(
                    template = isReviewDialogVisible.second,
                    onCancelClick = { isReviewDialogVisible = Pair(false, ModuleTemplate.EMPTY) },
                )
            }
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
    onExport: () -> Unit,
    onReview: () -> Unit,
    onDuplicate: (ModuleTemplate) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        backgroundColor = QPWTheme.colors.gray,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
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

            Box {
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        imageVector = Icons.Rounded.MoreVert,
                        contentDescription = "More options",
                        tint = QPWTheme.colors.lightGray
                    )
                }
                DropdownMenu(
                    modifier = Modifier.background(
                        color = QPWTheme.colors.black,
                        shape = RoundedCornerShape(0.dp)
                    ),
                    properties = PopupProperties(dismissOnClickOutside = true),
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    if (template.id != defaultTemplateId) {
                        QPWDropdownItem(
                            text = "Set Default",
                            icon = Icons.Rounded.Check,
                            onClick = { expanded = false; onSetDefault() }
                        )
                    }
                    if (template.id != "candroid_template") {
                        QPWDropdownItem(
                            text = "Edit",
                            icon = Icons.Rounded.Edit,
                            onClick = { expanded = false; onEdit() }
                        )
                    } else {
                        QPWDropdownItem(
                            text = "Review",
                            icon = Icons.Rounded.RemoveRedEye,
                            onClick = { expanded = false; onReview() }
                        )
                    }
                    QPWDropdownItem(
                        text = "Duplicate",
                        icon = Icons.Rounded.ContentCopy,
                        onClick = { expanded = false; onDuplicate(template) }
                    )
                    QPWDropdownItem(
                        text = "Export",
                        icon = Icons.Rounded.Upload,
                        onClick = { expanded = false; onExport() }
                    )
                    if (!template.isDefault || template.id != "candroid_template") {
                        QPWDropdownItem(
                            text = "Delete",
                            icon = Icons.Rounded.Delete,
                            onClick = { expanded = false; onDelete() }
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
    project: Project,
    templates: List<FeatureTemplate>,
    defaultTemplateId: String,
    onTemplateDelete: (FeatureTemplate) -> Unit,
    onRefreshTriggered: () -> Unit,
    onSetDefault: (FeatureTemplate) -> Unit,
    onImport: () -> Unit,
) {
    var isCreateDialogVisible by remember { mutableStateOf(Pair(false, FeatureTemplate.EMPTY)) }
    var isEditDialogVisible by remember { mutableStateOf(Pair(false, FeatureTemplate.EMPTY)) }
    var isReviewDialogVisible by remember { mutableStateOf(Pair(false, FeatureTemplate.EMPTY)) }
    val settings = SettingsService.getInstance()
    val analyticsService = AnalyticsService.getInstance()

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
                text = "Feature",
                color = QPWTheme.colors.white,
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QPWActionCard(
                    title = "Add Template",
                    icon = Icons.Rounded.Add,
                    type = QPWActionCardType.SMALL,
                    actionColor = QPWTheme.colors.green,
                    onClick = { isCreateDialogVisible = Pair(true, FeatureTemplate.EMPTY) }
                )
                QPWActionCard(
                    title = "Import",
                    icon = Icons.Rounded.FileDownload,
                    type = QPWActionCardType.SMALL,
                    actionColor = QPWTheme.colors.lightGray,
                    onClick = onImport,
                )
            }
        }

        templates.forEach { template ->
            FeatureTemplateCard(
                template = template,
                defaultTemplateId = defaultTemplateId,
                onEdit = { isEditDialogVisible = Pair(true, template) },
                onDelete = { if (!template.isDefault) onTemplateDelete(template) },
                onSetDefault = { onSetDefault(template) },
                onExport = {
                    Utils.exportFeatureTemplate(project, template) { success, message ->
                        Utils.showInfo("Quick Project Wizard", message)
                    }
                },
                onReview = { isReviewDialogVisible = Pair(true, template) },
                onDuplicate = { templateToDuplicate ->
                    val duplicatedTemplate = templateToDuplicate.copy(
                        id = java.util.UUID.randomUUID().toString(),
                        name = "${templateToDuplicate.name} (Copy)",
                        isDefault = false
                    )
                    settings.addFeatureTemplate(duplicatedTemplate)
                    onRefreshTriggered()
                    analyticsService.track("feature_template_duplicated")
                    Utils.showInfo(
                        title = "Quick Project Wizard",
                        message = "Template duplicated as '${duplicatedTemplate.name}' successfully!",
                    )
                }
            )
        }

        if (isEditDialogVisible.first) {
            Dialog(
                onDismissRequest = {},
                properties = DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false,
                    usePlatformDefaultWidth = false,
                )
            ) {
                FeatureTemplateEditorContent(
                    template = isEditDialogVisible.second,
                    onCancelClick = {
                        onRefreshTriggered()
                        isEditDialogVisible = Pair(false, FeatureTemplate.EMPTY)
                    },
                    onApplyClick = { updatedTemplate ->
                        settings.saveFeatureTemplate(updatedTemplate)
                    },
                    onOkayClick = { updatedTemplate ->
                        settings.saveFeatureTemplate(updatedTemplate)
                        analyticsService.track("feature_template_updated")
                        Utils.showInfo(
                            title = "Quick Project Wizard",
                            message = "Feature template '${updatedTemplate.name}' updated successfully!",
                        )
                        onRefreshTriggered()
                        isEditDialogVisible = Pair(false, FeatureTemplate.EMPTY)
                    },
                )
            }
        }

        if (isCreateDialogVisible.first) {
            Dialog(
                onDismissRequest = {},
                properties = DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false,
                    usePlatformDefaultWidth = false,
                )
            ) {
                FeatureTemplateCreatorContent(
                    onCancelClick = {
                        onRefreshTriggered()
                        isCreateDialogVisible = Pair(false, FeatureTemplate.EMPTY)
                    },
                    onApplyClick = { template ->
                        settings.saveFeatureTemplate(template)
                    },
                    onOkayClick = { template ->
                        settings.saveFeatureTemplate(template)
                        analyticsService.track("feature_template_added")
                        Utils.showInfo(
                            title = "Quick Project Wizard",
                            message = "Feature template '${template.name}' added successfully!",
                        )
                        onRefreshTriggered()
                        isCreateDialogVisible = Pair(false, FeatureTemplate.EMPTY)
                    }
                )
            }
        }

        if (isReviewDialogVisible.first) {
            Dialog(
                onDismissRequest = { isReviewDialogVisible = Pair(false, FeatureTemplate.EMPTY) },
                properties = DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false,
                    usePlatformDefaultWidth = false,
                )
            ) {
                FeatureTemplateReviewContent(
                    template = isReviewDialogVisible.second,
                    onCancelClick = { isReviewDialogVisible = Pair(false, FeatureTemplate.EMPTY) },
                )
            }
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
    onExport: () -> Unit,
    onReview: () -> Unit,
    onDuplicate: (FeatureTemplate) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        backgroundColor = QPWTheme.colors.gray,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
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

            Box {
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        imageVector = Icons.Rounded.MoreVert,
                        contentDescription = "More options",
                        tint = QPWTheme.colors.lightGray
                    )
                }
                DropdownMenu(
                    modifier = Modifier.background(
                        color = QPWTheme.colors.black,
                        shape = RoundedCornerShape(0.dp)
                    ),
                    properties = PopupProperties(dismissOnClickOutside = true),
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    if (template.id != defaultTemplateId) {
                        QPWDropdownItem(
                            text = "Set Default",
                            icon = Icons.Rounded.Check,
                            onClick = { expanded = false; onSetDefault() }
                        )
                    }
                    if (template.id != "candroid_template") {
                        QPWDropdownItem(
                            text = "Edit",
                            icon = Icons.Rounded.Edit,
                            onClick = { expanded = false; onEdit() }
                        )
                    } else {
                        QPWDropdownItem(
                            text = "Review",
                            icon = Icons.Rounded.RemoveRedEye,
                            onClick = { expanded = false; onReview() }
                        )
                    }
                    QPWDropdownItem(
                        text = "Duplicate",
                        icon = Icons.Rounded.ContentCopy,
                        onClick = { expanded = false; onDuplicate(template) }
                    )
                    QPWDropdownItem(
                        text = "Export",
                        icon = Icons.Rounded.Upload,
                        onClick = { expanded = false; onExport() }
                    )
                    if (!template.isDefault || template.id != "candroid_template") {
                        QPWDropdownItem(
                            text = "Delete",
                            icon = Icons.Rounded.Delete,
                            onClick = { expanded = false; onDelete() }
                        )
                    }
                }
            }
        }
    }
}
