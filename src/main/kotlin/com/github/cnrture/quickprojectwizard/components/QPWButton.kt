package com.github.cnrture.quickprojectwizard.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.cnrture.quickprojectwizard.common.NoRippleInteractionSource
import com.github.cnrture.quickprojectwizard.theme.QPWTheme

@Composable
fun QPWButton(
    modifier: Modifier = Modifier,
    text: String,
    backgroundColor: Color,
    onClick: () -> Unit,
) {
    Button(
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = backgroundColor,
            contentColor = QPWTheme.colors.white,
        ),
        interactionSource = NoRippleInteractionSource(),
        onClick = onClick,
        content = {
            QPWText(
                text = text,
                color = QPWTheme.colors.white,
            )
        },
    )
}

@Composable
fun QPWOutlinedButton(
    modifier: Modifier = Modifier,
    text: String,
    backgroundColor: Color,
    onClick: () -> Unit,
) {
    OutlinedButton(
        modifier = modifier,
        colors = ButtonDefaults.outlinedButtonColors(
            backgroundColor = backgroundColor.copy(alpha = 0.1f),
            contentColor = QPWTheme.colors.white,
        ),
        border = BorderStroke(
            width = 2.dp,
            color = backgroundColor,
        ),
        onClick = onClick,
        content = {
            QPWText(
                text = text,
                color = QPWTheme.colors.white,
            )
        },
    )
}