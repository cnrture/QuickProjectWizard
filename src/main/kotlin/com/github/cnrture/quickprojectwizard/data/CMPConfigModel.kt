package com.github.cnrture.quickprojectwizard.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.android.tools.idea.wizard.model.WizardModel

class CMPConfigModel : WizardModel() {
    var isAndroidEnable: Boolean by mutableStateOf(false)
    var isIOSEnable: Boolean by mutableStateOf(false)
    var isDesktopEnable: Boolean by mutableStateOf(false)
    var isKtorEnable: Boolean by mutableStateOf(false)
    var isRoomEnable: Boolean by mutableStateOf(false)
    var isCoilEnable: Boolean by mutableStateOf(false)
    var isKamelEnable: Boolean by mutableStateOf(false)
    var isKoinEnable: Boolean by mutableStateOf(false)
    var isNavigationEnable: Boolean by mutableStateOf(false)
    var isDataDomainDiUiEnable: Boolean by mutableStateOf(false)
    var screens: List<String> by mutableStateOf(emptyList())
    var packageName by mutableStateOf("")

    override fun handleFinished() = Unit
}