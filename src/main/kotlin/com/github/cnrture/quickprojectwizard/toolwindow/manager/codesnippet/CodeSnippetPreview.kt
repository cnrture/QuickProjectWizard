package com.github.cnrture.quickprojectwizard.toolwindow.manager.codesnippet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.cnrture.quickprojectwizard.toolwindow.manager.codesnippet.model.CodeSnippetTheme
import com.github.cnrture.quickprojectwizard.toolwindow.manager.codesnippet.syntax.SyntaxHighlighter

@Composable
fun CodeSnippetPreview(
    code: String,
    theme: CodeSnippetTheme,
    language: String,
    fontSize: Int,
    showLineNumbers: Boolean,
    showWindowControls: Boolean,
    cornerRadius: Int,
    padding: Int,
    transparentBackground: Boolean,
    customBackgroundColor: Color,
    modifier: Modifier = Modifier,
) {
    // Calculate actual dimensions like in the export
    val lines = code.split('\n')
    val maxLineLength = lines.maxOfOrNull { it.length } ?: 0
    val estimatedWidth = (maxLineLength * fontSize * 1f).dp + if (showLineNumbers) 60.dp else 0.dp + 32.dp
    val estimatedHeight = (lines.size * (fontSize * 1.5f)).dp + 32.dp + if (showWindowControls) 40.dp else 0.dp

    val totalWidth = estimatedWidth + (padding * 2).dp
    val totalHeight = estimatedHeight + (padding * 2).dp

    Box(
        modifier = modifier
            .size(totalWidth, totalHeight)
            .background(
                if (transparentBackground) Color.Transparent else customBackgroundColor,
                RoundedCornerShape(cornerRadius.dp)
            )
            .padding(padding.dp),
        contentAlignment = Alignment.TopStart
    ) {
        Card(
            modifier = Modifier.fillMaxSize(),
            backgroundColor = theme.windowBackgroundColor,
            shape = RoundedCornerShape(cornerRadius.dp),
            elevation = if (transparentBackground) 0.dp else 8.dp
        ) {
            Column {
                // Window controls (macOS style)
                if (showWindowControls) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Traffic light buttons
                        Row(
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.Start),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(Color(0xFFFF5F57), CircleShape)
                            )
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(Color(0xFFFFBD2E), CircleShape)
                            )
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(Color(0xFF28CA42), CircleShape)
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // Language badge
                        Card(
                            backgroundColor = theme.backgroundColor.copy(alpha = 0.7f),
                            shape = RoundedCornerShape(6.dp),
                            elevation = 0.dp
                        ) {
                            Text(
                                text = language.uppercase(),
                                color = theme.textColor.copy(alpha = 0.8f),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Code content
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = if (transparentBackground) Color.Transparent else theme.backgroundColor,
                    shape = RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 0.dp,
                        bottomStart = cornerRadius.dp,
                        bottomEnd = cornerRadius.dp
                    ),
                    elevation = 0.dp
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        // Line numbers
                        if (showLineNumbers) {
                            val lines = code.split('\n')
                            Column(
                                modifier = Modifier.padding(end = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                lines.forEachIndexed { index, _ ->
                                    Text(
                                        text = "${index + 1}",
                                        color = theme.lineNumberColor,
                                        fontSize = fontSize.sp,
                                        fontFamily = FontFamily.Monospace,
                                        modifier = Modifier.width(24.dp)
                                    )
                                }
                            }
                        }

                        // Code text with syntax highlighting
                        Column(
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            val highlightedCode = SyntaxHighlighter.highlight(code, language, theme)
                            val codeLines = highlightedCode.split('\n')

                            codeLines.forEach { line ->
                                Text(
                                    text = parseHighlightedLine(line, theme),
                                    fontSize = fontSize.sp,
                                    fontFamily = FontFamily.Monospace,
                                    lineHeight = (fontSize * 1.4).sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun parseHighlightedLine(line: String, theme: CodeSnippetTheme): AnnotatedString {
    return buildAnnotatedString {
        var currentIndex = 0
        val text = line

        val keywords = listOf(
            "fun", "val", "var", "class", "interface", "object", "enum", "data",
            "private", "public", "internal", "protected", "override", "abstract",
            "if", "else", "when", "for", "while", "do", "try", "catch", "finally",
            "return", "break", "continue", "null", "true", "false", "this", "super",
            "import", "package", "const", "companion", "init", "constructor",
            "void", "int", "String", "boolean", "double", "float", "long", "char",
            "suspend", "inline", "noinline", "crossinline", "reified", "lateinit",
            "sealed", "annotation", "expect", "actual", "external", "operator",
            "infix", "tailrec", "vararg", "out", "in", "is", "as", "typeof"
        )

        val functions = listOf(
            "println", "print", "readLine", "toString", "equals", "hashCode",
            "apply", "also", "let", "run", "with", "takeIf", "takeUnless",
            "map", "filter", "reduce", "fold", "forEach", "find", "any", "all"
        )

        val classes = listOf(
            "Int", "String", "Boolean", "Double", "Float", "Long", "Char", "Byte",
            "Short", "Array", "List", "Set", "Map", "MutableList", "MutableSet",
            "MutableMap", "Pair", "Triple", "Unit", "Nothing", "Any", "Comparable"
        )

        val operators = listOf(
            "=", "+", "-", "*", "/", "%", "++", "--", "+=", "-=", "*=", "/=", "%=",
            "==", "!=", "===", "!==", "<", ">", "<=", ">=", "&&", "||", "!", "&", "|", "^", "~",
            "<<", ">>", ">>>", "?:", "?.", "!!", "..", "until", "downTo", "step"
        )

        val punctuation = listOf("(", ")", "[", "]", "{", "}", ",", ";", ".", ":", "->", "=>")

        while (currentIndex < text.length) {
            val remainingText = text.substring(currentIndex)

            when {
                // Skip whitespace
                remainingText[0].isWhitespace() -> {
                    append(remainingText[0])
                    currentIndex++
                }

                // String literals (double quotes)
                remainingText.startsWith("\"") -> {
                    val endIndex = remainingText.indexOf("\"", 1)
                    if (endIndex != -1) {
                        val stringLiteral = remainingText.substring(0, endIndex + 1)
                        withStyle(SpanStyle(color = theme.stringColor)) {
                            append(stringLiteral)
                        }
                        currentIndex += stringLiteral.length
                    } else {
                        withStyle(SpanStyle(color = theme.textColor)) {
                            append(remainingText[0])
                        }
                        currentIndex++
                    }
                }

                // String literals (single quotes)
                remainingText.startsWith("'") -> {
                    val endIndex = remainingText.indexOf("'", 1)
                    if (endIndex != -1) {
                        val stringLiteral = remainingText.substring(0, endIndex + 1)
                        withStyle(SpanStyle(color = theme.stringColor)) {
                            append(stringLiteral)
                        }
                        currentIndex += stringLiteral.length
                    } else {
                        withStyle(SpanStyle(color = theme.textColor)) {
                            append(remainingText[0])
                        }
                        currentIndex++
                    }
                }

                // Template strings
                remainingText.startsWith("\$") -> {
                    var templateEnd = 1
                    if (templateEnd < remainingText.length && remainingText[templateEnd] == '{') {
                        var braceCount = 1
                        templateEnd++
                        while (templateEnd < remainingText.length && braceCount > 0) {
                            when (remainingText[templateEnd]) {
                                '{' -> braceCount++
                                '}' -> braceCount--
                            }
                            templateEnd++
                        }
                    } else {
                        while (templateEnd < remainingText.length &&
                            (remainingText[templateEnd].isLetterOrDigit() || remainingText[templateEnd] == '_')
                        ) {
                            templateEnd++
                        }
                    }

                    val template = remainingText.substring(0, templateEnd)
                    withStyle(SpanStyle(color = theme.variableColor)) {
                        append(template)
                    }
                    currentIndex += template.length
                }

                // Comments (single line)
                remainingText.startsWith("//") -> {
                    withStyle(SpanStyle(color = theme.commentColor)) {
                        append(remainingText)
                    }
                    break
                }

                // Comments (multi-line start)
                remainingText.startsWith("/*") -> {
                    val endIndex = remainingText.indexOf("*/")
                    if (endIndex != -1) {
                        val comment = remainingText.substring(0, endIndex + 2)
                        withStyle(SpanStyle(color = theme.commentColor)) {
                            append(comment)
                        }
                        currentIndex += comment.length
                    } else {
                        withStyle(SpanStyle(color = theme.commentColor)) {
                            append(remainingText)
                        }
                        break
                    }
                }

                // Numbers (including hex, binary, scientific notation)
                remainingText[0].isDigit() ||
                    (remainingText.startsWith("0x") || remainingText.startsWith("0X") ||
                        remainingText.startsWith("0b") || remainingText.startsWith("0B")) -> {
                    var numberEnd = 0

                    // Hex numbers
                    if (remainingText.startsWith("0x") || remainingText.startsWith("0X")) {
                        numberEnd = 2
                        while (numberEnd < remainingText.length &&
                            remainingText[numberEnd].isDigit() ||
                            remainingText[numberEnd].lowercase() in "abcdef"
                        ) {
                            numberEnd++
                        }
                    }
                    // Binary numbers
                    else if (remainingText.startsWith("0b") || remainingText.startsWith("0B")) {
                        numberEnd = 2
                        while (numberEnd < remainingText.length && remainingText[numberEnd] in "01") {
                            numberEnd++
                        }
                    }
                    // Regular numbers
                    else {
                        while (numberEnd < remainingText.length &&
                            (remainingText[numberEnd].isDigit() ||
                                remainingText[numberEnd] in ".eE+-fFdDlL")
                        ) {
                            numberEnd++
                        }
                    }

                    val number = remainingText.substring(0, maxOf(numberEnd, 1))
                    withStyle(SpanStyle(color = theme.numberColor)) {
                        append(number)
                    }
                    currentIndex += number.length
                }

                // Operators
                operators.any { remainingText.startsWith(it) } -> {
                    val operator = operators.first { remainingText.startsWith(it) }
                    withStyle(SpanStyle(color = theme.operatorColor, fontWeight = FontWeight.Bold)) {
                        append(operator)
                    }
                    currentIndex += operator.length
                }

                // Punctuation
                punctuation.any { remainingText.startsWith(it) } -> {
                    val punct = punctuation.first { remainingText.startsWith(it) }
                    withStyle(SpanStyle(color = theme.punctuationColor)) {
                        append(punct)
                    }
                    currentIndex += punct.length
                }

                // Identifiers (keywords, functions, classes, variables)
                remainingText[0].isLetter() || remainingText[0] == '_' -> {
                    var wordEnd = 0
                    while (wordEnd < remainingText.length &&
                        (remainingText[wordEnd].isLetterOrDigit() || remainingText[wordEnd] == '_')
                    ) {
                        wordEnd++
                    }

                    val word = remainingText.substring(0, wordEnd)
                    val isKeyword = keywords.any { it.equals(word, ignoreCase = false) }
                    val isFunction = functions.any { it.equals(word, ignoreCase = false) }
                    val isClass = classes.any { it.equals(word, ignoreCase = false) } ||
                        word.firstOrNull()?.isUpperCase() == true

                    val color = when {
                        isKeyword -> theme.keywordColor
                        isFunction -> theme.functionColor
                        isClass -> theme.classColor
                        else -> theme.textColor
                    }

                    val fontWeight = when {
                        isKeyword -> FontWeight.Bold
                        isFunction -> FontWeight.SemiBold
                        isClass -> FontWeight.Medium
                        else -> FontWeight.Normal
                    }

                    withStyle(SpanStyle(color = color, fontWeight = fontWeight)) {
                        append(word)
                    }
                    currentIndex += word.length
                }

                // Everything else
                else -> {
                    withStyle(SpanStyle(color = theme.textColor)) {
                        append(remainingText[0])
                    }
                    currentIndex++
                }
            }
        }
    }
}
