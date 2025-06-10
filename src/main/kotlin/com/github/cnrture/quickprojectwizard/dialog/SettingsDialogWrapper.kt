package com.github.cnrture.quickprojectwizard.dialog

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.cnrture.quickprojectwizard.common.Constants
import com.github.cnrture.quickprojectwizard.components.*
import com.github.cnrture.quickprojectwizard.composearch.ui.main.emptyMainContract
import com.github.cnrture.quickprojectwizard.composearch.ui.main.emptyMainScreen
import com.github.cnrture.quickprojectwizard.composearch.ui.main.emptyMainScreenPreviewProvider
import com.github.cnrture.quickprojectwizard.composearch.ui.main.emptyMainViewModel
import com.github.cnrture.quickprojectwizard.data.SettingsService
import com.github.cnrture.quickprojectwizard.template.GradleTemplate
import com.github.cnrture.quickprojectwizard.template.ManifestTemplate
import com.github.cnrture.quickprojectwizard.theme.QPWTheme
import com.intellij.openapi.project.Project

class SettingsDialogWrapper(project: Project) : QPWDialogWrapper(
    width = Constants.SETTINGS_WINDOW_WIDTH,
    height = Constants.SETTINGS_WINDOW_HEIGHT
) {
    private val settings = project.getService(SettingsService::class.java)
    private var currentSettings by mutableStateOf(settings.state.copy())

    private val tabs = listOf("General", "Templates")
    private var selectedTabIndex by mutableStateOf(0)

    private val templateTypes = listOf("Feature", "Module", "Manifest", "Gradle")
    private var selectedTemplateType by mutableStateOf("Feature")
    private var selectedTemplateName by mutableStateOf(Constants.EMPTY)
    private var templateContent by mutableStateOf(Constants.EMPTY)
    private var showTemplateDropdown by mutableStateOf(false)

    @Composable
    override fun createDesign() {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(QPWTheme.colors.gray)
                .padding(24.dp),
            backgroundColor = QPWTheme.colors.gray,
            bottomBar = {
                QPWDialogActions(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(QPWTheme.colors.gray),
                    positiveText = "Save",
                    negativeText = "Cancel",
                    onCancelClick = { close(Constants.DEFAULT_EXIT_CODE) },
                    onCreateClick = {
                        settings.loadState(currentSettings)
                        close(0)
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                QPWText(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Settings",
                    style = TextStyle(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                QPWTheme.colors.blue,
                                QPWTheme.colors.purple,
                            ),
                            tileMode = TileMode.Mirror,
                        ),
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                )
                Spacer(modifier = Modifier.size(24.dp))
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    backgroundColor = QPWTheme.colors.gray,
                    contentColor = QPWTheme.colors.lightGray,
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            text = {
                                Text(
                                    text = title,
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                    ),
                                    color = if (selectedTabIndex == index) QPWTheme.colors.white
                                    else QPWTheme.colors.white
                                )
                            },
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    when (selectedTabIndex) {
                        0 -> GeneralSettingsTab()
                        1 -> TemplatesTab()
                    }
                }
            }
        }
    }

    @Composable
    private fun GeneralSettingsTab() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            SettingItem("Default Package Name") {
                QPWTextField(
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = "com.example",
                    value = currentSettings.defaultPackageName,
                    onValueChange = {
                        currentSettings = currentSettings.copy(defaultPackageName = it)
                    },
                )
            }

            SettingItem("WebView URL") {
                QPWTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = currentSettings.webViewUrl,
                    placeholder = "https://google.com/",
                    onValueChange = { currentSettings = currentSettings.copy(webViewUrl = it) },
                )
            }

            SettingItem("Preferred Module Type") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    QPWRadioButton(
                        text = Constants.ANDROID,
                        selected = currentSettings.preferredModuleType == Constants.ANDROID,
                        isBackgroundEnable = true,
                        onClick = {
                            currentSettings = currentSettings.copy(preferredModuleType = Constants.ANDROID)
                        },
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    QPWRadioButton(
                        text = Constants.KOTLIN,
                        selected = currentSettings.preferredModuleType == Constants.KOTLIN,
                        isBackgroundEnable = true,
                        onClick = {
                            currentSettings = currentSettings.copy(preferredModuleType = Constants.KOTLIN)
                        },
                    )
                }
            }
        }
    }

    @Composable
    private fun TemplatesTab() {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            SettingItem("Template Type") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    templateTypes.forEach { templateType ->
                        QPWRadioButton(
                            text = templateType,
                            selected = selectedTemplateType == templateType,
                            isBackgroundEnable = true,
                            onClick = {
                                selectedTemplateType = templateType
                                loadTemplateContent()
                            },
                        )
                    }
                }
            }

            SettingItem("Template Name") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box {
                        QPWTextField(
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .clickable { showTemplateDropdown = true },
                            value = selectedTemplateName,
                            placeholder = "Enter template name",
                            onValueChange = { selectedTemplateName = it },
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "New Template",
                            tint = QPWTheme.colors.blue,
                            modifier = Modifier
                                .clickable { createNewTemplate() }
                                .padding(8.dp)
                        )

                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Delete Template",
                            tint = QPWTheme.colors.orange,
                            modifier = Modifier
                                .clickable { deleteCurrentTemplate() }
                                .padding(8.dp)
                        )
                    }
                }
            }

            getAvailableTemplates().forEach { template ->
                QPWRadioButton(
                    text = template,
                    selected = selectedTemplateName == template,
                    isBackgroundEnable = true,
                    onClick = {
                        selectedTemplateName = template
                        loadTemplateContent()
                    },
                )
            }

            SettingItem("Template Content") {
                QPWTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp),
                    placeholder = "Enter template content",
                    value = templateContent,
                    onValueChange = { templateContent = it },
                    textStyle = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp,
                    ),
                    isSingleLine = false,
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                QPWButton(
                    text = "Save Template",
                    backgroundColor = QPWTheme.colors.blue,
                    onClick = { saveCurrentTemplate() }
                )
            }
        }
    }

    private fun loadTemplateContent() {
        templateContent = when (selectedTemplateType) {
            "Feature" -> {
                when (selectedTemplateName) {
                    "Screen" -> emptyMainScreen("com.example", "Example")
                    "ViewModel" -> emptyMainViewModel("com.example", "Example", currentSettings.isHiltEnable)
                    "Contract" -> emptyMainContract("com.example", "Example")
                    "PreviewProvider" -> emptyMainScreenPreviewProvider("com.example", "Example")
                    else -> Constants.EMPTY
                }
            }

            "Module" -> {
                when (selectedTemplateName) {
                    "README" -> currentSettings.moduleReadmeTemplate
                    else -> Constants.EMPTY
                }
            }

            "Manifest" -> ManifestTemplate.getManifestTemplate("com.example")
            "Gradle" -> {
                when (selectedTemplateName) {
                    "Android Module" -> GradleTemplate.getAndroidModuleGradleTemplate(
                        "com.example",
                        "// Add dependencies here",
                    )

                    "Kotlin Module" -> GradleTemplate.getKotlinModuleGradleTemplate()
                    else -> Constants.EMPTY
                }
            }

            else -> Constants.EMPTY
        }
    }

    private fun saveCurrentTemplate() {
        when (selectedTemplateType) {
            "Feature" -> {
                currentSettings = when (selectedTemplateName) {
                    "Screen" -> currentSettings.copy(featureScreenTemplate = templateContent)
                    "ViewModel" -> currentSettings.copy(featureViewModelTemplate = templateContent)
                    "Contract" -> currentSettings.copy(featureContractTemplate = templateContent)
                    "ComponentKey" -> currentSettings.copy(featureComponentKeyTemplate = templateContent)
                    "PreviewProvider" -> currentSettings.copy(featurePreviewProviderTemplate = templateContent)
                    else -> currentSettings
                }
            }

            "Module" -> {
                currentSettings = when (selectedTemplateName) {
                    "README" -> currentSettings.copy(moduleReadmeTemplate = templateContent)
                    else -> currentSettings
                }
            }

            "Manifest" -> {
                currentSettings = currentSettings.copy(manifestTemplate = templateContent)
            }

            "Gradle" -> {
                currentSettings = when (selectedTemplateName) {
                    "Android Module" -> currentSettings.copy(gradleAndroidTemplate = templateContent)
                    "Kotlin Module" -> currentSettings.copy(gradleKotlinTemplate = templateContent)
                    else -> currentSettings
                }
            }
        }
    }

    private fun createNewTemplate() {
        selectedTemplateName = "New Template"
        templateContent = Constants.EMPTY
    }

    private fun deleteCurrentTemplate() {
        if (selectedTemplateName.isNotEmpty()) {
            selectedTemplateName = Constants.EMPTY
            templateContent = Constants.EMPTY
        }
    }

    private fun getAvailableTemplates(): List<String> {
        return when (selectedTemplateType) {
            "Feature" -> listOf("Screen", "ViewModel", "Contract", "ComponentKey", "PreviewProvider")
            "Module" -> listOf("README")
            "Manifest" -> listOf("Android Manifest")
            "Gradle" -> listOf("Android Module", "Kotlin Module")
            else -> emptyList()
        }
    }

    @Composable
    private fun SettingItem(
        label: String,
        description: String? = null,
        content: @Composable () -> Unit,
    ) {
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
                .padding(24.dp),
        ) {
            QPWText(
                text = label,
                color = QPWTheme.colors.white,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            )

            description?.let {
                Spacer(modifier = Modifier.size(8.dp))
                QPWText(
                    text = it,
                    color = QPWTheme.colors.lightGray,
                    style = TextStyle(
                        fontSize = 14.sp,
                    )
                )
            }

            Divider(
                color = QPWTheme.colors.lightGray,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            content()
        }
    }
}