package com.github.cnrture.quickprojectwizard.projectwizard

import com.android.tools.idea.wizard.template.Template
import com.android.tools.idea.wizard.template.WizardTemplateProvider
import com.github.cnrture.quickprojectwizard.projectwizard.gradle.network.getVersions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class AndroidStudioTemplateProvider : WizardTemplateProvider() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        // Start fetching versions asynchronously as soon as the provider is initialized
        scope.launch {
            getVersions()
        }
    }

    override fun getTemplates(): List<Template> = listOf(composeMultiplatformTemplate, composeTemplate, xmlTemplate)
}
