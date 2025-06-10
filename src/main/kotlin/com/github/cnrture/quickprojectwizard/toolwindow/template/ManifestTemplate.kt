package com.github.cnrture.quickprojectwizard.toolwindow.template

object ManifestTemplate {
    fun getManifestTemplate(packageName: String) = """
        <?xml version="1.0" encoding="utf-8"?>
        <manifest xmlns:android="http://schemas.android.com/apk/res/android"
            package="$packageName">
        </manifest>
    """.trimIndent()
}