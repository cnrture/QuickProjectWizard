package com.github.cnrture.quickprojectwizard.cmparch

import com.intellij.ide.fileTemplates.FileTemplateManager
import com.intellij.ide.starters.local.GeneratorAsset
import com.intellij.ide.starters.local.GeneratorTemplateFile

class AndroidFileGenerator(params: CMPConfigModel) : FileGenerator(params) {
    override fun generate(ftManager: FileTemplateManager, packageName: String): List<GeneratorAsset> {
        val list = mutableListOf<GeneratorAsset>()
        return list.apply {
            if (params.isDataDomainDiUiEnable && params.isKoinEnable) {
                add(
                    GeneratorTemplateFile(
                        "composeApp/src/androidMain/kotlin/$packageName/MainApp.kt",
                        ftManager.getCodeTemplate(Template.APPLICATION)
                    )
                )
            }
            addAll(
                listOf(
                    GeneratorTemplateFile(
                        "composeApp/src/androidMain/kotlin/$packageName/MainActivity.kt",
                        ftManager.getCodeTemplate(Template.ANDROID_MAIN_ACTIVITY)
                    ),
                    GeneratorTemplateFile(
                        "composeApp/src/androidMain/AndroidManifest.xml",
                        ftManager.getCodeTemplate(Template.ANDROID_MANIFEST)
                    ),
                    GeneratorTemplateFile(
                        "composeApp/src/androidMain/res/values/strings.xml",
                        ftManager.getCodeTemplate(Template.ANDROID_VALUES_XML)
                    ),
                )
            )
        }
    }
}