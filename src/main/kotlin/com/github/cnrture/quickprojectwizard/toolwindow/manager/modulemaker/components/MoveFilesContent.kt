package com.github.cnrture.quickprojectwizard.toolwindow.manager.modulemaker.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.github.cnrture.quickprojectwizard.components.QPWCheckbox
import com.github.cnrture.quickprojectwizard.components.QPWText
import com.github.cnrture.quickprojectwizard.theme.QPWTheme

@Composable
fun MoveFilesContent(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    analyzeLibraries: Boolean = false,
    onAnalyzeLibrariesChange: (Boolean) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = 2.dp,
                color = QPWTheme.colors.white,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(8.dp),
    ) {
        QPWCheckbox(
            label = "Move selected files to new module",
            checked = isChecked,
            color = QPWTheme.colors.green,
            onCheckedChange = { onCheckedChange(it) },
        )
        Spacer(modifier = Modifier.height(8.dp))
        QPWText(
            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp),
            text = "This will move files from the selected directory to the new module.",
            color = QPWTheme.colors.lightGray,
        )

        if (isChecked) {
            Spacer(modifier = Modifier.height(8.dp))
            QPWCheckbox(
                label = "Analyze and include library dependencies",
                checked = analyzeLibraries,
                color = QPWTheme.colors.green,
                onCheckedChange = { onAnalyzeLibrariesChange(it) },
            )
            Spacer(modifier = Modifier.height(4.dp))
            QPWText(
                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp),
                text = "Parse libs.versions.toml and include relevant library dependencies.",
                color = QPWTheme.colors.lightGray,
            )
        }
    }
}