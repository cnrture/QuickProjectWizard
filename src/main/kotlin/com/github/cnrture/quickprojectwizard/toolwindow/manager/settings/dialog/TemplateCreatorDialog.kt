package com.github.cnrture.quickprojectwizard.toolwindow.manager.settings.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.cnrture.quickprojectwizard.common.Constants
import com.github.cnrture.quickprojectwizard.components.QPWActionCard
import com.github.cnrture.quickprojectwizard.components.QPWActionCardType
import com.github.cnrture.quickprojectwizard.components.QPWText
import com.github.cnrture.quickprojectwizard.components.QPWTextField
import com.github.cnrture.quickprojectwizard.data.FeatureTemplate
import com.github.cnrture.quickprojectwizard.data.FileTemplate
import com.github.cnrture.quickprojectwizard.data.ModuleTemplate
import com.github.cnrture.quickprojectwizard.theme.QPWTheme
import com.github.cnrture.quickprojectwizard.toolwindow.manager.settings.component.FileTemplateEditor
import java.util.*

@Composable
fun TemplateCreatorContent(
    onCancelClick: () -> Unit,
    onApplyClick: (ModuleTemplate) -> Unit,
    onOkayClick: (ModuleTemplate) -> Unit,
) {
    var templateName by remember { mutableStateOf("") }
    val fileTemplates = remember { mutableStateListOf<FileTemplate>() }
    var id by remember { mutableStateOf(Constants.EMPTY) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 80.dp, horizontal = 32.dp)
            .background(
                color = QPWTheme.colors.black,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(24.dp)
    ) {
        Row {
            QPWText(
                text = "Create New Module Template",
                color = QPWTheme.colors.white,
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = "Close",
                tint = QPWTheme.colors.lightGray,
                modifier = Modifier
                    .size(32.dp)
                    .clickable { onCancelClick() }
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
                title = "Apply",
                type = QPWActionCardType.MEDIUM,
                actionColor = QPWTheme.colors.green,
                isEnabled = templateName.isNotBlank() && fileTemplates.any { it.fileName.isNotBlank() },
                onClick = {
                    if (templateName.isNotBlank()) {
                        val template = ModuleTemplate(
                            id = id.ifEmpty { UUID.randomUUID().toString().apply { id = this } },
                            name = templateName,
                            fileTemplates = fileTemplates.filter { it.fileName.isNotBlank() },
                            isDefault = false
                        )
                        onApplyClick(template)
                    }
                }
            )

            QPWActionCard(
                title = "Okay",
                type = QPWActionCardType.MEDIUM,
                actionColor = QPWTheme.colors.green,
                isEnabled = templateName.isNotBlank() && fileTemplates.any { it.fileName.isNotBlank() },
                onClick = {
                    if (templateName.isNotBlank()) {
                        val template = ModuleTemplate(
                            id = id.ifEmpty { UUID.randomUUID().toString().apply { id = this } },
                            name = templateName,
                            fileTemplates = fileTemplates.filter { it.fileName.isNotBlank() },
                            isDefault = false
                        )
                        onOkayClick(template)
                    }
                },
            )
        }
    }
}

@Composable
fun FeatureTemplateCreatorContent(
    onCancelClick: () -> Unit,
    onApplyClick: (FeatureTemplate) -> Unit,
    onOkayClick: (FeatureTemplate) -> Unit,
) {
    var templateName by remember { mutableStateOf("") }
    val fileTemplates = remember { mutableStateListOf<FileTemplate>() }
    var id by remember { mutableStateOf(Constants.EMPTY) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 80.dp, horizontal = 32.dp)
            .background(
                color = QPWTheme.colors.black,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(24.dp)
    ) {
        Row {
            QPWText(
                text = "Create New Feature Template",
                color = QPWTheme.colors.white,
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = "Close",
                tint = QPWTheme.colors.lightGray,
                modifier = Modifier
                    .size(32.dp)
                    .clickable { onCancelClick() }
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
                title = "Apply",
                type = QPWActionCardType.MEDIUM,
                actionColor = QPWTheme.colors.green,
                isEnabled = templateName.isNotBlank() && fileTemplates.any { it.fileName.isNotBlank() },
                onClick = {
                    if (templateName.isNotBlank()) {
                        val template = FeatureTemplate(
                            id = id.ifEmpty { UUID.randomUUID().toString().apply { id = this } },
                            name = templateName,
                            fileTemplates = fileTemplates.filter { it.fileName.isNotBlank() },
                            isDefault = false
                        )
                        onApplyClick(template)
                    }
                },
            )

            QPWActionCard(
                title = "Okay",
                type = QPWActionCardType.MEDIUM,
                actionColor = QPWTheme.colors.green,
                isEnabled = templateName.isNotBlank() && fileTemplates.any { it.fileName.isNotBlank() },
                onClick = {
                    if (templateName.isNotBlank()) {
                        val template = FeatureTemplate(
                            id = id.ifEmpty { UUID.randomUUID().toString().apply { id = this } },
                            name = templateName,
                            fileTemplates = fileTemplates.filter { it.fileName.isNotBlank() },
                            isDefault = false
                        )
                        onOkayClick(template)
                    }
                },
            )
        }
    }
}
