package com.github.cnrture.quickprojectwizard.projectwizard.general

fun emptyMainApplication(packageName: String, isKoin: Boolean = false) = if (isKoin) {
    """
package $packageName

import android.app.Application
import $packageName.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainApp: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MainApp)
            modules(appModule)
        }
    }
}
""".trimIndent()
} else {
    """
package $packageName

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApp: Application()
""".trimIndent()
}