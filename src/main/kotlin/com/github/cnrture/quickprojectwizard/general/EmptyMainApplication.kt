package com.github.cnrture.quickprojectwizard.general

fun emptyMainApplication(packageName: String, projectName: String) = """
package $packageName

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ${projectName}Application: Application()
""".trimIndent()