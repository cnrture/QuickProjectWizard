package com.github.cnrture.quickprojectwizard.arch.ui.compose

fun emptyActivity(packageName: String, startDest: String) = """
package $packageName.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import $packageName.ui.navigation.NavigationGraph
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val startDestination = "$startDest"
            NavigationGraph(
                navController = navController,
                startDestination = startDestination,
            )
        }
    }
}
"""
