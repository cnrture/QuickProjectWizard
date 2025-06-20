package com.github.cnrture.quickprojectwizard.toolwindow.manager.settings.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material.icons.rounded.Save
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.cnrture.quickprojectwizard.components.*
import com.github.cnrture.quickprojectwizard.service.SettingsService
import com.github.cnrture.quickprojectwizard.theme.QPWTheme
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.vfs.VirtualFile
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ExportSettingsDialog(
    private val settings: SettingsService,
    private val onComplete: (Boolean, String) -> Unit,
) : QPWDialogWrapper(600, 400) {

    @Composable
    override fun createDesign() {
        ExportSettingsContent(
            settings = settings,
            onExport = { success, message ->
                onComplete(success, message)
                close(if (success) 0 else 1)
            },
            onCancel = { close(1) }
        )
    }
}

@Composable
private fun ExportSettingsContent(
    settings: SettingsService,
    onExport: (Boolean, String) -> Unit,
    onCancel: () -> Unit,
) {
    var selectedDirectory by remember { mutableStateOf<VirtualFile?>(null) }
    var fileName by remember {
        mutableStateOf(
            "qpw-settings-${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))}"
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(QPWTheme.colors.black)
            .padding(24.dp)
    ) {
        QPWText(
            text = "Export Settings",
            color = QPWTheme.colors.white,
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        QPWText(
            text = "Export all your templates and configurations to a JSON file",
            color = QPWTheme.colors.lightGray,
            style = TextStyle(fontSize = 14.sp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Column {
            QPWText(
                text = "Export Location",
                color = QPWTheme.colors.white,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QPWText(
                    modifier = Modifier.weight(1f),
                    text = selectedDirectory?.path ?: "Select a directory...",
                    color = if (selectedDirectory != null) QPWTheme.colors.white else QPWTheme.colors.lightGray,
                    style = TextStyle(fontSize = 14.sp)
                )

                QPWActionCard(
                    title = "Browse",
                    icon = Icons.Rounded.FolderOpen,
                    type = QPWActionCardType.SMALL,
                    actionColor = QPWTheme.colors.lightGray,
                    onClick = {
                        val project = ProjectManager.getInstance().defaultProject
                        val descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor()
                        descriptor.title = "Select Export Directory"

                        FileChooser.chooseFile(descriptor, project, null) { directory ->
                            selectedDirectory = directory
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column {
            QPWText(
                text = "File Name",
                color = QPWTheme.colors.white,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            QPWTextField(
                modifier = Modifier.fillMaxWidth(),
                placeholder = "Enter filename (without .json)",
                color = QPWTheme.colors.white,
                value = fileName,
                onValueChange = { fileName = it }
            )

            Spacer(modifier = Modifier.height(4.dp))

            QPWText(
                text = "File will be saved as: $fileName.json",
                color = QPWTheme.colors.lightGray,
                style = TextStyle(fontSize = 12.sp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End)
        ) {
            QPWActionCard(
                title = "Cancel",
                type = QPWActionCardType.MEDIUM,
                actionColor = QPWTheme.colors.lightGray,
                onClick = onCancel
            )

            QPWActionCard(
                title = "Export",
                icon = Icons.Rounded.Save,
                type = QPWActionCardType.MEDIUM,
                actionColor = QPWTheme.colors.green,
                onClick = {
                    if (selectedDirectory != null && fileName.isNotBlank()) {
                        val filePath = "${selectedDirectory!!.path}/$fileName.json"
                        val success = settings.exportToFile(filePath)
                        val message = if (success) {
                            "Settings exported successfully to:\n$fileName.json"
                        } else {
                            "Failed to export settings. Please check permissions."
                        }
                        onExport(success, message)
                    } else {
                        onExport(false, "Please select a directory and enter a filename.")
                    }
                }
            )
        }
    }
}