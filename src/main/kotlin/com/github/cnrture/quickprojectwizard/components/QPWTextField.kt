package com.github.cnrture.quickprojectwizard.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.cnrture.quickprojectwizard.theme.QPWTheme

@Composable
fun QPWTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String? = null,
    color: Color = QPWTheme.colors.white,
    textStyle: TextStyle = TextStyle.Default,
    isSingleLine: Boolean = true,
) {
    BasicTextField(
        modifier = modifier
            .heightIn(min = 44.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = 1.dp,
                color = QPWTheme.colors.white,
                shape = RoundedCornerShape(8.dp)
            ),
        value = value,
        onValueChange = onValueChange,
        textStyle = textStyle.copy(
            color = QPWTheme.colors.white,
            fontSize = 14.sp,
        ),
        singleLine = isSingleLine,
        cursorBrush = SolidColor(value = color),
        decorationBox = {
            Box(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                if (value.isBlank()) {
                    QPWText(
                        text = value.ifBlank { placeholder ?: "" },
                        color = QPWTheme.colors.lightGray,
                        style = textStyle.copy(
                            fontSize = 14.sp,
                        ),
                    )
                } else {
                    it()
                }
            }
        },
    )
}