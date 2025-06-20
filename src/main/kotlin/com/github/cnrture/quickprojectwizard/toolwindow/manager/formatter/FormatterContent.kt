package com.github.cnrture.quickprojectwizard.toolwindow.manager.formatter

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.FormatAlignLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.cnrture.quickprojectwizard.components.QPWActionCard
import com.github.cnrture.quickprojectwizard.components.QPWActionCardType
import com.github.cnrture.quickprojectwizard.components.QPWTabRow
import com.github.cnrture.quickprojectwizard.components.QPWText
import com.github.cnrture.quickprojectwizard.data.SettingsService
import com.github.cnrture.quickprojectwizard.theme.QPWTheme
import com.intellij.openapi.application.ApplicationManager
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import org.xml.sax.InputSource
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.io.StringReader
import java.io.StringWriter
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

@Composable
fun FormatterContent() {
    val settings = ApplicationManager.getApplication().getService(SettingsService::class.java)

    var selectedFormat by remember { mutableStateOf(settings.getFormatterSelectedFormat()) }
    var inputText by remember {
        mutableStateOf(
            settings.getFormatterInputText().ifEmpty {
                if (selectedFormat == "JSON") getSampleJson() else getSampleXml()
            }
        )
    }
    var outputText by remember { mutableStateOf("") }
    var isValidInput by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf(settings.getFormatterErrorMessage()) }

    // Save state whenever it changes
    LaunchedEffect(selectedFormat, inputText, errorMessage) {
        settings.saveFormatterState(selectedFormat, inputText, errorMessage)
    }

    LaunchedEffect(inputText, selectedFormat) {
        if (inputText.isNotEmpty()) {
            val result = if (selectedFormat == "JSON") {
                formatJson(inputText)
            } else {
                formatXml(inputText)
            }

            if (result.second) {
                val formattedInput = result.first
                if (formattedInput != inputText) {
                    inputText = formattedInput
                }
                outputText = formattedInput
            } else {
                outputText = ""
            }

            isValidInput = result.second
            errorMessage = result.third
        }
    }

    LaunchedEffect(selectedFormat) {
        inputText = if (selectedFormat == "JSON") {
            getSampleJson()
        } else {
            getSampleXml()
        }
        outputText = ""
        errorMessage = ""
        isValidInput = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(QPWTheme.colors.black)
            .padding(24.dp)
    ) {
        QPWText(
            modifier = Modifier.fillMaxWidth(),
            text = "JSON/XML Formatter & Validator",
            style = TextStyle(
                color = QPWTheme.colors.green,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            QPWTabRow(
                text = "JSON",
                isSelected = selectedFormat == "JSON",
                color = QPWTheme.colors.green,
                onTabSelected = {
                    selectedFormat = "JSON"
                    outputText = ""
                    errorMessage = ""
                }
            )
            QPWTabRow(
                text = "XML",
                isSelected = selectedFormat == "XML",
                color = QPWTheme.colors.green,
                onTabSelected = {
                    selectedFormat = "XML"
                    outputText = ""
                    errorMessage = ""
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            QPWActionCard(
                title = "Format",
                icon = Icons.AutoMirrored.Rounded.FormatAlignLeft,
                actionColor = QPWTheme.colors.green,
                type = QPWActionCardType.MEDIUM,
                onClick = {
                    val result = if (selectedFormat == "JSON") {
                        formatJson(inputText)
                    } else {
                        formatXml(inputText)
                    }
                    outputText = result.first
                    isValidInput = result.second
                    errorMessage = result.third
                }
            )

            QPWActionCard(
                title = "Minify",
                icon = Icons.Rounded.Compress,
                actionColor = QPWTheme.colors.green,
                type = QPWActionCardType.MEDIUM,
                onClick = {
                    val result = if (selectedFormat == "JSON") {
                        minifyJson(inputText)
                    } else {
                        minifyXml(inputText)
                    }
                    outputText = result.first
                    isValidInput = result.second
                    errorMessage = result.third
                }
            )

            QPWActionCard(
                title = "Clear",
                icon = Icons.Rounded.Clear,
                actionColor = QPWTheme.colors.red,
                type = QPWActionCardType.MEDIUM,
                onClick = {
                    inputText = if (selectedFormat == "JSON") {
                        getSampleJson()
                    } else {
                        getSampleXml()
                    }
                    outputText = ""
                    errorMessage = ""
                    isValidInput = true
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (errorMessage.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                backgroundColor = if (isValidInput) QPWTheme.colors.green.copy(alpha = 0.1f) else QPWTheme.colors.red.copy(
                    alpha = 0.1f
                ),
                elevation = 0.dp,
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isValidInput) Icons.Rounded.CheckCircle else Icons.Rounded.Error,
                        contentDescription = null,
                        tint = if (isValidInput) QPWTheme.colors.green else QPWTheme.colors.red,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    QPWText(
                        text = errorMessage,
                        color = if (isValidInput) QPWTheme.colors.green else QPWTheme.colors.red,
                        style = TextStyle(fontSize = 14.sp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                QPWText(
                    text = "Input",
                    color = QPWTheme.colors.white,
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(8.dp))

                CollapsibleJsonTextArea(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = "Enter your $selectedFormat here...",
                    syntaxHighlighting = selectedFormat,
                    readOnly = false
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    QPWText(
                        text = "Output",
                        color = QPWTheme.colors.white,
                        style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    )
                    QPWActionCard(
                        title = "Copy",
                        icon = Icons.Rounded.ContentCopy,
                        actionColor = QPWTheme.colors.green,
                        type = QPWActionCardType.SMALL,
                        onClick = {
                            try {
                                val clipboard = Toolkit.getDefaultToolkit().systemClipboard
                                clipboard.setContents(StringSelection(outputText), null)
                                errorMessage = "Copied to clipboard!"
                                isValidInput = true
                            } catch (_: Exception) {
                                errorMessage = "Failed to copy to clipboard"
                                isValidInput = false
                                val result = if (selectedFormat == "JSON") {
                                    formatJson(inputText)
                                } else {
                                    formatXml(inputText)
                                }
                                outputText = result.first
                                isValidInput = result.second
                                errorMessage = result.third
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                CollapsibleJsonTextArea(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    value = outputText,
                    placeholder = "Formatted output will appear here...",
                    syntaxHighlighting = selectedFormat,
                    readOnly = true
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (inputText.isNotEmpty() || outputText.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (inputText.isNotEmpty()) {
                    QPWText(
                        text = "Input: ${inputText.lines().size} lines",
                        color = QPWTheme.colors.lightGray,
                        style = TextStyle(fontSize = 12.sp)
                    )
                }

                if (outputText.isNotEmpty()) {
                    QPWText(
                        text = "Output: ${outputText.lines().size} lines",
                        color = QPWTheme.colors.lightGray,
                        style = TextStyle(fontSize = 12.sp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CollapsibleJsonTextArea(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit = {},
    placeholder: String = "",
    syntaxHighlighting: String = "JSON",
    readOnly: Boolean = false,
) {
    var collapsedRanges by remember { mutableStateOf(setOf<Int>()) }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        backgroundColor = QPWTheme.colors.gray,
        elevation = 2.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            val scrollState = rememberScrollState()

            if (value.isEmpty()) {
                QPWText(
                    text = placeholder,
                    color = QPWTheme.colors.lightGray.copy(alpha = 0.6f),
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Monospace
                    )
                )
            } else {
                if (readOnly && syntaxHighlighting == "JSON") {
                    CollapsibleJsonRenderer(
                        jsonText = value,
                        collapsedRanges = collapsedRanges,
                        onToggleCollapse = { lineIndex ->
                            collapsedRanges = if (collapsedRanges.contains(lineIndex)) {
                                collapsedRanges - lineIndex
                            } else {
                                collapsedRanges + lineIndex
                            }
                        },
                        scrollState = scrollState
                    )
                } else {
                    BasicTextField(
                        value = value,
                        onValueChange = if (readOnly) {
                            {}
                        } else onValueChange,
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState),
                        textStyle = TextStyle(
                            color = QPWTheme.colors.white,
                            fontSize = 14.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 20.sp
                        ),
                        cursorBrush = SolidColor(QPWTheme.colors.green),
                        readOnly = readOnly,
                        decorationBox = { innerTextField ->
                            Box(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                if (value.isEmpty()) {
                                    QPWText(
                                        text = placeholder,
                                        color = QPWTheme.colors.lightGray.copy(alpha = 0.6f),
                                        style = TextStyle(
                                            fontSize = 14.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CollapsibleJsonRenderer(
    jsonText: String,
    collapsedRanges: Set<Int>,
    onToggleCollapse: (Int) -> Unit,
    scrollState: ScrollState,
) {
    val lines = jsonText.lines()
    val objectRanges = remember(jsonText) { findObjectRanges(lines) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        lines.forEachIndexed { index, line ->
            val isObjectStart = objectRanges.any { it.start == index }
            val isInCollapsedRange = objectRanges.any { range ->
                collapsedRanges.contains(range.start) && index in range.start..range.end && index != range.start
            }

            if (!isInCollapsedRange) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (isObjectStart) {
                        val isCollapsed = collapsedRanges.contains(index)

                        Icon(
                            modifier = Modifier
                                .size(24.dp)
                                .clickable { onToggleCollapse(index) },
                            imageVector = if (isCollapsed) Icons.AutoMirrored.Rounded.KeyboardArrowRight else Icons.Rounded.KeyboardArrowDown,
                            contentDescription = if (isCollapsed) "Expand" else "Collapse",
                            tint = QPWTheme.colors.green,
                        )
                    } else {
                        Spacer(modifier = Modifier.width(24.dp))
                    }

                    SyntaxHighlightedText(
                        text = line,
                        modifier = Modifier.weight(1f),
                    )
                }

                if (isObjectStart && collapsedRanges.contains(index)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        QPWText(
                            text = "...",
                            color = QPWTheme.colors.lightGray,
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        QPWText(
                            text = "(${objectRanges.first { it.start == index }.end - index} lines hidden)",
                            color = QPWTheme.colors.lightGray.copy(alpha = 0.6f),
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SyntaxHighlightedText(
    text: String,
    modifier: Modifier = Modifier,
) {
    val trimmedText = text.trim()
    val color = when {
        trimmedText.startsWith("\"") && trimmedText.contains(":") -> QPWTheme.colors.green
        trimmedText.startsWith("\"") -> QPWTheme.colors.green
        trimmedText.matches(Regex("\\d+")) || trimmedText.matches(Regex("\\d+\\.\\d+")) -> QPWTheme.colors.red
        trimmedText in listOf("true", "false") -> QPWTheme.colors.red
        trimmedText == "null" -> QPWTheme.colors.lightGray
        trimmedText.contains("{") || trimmedText.contains("}") || trimmedText.contains("[") || trimmedText.contains("]") -> QPWTheme.colors.white
        else -> QPWTheme.colors.white
    }

    QPWText(
        text = text,
        color = color,
        style = TextStyle(
            fontSize = 14.sp,
            fontFamily = FontFamily.Monospace,
            lineHeight = 20.sp
        ),
        modifier = modifier
    )
}

private data class LineRange(val start: Int, val end: Int)

private fun findObjectRanges(lines: List<String>): List<LineRange> {
    val ranges = mutableListOf<LineRange>()
    val stack = mutableListOf<Int>()

    lines.forEachIndexed { index, line ->
        val trimmed = line.trim()

        if (trimmed.contains("{") || trimmed.contains("[")) {
            if (trimmed.endsWith("{") || trimmed.endsWith("[")) {
                stack.add(index)
            }
        }

        if (trimmed.startsWith("}") || trimmed.startsWith("]")) {
            if (stack.isNotEmpty()) {
                val start = stack.removeLastOrNull()
                if (start != null && index > start) {
                    ranges.add(LineRange(start, index))
                }
            }
        }
    }

    return ranges.filter { it.end - it.start > 1 }
}

@OptIn(ExperimentalSerializationApi::class)
private fun formatJson(input: String): Triple<String, Boolean, String> {
    if (input.isEmpty()) return Triple("", true, "")

    return try {
        val jsonElement = Json.parseToJsonElement(input)
        val formatted = Json {
            prettyPrint = true
            prettyPrintIndent = " ".repeat(2)
        }
        val formattedJson = formatted.encodeToString(JsonElement.serializer(), jsonElement)
        Triple(formattedJson, true, "JSON formatted successfully!")
    } catch (e: Exception) {
        Triple("", false, "Invalid JSON: ${e.message}")
    }
}

private fun minifyJson(input: String): Triple<String, Boolean, String> {
    if (input.isEmpty()) return Triple("", true, "")

    return try {
        val jsonElement = Json.parseToJsonElement(input)
        val minified = Json.encodeToString(JsonElement.serializer(), jsonElement)
        Triple(minified, true, "JSON minified successfully!")
    } catch (e: Exception) {
        Triple("", false, "Invalid JSON: ${e.message}")
    }
}

private fun formatXml(input: String): Triple<String, Boolean, String> {
    if (input.isEmpty()) return Triple("", true, "")

    return try {
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        val document = builder.parse(InputSource(StringReader(input)))

        val transformer = TransformerFactory.newInstance().newTransformer()
        transformer.setOutputProperty(OutputKeys.INDENT, "yes")
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", 2.toString())
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no")

        val source = DOMSource(document)
        val writer = StringWriter()
        val result = StreamResult(writer)
        transformer.transform(source, result)

        Triple(writer.toString(), true, "✅ XML formatted successfully!")
    } catch (e: Exception) {
        Triple("", false, "❌ Invalid XML: ${e.message}")
    }
}

private fun minifyXml(input: String): Triple<String, Boolean, String> {
    if (input.isEmpty()) return Triple("", true, "")

    return try {
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        val document = builder.parse(InputSource(StringReader(input)))

        val transformer = TransformerFactory.newInstance().newTransformer()
        transformer.setOutputProperty(OutputKeys.INDENT, "no")
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes")

        val source = DOMSource(document)
        val writer = StringWriter()
        val result = StreamResult(writer)
        transformer.transform(source, result)

        val minified = writer.toString().replace(">\\s+<".toRegex(), "><")
        Triple(minified, true, "✅ XML minified successfully!")
    } catch (e: Exception) {
        Triple("", false, "❌ Invalid XML: ${e.message}")
    }
}

private fun getSampleJson(): String = """
{
  "name": "John Doe",
  "age": 30,
  "isEmployed": true,
  "address": {
    "street": "123 Main St",
    "city": "New York",
    "zipCode": "10001"
  },
  "hobbies": [
    "reading",
    "swimming",
    "coding"
  ],
  "spouse": null,
  "children": [
    {
      "name": "Jane Doe",
      "age": 8
    },
    {
      "name": "Bob Doe",
      "age": 12
    }
  ]
}
""".trimIndent()

private fun getSampleXml(): String = """
<?xml version="1.0" encoding="UTF-8"?>
<person>
  <name>John Doe</name>
  <age>30</age>
  <isEmployed>true</isEmployed>
  <address>
    <street>123 Main St</street>
    <city>New York</city>
    <zipCode>10001</zipCode>
  </address>
  <hobbies>
    <hobby>reading</hobby>
    <hobby>swimming</hobby>
    <hobby>coding</hobby>
  </hobbies>
  <children>
    <child>
      <name>Jane Doe</name>
      <age>8</age>
    </child>
    <child>
      <name>Bob Doe</name>
      <age>12</age>
    </child>
  </children>
</person>
""".trimIndent()
