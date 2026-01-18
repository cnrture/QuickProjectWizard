package com.github.cnrture.quickprojectwizard.projectwizard.recipes

import com.android.tools.idea.npw.module.recipes.generateManifest
import com.android.tools.idea.wizard.template.ModuleTemplateData
import com.android.tools.idea.wizard.template.PackageName
import com.android.tools.idea.wizard.template.RecipeExecutor
import com.android.tools.idea.wizard.template.escapeKotlinIdentifier
import com.github.cnrture.quickprojectwizard.common.Utils
import com.github.cnrture.quickprojectwizard.common.addRootFile
import com.github.cnrture.quickprojectwizard.common.addSrcFile
import com.github.cnrture.quickprojectwizard.data.DILibrary
import com.github.cnrture.quickprojectwizard.data.ImageLibrary
import com.github.cnrture.quickprojectwizard.data.NetworkLibrary
import com.github.cnrture.quickprojectwizard.projectwizard.composearch.common.emptyCollectExtension
import com.github.cnrture.quickprojectwizard.projectwizard.composearch.delegation.emptyMVI
import com.github.cnrture.quickprojectwizard.projectwizard.composearch.delegation.emptyMVIDelegate
import com.github.cnrture.quickprojectwizard.projectwizard.composearch.ui.components.emptyEmptyScreen
import com.github.cnrture.quickprojectwizard.projectwizard.composearch.ui.components.emptyLoadingBar
import com.github.cnrture.quickprojectwizard.projectwizard.composearch.ui.emptyActivity
import com.github.cnrture.quickprojectwizard.projectwizard.composearch.ui.main.emptyMainContract
import com.github.cnrture.quickprojectwizard.projectwizard.composearch.ui.main.emptyMainScreen
import com.github.cnrture.quickprojectwizard.projectwizard.composearch.ui.main.emptyMainScreenPreviewProvider
import com.github.cnrture.quickprojectwizard.projectwizard.composearch.ui.main.emptyMainViewModel
import com.github.cnrture.quickprojectwizard.projectwizard.composearch.ui.navigation.emptyNavigationGraph
import com.github.cnrture.quickprojectwizard.projectwizard.composearch.ui.navigation.emptyNavigationScreen
import com.github.cnrture.quickprojectwizard.projectwizard.composearch.ui.theme.emptyColor
import com.github.cnrture.quickprojectwizard.projectwizard.composearch.ui.theme.emptyTheme
import com.github.cnrture.quickprojectwizard.projectwizard.composearch.ui.theme.emptyType
import com.github.cnrture.quickprojectwizard.projectwizard.general.data.model.emptyMainEntityModel
import com.github.cnrture.quickprojectwizard.projectwizard.general.data.repository.emptyMainRepositoryImpl
import com.github.cnrture.quickprojectwizard.projectwizard.general.data.source.local.emptyMainDao
import com.github.cnrture.quickprojectwizard.projectwizard.general.data.source.local.emptyMainRoomDB
import com.github.cnrture.quickprojectwizard.projectwizard.general.data.source.remote.emptyKtorApi
import com.github.cnrture.quickprojectwizard.projectwizard.general.data.source.remote.emptyMainService
import com.github.cnrture.quickprojectwizard.projectwizard.general.detekt.emptyDetektConfig
import com.github.cnrture.quickprojectwizard.projectwizard.general.di.*
import com.github.cnrture.quickprojectwizard.projectwizard.general.domain.emptyMainRepository
import com.github.cnrture.quickprojectwizard.projectwizard.general.emptyConstants
import com.github.cnrture.quickprojectwizard.projectwizard.general.emptyMainApplication
import com.github.cnrture.quickprojectwizard.projectwizard.general.emptyManifestXml
import com.github.cnrture.quickprojectwizard.projectwizard.gradle.getDependencies
import com.github.cnrture.quickprojectwizard.projectwizard.gradle.getGradleKts
import com.github.cnrture.quickprojectwizard.projectwizard.gradle.getProjectGradleKts
import com.github.cnrture.quickprojectwizard.service.AnalyticsService
import com.github.cnrture.quickprojectwizard.service.SettingsService
import java.io.File

