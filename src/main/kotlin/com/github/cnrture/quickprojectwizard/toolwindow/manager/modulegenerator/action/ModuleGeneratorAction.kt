package com.github.cnrture.quickprojectwizard.toolwindow.manager.modulegenerator.action

import com.github.cnrture.quickprojectwizard.toolwindow.manager.modulegenerator.dialog.ModuleGeneratorDialog
import com.intellij.icons.AllIcons
import com.intellij.ide.actions.CreateElementActionBase
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElement
import java.util.function.Consumer

class ModuleGeneratorAction : CreateElementActionBase(
    "New Module with This Files",
    "Create a new module with the selected files",
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
        ModuleGeneratorDialog(project, virtualFile).show()
    }

    override fun update(e: AnActionEvent) {
        val project = e.project
        val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE)
        e.presentation.isEnabledAndVisible = project != null && virtualFile?.isDirectory == true
    }

    override fun getActionName(directory: PsiDirectory, newName: String) = "New Module with This Files"
    override fun getErrorTitle(): String = "Error Creating Module"
    override fun startInWriteAction(): Boolean = false
}