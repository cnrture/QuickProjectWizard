package com.github.cnrture.quickprojectwizard.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

object QPWTheme {
    val colors: QPWColor
        @Composable
        @ReadOnlyComposable
        get() = LocalColors.current
}

@Composable
fun QPWTheme(content: @Composable () -> Unit) {
    QPWTheme(
        colors = lightColors(),
        content = content,
    )
}

@Composable
private fun QPWTheme(
    colors: QPWColor = QPWTheme.colors,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalColors provides colors,
    ) {
        content()
    }
}