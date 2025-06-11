package com.github.cnrture.quickprojectwizard.projectwizard.recipes

import com.android.tools.idea.npw.module.recipes.generateManifest
import com.android.tools.idea.wizard.template.ModuleTemplateData
import com.android.tools.idea.wizard.template.PackageName
import com.android.tools.idea.wizard.template.RecipeExecutor
import com.android.tools.idea.wizard.template.escapeKotlinIdentifier
import com.github.cnrture.quickprojectwizard.data.SettingsService
import com.github.cnrture.quickprojectwizard.projectwizard.addRootFile
import com.github.cnrture.quickprojectwizard.projectwizard.addSrcFile
import com.github.cnrture.quickprojectwizard.projectwizard.general.*
import com.github.cnrture.quickprojectwizard.projectwizard.general.data.model.emptyMainEntityModel
import com.github.cnrture.quickprojectwizard.projectwizard.general.data.repository.emptyMainRepositoryImpl
import com.github.cnrture.quickprojectwizard.projectwizard.general.data.source.local.emptyMainDao
import com.github.cnrture.quickprojectwizard.projectwizard.general.data.source.local.emptyMainRoomDB
import com.github.cnrture.quickprojectwizard.projectwizard.general.data.source.remote.emptyKtorApi
import com.github.cnrture.quickprojectwizard.projectwizard.general.data.source.remote.emptyMainService
import com.github.cnrture.quickprojectwizard.projectwizard.general.detekt.emptyDetektConfig
import com.github.cnrture.quickprojectwizard.projectwizard.general.di.emptyLocalModule
import com.github.cnrture.quickprojectwizard.projectwizard.general.di.emptyMainRepositoryModule
import com.github.cnrture.quickprojectwizard.projectwizard.general.di.emptyNetworkModule
import com.github.cnrture.quickprojectwizard.projectwizard.general.domain.emptyMainRepository
import com.github.cnrture.quickprojectwizard.projectwizard.gradle.getDependencies
import com.github.cnrture.quickprojectwizard.projectwizard.gradle.getGradleKts
import com.github.cnrture.quickprojectwizard.projectwizard.gradle.getProjectGradleKts
import com.github.cnrture.quickprojectwizard.projectwizard.util.NotificationUtil
import com.github.cnrture.quickprojectwizard.projectwizard.xmlarch.common.emptyCollectExtension
import com.github.cnrture.quickprojectwizard.projectwizard.xmlarch.ui.*
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import java.io.File

fun RecipeExecutor.xmlProjectRecipe(
    moduleData: ModuleTemplateData,
    packageName: PackageName,
    isRoomEnable: Boolean,
    isWorkManagerEnable: Boolean,
    selectedNetworkLibrary: NetworkLibrary,
    isHiltEnable: Boolean,
    isNavigationEnable: Boolean,
    selectedImageLibrary: ImageLibrary,
    isKtLintEnable: Boolean,
    isDetektEnable: Boolean,
    isFirebaseEnable: Boolean,
    dataDiDomainPresentationUiPackages: Boolean,
    screens: String,
    javaJvmVersion: String,
) {
    val packagePath = escapeKotlinIdentifier(packageName)

    val settings = ApplicationManager.getApplication().service<SettingsService>()
    settings.loadState(
        settings.state.copy(
            isHiltEnable = isHiltEnable,
            isCompose = false,
            defaultPackageName = packagePath,
        )
    )

    generateManifest(hasApplicationBlock = true)

    val screenList = if (screens.isNotEmpty()) {
        screens.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    } else {
        listOf("Main")
    }

    addActivity(
        moduleData,
        packagePath,
        isHiltEnable,
        dataDiDomainPresentationUiPackages,
        isNavigationEnable,
    )

    val (_, _, _, manifestOut) = moduleData

    mergeXml(
        emptyManifestXml(
            "@style/${moduleData.themesData.main.name}",
            isHiltEnable,
            dataDiDomainPresentationUiPackages,
        ),
        manifestOut.resolve("AndroidManifest.xml")
    )

    if (isDetektEnable) {
        addRootFile(emptyDetektConfig(), moduleData, "detekt/detektConfig.yml")
    }

    if (dataDiDomainPresentationUiPackages) {
        if (isHiltEnable) {
            addSrcFile(emptyMainApplication(packagePath), moduleData, "MainApp.kt")
        }

        addSrcFile(emptyConstants(packagePath), moduleData, "common/Constants.kt")

        addExtensions(moduleData, packagePath)

        addNavigation(moduleData, packagePath, isNavigationEnable, screenList)

        addScreens(moduleData, packagePath, isHiltEnable, screenList)

        addSrcFile(emptyMainRepository(packagePath), moduleData, "domain/repository/MainRepository.kt")

        addSrcFile(
            emptyMainRepositoryImpl(
                packagePath,
                isRoomEnable,
                selectedNetworkLibrary != NetworkLibrary.None,
                isHiltEnable
            ),
            moduleData,
            "data/repository/MainRepositoryImpl.kt"
        )

        if (isHiltEnable) {
            addSrcFile(emptyMainRepositoryModule(packagePath), moduleData, "di/RepositoryModule.kt")
        }

        if (selectedNetworkLibrary != NetworkLibrary.None) {
            addSrcFile(
                emptyMainService(packagePath, selectedNetworkLibrary),
                moduleData,
                "data/source/remote/MainService.kt"
            )

            if (isHiltEnable) {
                addSrcFile(emptyNetworkModule(packagePath, selectedNetworkLibrary), moduleData, "di/NetworkModule.kt")
            }

            if (selectedNetworkLibrary == NetworkLibrary.Ktor) {
                addSrcFile(emptyKtorApi(packagePath), moduleData, "data/source/remote/KtorApi.kt")
            }
        }

        if (isRoomEnable) {
            addSrcFile(emptyMainEntityModel(packagePath), moduleData, "data/model/MainEntityModel.kt")
            addSrcFile(emptyMainDao(packagePath), moduleData, "data/source/local/MainDao.kt")
            addSrcFile(emptyMainRoomDB(packagePath), moduleData, "data/source/local/MainRoomDB.kt")

            if (isHiltEnable) {
                addSrcFile(emptyLocalModule(packagePath), moduleData, "di/LocalModule.kt")
            }
        }
    }

    addDependenciesAndGradle(
        moduleData,
        isHiltEnable,
        isKtLintEnable,
        isDetektEnable,
        isFirebaseEnable,
        isRoomEnable,
        isNavigationEnable,
        isWorkManagerEnable,
        selectedNetworkLibrary,
        selectedImageLibrary,
        packagePath,
        moduleData.apis.minApi.api,
        javaJvmVersion,
        moduleData.themesData.main.name,
    )

    NotificationUtil.showInfo(
        title = "Quick Project Wizard",
        message = "Your project is ready! 🚀 If you like the plugin, please comment and rate it on the plugin page. 🙏",
    )
}

