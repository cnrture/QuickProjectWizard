package com.github.cnrture.quickprojectwizard.projectwizard.general

import com.github.cnrture.quickprojectwizard.data.DILibrary

fun emptyManifestXml(
    styleName: String,
    selectedDILibrary: DILibrary,
    dataDiDomainPresentationUiPackages: Boolean,
): String {
    val isHiltEnable = selectedDILibrary == DILibrary.Hilt
    val isKoinEnable = selectedDILibrary == DILibrary.Koin
    return when {
        isHiltEnable && dataDiDomainPresentationUiPackages -> hilt(styleName)
        isKoinEnable && dataDiDomainPresentationUiPackages -> koin(styleName)
        else -> withoutDI(styleName)
    }
}

fun hilt(styleName: String) = """
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <uses-permission android:name="android.permission.INTERNET" />

    <application
        android:name=".MainApp"
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="$styleName"
        tools:targetApi="31">
        <activity
            android:name=".ui.MainActivity"
            android:exported="true"
            android:theme="$styleName">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
""".trimIndent()

fun koin(styleName: String) = """
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <uses-permission android:name="android.permission.INTERNET" />

    <application
        android:name=".MainApp"
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="$styleName"
        tools:targetApi="31">
        <activity
            android:name=".ui.MainActivity"
            android:exported="true"
            android:theme="$styleName">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
""".trimIndent()

fun withoutDI(styleName: String) = """
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <uses-permission android:name="android.permission.INTERNET" />

    <application
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="$styleName"
        tools:targetApi="31">
        <activity
            android:name=".ui.MainActivity"
            android:exported="true"
            android:theme="$styleName">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
""".trimIndent()