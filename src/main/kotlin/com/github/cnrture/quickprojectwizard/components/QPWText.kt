package com.github.cnrture.quickprojectwizard.components

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.isUnspecified

@Composable
fun QPWText(
    modifier: Modifier = Modifier,
    text: String,
    style: TextStyle = MaterialTheme.typography.body1,
    color: Color = style.color,
    softWrap: Boolean = false,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
) {
    val defaultFontSize = MaterialTheme.typography.body1.fontSize
    val originalFontSize = if (style.fontSize.isUnspecified) defaultFontSize else style.fontSize

    var resizedTextStyle by remember { mutableStateOf(style.copy(fontSize = originalFontSize)) }
    var shouldDraw by remember { mutableStateOf(false) }
    var lastResizeWasDecrease by remember { mutableStateOf(false) }

    Text(
        text = text,
        color = color,
        modifier = modifier.drawWithContent {
            if (shouldDraw) {
                drawContent()
            }
        },
        softWrap = softWrap,
        overflow = overflow,
        maxLines = maxLines,
        style = resizedTextStyle,
        onTextLayout = { result ->
            if (result.didOverflowWidth) {
                resizedTextStyle = resizedTextStyle.copy(
                    fontSize = resizedTextStyle.fontSize * 0.95f
                )
                lastResizeWasDecrease = true
                shouldDraw = true
            } else {
                if (lastResizeWasDecrease) {
                    shouldDraw = true
                    lastResizeWasDecrease = false
                } else {
                    val increasedFontSize = resizedTextStyle.fontSize * 1.05f

                    if (increasedFontSize <= originalFontSize) {
                        resizedTextStyle = resizedTextStyle.copy(
                            fontSize = increasedFontSize
                        )
                        shouldDraw = true
                    } else if (resizedTextStyle.fontSize < originalFontSize) {
                        resizedTextStyle = resizedTextStyle.copy(
                            fontSize = originalFontSize
                        )
                        shouldDraw = true
                    } else {
                        shouldDraw = true
                    }
                }
            }
        }
    )
}