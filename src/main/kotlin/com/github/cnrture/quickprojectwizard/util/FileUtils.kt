package com.github.cnrture.quickprojectwizard.util

import com.intellij.ide.starters.local.GeneratorTemplateFile
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import freemarker.template.Configuration
import java.io.IOException
import java.io.StringWriter

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