package com.github.cnrture.quickprojectwizard.toolwindow

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.TabRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposePanel
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.cnrture.quickprojectwizard.common.Constants
import com.github.cnrture.quickprojectwizard.components.QPWActionCard
import com.github.cnrture.quickprojectwizard.components.QPWActionCardType
import com.github.cnrture.quickprojectwizard.components.QPWTabRow
import com.github.cnrture.quickprojectwizard.components.QPWText
import com.github.cnrture.quickprojectwizard.dialog.SettingsDialogWrapper
import com.github.cnrture.quickprojectwizard.theme.QPWTheme
import com.github.cnrture.quickprojectwizard.toolwindow.manager.colorpicker.ColorPickerComponent
import com.github.cnrture.quickprojectwizard.toolwindow.manager.featuremaker.FeatureMakerComponent
import com.github.cnrture.quickprojectwizard.toolwindow.manager.modulemaker.ModuleMakerComponent
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
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(QPWTheme.colors.black)
                            .padding(24.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        QPWText(
                            modifier = Modifier.weight(1f),
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
                        Spacer(modifier = Modifier.width(16.dp))
                        QPWActionCard(
                            modifier = Modifier.padding(),
                            title = "Settings",
                            type = QPWActionCardType.MEDIUM,
                            icon = Icons.Default.Settings,
                            actionColor = QPWTheme.colors.lightGray,
                            onClick = { SettingsDialogWrapper().apply { show() } },
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
        var selectedTab by remember { mutableStateOf(0) }
        val tabs = listOf("Module", "Feature", "Color Picker")
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(QPWTheme.colors.black)
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                backgroundColor = QPWTheme.colors.black,
                contentColor = QPWTheme.colors.white,
            ) {
                QPWTabRow(
                    text = tabs[0],
                    color = QPWTheme.colors.green,
                    isSelected = selectedTab == 0,
                    onTabSelected = { selectedTab = 0 }
                )
                QPWTabRow(
                    text = tabs[1],
                    color = QPWTheme.colors.red,
                    isSelected = selectedTab == 1,
                    onTabSelected = { selectedTab = 1 }
                )
                QPWTabRow(
                    text = tabs[2],
                    color = QPWTheme.colors.purple,
                    isSelected = selectedTab == 2,
                    onTabSelected = { selectedTab = 2 }
                )
            }

            when (selectedTab) {
                0 -> ModuleMakerComponent(project)
                1 -> FeatureMakerComponent(project)
                2 -> ColorPickerComponent()
            }
        }
    }
}