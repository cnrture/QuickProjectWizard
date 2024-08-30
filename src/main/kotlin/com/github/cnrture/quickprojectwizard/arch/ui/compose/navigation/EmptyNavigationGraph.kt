package com.github.cnrture.quickprojectwizard.arch.ui.compose.navigation

fun emptyNavigationGraph(packageName: String, screenListString: String, screensImports: String) = """
package $packageName.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
$screensImports

@Composable
fun NavigationGraph(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        $screenListString
    }
}
"""
