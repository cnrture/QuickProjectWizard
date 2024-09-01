package com.github.cnrture.quickprojectwizard

import com.android.tools.idea.npw.module.recipes.generateManifest
import com.android.tools.idea.wizard.template.ModuleTemplateData
import com.android.tools.idea.wizard.template.PackageName
import com.android.tools.idea.wizard.template.RecipeExecutor
import com.android.tools.idea.wizard.template.escapeKotlinIdentifier
import com.github.cnrture.quickprojectwizard.arch.app.emptyMainApplication
import com.github.cnrture.quickprojectwizard.arch.app.emptyManifestXml
import com.github.cnrture.quickprojectwizard.arch.common.emptyCollectExtension
import com.github.cnrture.quickprojectwizard.arch.common.emptyConstants
import com.github.cnrture.quickprojectwizard.arch.data.model.emptyMainEntityModel
import com.github.cnrture.quickprojectwizard.arch.data.repository.emptyMainRepositoryImpl
import com.github.cnrture.quickprojectwizard.arch.data.source.local.emptyMainDao
import com.github.cnrture.quickprojectwizard.arch.data.source.local.emptyMainRoomDB
import com.github.cnrture.quickprojectwizard.arch.data.source.remote.emptyKtorApi
import com.github.cnrture.quickprojectwizard.arch.data.source.remote.emptyMainService
import com.github.cnrture.quickprojectwizard.arch.detekt.emptyDetektConfig
import com.github.cnrture.quickprojectwizard.arch.di.emptyLocalModule
import com.github.cnrture.quickprojectwizard.arch.di.emptyMainRepositoryModule
import com.github.cnrture.quickprojectwizard.arch.di.emptyNetworkModule
import com.github.cnrture.quickprojectwizard.arch.domain.emptyMainRepository
import com.github.cnrture.quickprojectwizard.arch.res.emptyMainNavGraphXML
import com.github.cnrture.quickprojectwizard.arch.ui.compose.components.emptyEmptyScreen
import com.github.cnrture.quickprojectwizard.arch.ui.compose.components.emptyLoadingBar
import com.github.cnrture.quickprojectwizard.arch.ui.compose.emptyActivity
import com.github.cnrture.quickprojectwizard.arch.ui.compose.main.emptyMainContract
import com.github.cnrture.quickprojectwizard.arch.ui.compose.main.emptyMainScreen
import com.github.cnrture.quickprojectwizard.arch.ui.compose.main.emptyMainScreenPreviewProvider
import com.github.cnrture.quickprojectwizard.arch.ui.compose.main.emptyMainViewModel
import com.github.cnrture.quickprojectwizard.arch.ui.compose.navigation.emptyNavigationGraph
import com.github.cnrture.quickprojectwizard.arch.ui.compose.theme.emptyColor
import com.github.cnrture.quickprojectwizard.arch.ui.compose.theme.emptyTheme
import com.github.cnrture.quickprojectwizard.arch.ui.compose.theme.emptyType
import com.github.cnrture.quickprojectwizard.arch.ui.xml.*
import com.github.cnrture.quickprojectwizard.gradle.getDependencies
import com.github.cnrture.quickprojectwizard.gradle.getGradleKts
import com.github.cnrture.quickprojectwizard.gradle.getProjectGradleKts
import java.io.File

