package com.github.cnrture.quickprojectwizard.toolwindow.manager.modulemaker.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.cnrture.quickprojectwizard.components.QPWCheckbox
import com.github.cnrture.quickprojectwizard.components.QPWText
import com.github.cnrture.quickprojectwizard.theme.QPWTheme

@Composable
fun RowScope.MoveFilesContent(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    analyzeLibraries: Boolean = false,
    onAnalyzeLibrariesChange: (Boolean) -> Unit = {},
) {

}