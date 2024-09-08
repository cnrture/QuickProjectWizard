package com.github.cnrture.quickprojectwizard.recipes

import com.android.tools.idea.npw.module.recipes.generateManifest
import com.android.tools.idea.wizard.template.ModuleTemplateData
import com.android.tools.idea.wizard.template.PackageName
import com.android.tools.idea.wizard.template.RecipeExecutor
import com.android.tools.idea.wizard.template.escapeKotlinIdentifier
import com.github.cnrture.quickprojectwizard.addRootFile
import com.github.cnrture.quickprojectwizard.addSrcFile
import com.github.cnrture.quickprojectwizard.general.data.model.emptyMainEntityModel
import com.github.cnrture.quickprojectwizard.general.data.repository.emptyMainRepositoryImpl
import com.github.cnrture.quickprojectwizard.general.data.source.local.emptyMainDao
import com.github.cnrture.quickprojectwizard.general.data.source.local.emptyMainRoomDB
import com.github.cnrture.quickprojectwizard.general.data.source.remote.emptyKtorApi
import com.github.cnrture.quickprojectwizard.general.data.source.remote.emptyMainService
import com.github.cnrture.quickprojectwizard.general.detekt.emptyDetektConfig
import com.github.cnrture.quickprojectwizard.general.di.emptyLocalModule
import com.github.cnrture.quickprojectwizard.general.di.emptyMainRepositoryModule
import com.github.cnrture.quickprojectwizard.general.di.emptyNetworkModule
import com.github.cnrture.quickprojectwizard.general.domain.emptyMainRepository
import com.github.cnrture.quickprojectwizard.composearch.ui.components.emptyEmptyScreen
import com.github.cnrture.quickprojectwizard.composearch.ui.components.emptyLoadingBar
import com.github.cnrture.quickprojectwizard.composearch.ui.emptyActivity
import com.github.cnrture.quickprojectwizard.composearch.ui.main.emptyMainContract
import com.github.cnrture.quickprojectwizard.composearch.ui.main.emptyMainScreen
import com.github.cnrture.quickprojectwizard.composearch.ui.main.emptyMainScreenPreviewProvider
import com.github.cnrture.quickprojectwizard.composearch.ui.main.emptyMainViewModel
import com.github.cnrture.quickprojectwizard.composearch.ui.navigation.emptyNavigationGraph
import com.github.cnrture.quickprojectwizard.composearch.ui.theme.emptyColor
import com.github.cnrture.quickprojectwizard.composearch.ui.theme.emptyTheme
import com.github.cnrture.quickprojectwizard.composearch.ui.theme.emptyType
import com.github.cnrture.quickprojectwizard.composearch.common.emptyCollectExtension
import com.github.cnrture.quickprojectwizard.general.*
import com.github.cnrture.quickprojectwizard.gradle.getDependencies
import com.github.cnrture.quickprojectwizard.gradle.getGradleKts
import com.github.cnrture.quickprojectwizard.gradle.getProjectGradleKts
import java.io.File

fun RecipeExecutor.composeProjectRecipe(
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

    addSrcFile(emptyColor(packagePath), moduleData, "ui/theme/Color.kt")
    addSrcFile(emptyTheme(packagePath, projectName), moduleData, "ui/theme/Theme.kt")
    addSrcFile(emptyType(packagePath), moduleData, "ui/theme/Type.kt")

    addActivity(
        moduleData,
        packagePath,
        isHiltEnable,
        screenList,
        projectName,
        isNavigationEnable,
        dataDiDomainPresentationUiPackages,
    )

    val (_, _, _, manifestOut) = moduleData

    mergeXml(
        emptyManifestXml("@style/${moduleData.themesData.main.name}", isHiltEnable, dataDiDomainPresentationUiPackages),
        manifestOut.resolve("AndroidManifest.xml")
    )

    if (isDetektEnable) {
        addRootFile(emptyDetektConfig(), moduleData, "detekt/detektConfig.yml")
    }

    if (dataDiDomainPresentationUiPackages) {
        if (isHiltEnable) {
            addSrcFile(emptyMainApplication(packagePath, projectName), moduleData, "MainApplication.kt")
        }

        addSrcFile(emptyConstants(packagePath), moduleData, "common/Constants.kt")

        addExtensions(moduleData, packagePath)

        addNavigation(moduleData, packagePath, isNavigationEnable, screenList, isHiltEnable)

        addScreens(moduleData, packagePath, isHiltEnable, screenList)

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
        minApi,
        javaJvmVersion,
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
            isHiltEnable,
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
    isHiltEnable: Boolean,
) {
    when {
        isNavigationEnable -> {
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
    }
}

private fun RecipeExecutor.addScreens(
    moduleData: ModuleTemplateData,
    packagePath: String,
    isHiltEnable: Boolean,
    screenList: List<String>,
) {
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
) {
    val dependencies = getDependencies(
        isCompose = true,
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
        isCompose = true,
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
            true,
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