fun RecipeExecutor.projectRecipe(
    moduleData: ModuleTemplateData,
    packageName: PackageName,
    isCompose: Boolean,
    isRoomEnable: Boolean,
    isWorkManagerEnable: Boolean,
    selectedNetworkLibrary: NetworkLibrary,
    isHiltEnable: Boolean,
    isNavigationEnable: Boolean,
    selectedImageLibrary: ImageLibrary,
    isKtLintEnable: Boolean,
    isDetektEnable: Boolean,
    isFirebaseEnable: Boolean,
    screens: String,
    minApi: Int,
    javaJvmVersion: String,
    projectName: String,
) {
    val packagePath = escapeKotlinIdentifier(packageName)

    generateManifest(hasApplicationBlock = true)

    val screenList = if (screens.isNotEmpty()) {
        screens.split(",").map { it.trim() }
    } else {
        listOf("Main")
    }

    addSrcFile(emptyConstants(packagePath), moduleData, "common/Constants.kt")

    addExtensions(moduleData, packagePath, isCompose)

    if (isCompose) {
        addSrcFile(emptyColor(packagePath), moduleData, "ui/theme/Color.kt")
        addSrcFile(emptyTheme(packagePath, projectName), moduleData, "ui/theme/Theme.kt")
        addSrcFile(emptyType(packagePath), moduleData, "ui/theme/Type.kt")
    }

    addActivity(moduleData, packagePath, isCompose, isHiltEnable, screenList, projectName)

    if (isHiltEnable) {
        addSrcFile(emptyMainApplication(packagePath), moduleData, "MainApplication.kt")
    }

    addNavigation(moduleData, packagePath, isCompose, isNavigationEnable, screenList, isHiltEnable)

    addScreens(moduleData, packagePath, isCompose, isHiltEnable, screenList)

    val (_, _, _, manifestOut) = moduleData

    mergeXml(
        emptyManifestXml("@style/${moduleData.themesData.main.name}", isHiltEnable),
        manifestOut.resolve("AndroidManifest.xml")
    )

    if (isCompose) {
        addSrcFile(emptyEmptyScreen(packagePath), moduleData, "ui/components/EmptyScreen.kt")
        addSrcFile(emptyLoadingBar(packagePath), moduleData, "ui/components/LoadingBar.kt")
    }

    addSrcFile(emptyMainRepository(packagePath), moduleData, "domain/repository/MainRepository.kt")

    addSrcFile(
        emptyMainRepositoryImpl(packagePath, isRoomEnable, selectedNetworkLibrary != NetworkLibrary.None, isHiltEnable),
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

    if (isDetektEnable) {
        addRootFile(emptyDetektConfig(), moduleData, "detekt/detektConfig.yml")
    }

    addDependenciesAndGradle(
        moduleData,
        isCompose,
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
        minApi,
        javaJvmVersion,
    )
}

private fun RecipeExecutor.addExtensions(
    moduleData: ModuleTemplateData,
    packagePath: String,
    isCompose: Boolean,
) {
    addSrcFile(
        emptyCollectExtension(packagePath, isCompose),
        moduleData,
        "common/CollectExtension.kt"
    )

    addSrcFile(
        emptyCollectExtension(packagePath, isCompose),
        moduleData,
        "common/CollectExtension.kt"
    )
}

private fun RecipeExecutor.addActivity(
    moduleData: ModuleTemplateData,
    packagePath: String,
    isCompose: Boolean,
    isHiltEnable: Boolean,
    screenList: List<String>,
    projectName: String,
) {
    if (isCompose) {
        addSrcFile(
            emptyActivity(packagePath, projectName, screenList[0], isHiltEnable),
            moduleData,
            "ui/MainActivity.kt"
        )
    } else {
        addSrcFile(emptyActivityXML(packagePath, isHiltEnable), moduleData, "ui/MainActivity.kt")
        addRootFile(
            emptyActivityLayout(),
            moduleData,
            "app/src/main/res/layout/activity_main.xml"
        )
    }
}

private fun RecipeExecutor.addNavigation(
    moduleData: ModuleTemplateData,
    packagePath: String,
    isCompose: Boolean,
    isNavigationEnable: Boolean,
    screenList: List<String>,
    isHiltEnable: Boolean,
) {
    when {
        isCompose && isNavigationEnable -> {
            val screenListString = StringBuilder().apply {
                screenList.forEach {
                    append("        composable(\"$it\") {\n")
                    if (isHiltEnable) append("            val viewModel: ${it}ViewModel = hiltViewModel()\n")
                    else append("            val viewModel = viewModel<${it}ViewModel>(it)\n")
                    append("            val uiState by viewModel.uiState.collectAsStateWithLifecycle()\n")
                    append("            val uiEffect = viewModel.uiEffect\n")
                    append("            ${it}Screen(\n")
                    append("                uiState = uiState,\n")
                    append("                uiEffect = uiEffect,\n")
                    append("                onAction = viewModel::onAction\n")
                    append("            )\n")
                    append("        }\n")
                }
            }.toString()
            val screensImportsString = StringBuilder().apply {
                screenList.forEach {
                    append("import $packagePath.ui.${it.lowercase()}.${it}Screen\n")
                    append("import $packagePath.ui.${it.lowercase()}.${it}ViewModel\n")
                }
            }.toString()
            addSrcFile(
                emptyNavigationGraph(packagePath, screenListString, screensImportsString, isHiltEnable),
                moduleData,
                "ui/navigation/NavigationGraph.kt"
            )
        }

        !isCompose && isNavigationEnable -> {
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
    }
}

private fun RecipeExecutor.addScreens(
    moduleData: ModuleTemplateData,
    packagePath: String,
    isCompose: Boolean,
    isHiltEnable: Boolean,
    screenList: List<String>,
) {
    if (isCompose) {
        screenList.forEach {
            addSrcFile(emptyMainScreen(packagePath, it), moduleData, "ui/${it.lowercase()}/${it}Screen.kt")
            addSrcFile(
                emptyMainViewModel(packagePath, it, isHiltEnable),
                moduleData,
                "ui/${it.lowercase()}/${it}ViewModel.kt"
            )
            addSrcFile(emptyMainContract(packagePath, it), moduleData, "ui/${it.lowercase()}/${it}Contract.kt")
            addSrcFile(
                emptyMainScreenPreviewProvider(packagePath, it),
                moduleData,
                "ui/${it.lowercase()}/${it}ScreenPreviewProvider.kt"
            )
        }
    } else {
        screenList.forEach {
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
            addRootFile(emptyFragmentLayout(it), moduleData, "app/src/main/res/layout/fragment_${it.lowercase()}.xml")
        }
    }
}

private fun addDependenciesAndGradle(
    moduleData: ModuleTemplateData,
    isCompose: Boolean,
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
) {
    val dependencies = getDependencies(
        isCompose = isCompose,
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
        isCompose = isCompose,
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
            isCompose,
            isHiltEnable,
            isRoomEnable,
            isKtLintEnable,
            isDetektEnable,
            isFirebaseEnable,
            selectedImageLibrary,
        )

    val libsVersionFile = File(moduleData.rootDir.parentFile, "gradle/libs.versions.toml")
    val buildGradleFile = File(moduleData.rootDir.parentFile, "app/build.gradle.kts")
    val projectBuildGradleFile = File(moduleData.rootDir.parentFile, "build.gradle.kts")

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
}
