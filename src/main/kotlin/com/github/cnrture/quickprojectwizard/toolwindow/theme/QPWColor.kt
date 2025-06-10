package com.github.cnrture.quickprojectwizard.toolwindow.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

fun lightColors(
    white: Color = Color(0xffecedee),
    black: Color = Color(0xff000000),
    gray: Color = Color(0xff18181b),
    lightGray: Color = Color(0xffa1a1aa),
    purple: Color = Color(0xff7F52FF),
    green: Color = Color(0xff339e48),
    red: Color = Color(0xffE44857),
    lightRed: Color = Color(0xffEF9A9A9),
): QPWColor = QPWColor(
    white = white,
    black = black,
    gray = gray,
    lightGray = lightGray,
    purple = purple,
    green = green,
    red = red,
)

class QPWColor(
    white: Color,
    black: Color,
    gray: Color,
    lightGray: Color,
    purple: Color,
    green: Color,
    red: Color,
) {
    private var _white: Color by mutableStateOf(white)
    val white: Color = _white

    private var _black: Color by mutableStateOf(black)
    val black: Color = _black

    private var _gray: Color by mutableStateOf(gray)
    val gray: Color = _gray

    private var _lightGray: Color by mutableStateOf(lightGray)
    val lightGray: Color = _lightGray

    private var _red : Color by mutableStateOf(red)
    val red: Color = _red

    private var _purple: Color by mutableStateOf(purple)
    val purple: Color = _purple

    private var _green: Color by mutableStateOf(green)
    val green: Color = _green
}

internal val LocalColors = staticCompositionLocalOf { lightColors() }