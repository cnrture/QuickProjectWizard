package com.github.cnrture.quickprojectwizard.arch.res

fun emptyMainNavGraphXML(screenListString: String, startDest: String) = """
<navigation xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/main_nav_graph"
    app:startDestination="@id/${startDest.lowercase()}Fragment">
    
    $screenListString
</navigation>
""".trimIndent()
