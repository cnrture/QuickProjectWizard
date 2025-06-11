package com.github.cnrture.quickprojectwizard.toolwindow.manager.modulemaker.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.cnrture.quickprojectwizard.common.Constants
import com.github.cnrture.quickprojectwizard.components.QPWRadioButton
import com.github.cnrture.quickprojectwizard.components.QPWText
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
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = 2.dp,
                color = QPWTheme.colors.white,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            QPWText(
                text = "Module Type",
                color = QPWTheme.colors.white,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                ),
            )
            Spacer(modifier = Modifier.size(16.dp))
            Column {
                radioOptions.forEach { text ->
                    QPWRadioButton(
                        text = text,
                        selected = text == moduleTypeSelectionState,
                        isBackgroundEnable = true,
                        color = QPWTheme.colors.red,
                        onClick = { onModuleTypeSelected(text) },
                    )
                    if (text != radioOptions.last()) {
                        Spacer(modifier = Modifier.size(8.dp))
                    }
                }
            }
        }
        Spacer(modifier = Modifier.size(24.dp))
        Column {
            QPWTextField(
                modifier = Modifier.fillMaxWidth(),
                label = "Package Name",
                placeholder = "Package Name",
                value = packageName,
                onValueChange = { onPackageNameChanged(it) },
            )
            Spacer(modifier = Modifier.size(16.dp))
            QPWTextField(
                modifier = Modifier.fillMaxWidth(),
                label = "Module Name",
                placeholder = Constants.DEFAULT_MODULE_NAME,
                value = moduleNameState,
                onValueChange = { onModuleNameChanged(it) },
            )
        }
    }
}