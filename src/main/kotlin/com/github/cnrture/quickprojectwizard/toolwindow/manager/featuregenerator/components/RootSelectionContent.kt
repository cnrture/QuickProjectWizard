package com.github.cnrture.quickprojectwizard.toolwindow.manager.featuregenerator.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.cnrture.quickprojectwizard.components.QPWButton
import com.github.cnrture.quickprojectwizard.components.QPWText
import com.github.cnrture.quickprojectwizard.theme.QPWTheme

@Composable
fun RootSelectionContent(
    modifier: Modifier = Modifier,
    selectedSrc: String,
    showFileTreeDialog: Boolean,
    onChooseRootClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = QPWTheme.colors.gray,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(16.dp),
    ) {
        QPWText(
            text = "Selected: $selectedSrc",
            color = QPWTheme.colors.red,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
            ),
        )
        Spacer(modifier = Modifier.size(4.dp))
        QPWText(
            text = "Choose the root directory for your new module.",
            color = QPWTheme.colors.lightGray,
        )
        Spacer(modifier = Modifier.size(8.dp))
        QPWButton(
            text = if (showFileTreeDialog) "Close File Tree" else "Open File Tree",
            backgroundColor = QPWTheme.colors.red,
            onClick = onChooseRootClick,
        )
    }
}