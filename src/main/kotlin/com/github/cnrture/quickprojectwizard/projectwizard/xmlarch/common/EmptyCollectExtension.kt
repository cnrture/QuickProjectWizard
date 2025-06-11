package com.github.cnrture.quickprojectwizard.projectwizard.xmlarch.common

fun emptyCollectExtension(packageName: String) = """
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