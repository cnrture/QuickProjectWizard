package com.github.cnrture.quickprojectwizard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Tab
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.cnrture.quickprojectwizard.common.NoRippleInteractionSource
import com.github.cnrture.quickprojectwizard.theme.QPWTheme

@Composable
fun QPWTabRow(text: String, color: Color, isSelected: Boolean, onTabSelected: () -> Unit) {
    Box(
        modifier = Modifier
            .then(
                if (isSelected) {
                    Modifier.background(
                        brush = Brush.verticalGradient(listOf(Color.Transparent, color.copy(alpha = 0.3f)))
                    )
                } else {
                    Modifier.background(QPWTheme.colors.black)
                }
            )
    ) {
        Tab(
            modifier = Modifier.padding(8.dp),
            selected = isSelected,
            onClick = onTabSelected,
            interactionSource = NoRippleInteractionSource(),
            text = {
                QPWText(
                    text = text,
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    color = QPWTheme.colors.white,
                )
            }
        )
    }
}