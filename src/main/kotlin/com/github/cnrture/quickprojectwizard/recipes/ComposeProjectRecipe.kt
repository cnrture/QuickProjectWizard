package com.github.cnrture.quickprojectwizard.recipes

import com.android.tools.idea.npw.module.recipes.generateManifest
import com.android.tools.idea.wizard.template.ModuleTemplateData
import com.android.tools.idea.wizard.template.PackageName
import com.android.tools.idea.wizard.template.RecipeExecutor
import com.android.tools.idea.wizard.template.escapeKotlinIdentifier
import com.github.cnrture.quickprojectwizard.composearch.ComposeConfigModel
import com.github.cnrture.quickprojectwizard.composearch.ComposeFileGenerator
import com.github.cnrture.quickprojectwizard.general.ImageLibrary
import com.github.cnrture.quickprojectwizard.general.NetworkLibrary
import com.github.cnrture.quickprojectwizard.general.emptyManifestXml
import com.github.cnrture.quickprojectwizard.gradle.Versions
import com.github.cnrture.quickprojectwizard.gradle.getDependencies
import com.github.cnrture.quickprojectwizard.gradle.getGradleKts
import com.github.cnrture.quickprojectwizard.gradle.getProjectGradleKts
import com.github.cnrture.quickprojectwizard.util.FileUtils
import com.github.cnrture.quickprojectwizard.util.NotificationUtil
import com.intellij.ide.fileTemplates.FileTemplateManager
import com.intellij.ide.starters.local.GeneratorEmptyDirectory
import com.intellij.ide.starters.local.GeneratorTemplateFile
import org.jetbrains.kotlin.idea.core.util.toVirtualFile
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
    javaJvmVersion: String,
    projectName: String,
) {
    val (projectData, _, _) = moduleData
    val (_, _, _, manifestOut) = moduleData
    val packagePath = escapeKotlinIdentifier(packageName)

    generateManifest(hasApplicationBlock = true)

    mergeXml(
        emptyManifestXml(
            "@style/${moduleData.themesData.main.name}",
            isHiltEnable,
            dataDiDomainPresentationUiPackages,
            projectName
        ),
        manifestOut.resolve("AndroidManifest.xml")
    )

    val screenList = if (screens.isNotEmpty()) {
        screens.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    } else {
        listOf("Main")
    }

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

    val navigationScreens = StringBuilder().apply {
        screenList.forEachIndexed { index, it ->
            append("    @Serializable\n")
            if (index == screenList.lastIndex) append("    data object $it : Screen")
            else append("    data object $it : Screen\n")
        }
    }.toString()

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
    )

    val config = ComposeConfigModel().apply {
        this.isRoomEnable = isRoomEnable
        this.isWorkManagerEnable = isWorkManagerEnable
        this.isRetrofitEnable = selectedNetworkLibrary == NetworkLibrary.Retrofit
        this.isKtorEnable = selectedNetworkLibrary == NetworkLibrary.Ktor
        this.isHiltEnable = isHiltEnable
        this.isNavigationEnable = isNavigationEnable
        this.isCoilEnable = selectedImageLibrary == ImageLibrary.Coil
        this.isGlideEnable = selectedImageLibrary == ImageLibrary.Glide
        this.isKtLintEnable = isKtLintEnable
        this.isDetektEnable = isDetektEnable
        this.isFirebaseEnable = isFirebaseEnable
        this.isDataDomainDiUiEnable = dataDiDomainPresentationUiPackages
        this.screens = screenList
        this.packageName = packagePath
    }

    val dataModel = mutableMapOf(
        "APP_NAME" to moduleData.themesData.appName,
        "APP_NAME_LOWERCASE" to moduleData.themesData.appName.lowercase(),
        "PACKAGE_NAME" to config.packageName,
        "MODULE_NAME" to moduleData.name,
        "BUNDLE_ID" to "\${BUNDLE_ID}",
        "TEAM_ID" to "\${TEAM_ID}",
        "PROJECT_DIR" to "\${PROJECT_DIR}",
        "USER_HOME" to "\${USER_HOME}",
        "ROOT_NODE" to "\${RootNode}",
        "PROJECT" to moduleData.themesData.appName,
        "BUILD_VERSION_SDK_INT" to "\${Build.VERSION.SDK_INT}",
        "JVM_JAVA_VERSION" to "\${System.getProperty(\"java.version\")}",
        "IS_ROOM_ENABLE" to config.isRoomEnable,
        "IS_WORK_MANAGER_ENABLE" to config.isWorkManagerEnable,
        "IS_RETROFIT_ENABLE" to config.isRetrofitEnable,
        "IS_KTOR_ENABLE" to config.isKtorEnable,
        "IS_HILT_ENABLE" to config.isHiltEnable,
        "IS_NAVIGATION_ENABLE" to config.isNavigationEnable,
        "IS_COIL_ENABLE" to config.isCoilEnable,
        "IS_GLIDE_ENABLE" to config.isGlideEnable,
        "IS_KTLINT_ENABLE" to config.isKtLintEnable,
        "IS_DETEKT_ENABLE" to config.isDetektEnable,
        "IS_FIREBASE_ENABLE" to config.isFirebaseEnable,
        "IS_DATA_DOMAIN_DI_UI_ENABLE" to config.isDataDomainDiUiEnable,
        "SCREENS" to screenList,
        "CMP_AGP" to Versions.versionList["cmp-agp"].orEmpty(),
        "CMP_KOTLIN" to Versions.versionList["cmp-kotlin"].orEmpty(),
        "CMP_ACTIVITY_COMPOSE" to Versions.versionList["cmp-activity-compose"].orEmpty(),
        "CMP_UI_TOOLING" to Versions.versionList["cmp-ui-tooling"].orEmpty(),
        "CMP_MULTIPLATFORM" to Versions.versionList["cmp-multiplatform"].orEmpty(),
        "CMP_KOIN" to Versions.versionList["cmp-koin"].orEmpty(),
        "CMP_KTOR" to Versions.versionList["cmp-ktor"].orEmpty(),
        "CMP_NAVIGATION" to Versions.versionList["cmp-navigation"].orEmpty(),
        "CMP_KOTLINX_COROUTINES" to Versions.versionList["cmp-kotlinx-coroutines"].orEmpty(),
        "CMP_COIL" to Versions.versionList["cmp-coil"].orEmpty(),
        "CMP_KAMEL" to Versions.versionList["cmp-kamel"].orEmpty(),
        "CMP_KSP" to Versions.versionList["cmp-ksp"].orEmpty(),
        "CMP_ROOM" to Versions.versionList["cmp-room"].orEmpty(),
        "CMP_SQLITE" to Versions.versionList["cmp-sqlite"].orEmpty(),
        "CMP_KOTLINX_SERIALIZATION" to Versions.versionList["cmp-kotlinx-serialization"].orEmpty(),
        "SCREENS" to screenListString,
        "SCREENS_IMPORTS" to screensImportsString,
        "NAVIGATION_SCREENS" to navigationScreens,
        "START_DESTINATION" to screenList.first(),
    )

    projectData.rootDir.toVirtualFile()?.apply {
        val fileTemplateManager = FileTemplateManager.getDefaultInstance()
        ComposeFileGenerator(config, dataModel, this).generate(fileTemplateManager, config.packageName)
            .forEach { asset ->
                when (asset) {
                    is GeneratorEmptyDirectory -> FileUtils.createEmptyDirectory(this, asset.relativePath)
                    is GeneratorTemplateFile -> FileUtils.generateFileFromTemplate(dataModel, this, asset)
                    else -> throw IllegalArgumentException("Unknown asset type: $asset")
                }
            }
    }

    NotificationUtil.showInfo(
        title = "Quick Project Wizard",
        message = "Your project is ready! 🚀 If you like the plugin, please comment and rate it on the plugin page. 🙏",
    )
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
