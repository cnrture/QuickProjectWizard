package com.github.cnrture.quickprojectwizard.toolwindow.manager.modulemaker.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.cnrture.quickprojectwizard.common.Constants
import com.github.cnrture.quickprojectwizard.components.QPWRadioButton
import com.github.cnrture.quickprojectwizard.components.QPWTextField
import com.github.cnrture.quickprojectwizard.theme.QPWTheme

@Composable
fun ModuleTypeNameContent(
    moduleTypeSelectionState: String,
    packageName: String,
    moduleNameState: String,
    radioOptions: List<String>,
    onPackageNameChanged: (String) -> Unit,
    onModuleTypeSelected: (String) -> Unit,
    onModuleNameChanged: (String) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            radioOptions.forEach { text ->
                QPWRadioButton(
                    text = text,
                    selected = text == moduleTypeSelectionState,
                    color = QPWTheme.colors.green,
                    onClick = { onModuleTypeSelected(text) },
                )
                if (text != radioOptions.last()) {
                    Spacer(modifier = Modifier.size(16.dp))
                }
            }
        }
        Spacer(modifier = Modifier.size(24.dp))
        Column {
            QPWTextField(
                color = QPWTheme.colors.green,
                placeholder = "Package Name",
                value = packageName,
                onValueChange = { onPackageNameChanged(it) },
            )
            Spacer(modifier = Modifier.size(16.dp))
            QPWTextField(
                color = QPWTheme.colors.green,
                placeholder = Constants.DEFAULT_MODULE_NAME,
                value = moduleNameState,
                onValueChange = { onModuleNameChanged(it) },
            )
        }
    }
}