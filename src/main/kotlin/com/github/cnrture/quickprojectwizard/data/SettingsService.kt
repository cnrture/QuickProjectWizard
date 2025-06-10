package com.github.cnrture.quickprojectwizard.data

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@State(
    name = "QuickProjectWizardSettings",
    storages = [Storage("quickProjectWizard.xml")]
)
@Service(Service.Level.PROJECT)
class SettingsService : PersistentStateComponent<SettingsState> {
    private var myState = SettingsState()

    override fun getState(): SettingsState = myState

    override fun loadState(state: SettingsState) {
        myState = state
    }
}