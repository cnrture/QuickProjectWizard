package com.github.cnrture.quickprojectwizard.toolwindow

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposePanel
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.cnrture.quickprojectwizard.common.Constants
import com.github.cnrture.quickprojectwizard.components.QPWText
import com.github.cnrture.quickprojectwizard.theme.QPWTheme
import com.github.cnrture.quickprojectwizard.toolwindow.manager.colorpicker.ColorPickerContent
import com.github.cnrture.quickprojectwizard.toolwindow.manager.featuremaker.FeatureMakerContent
import com.github.cnrture.quickprojectwizard.toolwindow.manager.modulemaker.ModuleMakerContent
import com.github.cnrture.quickprojectwizard.toolwindow.manager.settings.SettingsContent
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import java.awt.BorderLayout
import javax.swing.JComponent
import javax.swing.JPanel

class QuickProjectWizardToolWindowFactory : ToolWindowFactory {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        toolWindow.contentManager.addContent(
            ContentFactory.getInstance().createContent(
                createToolWindowComponent(project),
                Constants.EMPTY,
                false,
            )
        )
    }

    private fun createToolWindowComponent(project: Project): JComponent {
        val panel = JPanel(BorderLayout())
        ComposePanel().apply {
            setContent {
                QPWTheme {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(QPWTheme.colors.gray)
                            .padding(24.dp),
                    ) {
                        QPWText(
                            text = "Quick Project Wizard",
                            style = TextStyle(
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold,
                                brush = Brush.horizontalGradient(
                                    colors = listOf(QPWTheme.colors.red, QPWTheme.colors.purple, QPWTheme.colors.green),
                                    tileMode = TileMode.Mirror,
                                ),
                            ),
                        )
                    }
                }
            }
            panel.add(this, BorderLayout.NORTH)
        }
        ComposePanel().apply {
            setContent {
                QPWTheme {
                    MainContent(project)
                }
            }
            panel.add(this, BorderLayout.CENTER)
        }
        return panel
    }

    @Composable
    private fun MainContent(project: Project) {
        var selectedSection by remember { mutableStateOf("module") }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(QPWTheme.colors.black)
        ) {
            Card(
                modifier = Modifier
                    .width(160.dp)
                    .fillMaxHeight(),
                backgroundColor = QPWTheme.colors.gray,
                elevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    QPWText(
                        text = "Quick Actions",
                        color = QPWTheme.colors.white,
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    SidebarButton(
                        title = "Module",
                        icon = Icons.Default.ViewModule,
                        isSelected = selectedSection == "module",
                        color = QPWTheme.colors.green,
                        onClick = { selectedSection = "module" }
                    )

                    SidebarButton(
                        title = "Feature",
                        icon = Icons.Default.FileOpen,
                        isSelected = selectedSection == "feature",
                        color = QPWTheme.colors.red,
                        onClick = { selectedSection = "feature" }
                    )

                    SidebarButton(
                        title = "Picker",
                        icon = Icons.Default.ColorLens,
                        isSelected = selectedSection == "color",
                        color = QPWTheme.colors.purple,
                        onClick = { selectedSection = "color" }
                    )

                    SidebarButton(
                        title = "Settings",
                        icon = Icons.Default.Settings,
                        isSelected = selectedSection == "settings",
                        color = QPWTheme.colors.lightGray,
                        onClick = { selectedSection = "settings" }
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                when (selectedSection) {
                    "module" -> ModuleMakerContent(project)
                    "feature" -> FeatureMakerContent(project)
                    "color" -> ColorPickerContent()
                    "settings" -> SettingsContent()
                }
            }
        }
    }

    @Composable
    private fun SidebarButton(
        title: String,
        icon: ImageVector,
        isSelected: Boolean,
        color: Color,
        onClick: () -> Unit,
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() },
            shape = RoundedCornerShape(12.dp),
            backgroundColor = if (isSelected) color.copy(alpha = 0.2f) else QPWTheme.colors.black,
            elevation = 0.dp
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) color else QPWTheme.colors.lightGray,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                QPWText(
                    text = title,
                    color = if (isSelected) QPWTheme.colors.white else QPWTheme.colors.lightGray,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                )
            }
        }
    }
}
