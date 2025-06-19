package com.github.cnrture.quickprojectwizard.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.cnrture.quickprojectwizard.theme.QPWTheme

@Composable
fun QPWTextArea(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    backgroundColor: Color = QPWTheme.colors.gray,
    textColor: Color = QPWTheme.colors.white,
    readOnly: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        backgroundColor = backgroundColor,
        elevation = 2.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            val scrollState = rememberScrollState()

            if (readOnly) {
                SelectionContainer {
                    BasicTextField(
                        value = value,
                        onValueChange = { },
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState),
                        textStyle = TextStyle(
                            color = textColor,
                            fontSize = 14.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 20.sp
                        ),
                        cursorBrush = SolidColor(QPWTheme.colors.green),
                        readOnly = true,
                        decorationBox = { innerTextField ->
                            Box(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                if (value.isEmpty()) {
                                    QPWText(
                                        text = placeholder,
                                        color = QPWTheme.colors.lightGray.copy(alpha = 0.6f),
                                        style = TextStyle(
                                            fontSize = 14.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )
                }
            } else {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState),
                    textStyle = TextStyle(
                        color = textColor,
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 20.sp
                    ),
                    cursorBrush = SolidColor(QPWTheme.colors.green),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            if (value.isEmpty()) {
                                QPWText(
                                    text = placeholder,
                                    color = QPWTheme.colors.lightGray.copy(alpha = 0.6f),
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                )
                            }
                            innerTextField()
                        }
                    }
                )
            }
        }
    }
}