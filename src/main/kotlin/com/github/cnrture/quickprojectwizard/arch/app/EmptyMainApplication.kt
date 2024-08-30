package com.github.cnrture.quickprojectwizard.arch.app

fun emptyMainApplication(packageName: String) = """
package $packageName

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication: Application()
"""
