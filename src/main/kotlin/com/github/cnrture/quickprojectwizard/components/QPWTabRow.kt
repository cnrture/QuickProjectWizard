package com.github.cnrture.quickprojectwizard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material.Tab
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.github.cnrture.quickprojectwizard.theme.QPWTheme

@Composable
fun QPWTabRow(text: String, color: Color, isSelected: Boolean, onTabSelected: () -> Unit) {
    Box(
        modifier = Modifier.then(
            if (isSelected) {
                Modifier.background(
                    brush = Brush.verticalGradient(listOf(QPWTheme.colors.black, color.copy(alpha = 0.3f)))
                )
            } else {
                Modifier.background(QPWTheme.colors.black)
            }
        )
    ) {
        Tab(
            selected = isSelected,
            onClick = onTabSelected,
            text = {
                QPWText(
                    text = text,
                    color = QPWTheme.colors.white,
                )
            }
        )
    }
}