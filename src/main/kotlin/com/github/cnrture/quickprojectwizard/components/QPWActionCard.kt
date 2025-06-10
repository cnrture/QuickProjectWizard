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

@Composable
fun QPWActionCard(
    modifier: Modifier = Modifier,
    title: String? = null,
    icon: ImageVector,
    actionColor: Color,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .background(
                color = QPWTheme.colors.gray,
                shape = RoundedCornerShape(12.dp),
            )
            .border(
                width = 3.dp,
                color = actionColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(actionColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = QPWTheme.colors.white,
                modifier = Modifier.size(24.dp)
            )
        }

        title?.let {
            Spacer(modifier = Modifier.width(8.dp))

            QPWText(
                text = it,
                style = TextStyle(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    color = QPWTheme.colors.white,
                ),
            )
        }
    }
}