fun RecipeExecutor.composeProjectRecipe(
    moduleData: ModuleTemplateData,
    packageName: PackageName,
    isRoomEnable: Boolean,
    isWorkManagerEnable: Boolean,
    selectedNetworkLibrary: NetworkLibrary,
    selectedDILibrary: DILibrary,
    isNavigationEnable: Boolean,
    selectedImageLibrary: ImageLibrary,
    isKtLintEnable: Boolean,
    isDetektEnable: Boolean,
    isFirebaseEnable: Boolean,
    dataDiDomainPresentationUiPackages: Boolean,
    screens: String,
    javaJvmVersion: String,
    projectName: String,
) {
    val packagePath = escapeKotlinIdentifier(packageName)
    val analyticsService = AnalyticsService.getInstance()
    val settings = SettingsService.getInstance()
    val isHiltEnable = selectedDILibrary == DILibrary.Hilt
    val isKoinEnable = selectedDILibrary == DILibrary.Koin
    settings.loadState(
        settings.state.copy(
            isCompose = true,
            defaultPackageName = packagePath,
        )
    )

    generateManifest(hasApplicationBlock = true)

    val screenList = if (screens.isNotEmpty()) {
        screens.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    } else {
        listOf("Main")
    }

    addSrcFile(emptyColor(packagePath), moduleData, "ui/theme/Color.kt")
    addSrcFile(emptyTheme(packagePath, projectName), moduleData, "ui/theme/Theme.kt")
    addSrcFile(emptyType(packagePath), moduleData, "ui/theme/Type.kt")

    addActivity(
        moduleData,
        packagePath,
        selectedDILibrary,
        screenList,
        projectName,
        isNavigationEnable,
        dataDiDomainPresentationUiPackages,
    )

    val (_, _, _, manifestOut) = moduleData

    mergeXml(
        emptyManifestXml(
            "@style/${moduleData.themesData.main.name}",
            selectedDILibrary,
            dataDiDomainPresentationUiPackages,
        ),
        manifestOut.resolve("AndroidManifest.xml")
    )

    if (isDetektEnable) addRootFile(emptyDetektConfig(), moduleData, "detekt/detektConfig.yml")

    if (dataDiDomainPresentationUiPackages) {
        if (isHiltEnable) {
            addSrcFile(emptyMainApplication(packagePath), moduleData, "MainApp.kt")
        }
        if (isKoinEnable) {
            addSrcFile(emptyMainApplication(packagePath, isKoin = true), moduleData, "MainApp.kt")
        }

        addSrcFile(emptyConstants(packagePath), moduleData, "common/Constants.kt")

        addExtensions(moduleData, packagePath)

        addNavigation(moduleData, packagePath, isNavigationEnable, screenList, selectedDILibrary)

        addSrcFile(emptyMVI(packagePath), moduleData, "delegation/MVI.kt")
        addSrcFile(emptyMVIDelegate(packagePath), moduleData, "delegation/MVIDelegate.kt")

        addScreens(moduleData, packagePath, selectedDILibrary, screenList)

        addSrcFile(emptyEmptyScreen(packagePath), moduleData, "ui/components/EmptyScreen.kt")
        addSrcFile(emptyLoadingBar(packagePath), moduleData, "ui/components/LoadingBar.kt")

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
        if (isKoinEnable) {
            val hasNetwork = selectedNetworkLibrary != NetworkLibrary.None
            addSrcFile(
                emptyMainRepositoryModule(
                    packagePath,
                    isKoin = true,
                    hasNetwork = hasNetwork,
                    hasLocal = isRoomEnable
                ),
                moduleData,
                "di/RepositoryModule.kt"
            )
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
            if (isKoinEnable) {
                addSrcFile(
                    emptyNetworkModule(packagePath, selectedNetworkLibrary, isKoin = true),
                    moduleData,
                    "di/NetworkModule.kt"
                )
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
            if (isKoinEnable) {
                addSrcFile(emptyLocalModule(packagePath, isKoin = true), moduleData, "di/LocalModule.kt")
            }
        }

        // Generate appModule for Koin
        if (isKoinEnable) {
            val hasNetworkModule = selectedNetworkLibrary != NetworkLibrary.None
            val hasRepositoryModule = true
            addSrcFile(emptyViewModelModule(packagePath, screenList), moduleData, "di/ViewModelModule.kt")
            addSrcFile(
                emptyAppModule(packagePath, hasNetworkModule, isRoomEnable, hasRepositoryModule, hasViewModel = true),
                moduleData,
                "di/AppModule.kt"
            )
        }
    }

    addDependenciesAndGradle(
        moduleData,
        selectedDILibrary,
        isKtLintEnable,
        isDetektEnable,
        isFirebaseEnable,
        isRoomEnable,
        isNavigationEnable,
        isWorkManagerEnable,
        selectedNetworkLibrary,
        selectedImageLibrary,
        packagePath,
        moduleData.getMinApiLevel(),
        javaJvmVersion,
    )
    analyticsService.track("compose_project_created")
    Utils.showInfo(
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
    selectedDILibrary: DILibrary,
    screenList: List<String>,
    projectName: String,
    isNavigationEnable: Boolean,
    dataDiDomainPresentationUiPackages: Boolean,
) {
    addSrcFile(
        emptyActivity(
            packagePath,
            projectName,
            screenList[0],
            selectedDILibrary,
            isNavigationEnable,
            dataDiDomainPresentationUiPackages,
        ),
        moduleData,
        "ui/MainActivity.kt"
    )
}

private fun RecipeExecutor.addNavigation(
    moduleData: ModuleTemplateData,
    packagePath: String,
    isNavigationEnable: Boolean,
    screenList: List<String>,
    selectedDILibrary: DILibrary,
) {
    when {
        isNavigationEnable -> {
            val isHiltEnable = selectedDILibrary == DILibrary.Hilt
            val isKoinEnable = selectedDILibrary == DILibrary.Koin
            val screenListString = StringBuilder().apply {
                screenList.forEachIndexed { index, screen ->
                    append("        composable<$screen> {\n")
                    when {
                        isHiltEnable -> append("            val viewModel: ${screen}ViewModel = hiltViewModel()\n")
                        isKoinEnable -> append("            val viewModel: ${screen}ViewModel = koinViewModel()\n")
                        else -> append("            val viewModel = viewModel<${screen}ViewModel>(it)\n")
                    }
                    append("            val uiState by viewModel.uiState.collectAsStateWithLifecycle()\n")
                    append("            val uiEffect = viewModel.uiEffect\n")
                    append("            ${screen}Screen(\n")
                    append("                uiState = uiState,\n")
                    append("                uiEffect = uiEffect,\n")
                    append("                onAction = viewModel::onAction\n")
                    append("            )\n")
                    if (index != screenList.lastIndex) append("        }\n") else append("        }")
                }
            }.toString()
            val screensImportsString = StringBuilder().apply {
                screenList.forEach {
                    append("import $packagePath.navigation.Screen.$it\n")
                }
                screenList.forEach {
                    append("import $packagePath.ui.${it.lowercase()}.${it}Screen\n")
                    append("import $packagePath.ui.${it.lowercase()}.${it}ViewModel\n")
                }
            }.toString()
            val navScreenListString = StringBuilder().apply {
                screenList.forEachIndexed { index, screen ->
                    append("    @Serializable")
                    if (index == screenList.lastIndex) append("    data object $screen : Screen")
                    else append("    data object $screen : Screen\n\n")
                }
            }.toString()
            addSrcFile(
                emptyNavigationGraph(packagePath, screenListString, screensImportsString, selectedDILibrary),
                moduleData,
                "navigation/NavigationGraph.kt"
            )
            addSrcFile(
                emptyNavigationScreen(packagePath, navScreenListString),
                moduleData,
                "navigation/Screen.kt"
            )
        }
    }
}

private fun RecipeExecutor.addScreens(
    moduleData: ModuleTemplateData,
    packagePath: String,
    selectedDILibrary: DILibrary,
    screenList: List<String>,
) {
    screenList.forEach {
        addSrcFile(emptyMainScreen(packagePath, it), moduleData, "ui/${it.lowercase()}/${it}Screen.kt")
        addSrcFile(
            emptyMainViewModel(packagePath, it, selectedDILibrary),
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
}

private fun addDependenciesAndGradle(
    moduleData: ModuleTemplateData,
    selectedDILibrary: DILibrary,
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
        isCompose = true,
        selectedDILibrary = selectedDILibrary,
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
        isCompose = true,
        selectedDILibrary = selectedDILibrary,
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
            true,
            selectedDILibrary,
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
