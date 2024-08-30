package com.github.cnrture.quickprojectwizard.arch.common

fun emptyCollectExtension(packageName: String, isCompose: Boolean): String {
    return if (isCompose) compose(packageName) else xml(packageName)
}

private fun compose(packageName: String) = """
package $packageName.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow

@Composable
fun <T> Flow<T>.collectWithLifecycle(
    collect: suspend (T) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(this, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            this@collectWithLifecycle.collect { effect ->
                collect(effect)
            }
        }
    }
}
""".trimIndent()

private fun xml(packageName: String) = """
package $packageName.common

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

fun <T> Flow<T>.collect(
    lifecycleOwner: LifecycleOwner,
    function: (T) -> Unit
) {
    lifecycleOwner.lifecycleScope.launch {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            collect {
                function(it)
            }
        }
    }
}
""".trimIndent()
