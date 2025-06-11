package com.github.cnrture.quickprojectwizard.toolwindow.manager.modulemaker.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.cnrture.quickprojectwizard.toolwindow.components.QPWCheckbox
import com.github.cnrture.quickprojectwizard.toolwindow.components.QPWText
import com.github.cnrture.quickprojectwizard.toolwindow.theme.QPWTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExistingModulesContent(
    existingModules: List<String>,
    selectedDependencies: List<String>,
    onCheckedModule: (String) -> Unit,
) {
    if (existingModules.isNotEmpty()) {
        Spacer(modifier = Modifier.height(16.dp))
        Column(
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
            Spacer(modifier = Modifier.size(8.dp))
            QPWText(
                text = "Select modules that your new module will depend on:",
                color = QPWTheme.colors.lightGray,
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
                        color = QPWTheme.colors.red,
                        onCheckedChange = { onCheckedModule(module) },
                    )
                }
            }
        }
    }
}