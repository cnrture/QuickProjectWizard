package com.github.cnrture.quickprojectwizard.projectwizard.cmparch

import com.github.cnrture.quickprojectwizard.data.CMPConfigModel
import com.intellij.ide.fileTemplates.FileTemplateManager
import com.intellij.ide.starters.local.GeneratorAsset

abstract class FileGenerator(protected val params: CMPConfigModel) {
    abstract fun generate(ftManager: FileTemplateManager, packageName: String): List<GeneratorAsset>
}