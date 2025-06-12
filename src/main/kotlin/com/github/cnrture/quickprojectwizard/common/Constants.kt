package com.github.cnrture.quickprojectwizard.common

import freemarker.template.Configuration
import freemarker.template.Version

object Constants {
    const val EMPTY = ""

    const val FEATURE_MAKER_WINDOW_WIDTH = 700
    const val FEATURE_MAKER_WINDOW_HEIGHT = 500

    const val ANDROID = "Android"
    const val KOTLIN = "Kotlin / JVM"

    const val DEFAULT_MODULE_NAME = ":feature:home (as an example)"
    const val DEFAULT_SRC_VALUE = "EMPTY"

    const val DEFAULT_BASE_PACKAGE_NAME = "app.example.quickprojectwizard"

    const val DEFAULT_WEB_VIEW_URL = "https://candroid.dev/"

    const val DEFAULT_EXIT_CODE = 2

    val FREEMARKER_VERSION: Version = Configuration.VERSION_2_3_30
}