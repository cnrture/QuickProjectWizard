package com.github.cnrture.quickprojectwizard.projectwizard.general

fun emptyMainApplication(packageName: String, projectName: String) = """
package $packageName

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApp: Application()
""".trimIndent()