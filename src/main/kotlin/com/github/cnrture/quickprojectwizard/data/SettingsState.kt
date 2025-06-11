package com.github.cnrture.quickprojectwizard.data

import com.github.cnrture.quickprojectwizard.common.Constants

data class SettingsState(
    var defaultPackageName: String = Constants.DEFAULT_BASE_PACKAGE_NAME,
    var preferredModuleType: String = Constants.ANDROID,
    var webViewUrl: String = Constants.DEFAULT_WEB_VIEW_URL,

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