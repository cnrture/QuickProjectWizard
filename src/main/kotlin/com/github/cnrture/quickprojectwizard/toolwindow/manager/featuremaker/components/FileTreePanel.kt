package com.github.cnrture.quickprojectwizard.toolwindow.manager.featuremaker.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.cnrture.quickprojectwizard.common.file.FileTree
import com.github.cnrture.quickprojectwizard.common.rootDirectoryString
import com.github.cnrture.quickprojectwizard.common.rootDirectoryStringDropLast
import com.github.cnrture.quickprojectwizard.common.toProjectFile
import com.github.cnrture.quickprojectwizard.components.QPWFileTree
import com.github.cnrture.quickprojectwizard.theme.QPWTheme
import com.intellij.openapi.project.Project
import java.io.File

@Composable
fun FileTreePanel(
    modifier: Modifier = Modifier,
    project: Project,
    onSelectedSrc: (String) -> Unit = {},
) {
    QPWFileTree(
        modifier = modifier,
        model = FileTree(root = File(project.rootDirectoryString()).toProjectFile()),
        titleColor = QPWTheme.colors.red,
        containerColor = QPWTheme.colors.black,
        onClick = { fileTreeNode ->
            val absolutePathAtNode = fileTreeNode.file.absolutePath
            val relativePath = absolutePathAtNode.removePrefix(project.rootDirectoryStringDropLast())
                .removePrefix(File.separator)
            if (fileTreeNode.file.isDirectory) {
                onSelectedSrc(relativePath)
            }
        }
    )
}