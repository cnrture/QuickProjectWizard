package com.github.cnrture.quickprojectwizard.composearch

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.android.tools.idea.wizard.model.WizardModel

class ComposeConfigModel : WizardModel() {
    var isRoomEnable by mutableStateOf(false)
    var isWorkManagerEnable by mutableStateOf(false)
    var isRetrofitEnable by mutableStateOf(false)
    var isKtorEnable by mutableStateOf(false)
    var isHiltEnable by mutableStateOf(false)
    var isNavigationEnable by mutableStateOf(false)
    var isCoilEnable by mutableStateOf(false)
    var isGlideEnable by mutableStateOf(false)
    var isKtLintEnable by mutableStateOf(false)
    var isDetektEnable by mutableStateOf(false)
    var isFirebaseEnable by mutableStateOf(false)
    var isDataDomainDiUiEnable by mutableStateOf(false)
    var screens: List<String> by mutableStateOf(emptyList())
    var packageName by mutableStateOf("")

    override fun handleFinished() = Unit
}
