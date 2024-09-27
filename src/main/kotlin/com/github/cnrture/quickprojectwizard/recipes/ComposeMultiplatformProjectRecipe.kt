package com.github.cnrture.quickprojectwizard.recipes

import com.android.tools.idea.wizard.template.ModuleTemplateData
import com.android.tools.idea.wizard.template.escapeKotlinIdentifier
import com.github.cnrture.quickprojectwizard.cmparch.*
import com.github.cnrture.quickprojectwizard.gradle.Versions
import com.github.cnrture.quickprojectwizard.util.NotificationUtil
import com.intellij.ide.fileTemplates.FileTemplateManager
import com.intellij.ide.starters.local.GeneratorAsset
import com.intellij.ide.starters.local.GeneratorEmptyDirectory
import com.intellij.ide.starters.local.GeneratorTemplateFile
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import freemarker.template.Configuration
import org.jetbrains.kotlin.idea.core.util.toVirtualFile
import java.io.IOException
import java.io.StringWriter

fun composeMultiplatformProjectRecipe(
    moduleData: ModuleTemplateData,
    packageName: String,
    isAndroidEnable: Boolean,
    isIosEnable: Boolean,
    isDesktopEnable: Boolean,
    isKtorServiceEnable: Boolean,
    isRoomEnable: Boolean,
    selectedImageLibrary: CMPImageLibrary,
    isKoinEnable: Boolean,
    isNavigationEnable: Boolean,
    isDataDomainDiUiEnable: Boolean,
    screens: String,
) {
    val (projectData, _, _) = moduleData
    val packagePath = escapeKotlinIdentifier(packageName)

    val screenList = if (screens.isNotEmpty()) {
        screens.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    } else {
        listOf("Main")
    }

    val screenListString = StringBuilder().apply {
        screenList.forEach {
            append("        composable(\"$it\") {\n")
            if (isKoinEnable) append("            val viewModel: ${it}ViewModel = koinInject()\n")
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

    val config = CMPConfigModel().apply {
        this.isAndroidEnable = isAndroidEnable
        this.isIOSEnable = isIosEnable
        this.isDesktopEnable = isDesktopEnable
        this.isKtorEnable = isKtorServiceEnable
        this.isRoomEnable = isRoomEnable
        this.isCoilEnable = selectedImageLibrary == CMPImageLibrary.Coil
        this.isKamelEnable = selectedImageLibrary == CMPImageLibrary.Kamel
        this.isKoinEnable = isKoinEnable
        this.isNavigationEnable = isNavigationEnable
        this.isDataDomainDiUiEnable = isDataDomainDiUiEnable
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
        "IS_ANDROID_ENABLE" to config.isAndroidEnable,
        "IS_IOS_ENABLE" to config.isIOSEnable,
        "IS_DESKTOP_ENABLE" to config.isDesktopEnable,
        "IS_KTOR_ENABLE" to config.isKtorEnable,
        "IS_ROOM_ENABLE" to config.isRoomEnable,
        "IS_COIL_ENABLE" to config.isCoilEnable,
        "IS_KAMEL_ENABLE" to config.isKamelEnable,
        "IS_KOIN_ENABLE" to config.isKoinEnable,
        "IS_NAVIGATION_ENABLE" to config.isNavigationEnable,
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
        "SCREENS" to screenListString,
        "SCREENS_IMPORTS" to screensImportsString,
    )

    projectData.rootDir.toVirtualFile()?.apply {
        val fileTemplateManager = FileTemplateManager.getDefaultInstance()
        val assets = mutableListOf<GeneratorAsset>()
        val platforms: List<FileGenerator> = listOfNotNull(
            CommonFileGenerator(config, dataModel, this),
            if (config.isAndroidEnable) AndroidFileGenerator(config) else null,
            if (config.isIOSEnable) IOSFileGenerator(config) else null,
            if (config.isDesktopEnable) DesktopFileGenerator(config) else null,
        )
        assets.addAll(platforms.flatMap { it.generate(fileTemplateManager, config.packageName) })
        assets.forEach { asset ->
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

object FileUtils {
    fun createEmptyDirectory(parent: VirtualFile, path: String) {
        VfsUtil.createDirectoryIfMissing(parent, path)
    }

    fun generateFileFromTemplate(
        dataModel: Map<String, Any>,
        outputDir: VirtualFile,
        asset: GeneratorTemplateFile,
    ) {
        Configuration(Configuration.VERSION_2_3_33).apply {
            setClassLoaderForTemplateLoading(this::class.java.classLoader, "fileTemplates/code")
            val outputFilePathParts = asset.relativePath.split('/')
            val dirPath = outputFilePathParts.dropLast(1).joinToString("/")
            val targetDir = VfsUtil.createDirectoryIfMissing(outputDir, dirPath)
                ?: throw IOException("Failed to create directory: $dirPath")
            val outputFile = targetDir.createChildData(this, outputFilePathParts.last())
            StringWriter().use { writer ->
                val template = "${asset.template.name}.${asset.template.extension}"
                getTemplate("${template}.ft").process(dataModel, writer)
                VfsUtil.saveText(outputFile, writer.toString())
            }
        }
    }
}