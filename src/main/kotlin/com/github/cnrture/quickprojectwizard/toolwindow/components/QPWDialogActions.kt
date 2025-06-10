package com.github.cnrture.quickprojectwizard.toolwindow.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.cnrture.quickprojectwizard.toolwindow.theme.QPWTheme

@Composable
fun QPWDialogActions(
    modifier: Modifier = Modifier,
    positiveText: String = "Create",
    negativeText: String = "Cancel",
    onCancelClick: () -> Unit,
    onCreateClick: () -> Unit,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.End)
    ) {
        QPWOutlinedButton(
            text = negativeText,
            backgroundColor = QPWTheme.colors.blue,
            onClick = onCancelClick,
        )
        QPWButton(
            text = positiveText,
            backgroundColor = QPWTheme.colors.blue,
            onClick = onCreateClick,
        )
    }
}