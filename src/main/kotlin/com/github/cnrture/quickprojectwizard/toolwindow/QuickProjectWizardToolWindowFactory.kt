package com.github.cnrture.quickprojectwizard.toolwindow

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposePanel
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.cnrture.quickprojectwizard.common.Constants
import com.github.cnrture.quickprojectwizard.toolwindow.components.QPWText
import com.github.cnrture.quickprojectwizard.toolwindow.manager.ColorPickerComponent
import com.github.cnrture.quickprojectwizard.toolwindow.manager.FeatureMakerComponent
import com.github.cnrture.quickprojectwizard.toolwindow.manager.ModuleMakerComponent
import com.github.cnrture.quickprojectwizard.toolwindow.theme.QPWTheme
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import java.awt.BorderLayout
import javax.swing.JComponent
import javax.swing.JPanel

class QuickProjectWizardToolWindowFactory : ToolWindowFactory {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val contentFactory = ContentFactory.getInstance()
        val content = contentFactory.createContent(
            createToolWindowComponent(project),
            Constants.EMPTY,
            false
        )
        toolWindow.contentManager.addContent(content)
    }

    private fun createToolWindowComponent(project: Project): JComponent {
        val panel = JPanel(BorderLayout())

        ComposePanel().apply {
            setContent {
                QPWTheme {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(QPWTheme.colors.black),
                    ) {
                        QPWText(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            text = "Quick Project Wizard",
                            style = TextStyle(
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                            ),
                            color = QPWTheme.colors.green,
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
                Box(
                    modifier = Modifier
                        .then(
                            if (selectedTab == 0) {
                                Modifier.background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            QPWTheme.colors.black,
                                            QPWTheme.colors.red.copy(alpha = 0.3f),
                                        )
                                    )
                                )
                            } else {
                                Modifier.background(QPWTheme.colors.black)
                            }
                        )
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            QPWText(
                                text = tabs[0],
                                color = QPWTheme.colors.white,
                            )
                        }
                    )
                }
                Box(
                    modifier = Modifier
                        .then(
                            if (selectedTab == 1) {
                                Modifier.background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            QPWTheme.colors.black,
                                            QPWTheme.colors.purple.copy(alpha = 0.3f),
                                        )
                                    )
                                )
                            } else {
                                Modifier.background(QPWTheme.colors.black)
                            }
                        )
                ) {
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            QPWText(
                                text = tabs[1],
                                color = QPWTheme.colors.white,
                            )
                        }
                    )
                }
                Box(
                    modifier = Modifier
                        .then(
                            if (selectedTab == 2) {
                                Modifier.background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            QPWTheme.colors.black,
                                            QPWTheme.colors.green.copy(alpha = 0.3f),
                                        )
                                    )
                                )
                            } else {
                                Modifier.background(QPWTheme.colors.black)
                            }
                        )
                ) {
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = {
                            QPWText(
                                text = tabs[2],
                                color = QPWTheme.colors.white,
                            )
                        }
                    )
                }
            }

            when (selectedTab) {
                0 -> ModuleMakerComponent(project)
                1 -> FeatureMakerComponent(project)
                2 -> ColorPickerComponent()
            }
        }
    }
}
