package com.github.cnrture.quickprojectwizard.common

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import java.io.File

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