package com.github.cnrture.quickprojectwizard.toolwindow.file

fun java.io.File.toProjectFile(): File = object : File {
    override val name: String
        get() = this@toProjectFile.name

    override val absolutePath: String
        get() = this@toProjectFile.absolutePath

    override val isDirectory: Boolean
        get() = this@toProjectFile.isDirectory

    override val children: List<File>
        get() = this@toProjectFile
            .listFiles { _, name -> !name.startsWith(".") }
            .orEmpty()
            .map { it.toProjectFile() }

    private val numberOfFiles
        get() = listFiles()?.size ?: 0

    override val hasChildren: Boolean
        get() = isDirectory && numberOfFiles > 0
}
