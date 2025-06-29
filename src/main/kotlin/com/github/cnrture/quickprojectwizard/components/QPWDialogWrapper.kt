package com.github.cnrture.quickprojectwizard.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.awt.ComposePanel
import com.github.cnrture.quickprojectwizard.theme.QPWTheme
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.JBColor
import com.intellij.util.ui.JBUI
import java.awt.Color
import javax.swing.Action
import javax.swing.JComponent
import javax.swing.JRootPane
import javax.swing.UIManager
import javax.swing.border.Border

abstract class QPWDialogWrapper(
    width: Int = 0,
    height: Int = 0,
    modal: Boolean = true,
) : DialogWrapper(modal) {

    private val color = JBColor(Color(0xFF18181B.toInt()), Color(0xFF18181B.toInt()))

    init {
        init()
        UIManager.put("Panel.background", color)
        if (width > 0 && height > 0) setSize(width, height)
        window?.setLocationRelativeTo(null)

        if (!modal) {
            isModal = false
            window?.let { window ->
                window.isAlwaysOnTop = false
                window.isAutoRequestFocus = false
                window.focusableWindowState = true
            }
        }
    }

    @Composable
    abstract fun createDesign()

    override fun createCenterPanel(): JComponent {
        return ComposePanel().apply {
            setContent {
                QPWTheme {
                    createDesign()
                }
            }
        }
    }

    override fun createActions(): Array<Action> = emptyArray()

    override fun createSouthPanel(): JComponent {
        val southPanel = super.createSouthPanel()
        southPanel.background = color
        for (component in southPanel.components) {
            component.background = color
            if (component is JComponent) component.isOpaque = true
        }
        return southPanel
    }

    override fun getRootPane(): JRootPane? {
        val rootPane = super.getRootPane()
        rootPane.background = color
        return rootPane
    }

    override fun createContentPaneBorder(): Border = JBUI.Borders.empty()
}