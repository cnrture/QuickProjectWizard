package com.github.cnrture.quickprojectwizard.toolwindow

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
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
import com.github.cnrture.quickprojectwizard.service.AnalyticsService
import com.github.cnrture.quickprojectwizard.common.Constants
import com.github.cnrture.quickprojectwizard.components.QPWActionCard
import com.github.cnrture.quickprojectwizard.components.QPWActionCardType
import com.github.cnrture.quickprojectwizard.components.QPWMessageDialog
import com.github.cnrture.quickprojectwizard.components.QPWText
import com.github.cnrture.quickprojectwizard.service.SettingsService
import com.github.cnrture.quickprojectwizard.data.SettingsState
import com.github.cnrture.quickprojectwizard.theme.QPWTheme
import com.github.cnrture.quickprojectwizard.toolwindow.manager.apitester.ApiTesterContent
import com.github.cnrture.quickprojectwizard.toolwindow.manager.colorpicker.ColorPickerContent
import com.github.cnrture.quickprojectwizard.toolwindow.manager.featuregenerator.FeatureGeneratorContent
import com.github.cnrture.quickprojectwizard.toolwindow.manager.formatter.FormatterContent
import com.github.cnrture.quickprojectwizard.toolwindow.manager.modulegenerator.ModuleGeneratorContent
import com.github.cnrture.quickprojectwizard.toolwindow.manager.settings.SettingsContent
import com.github.cnrture.quickprojectwizard.toolwindow.manager.settings.dialog.ExportSettingsDialog
import com.intellij.ide.BrowserUtil
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import java.awt.BorderLayout
import javax.swing.JComponent
import javax.swing.JPanel

class QuickProjectWizardToolWindowFactory : ToolWindowFactory {

