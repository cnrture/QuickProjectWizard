package com.github.cnrture.quickprojectwizard.toolwindow.manager.modulegenerator.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = QPWTheme.colors.gray,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(16.dp),
    ) {
        QPWText(
            text = "Module Configuration",
            color = QPWTheme.colors.white,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        QPWText(
            text = "Select module type, provide package name and module name",
            color = QPWTheme.colors.lightGray,
            style = TextStyle(fontSize = 12.sp)
        )

        Divider(
            color = QPWTheme.colors.lightGray,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
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
                        Spacer(modifier = Modifier.size(8.dp))
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
                    placeholder = ":module",
                    value = moduleNameState,
                    onValueChange = { onModuleNameChanged(it) },
                )
            }
        }
    }
}