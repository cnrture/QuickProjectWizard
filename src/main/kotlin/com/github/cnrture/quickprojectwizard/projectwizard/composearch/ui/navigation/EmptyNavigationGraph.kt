package com.github.cnrture.quickprojectwizard.projectwizard.composearch.ui.navigation

import com.github.cnrture.quickprojectwizard.data.DILibrary

fun emptyNavigationGraph(
    packageName: String,
    screenListString: String,
    screensImports: String,
    selectedDILibrary: DILibrary,
): String {
    return when (selectedDILibrary) {
        DILibrary.Hilt -> hilt(packageName, screenListString, screensImports)
        DILibrary.Koin -> koin(packageName, screenListString, screensImports)
        DILibrary.None -> withoutDI(packageName, screenListString, screensImports)
    }
}

fun hilt(packageName: String, screenListString: String, screensImports: String) = """
package $packageName.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
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

fun koin(packageName: String, screenListString: String, screensImports: String) = """
package $packageName.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import org.koin.androidx.compose.koinViewModel
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

fun withoutDI(packageName: String, screenListString: String, screensImports: String) = """
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
