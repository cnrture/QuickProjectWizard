package com.github.cnrture.quickprojectwizard.toolwindow.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun QPWDialogActions(
    modifier: Modifier = Modifier,
    positiveText: String = "Create",
    negativeText: String = "Cancel",
    color: Color,
    onCancelClick: (() -> Unit)? = null,
    onCreateClick: () -> Unit,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.End)
    ) {
        if (onCancelClick != null) {
            QPWOutlinedButton(
                text = negativeText,
                backgroundColor = color,
                onClick = onCancelClick,
            )
        }
        QPWButton(
            text = positiveText,
            backgroundColor = color,
            onClick = onCreateClick,
        )
    }
}