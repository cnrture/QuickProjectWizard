package com.github.cnrture.quickprojectwizard.toolwindow.manager.modulegenerator.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.cnrture.quickprojectwizard.components.QPWText
import com.github.cnrture.quickprojectwizard.components.QPWTextField
import com.github.cnrture.quickprojectwizard.data.ModuleTemplate
import com.github.cnrture.quickprojectwizard.theme.QPWTheme

@Composable
fun TemplateSelectionContent(
    templates: List<ModuleTemplate>,
    selectedTemplate: ModuleTemplate?,
    defaultTemplateId: String,
    nameState: String,
    onTemplateSelected: (ModuleTemplate?) -> Unit,
    onNameChanged: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .background(
                color = QPWTheme.colors.gray,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(16.dp)
    ) {
        QPWText(
            text = "Module Templates",
            color = QPWTheme.colors.white,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        QPWText(
            text = "Choose a template to auto-configure your module",
            color = QPWTheme.colors.lightGray,
            style = TextStyle(fontSize = 12.sp)
        )

        Divider(
            color = QPWTheme.colors.lightGray,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        TemplateOption(
            title = "Custom Configuration",
            isSelected = selectedTemplate == null,
            onClick = { onTemplateSelected(null) },
            badge = "Manual",
            badgeColor = QPWTheme.colors.lightGray
        )

        Spacer(modifier = Modifier.height(12.dp))

        templates.forEach { template ->
            TemplateOption(
                title = template.name,
                isSelected = selectedTemplate?.id == template.id,
                onClick = {
                    onTemplateSelected(template)
                },
                badge = if (template.id == defaultTemplateId) "Default" else "",
                badgeColor = if (template.id == defaultTemplateId) QPWTheme.colors.green else QPWTheme.colors.purple,
                nameState = nameState,
                onNameChanged = onNameChanged,
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun TemplateOption(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    badge: String,
    badgeColor: Color,
    nameState: String? = null,
    onNameChanged: ((String) -> Unit)? = null,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 0.dp,
            color = if (isSelected) QPWTheme.colors.green else Color.Transparent
        ),
        backgroundColor = QPWTheme.colors.gray,
        elevation = 0.dp
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    QPWText(
                        text = title,
                        color = QPWTheme.colors.white,
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    if (badge.isNotEmpty()) {
                        Card(
                            shape = RoundedCornerShape(4.dp),
                            backgroundColor = badgeColor.copy(alpha = 0.2f)
                        ) {
                            QPWText(
                                text = badge,
                                color = badgeColor,
                                style = TextStyle(fontSize = 9.sp),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                nameState?.let {
                    Spacer(modifier = Modifier.height(12.dp))
                    QPWTextField(
                        color = QPWTheme.colors.green,
                        placeholder = "{NAME} value",
                        value = nameState,
                        onValueChange = { onNameChanged?.invoke(it) },
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    QPWText(
                        text = "If you use {NAME} in your template, it will be replaced with this value.",
                        color = QPWTheme.colors.green,
                        style = TextStyle(fontSize = 12.sp),
                    )
                }
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = "Selected",
                    tint = QPWTheme.colors.green,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
