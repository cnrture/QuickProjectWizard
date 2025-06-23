package com.github.cnrture.quickprojectwizard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.cnrture.quickprojectwizard.theme.QPWTheme

enum class QPWActionCardType { SMALL, MEDIUM, LARGE }

@Composable
fun QPWActionCard(
    modifier: Modifier = Modifier,
    title: String? = null,
    icon: ImageVector? = null,
    actionColor: Color,
    isTextVisible: Boolean = true,
    type: QPWActionCardType = QPWActionCardType.LARGE,
    isEnabled: Boolean = true,
    onClick: () -> Unit,
) {
    val fontSize = when (type) {
        QPWActionCardType.SMALL -> 14.sp
        QPWActionCardType.MEDIUM -> 16.sp
        QPWActionCardType.LARGE -> 20.sp
    }
    val iconBoxSize = when (type) {
        QPWActionCardType.SMALL -> 24.dp
        QPWActionCardType.MEDIUM -> 28.dp
        QPWActionCardType.LARGE -> 32.dp
    }
    val iconSize = when (type) {
        QPWActionCardType.SMALL -> 16.dp
        QPWActionCardType.MEDIUM -> 20.dp
        QPWActionCardType.LARGE -> 24.dp
    }
    val borderSize = when (type) {
        QPWActionCardType.SMALL -> 1.dp
        QPWActionCardType.MEDIUM -> 2.dp
        QPWActionCardType.LARGE -> 3.dp
    }
    val padding = when (type) {
        QPWActionCardType.SMALL -> 8.dp
        QPWActionCardType.MEDIUM -> 12.dp
        QPWActionCardType.LARGE -> 16.dp
    }
    Row(
        modifier = modifier
            .background(
                color = if (isEnabled) QPWTheme.colors.gray else QPWTheme.colors.lightGray.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp),
            )
            .border(
                width = borderSize,
                color = if (isEnabled) actionColor else QPWTheme.colors.lightGray.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            )
            .then(
                if (isEnabled) Modifier.clickable { onClick() }
                else Modifier
            )
            .padding(padding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        icon?.let {
            if (type == QPWActionCardType.LARGE) {
                Box(
                    modifier = Modifier
                        .size(iconBoxSize)
                        .clip(RoundedCornerShape(8.dp))
                        .background(actionColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = QPWTheme.colors.white,
                        modifier = Modifier.size(iconSize)
                    )
                }
            } else {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = actionColor,
                    modifier = Modifier.size(iconSize)
                )
            }
        }

        if (icon != null && title != null && isTextVisible) {
            Spacer(modifier = Modifier.width(8.dp))
        }

        if (isTextVisible) {
            title?.let {
                QPWText(
                    text = it,
                    style = TextStyle(
                        fontWeight = FontWeight.Medium,
                        fontSize = fontSize,
                        color = QPWTheme.colors.white,
                    ),
                )
            }
        }
    }
}