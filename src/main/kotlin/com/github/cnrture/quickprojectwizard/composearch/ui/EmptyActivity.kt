package com.github.cnrture.quickprojectwizard.composearch.ui

fun emptyActivity(
    packageName: String,
    projectName: String,
    startDest: String,
    isHiltEnable: Boolean,
    isNavigationEnable: Boolean,
    dataDiDomainPresentationUiPackages: Boolean,
): String {
    return when {
        !dataDiDomainPresentationUiPackages -> emptyActivity(packageName, projectName)
        isHiltEnable && isNavigationEnable -> hiltNavigation(packageName, projectName, startDest)
        isHiltEnable -> hilt(packageName, projectName)
        isNavigationEnable -> navigation(packageName, projectName, startDest)
        else -> emptyActivity(packageName, projectName)
    }
}

private fun hilt(packageName: String, projectName: String) = """
package $packageName.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.ui.setContent
import androidx.activity.enableEdgeToEdge
import androidx.ui.foundation.layout.fillMaxSize
import androidx.ui.foundation.layout.padding
import androidx.ui.material3.Scaffold
import androidx.ui.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import $packageName.ui.theme.${projectName}Theme
import androidx.ui.material3.Text

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ${projectName}Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Text(text = "Hello, World!", modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
""".trimIndent()

private fun hiltNavigation(packageName: String, projectName: String, startDest: String) = """
package $packageName.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.ui.setContent
import androidx.navigation.ui.rememberNavController
import androidx.activity.enableEdgeToEdge
import androidx.ui.foundation.layout.fillMaxSize
import androidx.ui.foundation.layout.padding
import androidx.ui.material3.Scaffold
import androidx.ui.ui.Modifier
import $packageName.ui.navigation.NavigationGraph
import dagger.hilt.android.AndroidEntryPoint
import $packageName.ui.theme.${projectName}Theme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ${projectName}Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()
                    val startDestination = "$startDest"
                    NavigationGraph(
                        navController = navController,
                        startDestination = startDestination,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
""".trimIndent()

private fun navigation(packageName: String, projectName: String, startDest: String) = """
package $packageName.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.ui.setContent
import androidx.navigation.ui.rememberNavController
import androidx.activity.enableEdgeToEdge
import androidx.ui.foundation.layout.fillMaxSize
import androidx.ui.foundation.layout.padding
import androidx.ui.material3.Scaffold
import androidx.ui.ui.Modifier
import $packageName.ui.navigation.NavigationGraph
import $packageName.ui.theme.${projectName}Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ${projectName}Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()
                    val startDestination = "$startDest"
                    NavigationGraph(
                        navController = navController,
                        startDestination = startDestination,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
""".trimIndent()

private fun emptyActivity(packageName: String, projectName: String) = """
package $packageName.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.ui.setContent
import androidx.activity.enableEdgeToEdge
import androidx.ui.foundation.layout.fillMaxSize
import androidx.ui.foundation.layout.padding
import androidx.ui.material3.Scaffold
import androidx.ui.ui.Modifier
import $packageName.ui.theme.${projectName}Theme
import androidx.ui.material3.Text

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ${projectName}Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Text(text = "Hello, World!", modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
""".trimIndent()
