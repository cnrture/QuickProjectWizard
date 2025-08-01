package com.github.cnrture.quickprojectwizard.toolwindow.manager.featuregenerator.action

import com.github.cnrture.quickprojectwizard.toolwindow.manager.featuregenerator.dialog.FeatureGeneratorDialog
import com.intellij.icons.AllIcons
import com.intellij.ide.actions.CreateElementActionBase
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElement
import java.util.function.Consumer

class FeatureGeneratorAction : CreateElementActionBase(
    "Add Feature",
    "Create a new feature",
    AllIcons.Nodes.Folder,
) {
    override fun create(name: String, directory: PsiDirectory): Array<PsiElement> = emptyArray()

    override fun invokeDialog(
        project: Project,
        directory: PsiDirectory,
        elementsConsumer: Consumer<in Array<PsiElement>>,
    ) {
        val project = directory.project
        val virtualFile = directory.virtualFile
        FeatureGeneratorDialog(project, virtualFile).show()
    }

    override fun update(e: AnActionEvent) {
        val project = e.project
        val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE)
        e.presentation.isEnabledAndVisible = project != null && virtualFile?.isDirectory == true
    }

    override fun getActionName(directory: PsiDirectory, newName: String) = "Add Feature"
    override fun getErrorTitle(): String = "Error Creating Feature"
    override fun startInWriteAction(): Boolean = false
}