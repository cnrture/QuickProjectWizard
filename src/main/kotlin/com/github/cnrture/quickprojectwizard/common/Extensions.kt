package com.github.cnrture.quickprojectwizard.common

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import java.io.File
import com.github.cnrture.quickprojectwizard.common.file.File as ProjectFile

fun Project.getCurrentlySelectedFile(selectedSrc: String): File {
    return File(rootDirectoryStringDropLast() + File.separator + selectedSrc)
}

fun Project.rootDirectoryStringDropLast(): String {
    return basePath!!.split(File.separator).dropLast(1).joinToString(File.separator)
}

fun Project.rootDirectoryString(): String = basePath!!

fun List<File>.refreshFileSystem() {
    VfsUtil.markDirtyAndRefresh(false, true, true, *this.toTypedArray())
}

fun File.toProjectFile(): ProjectFile = object : ProjectFile {
    override val name: String
        get() = this@toProjectFile.name

    override val absolutePath: String
        get() = this@toProjectFile.absolutePath

    override val isDirectory: Boolean
        get() = this@toProjectFile.isDirectory

    override val children: List<ProjectFile>
        get() = this@toProjectFile
            .listFiles { _, name -> !name.startsWith(".") }
            .orEmpty()
            .map { it.toProjectFile() }

    private val numberOfFiles
        get() = listFiles()?.size ?: 0

    override val hasChildren: Boolean
        get() = isDirectory && numberOfFiles > 0
}