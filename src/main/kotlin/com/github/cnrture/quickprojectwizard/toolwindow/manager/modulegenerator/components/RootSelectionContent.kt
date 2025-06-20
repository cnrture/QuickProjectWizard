package com.github.cnrture.quickprojectwizard.toolwindow.manager.modulegenerator.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.cnrture.quickprojectwizard.components.QPWButton
import com.github.cnrture.quickprojectwizard.components.QPWCheckbox
import com.github.cnrture.quickprojectwizard.components.QPWText
import com.github.cnrture.quickprojectwizard.theme.QPWTheme

@Composable
fun RootSelectionContent(
    selectedSrc: String,
    showFileTreeDialog: Boolean,
    isMoveFiles: Boolean,
    onMoveFilesChange: (Boolean) -> Unit,
    onChooseRootClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = QPWTheme.colors.gray,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            QPWText(
                text = "Selected: $selectedSrc",
                color = QPWTheme.colors.green,
                style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium)
            )
            Spacer(modifier = Modifier.size(4.dp))
            QPWText(
                text = "Choose the root directory for your new module.",
                color = QPWTheme.colors.lightGray,
                style = TextStyle(fontSize = 12.sp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                QPWButton(
                    text = if (showFileTreeDialog) "Close File Tree" else "Open File Tree",
                    backgroundColor = QPWTheme.colors.green,
                    onClick = onChooseRootClick,
                )
                Spacer(modifier = Modifier.size(8.dp))
                QPWCheckbox(
                    label = "Move selected files to new module",
                    checked = isMoveFiles,
                    color = QPWTheme.colors.green,
                    onCheckedChange = { onMoveFilesChange(it) },
                )
            }
        }
    }
}