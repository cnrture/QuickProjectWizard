package com.github.cnrture.quickprojectwizard.cmparch

import com.intellij.ide.fileTemplates.FileTemplateManager
import com.intellij.ide.starters.local.GeneratorAsset
import com.intellij.ide.starters.local.GeneratorEmptyDirectory
import com.intellij.ide.starters.local.GeneratorTemplateFile

class IOSFileGenerator(params: CMPConfigModel) : FileGenerator(params) {
    override fun generate(ftManager: FileTemplateManager, packageName: String): List<GeneratorAsset> {
        return listOf(
            GeneratorTemplateFile(
                "composeApp/src/iosMain/kotlin/$packageName/MainViewController.kt",
                ftManager.getCodeTemplate(Template.COMPOSE_IOS_MAIN)
            ),
            GeneratorEmptyDirectory("iosApp/iosApp.xcodeproj/project.xcworkspace/xcshareddata/swiftpm/configuration"),
            GeneratorTemplateFile(
                "iosApp/iosApp/ContentView.swift",
                ftManager.getCodeTemplate(Template.IOS_CONTENT_VIEW_SWIFT)
            ),
            GeneratorTemplateFile(
                "iosApp/iosApp/Assets.xcassets/AppIcon.appiconset/Contents.json",
                ftManager.getCodeTemplate(Template.IOS_ICONS_CONTENTS_JSON)
            ),
            GeneratorTemplateFile(
                "iosApp/iosApp/Assets.xcassets/AccentColor.colorset/Contents.json",
                ftManager.getCodeTemplate(Template.IOS_COLORS_CONTENTS_JSON)
            ),
            GeneratorTemplateFile(
                "iosApp/iosApp/Assets.xcassets/Contents.json",
                ftManager.getCodeTemplate(Template.IOS_ASSETS_CONTENTS_JSON)
            ),
            GeneratorTemplateFile(
                "iosApp/iosApp/Preview Content/Preview Assets.xcassets/Contents.json",
                ftManager.getCodeTemplate(Template.IOS_PREVIEW_CONTENTS_JSON)
            ),
            GeneratorTemplateFile(
                "iosApp/iosApp/iOSApp.swift",
                ftManager.getCodeTemplate(Template.IOS_IOS_APP)
            ),
            GeneratorTemplateFile(
                "iosApp/Configuration/Config.xcconfig",
                ftManager.getCodeTemplate(Template.IOS_APP_CONFIGURATION)
            ),
            GeneratorTemplateFile(
                "iosApp/iosApp.xcodeproj/project.pbxproj",
                ftManager.getCodeTemplate(Template.IOS_PROJECT)
            ),
            GeneratorTemplateFile(
                "iosApp/iosApp/Info.plist",
                ftManager.getCodeTemplate(Template.IOS_INFO_PLIST)
            )
        )
    }
}