package com.github.cnrture.quickprojectwizard.projectwizard.composearch.ui.main

fun emptyMainScreen(packageName: String, screen: String) = """
package $packageName.ui.${screen.lowercase()}

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.sp
import $packageName.common.collectWithLifecycle
import $packageName.ui.${screen.lowercase()}.${screen}Contract.UiAction
import $packageName.ui.${screen.lowercase()}.${screen}Contract.UiEffect
import $packageName.ui.${screen.lowercase()}.${screen}Contract.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@Composable
fun ${screen}Screen(
    uiState: UiState,
    uiEffect: Flow<UiEffect>,
    onAction: (UiAction) -> Unit,
) {
    uiEffect.collectWithLifecycle {}

    ${screen}Content(
        modifier = Modifier.fillMaxSize(),
        uiState = uiState,
        onAction = onAction,
    )
}

@Composable
fun ${screen}Content(
    modifier: Modifier = Modifier,
    uiState: UiState,
    onAction: (UiAction) -> Unit,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "$screen Content",
            fontSize = 24.sp,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ${screen}ScreenPreview(
    @PreviewParameter(${screen}ScreenPreviewProvider::class) uiState: UiState,
) {
    ${screen}Screen(
        uiState = uiState,
        uiEffect = emptyFlow(),
        onAction = {},
    )
}
"""
