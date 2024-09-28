package com.github.cnrture.quickprojectwizard.cmparch

import com.intellij.ide.fileTemplates.FileTemplateManager
import com.intellij.ide.starters.local.GeneratorAsset

abstract class FileGenerator<T>(protected val params: T) {
    abstract fun generate(ftManager: FileTemplateManager, packageName: String): List<GeneratorAsset>
}