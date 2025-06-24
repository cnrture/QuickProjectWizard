package com.github.cnrture.quickprojectwizard.toolwindow.manager.codesnippet.service

import androidx.compose.ui.graphics.Color
import com.github.cnrture.quickprojectwizard.common.Utils
import com.github.cnrture.quickprojectwizard.toolwindow.manager.codesnippet.model.CodeSnippetTheme
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.ProjectManager
import com.intellij.ui.JBColor
import com.intellij.util.ui.UIUtil
import java.awt.Font
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

object CodeSnippetExporter {

    fun exportToPng(
        code: String,
        theme: CodeSnippetTheme,
        language: String,
        fontSize: Int,
        showLineNumbers: Boolean,
        showWindowControls: Boolean,
        cornerRadius: Int,
        padding: Int,
        dropShadow: Boolean,
        transparentBackground: Boolean,
        customBackgroundColor: Color,
    ) {
        // Show file chooser on EDT
        ApplicationManager.getApplication().invokeLater {
            val project = ProjectManager.getInstance().defaultProject
            val descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor()
            descriptor.title = "Save Code Snippet"

            FileChooser.chooseFile(descriptor, project, null) { folder ->
                // Create image in background thread
                ApplicationManager.getApplication().executeOnPooledThread {
                    try {
                        val fileName = "code-snippet-${System.currentTimeMillis()}.png"
                        val file = File(folder.path, fileName)

                        val image = createCodeSnippetImage(
                            code = code,
                            theme = theme,
                            language = language,
                            fontSize = fontSize,
                            showLineNumbers = showLineNumbers,
                            showWindowControls = showWindowControls,
                            cornerRadius = cornerRadius,
                            padding = padding,
                            dropShadow = dropShadow,
                            transparentBackground = transparentBackground,
                            customBackgroundColor = customBackgroundColor
                        )

                        ImageIO.write(image, "PNG", file)

                        // Show notification on EDT
                        ApplicationManager.getApplication().invokeLater {
                            Utils.showInfo(
                                message = "Code snippet exported successfully to ${file.absolutePath}",
                                type = NotificationType.INFORMATION
                            )
                        }
                    } catch (e: Exception) {
                        // Show error notification on EDT
                        ApplicationManager.getApplication().invokeLater {
                            Utils.showInfo(
                                message = "Failed to export code snippet: ${e.message}",
                                type = NotificationType.ERROR
                            )
                        }
                    }
                }
            }
        }
    }

    private fun createCodeSnippetImage(
        code: String,
        theme: CodeSnippetTheme,
        language: String,
        fontSize: Int,
        showLineNumbers: Boolean,
        showWindowControls: Boolean,
        cornerRadius: Int,
        padding: Int,
        dropShadow: Boolean,
        transparentBackground: Boolean,
        customBackgroundColor: Color,
    ): BufferedImage {
        val lines = code.split('\n')
        val font = Font(Font.MONOSPACED, Font.PLAIN, fontSize)
        val boldFont = Font(Font.MONOSPACED, Font.BOLD, fontSize)

        // Create temporary graphics to measure text
        val tempImage = UIUtil.createImage(null, 1, 1, BufferedImage.TYPE_INT_ARGB)
        val tempG2d = tempImage.createGraphics()
        tempG2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        tempG2d.font = font

        val fontMetrics = tempG2d.fontMetrics
        val lineHeight = fontMetrics.height + 4

        // Calculate dimensions
        val maxLineWidth = lines.maxOfOrNull { line ->
            fontMetrics.stringWidth(line)
        } ?: 0

        val lineNumberWidth = if (showLineNumbers) {
            fontMetrics.stringWidth("${lines.size} ") + 16
        } else 0

        val codeWidth = maxLineWidth + lineNumberWidth + 32
        val windowControlsHeight = if (showWindowControls) 44 else 0
        val codeHeight = lines.size * lineHeight + 32 + windowControlsHeight

        val totalWidth = codeWidth + padding * 2
        val totalHeight = codeHeight + padding * 2

        tempG2d.dispose()

        // Create the actual image
        val image = UIUtil.createImage(null, totalWidth, totalHeight, BufferedImage.TYPE_INT_ARGB)
        val g2d = image.createGraphics()
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)

