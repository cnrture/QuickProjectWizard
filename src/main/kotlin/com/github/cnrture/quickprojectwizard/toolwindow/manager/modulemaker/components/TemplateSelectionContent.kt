package com.github.cnrture.quickprojectwizard.toolwindow.manager.modulemaker.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.cnrture.quickprojectwizard.components.QPWText
import com.github.cnrture.quickprojectwizard.data.ModuleTemplate
import com.github.cnrture.quickprojectwizard.theme.QPWTheme

@Composable
fun TemplateSelectionContent(
    templates: List<ModuleTemplate>,
    selectedTemplate: ModuleTemplate?,
    onTemplateSelected: (ModuleTemplate?) -> Unit,
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

        Spacer(modifier = Modifier.height(16.dp))

        // Manual/Custom option
        TemplateOption(
            title = "Custom Configuration",
            description = "Manually configure packages, dependencies, and files",
            isSelected = selectedTemplate == null,
            onClick = { onTemplateSelected(null) },
            badge = "Manual",
            badgeColor = QPWTheme.colors.lightGray
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Template options
        templates.forEach { template ->
            TemplateOption(
                title = template.name,
                description = template.description,
                isSelected = selectedTemplate?.id == template.id,
                onClick = {
                    onTemplateSelected(template)
                },
                badge = if (template.isDefault) "Default" else "Custom",
                badgeColor = if (template.isDefault) QPWTheme.colors.green else QPWTheme.colors.purple
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Template Preview
        selectedTemplate?.let { template ->
            Spacer(modifier = Modifier.height(16.dp))
            TemplatePreview(template = template)
        }
    }
}

@Composable
private fun TemplatePreview(template: ModuleTemplate) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        backgroundColor = QPWTheme.colors.black.copy(alpha = 0.4f),
        elevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            QPWText(
                text = "Package Structure Preview",
                color = QPWTheme.colors.white,
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (template.packageStructure.isNotEmpty()) {
                template.packageStructure.forEach { packagePath ->
                    QPWText(
                        text = "📁 $packagePath",
                        color = QPWTheme.colors.lightGray,
                        style = TextStyle(fontSize = 11.sp),
                        modifier = Modifier.padding(start = 8.dp, bottom = 2.dp)
                    )
                }
            } else {
                QPWText(
                    text = "No package structure defined",
                    color = QPWTheme.colors.lightGray.copy(alpha = 0.7f),
                    style = TextStyle(fontSize = 11.sp, fontStyle = FontStyle.Italic),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun TemplateOption(
    title: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    badge: String,
    badgeColor: Color,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        backgroundColor = if (isSelected) QPWTheme.colors.black.copy(alpha = 0.6f) else QPWTheme.colors.black.copy(alpha = 0.3f),
        elevation = if (isSelected) 4.dp else 2.dp
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
                Spacer(modifier = Modifier.height(4.dp))
                QPWText(
                    text = description,
                    color = QPWTheme.colors.lightGray,
                    style = TextStyle(fontSize = 11.sp)
                )
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = QPWTheme.colors.green,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
