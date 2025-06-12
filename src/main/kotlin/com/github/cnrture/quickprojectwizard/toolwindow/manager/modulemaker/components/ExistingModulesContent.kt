package com.github.cnrture.quickprojectwizard.toolwindow.manager.modulemaker.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.cnrture.quickprojectwizard.components.QPWCheckbox
import com.github.cnrture.quickprojectwizard.components.QPWText
import com.github.cnrture.quickprojectwizard.theme.QPWTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExistingModulesContent(
    existingModules: List<String>,
    selectedDependencies: List<String>,
    onCheckedModule: (String) -> Unit,
) {
    if (existingModules.isNotEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = QPWTheme.colors.gray,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(16.dp)
        ) {
            QPWText(
                text = "Module Dependencies",
                color = QPWTheme.colors.white,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                ),
            )
            Spacer(modifier = Modifier.size(4.dp))
            QPWText(
                text = "Select modules that your new module will depend on:",
                color = QPWTheme.colors.lightGray,
                style = TextStyle(fontSize = 13.sp),
            )
            Divider(
                color = QPWTheme.colors.lightGray,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                existingModules.forEachIndexed { index, module ->
                    val isChecked = module in selectedDependencies
                    QPWCheckbox(
                        checked = isChecked,
                        label = module,
                        isBackgroundEnable = true,
                        color = QPWTheme.colors.green,
                        onCheckedChange = { onCheckedModule(module) },
                    )
                }
            }
        }
    }
}