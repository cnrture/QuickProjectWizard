package com.github.cnrture.quickprojectwizard.data

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ColorInfo(
    val red: Float,
    val green: Float,
    val blue: Float,
    val alpha: Float = 1.0f,
    val hex: String,
    val rgb: String,
    val timestamp: String,
) {
    @Transient
    val color: Color = Color(red, green, blue, alpha)

    companion object {
        fun from(color: Color, hex: String, rgb: String, timestamp: String): ColorInfo {
            return ColorInfo(
                red = color.red,
                green = color.green,
                blue = color.blue,
                alpha = color.alpha,
                hex = hex,
                rgb = rgb,
                timestamp = timestamp
            )
        }
    }
}