private fun RecipeExecutor.addExtensions(
    moduleData: ModuleTemplateData,
    packagePath: String,
) {
    addSrcFile(
        emptyCollectExtension(packagePath),
        moduleData,
        "common/CollectExtension.kt"
    )
}

private fun RecipeExecutor.addActivity(
    moduleData: ModuleTemplateData,
    packagePath: String,
    isHiltEnable: Boolean,
    dataDiDomainPresentationUiPackages: Boolean,
    isNavigationEnable: Boolean,
) {
    addSrcFile(
        emptyActivityXML(packagePath, isHiltEnable, dataDiDomainPresentationUiPackages),
        moduleData,
        "ui/MainActivity.kt"
    )
    addRootFile(
        emptyActivityLayout(isNavigationEnable, dataDiDomainPresentationUiPackages),
        moduleData,
        "app/src/main/res/layout/activity_main.xml"
    )
}

private fun RecipeExecutor.addNavigation(
    moduleData: ModuleTemplateData,
    packagePath: String,
    isNavigationEnable: Boolean,
    screenList: List<String>,
) {
    if (!isNavigationEnable) return
    val mainGraph = StringBuilder().apply {
        screenList.forEach {
            append("    <fragment\n")
            append("        android:id=\"@+id/${it.lowercase()}Fragment\"\n")
            append("        android:name=\"$packagePath.ui.${it.lowercase()}.${it}Fragment\"\n")
            append("        android:label=\"${it}Fragment\"\n")
            append("        tools:layout=\"@layout/fragment_${it.lowercase()}\"/>\n")
        }
    }.toString()
    addRootFile(
        emptyMainNavGraphXML(mainGraph, screenList[0]),
        moduleData,
        "app/src/main/res/navigation/main_nav_graph.xml"
    )
}

private fun RecipeExecutor.addScreens(
    moduleData: ModuleTemplateData,
    packagePath: String,
    isHiltEnable: Boolean,
    screenList: List<String>,
) {
    screenList.forEach {
        val isFirstLetterUpperCase = it[0].isUpperCase()
        val xmlName = it.split("(?=[A-Z])".toRegex()).joinToString("_").lowercase()
        addSrcFile(
            emptyMainFragment(packagePath, it, isHiltEnable),
            moduleData,
            "ui/${it.lowercase()}/${it}Fragment.kt"
        )
        addSrcFile(
            emptyMainViewModelXML(packagePath, it, isHiltEnable),
            moduleData,
            "ui/${it.lowercase()}/${it}ViewModel.kt"
        )
        addSrcFile(emptyMainUIState(packagePath, it), moduleData, "ui/${it.lowercase()}/${it}UiState.kt")
        if (isFirstLetterUpperCase) {
            addRootFile(emptyFragmentLayout(it), moduleData, "app/src/main/res/layout/fragment$xmlName.xml")
        } else {
            addRootFile(emptyFragmentLayout(it), moduleData, "app/src/main/res/layout/fragment_${xmlName}.xml")
        }
    }
}

