package com.github.cnrture.quickprojectwizard.projectwizard.composearch.ui.navigation

fun emptyNavigationGraph(
    packageName: String,
    screenListString: String,
    screensImports: String,
    isHiltEnable: Boolean,
): String {
    return if (isHiltEnable) {
        hilt(packageName, screenListString, screensImports)
    } else {
        withoutHilt(packageName, screenListString, screensImports)
    }
}

fun hilt(packageName: String, screenListString: String, screensImports: String) = """
package $packageName.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
$screensImports

@Composable
fun NavigationGraph(
    navController: NavHostController,
    startDestination: Screen,
    modifier: Modifier = Modifier,
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination,
    ) {
        $screenListString
    }
}
"""

fun withoutHilt(packageName: String, screenListString: String, screensImports: String) = """
package $packageName.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
$screensImports

@Composable
fun NavigationGraph(
    navController: NavHostController,
    startDestination: Screen,
    modifier: Modifier = Modifier,
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination,
    ) {
        $screenListString
    }
}
"""
