package com.github.cnrture.quickprojectwizard.composearch.ui.navigation

fun emptyNavigationScreen(packageName: String, navScreenListString: String) = """
package $packageName.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {
    $navScreenListString
}
"""