package com.github.cnrture.quickprojectwizard.toolwindow.manager.codesnippet

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.cnrture.quickprojectwizard.components.QPWButton
import com.github.cnrture.quickprojectwizard.components.QPWText
import com.github.cnrture.quickprojectwizard.service.AnalyticsService
import com.github.cnrture.quickprojectwizard.theme.QPWTheme
import com.github.cnrture.quickprojectwizard.toolwindow.manager.codesnippet.model.CodeSnippetTheme
import com.github.cnrture.quickprojectwizard.toolwindow.manager.codesnippet.service.CodeSnippetExporter

@Composable
fun CodeSnippetContent() {
    val analyticsService = AnalyticsService.getInstance()

    var code by remember { mutableStateOf("fun helloWorld() {\n    println(\"Hello, World!\")\n}") }
    var selectedTheme by remember { mutableStateOf(CodeSnippetTheme.TOKYO_NIGHT) }
    var selectedLanguage by remember { mutableStateOf("kotlin") }
    var fontSize by remember { mutableStateOf(14) }
    var showLineNumbers by remember { mutableStateOf(true) }
    var showWindowControls by remember { mutableStateOf(true) }
    var cornerRadius by remember { mutableStateOf(8) }
    var padding by remember { mutableStateOf(64) }
    var dropShadow by remember { mutableStateOf(true) }
    var transparentBackground by remember { mutableStateOf(false) }
    var customBackgroundColor by remember { mutableStateOf(Color(0xFF1A1B26)) }

    // Background color options
    val backgroundColors = listOf(
        Color(0xFF1A1B26), // Tokyo Night
        Color(0xFF282A36), // Dracula
        Color(0xFF0D1117), // GitHub Dark
        Color(0xFF212121), // Material Dark
        Color(0xFF2D353B), // Everforest
        Color(0xFF011627), // Night Owl
        Color(0xFF1E2030), // Moonlight
        Color(0xFF0A0E27), // Cyberpunk
        Color(0xFF2B213A), // Synthwave
        Color(0xFFFFFFFF), // White
        Color(0xFF000000), // Black
        Color(0xFF1C1E26), // Horizon
    )

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(QPWTheme.colors.black)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Settings Panel
        Card(
            modifier = Modifier
                .width(280.dp)
                .fillMaxHeight(),
            backgroundColor = QPWTheme.colors.gray,
            elevation = 4.dp,
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                QPWText(
                    text = "Code Snippet Generator",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = QPWTheme.colors.white
                    )
                )

                Divider(color = QPWTheme.colors.lightGray)

                // Theme Selection
                SettingsSection(title = "Theme") {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.height(200.dp)
                    ) {
                        items(CodeSnippetTheme.entries) { theme ->
                            ThemeCard(
                                theme = theme,
                                isSelected = selectedTheme == theme,
                                onClick = { selectedTheme = theme }
                            )
                        }
                    }
                }

                // Background Color Selection (only when transparent is off)
                if (!transparentBackground) {
                    SettingsSection(title = "Background Color") {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(6),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.height(60.dp)
                        ) {
                            items(backgroundColors) { color ->
                                BackgroundColorCard(
                                    color = color,
                                    isSelected = customBackgroundColor == color,
                                    onClick = { customBackgroundColor = color }
                                )
                            }
                        }
                    }
                }

                // Language Selection
                SettingsSection(title = "Language") {
                    LanguageDropdown(
                        selectedLanguage = selectedLanguage,
                        onLanguageChange = { selectedLanguage = it }
                    )
                }

                // Typography Settings
                SettingsSection(title = "Typography") {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        SliderSetting(
                            label = "Font Size",
                            value = fontSize.toFloat(),
                            range = 10f..24f,
                            onValueChange = { fontSize = it.toInt() }
                        )
                    }
                }

                // Layout Settings
                SettingsSection(title = "Layout") {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        SwitchSetting(
                            label = "Line Numbers",
                            checked = showLineNumbers,
                            onCheckedChange = { showLineNumbers = it }
                        )
                        SwitchSetting(
                            label = "Window Controls",
                            checked = showWindowControls,
                            onCheckedChange = { showWindowControls = it }
                        )
                        SliderSetting(
                            label = "Corner Radius",
                            value = cornerRadius.toFloat(),
                            range = 0f..20f,
                            onValueChange = { cornerRadius = it.toInt() }
                        )
                        SliderSetting(
                            label = "Padding",
                            value = padding.toFloat(),
                            range = 16f..128f,
                            onValueChange = { padding = it.toInt() }
                        )
                        SwitchSetting(
                            label = "Drop Shadow",
                            checked = dropShadow,
                            onCheckedChange = { dropShadow = it }
                        )
                        SwitchSetting(
                            label = "Transparent Background",
                            checked = transparentBackground,
                            onCheckedChange = {
                                transparentBackground = it
                                if (it) {
                                    dropShadow = false
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Export Button
                QPWButton(
                    text = "Export as PNG",
                    backgroundColor = QPWTheme.colors.green,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        analyticsService.track("export_code_snippet")

                        CodeSnippetExporter.exportToPng(
                            code = code,
                            theme = selectedTheme,
                            language = selectedLanguage,
                            fontSize = fontSize,
                            showLineNumbers = showLineNumbers,
                            showWindowControls = showWindowControls,
                            cornerRadius = cornerRadius,
                            padding = padding,
                            dropShadow = dropShadow,
                            transparentBackground = transparentBackground,
                            customBackgroundColor = customBackgroundColor
                        )
                    }
                )
            }
        }

        // Code Editor and Preview
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Code Editor
            Card(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = QPWTheme.colors.gray,
                elevation = 4.dp,
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        QPWText(
                            text = "Code Editor",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = QPWTheme.colors.white
                            )
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconButton(
                                onClick = { code = "" }
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Clear,
                                    contentDescription = "Clear",
                                    tint = QPWTheme.colors.lightGray
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    BasicTextField(
                        value = code,
                        onValueChange = { code = it },
                        modifier = Modifier
                            .background(
                                QPWTheme.colors.black,
                                RoundedCornerShape(8.dp)
                            )
                            .padding(16.dp),
                        textStyle = TextStyle(
                            color = QPWTheme.colors.white,
                            fontSize = 14.sp,
                            fontFamily = FontFamily.Monospace
                        ),
                        cursorBrush = SolidColor(QPWTheme.colors.white)
                    )
                }
            }

            // Preview
            Card(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = QPWTheme.colors.gray,
                elevation = 4.dp,
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    QPWText(
                        text = "Preview (Actual Size)",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = QPWTheme.colors.white
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Preview container with scrolling
                    Box(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .horizontalScroll(rememberScrollState())
                            .background(
                                if (transparentBackground) QPWTheme.colors.lightGray
                                else customBackgroundColor,
                                RoundedCornerShape(8.dp)
                            )
                            .padding(16.dp)
                    ) {
                        CodeSnippetPreview(
                            code = code,
                            theme = selectedTheme,
                            language = selectedLanguage,
                            fontSize = fontSize,
                            showLineNumbers = showLineNumbers,
                            showWindowControls = showWindowControls,
                            cornerRadius = cornerRadius,
                            padding = padding,
                            transparentBackground = transparentBackground,
                            customBackgroundColor = customBackgroundColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit,
) {
    Column {
        QPWText(
            text = title,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = QPWTheme.colors.white
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        content()
    }
}

@Composable
private fun ThemeCard(
    theme: CodeSnippetTheme,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .width(60.dp)
            .height(50.dp)
            .clickable { onClick() },
        backgroundColor = theme.backgroundColor,
        elevation = if (isSelected) 8.dp else 2.dp,
        shape = RoundedCornerShape(8.dp),
        border = if (isSelected) BorderStroke(2.dp, QPWTheme.colors.green) else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Small code preview
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                // Simulate code lines with different colors
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(2.dp)
                        .background(theme.keywordColor, RoundedCornerShape(1.dp))
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(2.dp)
                        .background(theme.stringColor, RoundedCornerShape(1.dp))
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(2.dp)
                        .background(theme.functionColor, RoundedCornerShape(1.dp))
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .height(2.dp)
                        .background(theme.commentColor, RoundedCornerShape(1.dp))
                )
            }

            // Theme name
            Text(
                text = theme.displayName.take(8),
                color = theme.textColor,
                fontSize = 8.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            // Selection indicator
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(4.dp)
                        .background(QPWTheme.colors.green, CircleShape)
                )
            }
        }
    }
}

