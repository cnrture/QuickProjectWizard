package com.github.cnrture.quickprojectwizard.data

import com.github.cnrture.quickprojectwizard.common.Constants
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@State(name = "QuickProjectWizardSettings", storages = [Storage("quickProjectWizard.xml")])
@Service(Service.Level.APP)
class SettingsService : PersistentStateComponent<SettingsState> {
    private var myState = SettingsState()
    override fun getState(): SettingsState = myState
    override fun loadState(state: SettingsState) {
        myState = state
    }

    fun saveTemplate(template: ModuleTemplate) {
        val existingIndex = myState.moduleTemplates.indexOfFirst { it.id == template.id }
        if (existingIndex != -1) {
            myState.moduleTemplates[existingIndex] = template
        } else {
            myState.moduleTemplates.add(template)
        }
    }

    fun removeTemplate(template: ModuleTemplate) {
        myState.moduleTemplates.removeAll { it.id == template.id }
    }
}

data class SettingsState(
    var moduleTemplates: MutableList<ModuleTemplate> = mutableListOf(),
    var defaultPackageName: String = Constants.DEFAULT_BASE_PACKAGE_NAME,
    var preferredModuleType: String = Constants.ANDROID,
    var featureScreenTemplate: String = Constants.EMPTY,
    var featureViewModelTemplate: String = Constants.EMPTY,
    var featureContractTemplate: String = Constants.EMPTY,
    var featureComponentKeyTemplate: String = Constants.EMPTY,
    var featurePreviewProviderTemplate: String = Constants.EMPTY,
    var moduleReadmeTemplate: String = Constants.EMPTY,
    var manifestTemplate: String = Constants.EMPTY,
    var gradleAndroidTemplate: String = Constants.EMPTY,
    var gradleKotlinTemplate: String = Constants.EMPTY,

    var isCompose: Boolean = true,
    var isHiltEnable: Boolean = true,
)

data class ModuleTemplate(
    val id: String,
    val name: String,
    val fileTemplates: List<FileTemplate>,
    val isDefault: Boolean = false,
)

data class FileTemplate(
    val fileName: String,
    val filePath: String,
    val fileContent: String,
    val fileType: String,
)
