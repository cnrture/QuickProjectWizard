package com.github.cnrture.quickprojectwizard.data

import com.github.cnrture.quickprojectwizard.common.Constants
import kotlinx.serialization.Serializable

@Serializable
data class SettingsState(
    var moduleTemplates: MutableList<ModuleTemplate> = mutableListOf(),
    var featureTemplates: MutableList<FeatureTemplate> = mutableListOf(),
    var defaultModuleTemplateId: String = "candroid_template",
    var defaultFeatureTemplateId: String = "candroid_template",
    var defaultPackageName: String = Constants.DEFAULT_BASE_PACKAGE_NAME,
    var preferredModuleType: String = Constants.ANDROID,
    var featureScreenTemplate: String = Constants.EMPTY,
    var featureViewModelTemplate: String = Constants.EMPTY,
    var featureContractTemplate: String = Constants.EMPTY,
    var featurePreviewProviderTemplate: String = Constants.EMPTY,
    var moduleReadmeTemplate: String = Constants.EMPTY,
    var manifestTemplate: String = Constants.EMPTY,
    var gradleAndroidTemplate: String = Constants.EMPTY,
    var gradleKotlinTemplate: String = Constants.EMPTY,

    var isCompose: Boolean = true,
    var isHiltEnable: Boolean = true,
    var isActionsExpanded: Boolean = true,

    var colorHistory: MutableList<ColorInfo> = mutableListOf(),

    var formatterSelectedFormat: String = "JSON",
    var formatterInputText: String = "",
    var formatterErrorMessage: String = "",

    var apiSelectedMethod: String = "GET",
    var apiUrl: String = "https://api.canerture.com/harrypotterapp/characters",
    var apiRequestBody: String = "",
    var apiHeaders: MutableMap<String, String> = mutableMapOf("Content-Type" to "application/json"),
    var apiQueryParams: MutableMap<String, String> = mutableMapOf(),
    var apiSelectedTab: String = "headers",
)