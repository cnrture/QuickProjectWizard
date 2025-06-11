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

enum class QPWActionCardType { SMALL, LARGE }

@Composable
fun QPWActionCard(
    modifier: Modifier = Modifier,
    title: String? = null,
    icon: ImageVector,
    actionColor: Color,
    type: QPWActionCardType = QPWActionCardType.LARGE,
    onClick: () -> Unit,
) {
    val fontSize = when (type) {
        QPWActionCardType.SMALL -> 14.sp
        QPWActionCardType.LARGE -> 20.sp
    }
    val iconBoxSize = when (type) {
        QPWActionCardType.SMALL -> 24.dp
        QPWActionCardType.LARGE -> 32.dp
    }
    val iconSize = when (type) {
        QPWActionCardType.SMALL -> 16.dp
        QPWActionCardType.LARGE -> 24.dp
    }
    val borderSize = when (type) {
        QPWActionCardType.SMALL -> 1.dp
        QPWActionCardType.LARGE -> 3.dp
    }
    val padding = when (type) {
        QPWActionCardType.SMALL -> 8.dp
        QPWActionCardType.LARGE -> 16.dp
    }
    Row(
        modifier = modifier
            .background(
                color = QPWTheme.colors.gray,
                shape = RoundedCornerShape(12.dp),
            )
            .border(
                width = borderSize,
                color = actionColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(padding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
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

        title?.let {
            Spacer(modifier = Modifier.width(8.dp))

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