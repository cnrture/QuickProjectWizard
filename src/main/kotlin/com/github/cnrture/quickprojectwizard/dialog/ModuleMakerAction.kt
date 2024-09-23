package com.github.cnrture.quickprojectwizard.dialog

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

class ModuleMakerAction : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val project: Project = event.getRequiredData(CommonDataKeys.PROJECT)

        val startingLocation: VirtualFile? = event.getData(CommonDataKeys.VIRTUAL_FILE)

        // we only want to use a starting location if it's coming from a directory
        val shouldUseStartingLocation = startingLocation != null && startingLocation.isDirectory

        ModuleMakerDialogWrapper(
            project = project,
            startingLocation = if (shouldUseStartingLocation) startingLocation else null
        ).show()
    }
}