        // Background - use custom color if not transparent
        if (!transparentBackground) {
            g2d.color = customBackgroundColor.toAwtColor()
            g2d.fillRect(0, 0, totalWidth, totalHeight)
        }

        // Drop shadow
        if (dropShadow && !transparentBackground) {
            g2d.color = JBColor(0x000000, 0x333333)
            g2d.fillRoundRect(
                padding + 4,
                padding + 4,
                codeWidth,
                codeHeight,
                cornerRadius * 2,
                cornerRadius * 2
            )
        }

        // Window background
        g2d.color = theme.windowBackgroundColor.toAwtColor()
        g2d.fillRoundRect(padding, padding, codeWidth, codeHeight, cornerRadius * 2, cornerRadius * 2)

        // Window controls
        var yOffset = padding
        if (showWindowControls) {
            yOffset += 22

            // Traffic light buttons
            g2d.color = JBColor(0xFF605C, 0xFF605C)
            g2d.fillOval(padding + 16, yOffset - 6, 12, 12)

            g2d.color = JBColor(0xFFBD46, 0xFFBD46)
            g2d.fillOval(padding + 36, yOffset - 6, 12, 12)

            g2d.color = JBColor(0x27C93F, 0x27C93F)
            g2d.fillOval(padding + 56, yOffset - 6, 12, 12)

            // Language badge
            g2d.color = theme.backgroundColor.toAwtColor()
            g2d.fillRoundRect(codeWidth - 60, yOffset - 10, 45, 20, 6, 6)

            g2d.color = theme.textColor.toAwtColor()
            g2d.font = Font(Font.SANS_SERIF, Font.BOLD, 10)
            val languageText = language.uppercase()
            val languageWidth = g2d.fontMetrics.stringWidth(languageText)
            g2d.drawString(
                languageText,
                codeWidth - 60 + (45 - languageWidth) / 2,
                yOffset + 1
            )

            yOffset += 22
        }

        // Code area background
        if (!transparentBackground) {
            g2d.color = theme.backgroundColor.toAwtColor()
            g2d.fillRoundRect(
                padding,
                yOffset,
                codeWidth,
                codeHeight - windowControlsHeight,
                if (showWindowControls) 0 else cornerRadius * 2,
                cornerRadius * 2
            )
        }

        yOffset += 16

        // Render code
        g2d.font = font
        val keywords = getLanguageKeywords(language)

        lines.forEachIndexed { index, line ->
            var xOffset = padding + 16

            // Line numbers
            if (showLineNumbers) {
                g2d.color = theme.lineNumberColor.toAwtColor()
                g2d.drawString("${index + 1}", xOffset, yOffset + fontMetrics.ascent)
                xOffset += lineNumberWidth
            }

            // Code text with syntax highlighting
            renderLineWithSyntaxHighlighting(
                g2d = g2d,
                line = line,
                x = xOffset,
                y = yOffset + fontMetrics.ascent,
                theme = theme,
                keywords = keywords,
                font = font,
                boldFont = boldFont
            )

            yOffset += lineHeight
        }