    private val settings = SettingsService.getInstance()

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
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(QPWTheme.colors.gray),
                    ) {
                        QPWText(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
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
                        MainContent(project)
                    }
                }
            }
            panel.add(this)
        }
        return panel
    }

    @Composable
    private fun MainContent(project: Project) {
        val analyticsService = AnalyticsService.getInstance()
        var selectedSection by remember { mutableStateOf("module") }
        var isExpanded by remember { mutableStateOf(settings.state.isActionsExpanded) }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(QPWTheme.colors.black)
        ) {
            Card(
                modifier = Modifier
                    .width(if (isExpanded) 180.dp else 60.dp)
                    .fillMaxHeight(),
                backgroundColor = QPWTheme.colors.gray,
                elevation = 8.dp,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(if (isExpanded) 16.dp else 8.dp),
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            if (isExpanded) {
                                QPWText(
                                    text = "Actions",
                                    color = QPWTheme.colors.white,
                                    style = TextStyle(
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                    ),
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Icon(
                                    imageVector = Icons.Rounded.KeyboardDoubleArrowLeft,
                                    contentDescription = null,
                                    tint = QPWTheme.colors.white,
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clickable {
                                            isExpanded = !isExpanded
                                            settings.loadState(settings.state.copy(isActionsExpanded = isExpanded))
                                        }
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Rounded.KeyboardDoubleArrowRight,
                                    contentDescription = null,
                                    tint = QPWTheme.colors.white,
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clickable {
                                            isExpanded = !isExpanded
                                            settings.loadState(settings.state.copy(isActionsExpanded = isExpanded))
                                        }
                                )
                            }
                        }

                        SidebarButton(
                            title = "Module",
                            icon = Icons.Rounded.ViewModule,
                            isSelected = selectedSection == "module",
                            color = QPWTheme.colors.green,
                            isExpanded = isExpanded,
                            onClick = { selectedSection = "module" }
                        )

                        SidebarButton(
                            title = "Feature",
                            icon = Icons.Rounded.FileOpen,
                            isSelected = selectedSection == "feature",
                            color = QPWTheme.colors.red,
                            isExpanded = isExpanded,
                            onClick = { selectedSection = "feature" }
                        )

                        SidebarButton(
                            title = "Picker",
                            icon = Icons.Rounded.ColorLens,
                            isSelected = selectedSection == "color",
                            color = QPWTheme.colors.purple,
                            isExpanded = isExpanded,
                            onClick = { selectedSection = "color" }
                        )

                        SidebarButton(
                            title = "Formatter",
                            icon = Icons.Rounded.FormatAlignCenter,
                            isSelected = selectedSection == "formatter",
                            color = QPWTheme.colors.green,
                            isExpanded = isExpanded,
                            onClick = { selectedSection = "formatter" }
                        )

                        SidebarButton(
                            title = "API Test",
                            icon = Icons.Rounded.Api,
                            isSelected = selectedSection == "api",
                            color = QPWTheme.colors.red,
                            isExpanded = isExpanded,
                            onClick = { selectedSection = "api" }
                        )

                        SidebarButton(
                            title = "Settings",
                            icon = Icons.Rounded.Settings,
                            isSelected = selectedSection == "settings",
                            color = QPWTheme.colors.lightGray,
                            isExpanded = isExpanded,
                            onClick = { selectedSection = "settings" }
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Column {
                        QPWActionCard(
                            title = "Export Settings",
                            icon = Icons.Rounded.FileUpload,
                            type = QPWActionCardType.SMALL,
                            actionColor = QPWTheme.colors.green,
                            isTextVisible = isExpanded,
                            onClick = {
                                ExportSettingsDialog(
                                    settings = settings,
                                    onComplete = { success, message ->
                                        analyticsService.track("export_settings")
                                        QPWMessageDialog(message).show()
                                    }
                                ).show()
                            }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        QPWActionCard(
                            title = "Import Settings",
                            icon = Icons.Rounded.FileDownload,
                            type = QPWActionCardType.SMALL,
                            actionColor = QPWTheme.colors.lightGray,
                            isTextVisible = isExpanded,
                            onClick = {
                                importSettings(settings) { newSettings ->
                                    analyticsService.track("import_settings")
                                    settings.loadState(newSettings)
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            backgroundColor = QPWTheme.colors.black,
                            elevation = 0.dp
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                ContactButton(
                                    title = "Website",
                                    icon = Icons.Rounded.Language,
                                    color = QPWTheme.colors.red,
                                    isExpanded = isExpanded,
                                    onClick = {
                                        BrowserUtil.browse("https://candroid.dev")
                                    }
                                )

                                ContactButton(
                                    title = "Plugin Page",
                                    icon = Icons.Rounded.Language,
                                    color = QPWTheme.colors.green,
                                    isExpanded = isExpanded,
                                    onClick = {
                                        BrowserUtil.browse("https://quickprojectwizard.candroid.dev")
                                    }
                                )

                                ContactButton(
                                    title = "Source Code",
                                    icon = Icons.Rounded.Source,
                                    color = QPWTheme.colors.purple,
                                    isExpanded = isExpanded,
                                    onClick = {
                                        BrowserUtil.browse("https://github.com/cnrture/QuickProjectWizard")
                                    },
                                )
                            }
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                when (selectedSection) {
                    "module" -> ModuleGeneratorContent(project)
                    "feature" -> {
                        analyticsService.track("view_feature_generator")
                        FeatureGeneratorContent(project)
                    }

                    "formatter" -> {
                        analyticsService.track("view_formatter")
                        FormatterContent()
                    }

                    "color" -> {
                        analyticsService.track("view_color_picker")
                        ColorPickerContent()
                    }

                    "api" -> {
                        analyticsService.track("view_api_tester")
                        ApiTesterContent()
                    }

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
        isExpanded: Boolean,
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
                modifier = Modifier.padding(if (isExpanded) 12.dp else 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) color else QPWTheme.colors.lightGray,
                    modifier = Modifier.size(24.dp)
                )
                if (isExpanded) {
                    QPWText(
                        modifier = Modifier.padding(8.dp),
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

    @Composable
    private fun ContactButton(
        title: String,
        icon: ImageVector,
        color: Color,
        isExpanded: Boolean,
        onClick: () -> Unit,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(horizontal = if (isExpanded) 8.dp else 4.dp, vertical = if (isExpanded) 4.dp else 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = if (isExpanded) Arrangement.Start else Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            if (isExpanded) Spacer(modifier = Modifier.width(8.dp))
            if (isExpanded) {
                QPWText(
                    text = title,
                    color = QPWTheme.colors.lightGray,
                    style = TextStyle(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    }

    private fun importSettings(settings: SettingsService, onSuccess: (SettingsState) -> Unit) {
        val project = ProjectManager.getInstance().defaultProject
        val descriptor = FileChooserDescriptorFactory.createSingleFileDescriptor("json")
        descriptor.title = "Import Settings"
        FileChooser.chooseFile(descriptor, project, null) { file ->
            if (settings.importFromFile(file.path)) {
                QPWMessageDialog("Settings imported successfully!").show()
                onSuccess(settings.state)
            } else {
                QPWMessageDialog("Failed to import settings. Please check the file format.").show()
            }
        }
    }
}
