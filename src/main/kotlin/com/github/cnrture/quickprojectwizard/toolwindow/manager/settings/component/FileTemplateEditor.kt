package com.github.cnrture.quickprojectwizard.toolwindow.manager.settings.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.cnrture.quickprojectwizard.components.QPWActionCard
import com.github.cnrture.quickprojectwizard.components.QPWActionCardType
import com.github.cnrture.quickprojectwizard.components.QPWTextField
import com.github.cnrture.quickprojectwizard.data.FileTemplate
import com.github.cnrture.quickprojectwizard.theme.QPWTheme

@Composable
fun FileTemplateEditor(
    fileTemplate: FileTemplate,
    isModuleEdit: Boolean,
    onUpdate: (FileTemplate) -> Unit,
    onDelete: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        backgroundColor = QPWTheme.colors.gray,
        elevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QPWTextField(
                        placeholder = "ex. Repository.kt",
                        color = QPWTheme.colors.white,
                        value = fileTemplate.fileName,
                        onValueChange = { onUpdate(fileTemplate.copy(fileName = it)) }
                    )
                }
                if (isModuleEdit) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QPWTextField(
                            placeholder = "ex. domain.repository",
                            color = QPWTheme.colors.white,
                            value = fileTemplate.filePath,
                            onValueChange = { onUpdate(fileTemplate.copy(filePath = it)) }
                        )
                    }
                }
            }
            QPWTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                placeholder = "package {FILE_PACKAGE}\n\ninterface {NAME}Repository {\n    // Define methods here\n}",
                color = QPWTheme.colors.white,
                textStyle = TextStyle(
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                ),
                value = fileTemplate.fileContent,
                onValueChange = { onUpdate(fileTemplate.copy(fileContent = it)) },
                isSingleLine = false,
            )
            QPWActionCard(
                modifier = Modifier.align(Alignment.End),
                title = "Delete File Template",
                icon = Icons.Rounded.Delete,
                type = QPWActionCardType.SMALL,
                actionColor = QPWTheme.colors.red,
                onClick = onDelete
            )
        }
    }
}