        g2d.dispose()
        return image
    }

    private fun renderLineWithSyntaxHighlighting(
        g2d: Graphics2D,
        line: String,
        x: Int,
        y: Int,
        theme: CodeSnippetTheme,
        keywords: List<String>,
        font: Font,
        boldFont: Font,
    ) {
        var currentX = x
        var currentIndex = 0
        val text = line

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

        while (currentIndex < text.length) {
            val remainingText = text.substring(currentIndex)

            when {
                // Skip whitespace but render it
                remainingText[0].isWhitespace() -> {
                    g2d.color = theme.textColor.toAwtColor()
                    g2d.font = font
                    g2d.drawString(remainingText[0].toString(), currentX, y)
                    currentX += g2d.fontMetrics.stringWidth(remainingText[0].toString())
                    currentIndex++
                }

                // String literals (double quotes)
                remainingText.startsWith("\"") -> {
                    val endIndex = remainingText.indexOf("\"", 1)
                    if (endIndex != -1) {
                        val stringLiteral = remainingText.substring(0, endIndex + 1)
                        g2d.color = theme.stringColor.toAwtColor()
                        g2d.font = font
                        g2d.drawString(stringLiteral, currentX, y)
                        currentX += g2d.fontMetrics.stringWidth(stringLiteral)
                        currentIndex += stringLiteral.length
                    } else {
                        g2d.color = theme.textColor.toAwtColor()
                        g2d.font = font
                        g2d.drawString(remainingText[0].toString(), currentX, y)
                        currentX += g2d.fontMetrics.stringWidth(remainingText[0].toString())
                        currentIndex++
                    }
                }

                // String literals (single quotes)
                remainingText.startsWith("'") -> {
                    val endIndex = remainingText.indexOf("'", 1)
                    if (endIndex != -1) {
                        val stringLiteral = remainingText.substring(0, endIndex + 1)
                        g2d.color = theme.stringColor.toAwtColor()
                        g2d.font = font
                        g2d.drawString(stringLiteral, currentX, y)
                        currentX += g2d.fontMetrics.stringWidth(stringLiteral)
                        currentIndex += stringLiteral.length
                    } else {
                        g2d.color = theme.textColor.toAwtColor()
                        g2d.font = font
                        g2d.drawString(remainingText[0].toString(), currentX, y)
                        currentX += g2d.fontMetrics.stringWidth(remainingText[0].toString())
                        currentIndex++
                    }
                }

                // Template strings
                remainingText.startsWith("$") -> {
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
                    g2d.color = theme.variableColor.toAwtColor()
                    g2d.font = font
                    g2d.drawString(template, currentX, y)
                    currentX += g2d.fontMetrics.stringWidth(template)
                    currentIndex += template.length
                }

                // Comments (single line)
                remainingText.startsWith("//") -> {
                    g2d.color = theme.commentColor.toAwtColor()
                    g2d.font = font
                    g2d.drawString(remainingText, currentX, y)
                    break
                }

                // Comments (multi-line)
                remainingText.startsWith("/*") -> {
                    val endIndex = remainingText.indexOf("*/")
                    if (endIndex != -1) {
                        val comment = remainingText.substring(0, endIndex + 2)
                        g2d.color = theme.commentColor.toAwtColor()
                        g2d.font = font
                        g2d.drawString(comment, currentX, y)
                        currentX += g2d.fontMetrics.stringWidth(comment)
                        currentIndex += comment.length
                    } else {
                        g2d.color = theme.commentColor.toAwtColor()
                        g2d.font = font
                        g2d.drawString(remainingText, currentX, y)
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
                            (remainingText[numberEnd].isDigit() ||
                                remainingText[numberEnd].lowercase() in "abcdef")
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
                    g2d.color = theme.numberColor.toAwtColor()
                    g2d.font = font
                    g2d.drawString(number, currentX, y)
                    currentX += g2d.fontMetrics.stringWidth(number)
                    currentIndex += number.length
                }

                // Operators
                operators.any { remainingText.startsWith(it) } -> {
                    val operator = operators.sortedByDescending { it.length }.first { remainingText.startsWith(it) }
                    g2d.color = theme.operatorColor.toAwtColor()
                    g2d.font = boldFont
                    g2d.drawString(operator, currentX, y)
                    currentX += g2d.fontMetrics.stringWidth(operator)
                    currentIndex += operator.length
                }

                // Punctuation
                remainingText[0] in "()[]{},.;:->=" -> {
                    g2d.color = theme.punctuationColor.toAwtColor()
                    g2d.font = font
                    g2d.drawString(remainingText[0].toString(), currentX, y)
                    currentX += g2d.fontMetrics.stringWidth(remainingText[0].toString())
                    currentIndex++
                }

                // Identifiers (keywords, functions, classes, variables)
                remainingText[0].isLetter() || remainingText[0] == '_' -> {
                    var wordEnd = 0
                    while (wordEnd < remainingText.length &&
                        (remainingText[wordEnd].isLetterOrDigit() || remainingText[wordEnd] == '_')
                    ) {
                        wordEnd++
                    }

                    val word = remainingText.substring(0, maxOf(wordEnd, 1))
                    val isKeyword = keywords.any { it.equals(word, ignoreCase = false) }
                    val isFunction = functions.any { it.equals(word, ignoreCase = false) }
                    val isClass = classes.any { it.equals(word, ignoreCase = false) } ||
                        word.firstOrNull()?.isUpperCase() == true

                    val color = when {
                        isKeyword -> theme.keywordColor.toAwtColor()
                        isFunction -> theme.functionColor.toAwtColor()
                        isClass -> theme.classColor.toAwtColor()
                        else -> theme.textColor.toAwtColor()
                    }

                    val selectedFont = when {
                        isKeyword -> boldFont
                        isFunction -> boldFont
                        isClass -> font
                        else -> font
                    }

                    g2d.color = color
                    g2d.font = selectedFont
                    g2d.drawString(word, currentX, y)
                    currentX += g2d.fontMetrics.stringWidth(word)
                    currentIndex += word.length
                }

                // Everything else
                else -> {
                    g2d.color = theme.textColor.toAwtColor()
                    g2d.font = font
                    g2d.drawString(remainingText[0].toString(), currentX, y)
                    currentX += g2d.fontMetrics.stringWidth(remainingText[0].toString())
                    currentIndex++
                }
            }
        }
    }

    private fun getLanguageKeywords(language: String): List<String> {
        return when (language.lowercase()) {
            "kotlin" -> listOf(
                "fun", "val", "var", "class", "interface", "object", "enum", "data",
                "private", "public", "internal", "protected", "override", "abstract",
                "if", "else", "when", "for", "while", "do", "try", "catch", "finally",
                "return", "break", "continue", "null", "true", "false", "this", "super",
                "import", "package", "const", "companion", "init", "constructor",
                "suspend", "inline", "noinline", "crossinline", "reified", "lateinit",
                "sealed", "annotation", "expect", "actual", "external", "operator",
                "infix", "tailrec", "vararg", "out", "in", "is", "as", "typeof"
            )

            "java" -> listOf(
                "public", "private", "protected", "static", "final", "abstract", "class",
                "interface", "extends", "implements", "import", "package", "if", "else",
                "for", "while", "do", "switch", "case", "default", "try", "catch",
                "finally", "throw", "throws", "return", "break", "continue", "new",
                "this", "super", "null", "true", "false", "void", "int", "String",
                "boolean", "double", "float", "long", "char", "byte", "short"
            )

            "javascript", "typescript" -> listOf(
                "abstract", "arguments", "await", "boolean", "break", "byte", "case", "catch",
                "char", "class", "const", "continue", "debugger", "default", "delete", "do",
                "double", "else", "enum", "eval", "export", "extends", "false", "final",
                "finally", "float", "for", "function", "goto", "if", "implements", "import",
                "in", "instanceof", "int", "interface", "let", "long", "native", "new",
                "null", "package", "private", "protected", "public", "return", "short",
                "static", "super", "switch", "synchronized", "this", "throw", "throws",
                "transient", "true", "try", "typeof", "var", "void", "volatile", "while",
                "with", "yield", "async"
            )

            else -> emptyList()
        }
    }

    private fun Color.toAwtColor(): java.awt.Color {
        return JBColor(
            java.awt.Color(red, green, blue, alpha),
            java.awt.Color(red, green, blue, alpha),
        )
    }
}