@Composable
private fun BackgroundColorCard(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .size(28.dp)
            .clickable { onClick() },
        backgroundColor = color,
        elevation = if (isSelected) 6.dp else 2.dp,
        shape = RoundedCornerShape(8.dp),
        border = if (isSelected) BorderStroke(2.dp, QPWTheme.colors.green) else null
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = null,
                    tint = if (color == Color.White || color.luminance() > 0.5f) Color.Black else Color.White,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}

@Composable
private fun LanguageDropdown(
    selectedLanguage: String,
    onLanguageChange: (String) -> Unit,
) {
    val languages = listOf(
        "kotlin",
        "java",
        "javascript",
        "typescript",
        "python",
        "go",
        "rust",
        "c",
        "cpp",
        "csharp",
        "php",
        "ruby",
        "swift",
        "dart",
        "xml",
        "json",
        "html",
        "css",
        "sql",
        "bash"
    )

    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                backgroundColor = QPWTheme.colors.black,
                contentColor = QPWTheme.colors.white
            )
        ) {
            Text(selectedLanguage.uppercase())
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Rounded.ArrowDropDown,
                contentDescription = null
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(QPWTheme.colors.black)
        ) {
            languages.forEach { language ->
                DropdownMenuItem(
                    onClick = {
                        onLanguageChange(language)
                        expanded = false
                    }
                ) {
                    Text(
                        text = language.uppercase(),
                        color = QPWTheme.colors.white
                    )
                }
            }
        }
    }
}

@Composable
private fun SliderSetting(
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                color = QPWTheme.colors.lightGray,
                fontSize = 12.sp
            )
            Text(
                text = value.toInt().toString(),
                color = QPWTheme.colors.white,
                fontSize = 12.sp
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            colors = SliderDefaults.colors(
                thumbColor = QPWTheme.colors.green,
                activeTrackColor = QPWTheme.colors.green,
                inactiveTrackColor = QPWTheme.colors.lightGray
            )
        )
    }
}

@Composable
private fun SwitchSetting(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = QPWTheme.colors.lightGray,
            fontSize = 12.sp
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = QPWTheme.colors.green,
                checkedTrackColor = QPWTheme.colors.green.copy(alpha = 0.5f)
            )
        )
    }
}
