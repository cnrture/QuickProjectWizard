package com.github.cnrture.quickprojectwizard.projectwizard.cmparch

import com.github.cnrture.quickprojectwizard.data.CMPConfigModel
import com.intellij.ide.fileTemplates.FileTemplateManager
import com.intellij.ide.starters.local.GeneratorAsset
import com.intellij.ide.starters.local.GeneratorTemplateFile

class DesktopFileGenerator(params: CMPConfigModel) : FileGenerator(params) {
    override fun generate(ftManager: FileTemplateManager, packageName: String): List<GeneratorAsset> {
        return listOf(
            GeneratorTemplateFile(
                "composeApp/src/desktopMain/kotlin/$packageName/main.kt",
                ftManager.getCodeTemplate(Template.DESKTOP_MAIN)
            )
        )
    }
}