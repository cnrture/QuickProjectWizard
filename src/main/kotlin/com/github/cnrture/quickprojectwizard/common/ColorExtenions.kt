package com.github.cnrture.quickprojectwizard.common

import androidx.compose.ui.graphics.Color


fun Color.toHSV(): FloatArray {
    val r = red
    val g = green
    val b = blue

    val max = maxOf(r, g, b)
    val min = minOf(r, g, b)
    val delta = max - min

    val h: Float
    val s: Float
    val v = max

    s = if (max == 0f) 0f else delta / max

    h = when {
        delta == 0f -> 0f
        max == r -> ((g - b) / delta + (if (g < b) 6f else 0f)) * 60f
        max == g -> ((b - r) / delta + 2f) * 60f
        else -> ((r - g) / delta + 4f) * 60f
    }

    return floatArrayOf(h % 360f, s, v)
}

fun Color.Companion.hsvToColor(h: Float, s: Float, v: Float): Color {
    val c = v * s
    val x = c * (1 - kotlin.math.abs((h / 60f) % 2 - 1))
    val m = v - c

    val (r1, g1, b1) = when {
        h < 60f -> Triple(c, x, 0f)
        h < 120f -> Triple(x, c, 0f)
        h < 180f -> Triple(0f, c, x)
        h < 240f -> Triple(0f, x, c)
        h < 300f -> Triple(x, 0f, c)
        else -> Triple(c, 0f, x)
    }

    return Color(r1 + m, g1 + m, b1 + m, 1f)
}