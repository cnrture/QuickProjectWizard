package com.github.cnrture.quickprojectwizard.toolwindow.manager

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ColorLens
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.cnrture.quickprojectwizard.toolwindow.components.QPWActionCard
import com.github.cnrture.quickprojectwizard.toolwindow.components.QPWText
import com.github.cnrture.quickprojectwizard.toolwindow.theme.QPWTheme
import com.intellij.ui.JBColor
import java.awt.*
import java.awt.datatransfer.StringSelection
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseMotionAdapter
import java.awt.geom.Ellipse2D
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.SwingUtilities

data class ColorInfo(
    val color: Color,
    val hex: String,
    val rgb: String,
    val timestamp: String,
)

@Composable
fun ColorPickerComponent() {
    var currentColor by remember { mutableStateOf(Color.White) }
    var colorHistory by remember { mutableStateOf(emptyList<ColorInfo>()) }
    var pickedColorInfo by remember { mutableStateOf<ColorInfo?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(QPWTheme.colors.black)
            .padding(24.dp)
    ) {
        QPWText(
            modifier = Modifier.fillMaxWidth(),
            text = "Color Picker",
            style = TextStyle(
                color = QPWTheme.colors.green,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
        )

        Spacer(modifier = Modifier.size(24.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            shape = RoundedCornerShape(8.dp),
            backgroundColor = QPWTheme.colors.gray,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(currentColor)
                        .border(2.dp, QPWTheme.colors.white, RoundedCornerShape(8.dp))
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    pickedColorInfo?.let { colorInfo ->
                        ColorInfoRow("HEX:", colorInfo.hex)
                        ColorInfoRow("RGB:", colorInfo.rgb)
                        QPWText(
                            text = "Picked: ${colorInfo.timestamp}",
                            color = QPWTheme.colors.white,
                            style = TextStyle(fontSize = 12.sp)
                        )
                    } ?: run {
                        QPWText(
                            text = "Click 'Pick Color' to select from screen",
                            color = QPWTheme.colors.white,
                            style = TextStyle(fontSize = 14.sp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            QPWActionCard(
                modifier = Modifier.weight(1f),
                title = "Pick Color",
                icon = Icons.Rounded.ColorLens,
                actionColor = QPWTheme.colors.green,
                onClick = {
                    startColorPicking { color ->
                        val colorInfo = createColorInfo(color)
                        currentColor = color
                        pickedColorInfo = colorInfo
                        colorHistory = listOf(colorInfo) + colorHistory.take(9)
                    }
                }
            )

            pickedColorInfo?.let {
                QPWActionCard(
                    title = "Copy HEX",
                    icon = Icons.Rounded.ContentCopy,
                    actionColor = QPWTheme.colors.green,
                    onClick = { copyToClipboard(it.hex) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (colorHistory.isNotEmpty()) {
            QPWText(
                text = "Recent Colors",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = QPWTheme.colors.white,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(colorHistory) { colorInfo ->
                    ColorHistoryItem(
                        colorInfo = colorInfo,
                        onCopyHex = { copyToClipboard(colorInfo.hex) },
                        onCopyRgb = { copyToClipboard(colorInfo.rgb) },
                        onSelect = {
                            currentColor = colorInfo.color
                            pickedColorInfo = colorInfo
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ColorInfoRow(label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        QPWText(
            text = label,
            color = QPWTheme.colors.white,
            style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.width(8.dp))
        QPWText(
            text = value,
            color = QPWTheme.colors.green,
            style = TextStyle(fontSize = 12.sp, fontFamily = FontFamily.Monospace)
        )
    }
}

@Composable
private fun ColorHistoryItem(
    colorInfo: ColorInfo,
    onCopyHex: () -> Unit,
    onCopyRgb: () -> Unit,
    onSelect: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        shape = RoundedCornerShape(8.dp),
        backgroundColor = QPWTheme.colors.gray,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(colorInfo.color)
                    .border(1.dp, QPWTheme.colors.white, RoundedCornerShape(6.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                QPWText(
                    text = colorInfo.hex,
                    color = QPWTheme.colors.white,
                    style = TextStyle(fontSize = 14.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                )
                QPWText(
                    text = colorInfo.rgb,
                    color = QPWTheme.colors.gray,
                    style = TextStyle(fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Button(
                    onClick = onCopyHex,
                    modifier = Modifier.size(width = 60.dp, height = 32.dp),
                    colors = androidx.compose.material.ButtonDefaults.buttonColors(
                        backgroundColor = QPWTheme.colors.green
                    )
                ) {
                    QPWText(
                        text = "HEX",
                        color = QPWTheme.colors.black,
                        style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    )
                }

                Button(
                    onClick = onCopyRgb,
                    modifier = Modifier.size(width = 60.dp, height = 32.dp),
                    colors = androidx.compose.material.ButtonDefaults.buttonColors(
                        backgroundColor = QPWTheme.colors.green
                    )
                ) {
                    QPWText(
                        text = "RGB",
                        color = QPWTheme.colors.white,
                        style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

private fun startColorPicking(onColorPicked: (Color) -> Unit) {
    SwingUtilities.invokeLater {
        try {
            val robot = Robot()

            val screenRect = Rectangle(Toolkit.getDefaultToolkit().screenSize)
            val screenCapture = robot.createScreenCapture(screenRect)

            val frame = JFrame()
            frame.isUndecorated = true
            frame.isAlwaysOnTop = true
            frame.extendedState = JFrame.MAXIMIZED_BOTH
            frame.defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
            frame.cursor = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR)

            val panel = object : JPanel() {
                override fun paintComponent(g: Graphics?) {
                    super.paintComponent(g)
                    g?.drawImage(screenCapture, 0, 0, width, height, null)

                    val mousePos = mousePosition
                    mousePos?.let { pos ->
                        g?.color = JBColor.WHITE
                        g?.drawLine(pos.x - 10, pos.y, pos.x + 10, pos.y)
                        g?.drawLine(pos.x, pos.y - 10, pos.x, pos.y + 10)

                        g?.color = JBColor.BLACK
                        g?.drawLine(pos.x - 11, pos.y - 1, pos.x + 11, pos.y - 1)
                        g?.drawLine(pos.x - 11, pos.y + 1, pos.x + 11, pos.y + 1)
                        g?.drawLine(pos.x - 1, pos.y - 11, pos.x - 1, pos.y + 11)
                        g?.drawLine(pos.x + 1, pos.y - 11, pos.x + 1, pos.y + 11)

                        drawZoomPreview(g, pos, screenCapture, screenRect)
                    }
                }

                private fun drawZoomPreview(
                    g: Graphics?,
                    mousePos: Point,
                    screenCapture: java.awt.image.BufferedImage,
                    screenRect: Rectangle,
                ) {
                    g?.let { graphics ->
                        try {
                            val screenX = (mousePos.x.toFloat() / width * screenRect.width).toInt()
                            val screenY = (mousePos.y.toFloat() / height * screenRect.height).toInt()

                            val zoomSize = 120
                            val zoomRadius = zoomSize / 2
                            val captureSize = 20

                            val previewX = mousePos.x + 20
                            val previewY = mousePos.y - zoomSize - 10

                            val finalPreviewX = when {
                                previewX + zoomSize > width -> mousePos.x - zoomSize - 20
                                else -> previewX
                            }
                            val finalPreviewY = when {
                                previewY < 0 -> mousePos.y + 20
                                else -> previewY
                            }

                            val zoomX = (screenX - captureSize / 2).coerceIn(0, screenCapture.width - captureSize)
                            val zoomY = (screenY - captureSize / 2).coerceIn(0, screenCapture.height - captureSize)

                            val zoomArea = screenCapture.getSubimage(zoomX, zoomY, captureSize, captureSize)

                            // Create circular clip for zoom preview
                            val originalClip = graphics.clip
                            val circularShape = Ellipse2D.Double(
                                finalPreviewX.toDouble(),
                                finalPreviewY.toDouble(),
                                zoomSize.toDouble(),
                                zoomSize.toDouble()
                            )
                            (graphics as Graphics2D).clip(circularShape)

                            // Draw black circular background
                            graphics.color = JBColor.BLACK
                            graphics.fillOval(finalPreviewX, finalPreviewY, zoomSize, zoomSize)

                            // Draw zoomed image (will be clipped to circle)
                            graphics.drawImage(
                                zoomArea,
                                finalPreviewX, finalPreviewY,
                                zoomSize, zoomSize,
                                null
                            )

                            // Draw center crosshair in zoom preview
                            graphics.color = JBColor.RED
                            val centerX = finalPreviewX + zoomRadius
                            val centerY = finalPreviewY + zoomRadius
                            graphics.drawLine(centerX - 5, centerY, centerX + 5, centerY)
                            graphics.drawLine(centerX, centerY - 5, centerX, centerY + 5)

                            // Restore original clip
                            graphics.clip = originalClip

                            // Draw circle border
                            graphics.color = JBColor.WHITE
                            graphics.drawOval(finalPreviewX, finalPreviewY, zoomSize, zoomSize)
                            graphics.color = JBColor.BLACK
                            graphics.drawOval(finalPreviewX - 1, finalPreviewY - 1, zoomSize + 2, zoomSize + 2)

                            val currentPixelColor = java.awt.Color(screenCapture.getRGB(screenX, screenY))
                            val hexColor = "#%02X%02X%02X".format(
                                currentPixelColor.red,
                                currentPixelColor.green,
                                currentPixelColor.blue
                            )

                            // Draw text with better outline
                            graphics.font = Font("Arial", Font.BOLD, 14)
                            val textY = finalPreviewY + zoomSize + 20

                            // Draw black outline (shadow effect)
                            graphics.color = JBColor.BLACK
                            for (dx in -1..1) {
                                for (dy in -1..1) {
                                    if (dx != 0 || dy != 0) {
                                        graphics.drawString(hexColor, finalPreviewX + dx, textY + dy)
                                    }
                                }
                            }

                            // Draw white text on top
                            graphics.color = JBColor.WHITE
                            graphics.drawString(hexColor, finalPreviewX, textY)

                        } catch (e: Exception) {
                        }
                    }
                }
            }

            panel.addMouseListener(object : MouseListener {
                override fun mouseClicked(e: MouseEvent?) {
                    e?.let { event ->
                        try {
                            val screenX = (event.x.toFloat() / panel.width * screenRect.width).toInt()
                            val screenY = (event.y.toFloat() / panel.height * screenRect.height).toInt()

                            val pixelColor = Color(screenCapture.getRGB(screenX, screenY))

                            frame.dispose()
                            onColorPicked(pixelColor)
                        } catch (ex: Exception) {
                            frame.dispose()
                            println("Error picking color: ${ex.message}")
                        }
                    }
                }

                override fun mousePressed(e: MouseEvent?) {}
                override fun mouseReleased(e: MouseEvent?) {}
                override fun mouseEntered(e: MouseEvent?) {}
                override fun mouseExited(e: MouseEvent?) {}
            })

            panel.addMouseMotionListener(object : java.awt.event.MouseMotionAdapter() {
                override fun mouseMoved(e: MouseEvent?) {
                    panel.repaint()
                }
            })

            panel.addKeyListener(object : java.awt.event.KeyAdapter() {
                override fun keyPressed(e: java.awt.event.KeyEvent?) {
                    if (e?.keyCode == java.awt.event.KeyEvent.VK_ESCAPE) {
                        frame.dispose()
                    }
                }
            })

            panel.isFocusable = true
            frame.add(panel)
            frame.isVisible = true
            panel.requestFocus()

        } catch (e: Exception) {
            println("❌ Error starting color picker: ${e.message}")
        }
    }
}

private fun createColorInfo(color: Color): ColorInfo {
    val r = (color.red * 255).toInt()
    val g = (color.green * 255).toInt()
    val b = (color.blue * 255).toInt()

    val hex = "#%02X%02X%02X".format(r, g, b)
    val rgb = "rgb($r, $g, $b)"
    val timestamp = java.time.LocalTime.now().toString().substring(0, 8)

    return ColorInfo(color, hex, rgb, timestamp)
}

private fun copyToClipboard(text: String) {
    try {
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        val selection = StringSelection(text)
        clipboard.setContents(selection, null)
    } catch (e: Exception) {
        println("❌ Error copying to clipboard: ${e.message}")
    }
}
