package com.github.cnrture.quickprojectwizard.components

import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import com.github.cnrture.quickprojectwizard.theme.QPWTheme

@Composable
fun QPWTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String? = null,
    placeholder: String? = null,
    textStyle: TextStyle = TextStyle.Default,
    isSingleLine: Boolean = true,
) {
    OutlinedTextField(
        modifier = modifier,
        label = { label?.let { Text(it) } },
        placeholder = { placeholder?.let { Text(it) } },
        value = value,
        onValueChange = { onValueChange(it) },
        textStyle = textStyle,
        singleLine = isSingleLine,
        maxLines = if (isSingleLine) 1 else Int.MAX_VALUE,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedLabelColor = QPWTheme.colors.white,
            unfocusedLabelColor = QPWTheme.colors.white,
            cursorColor = QPWTheme.colors.white,
            textColor = QPWTheme.colors.white,
            unfocusedBorderColor = QPWTheme.colors.white,
            focusedBorderColor = QPWTheme.colors.blue,
            placeholderColor = QPWTheme.colors.white,
        )
    )
}