private fun addDependenciesAndGradle(
    moduleData: ModuleTemplateData,
    isHiltEnable: Boolean,
    isKtLintEnable: Boolean,
    isDetektEnable: Boolean,
    isFirebaseEnable: Boolean,
    isRoomEnable: Boolean,
    isNavigationEnable: Boolean,
    isWorkManagerEnable: Boolean,
    selectedNetworkLibrary: NetworkLibrary,
    selectedImageLibrary: ImageLibrary,
    packagePath: String,
    minApi: Int,
    javaJvmVersion: String,
    styleName: String,
) {
    val dependencies = getDependencies(
        isCompose = false,
        isHiltEnable = isHiltEnable,
        isKtLintEnable = isKtLintEnable,
        isDetektEnable = isDetektEnable,
        isFirebaseEnable = isFirebaseEnable,
        isRoomEnable = isRoomEnable,
        isNavigationEnable = isNavigationEnable,
        isWorkManagerEnable = isWorkManagerEnable,
        selectedNetworkLibrary = selectedNetworkLibrary,
        selectedImageLibrary = selectedImageLibrary,
    )

    val gradleKts = getGradleKts(
        isCompose = false,
        isHiltEnable = isHiltEnable,
        isKtLintEnable = isKtLintEnable,
        isDetektEnable = isDetektEnable,
        isFirebaseEnable = isFirebaseEnable,
        isRoomEnable = isRoomEnable,
        isNavigationEnable = isNavigationEnable,
        isWorkManagerEnable = isWorkManagerEnable,
        selectedNetworkLibrary = selectedNetworkLibrary,
        selectedImageLibrary = selectedImageLibrary,
        packagePath = packagePath,
        minApi = minApi,
        javaJvmVersion = javaJvmVersion,
    )

    val projectGradleKts =
        getProjectGradleKts(
            false,
            isHiltEnable,
            isRoomEnable,
            isKtLintEnable,
            isDetektEnable,
            isFirebaseEnable,
            isNavigationEnable,
            selectedImageLibrary,
        )

    val libsVersionFile = File(moduleData.rootDir.parentFile, "gradle/libs.versions.toml")
    val buildGradleFile = File(moduleData.rootDir.parentFile, "app/build.gradle.kts")
    val projectBuildGradleFile = File(moduleData.rootDir.parentFile, "build.gradle.kts")
    val themesFile = File(moduleData.rootDir.parentFile, "app/src/main/res/values/themes.xml")
    val themesNightFile = File(moduleData.rootDir.parentFile, "app/src/main/res/values-night/themes.xml")

    if (libsVersionFile.exists() && libsVersionFile.isFile) {
        libsVersionFile.writeText(
            """$dependencies
            """.trimIndent()
        )
    }

    if (buildGradleFile.exists() && buildGradleFile.isFile) {
        buildGradleFile.writeText(
            """$gradleKts
            """.trimIndent()
        )
    }

    if (projectBuildGradleFile.exists() && projectBuildGradleFile.isFile) {
        projectBuildGradleFile.writeText(
            """$projectGradleKts
            """.trimIndent()
        )
    }

    if (themesFile.exists() && themesFile.isFile) {
        themesFile.writeText(
            """
<resources xmlns:tools="http://schemas.android.com/tools">
    <!-- Base application theme. -->
    <style name="Base.${styleName}" parent="Theme.Material3.DayNight.NoActionBar">
        <!-- Customize your light theme here. -->
        <!-- <item name="colorPrimary">@color/my_light_primary</item> -->
    </style>

    <style name="$styleName" parent="Base.${styleName}" />
</resources>
""".trimIndent()
        )
    }

    if (themesNightFile.exists() && themesNightFile.isFile) {
        themesNightFile.writeText(
            """
<resources xmlns:tools="http://schemas.android.com/tools">
    <!-- Base application theme. -->
    <style name="Base.${styleName}" parent="Theme.Material3.DayNight.NoActionBar">
        <!-- Customize your light theme here. -->
        <!-- <item name="colorPrimary">@color/my_light_primary</item> -->
    </style>

    <style name="$styleName" parent="Base.${styleName}" />
</resources>
""".trimIndent()
        )
    }
}
