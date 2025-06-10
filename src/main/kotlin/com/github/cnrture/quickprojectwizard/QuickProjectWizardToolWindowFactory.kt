package com.github.cnrture.quickprojectwizard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposePanel
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.cnrture.quickprojectwizard.common.Constants
import com.github.cnrture.quickprojectwizard.components.QPWActionCard
import com.github.cnrture.quickprojectwizard.components.QPWText
import com.github.cnrture.quickprojectwizard.data.SettingsService
import com.github.cnrture.quickprojectwizard.dialog.FeatureMakerDialogWrapper
import com.github.cnrture.quickprojectwizard.dialog.ModuleMakerDialogWrapper
import com.github.cnrture.quickprojectwizard.theme.QPWTheme
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.jcef.JBCefBrowser
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
        val settings = project.getService(SettingsService::class.java)
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
                            text = "QPW DevTools",
                            style = TextStyle(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        QPWTheme.colors.blue,
                                        QPWTheme.colors.purple,
                                    ),
                                    tileMode = TileMode.Mirror,
                                ),
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                            )
                        )
                    }
                }
            }

            panel.add(this, BorderLayout.NORTH)
        }

        try {
            JBCefBrowser().apply {
                loadURL(settings.state.webViewUrl)
                panel.add(this.component, BorderLayout.CENTER)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ComposePanel().apply {
                setContent {
                    QPWTheme {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(QPWTheme.colors.black),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                modifier = Modifier.padding(32.dp),
                                text = "Browser is not available",
                                fontSize = 20.sp,
                                color = QPWTheme.colors.white,
                            )
                        }
                    }
                }
                panel.add(this, BorderLayout.CENTER)
            }
        }

        ComposePanel().apply {
            setContent {
                QPWTheme {
                    ButtonsRow(project)
                }
            }
            panel.add(this, BorderLayout.SOUTH)
        }

        return panel
    }

    @Composable
    private fun ButtonsRow(project: Project) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(QPWTheme.colors.black)
                .padding(24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            QPWActionCard(
                modifier = Modifier.weight(1f),
                title = "Module",
                icon = Icons.Rounded.Add,
                actionColor = QPWTheme.colors.orange,
                onClick = { ModuleMakerDialogWrapper(project, null).apply { showAndGet() } },
            )

            QPWActionCard(
                modifier = Modifier.weight(1f),
                title = "Feature",
                icon = Icons.Rounded.Add,
                actionColor = QPWTheme.colors.purple,
                onClick = { FeatureMakerDialogWrapper(project, null).apply { showAndGet() } },
            )

            /*ActionCard(
                icon = Icons.Rounded.Settings,
                actionColor = QPWTheme.colors.lightGray,
                onClick = { SettingsDialogWrapper(project).apply { showAndGet() } },
            )*/
        }
    }
}