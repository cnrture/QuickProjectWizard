package com.github.cnrture.quickprojectwizard.projectwizard

import com.android.tools.idea.wizard.template.*
import com.android.tools.idea.wizard.template.impl.defaultPackageNameParameter
import com.github.cnrture.quickprojectwizard.common.getImage
import com.github.cnrture.quickprojectwizard.data.CMPImageLibrary
import com.github.cnrture.quickprojectwizard.data.CMPNetworkLibrary
import com.github.cnrture.quickprojectwizard.projectwizard.gradle.network.getVersions
import com.github.cnrture.quickprojectwizard.projectwizard.recipes.composeMultiplatformProjectRecipe
import kotlinx.coroutines.runBlocking

val composeMultiplatformTemplate = template {
    name = "QuickProjectWizard - CMP"
    description = "Quickly create a new project with libraries, tools and screens you want."
    minApi = 23
    category = Category.Other
    formFactor = FormFactor.Generic
    screens = listOf(WizardUiContext.NewProject, WizardUiContext.NewProjectExtraDetail)

    runBlocking {
        try {
            getVersions()
        } catch (e: Exception) {
            println("Failed to fetch versions: ${e.message}")
        }
    }

    val isAndroidEnable = booleanParameter {
        name = "Android"
        default = true
    }

    val isIosEnable = booleanParameter {
        name = "iOS"
        default = true
    }

    val isDesktopEnable = booleanParameter {
        name = "Desktop"
        default = false
    }

    val selectedNetworkLibrary = enumParameter<CMPNetworkLibrary> {
        name = "Network Library"
        default = CMPNetworkLibrary.None
    }

    val selectedImageLibrary = enumParameter<CMPImageLibrary> {
        name = "Image Library"
        default = CMPImageLibrary.None
    }

    val isKoinEnable = booleanParameter {
        name = "Koin"
        default = false
    }

    val isNavigationEnable = booleanParameter {
        name = "Navigation"
        default = false
    }

    val isDataDomainDiUiEnable = booleanParameter {
        name = "Common-Data-Domain-DI-UI Packages"
        default = false
    }

    val screens = stringParameter {
        name = "Screens"
        default = ""
        help = "Please enter the screens you want to create. (e.g. Home, Detail, Profile)"
    }

    widgets(
        CheckBoxWidget(isAndroidEnable),
        CheckBoxWidget(isIosEnable),
        CheckBoxWidget(isDesktopEnable),
        EnumWidget(selectedNetworkLibrary),
        EnumWidget(selectedImageLibrary),
        CheckBoxWidget(isKoinEnable),
        CheckBoxWidget(isNavigationEnable),
        CheckBoxWidget(isDataDomainDiUiEnable),
        TextFieldWidget(screens),
        LabelWidget(
            text = "Please enter the screens you want to create. (e.g. Home, Detail, Profile)" +
                "\nNote: First item is start destination"
        ),
        UrlLinkWidget("Created by Caner Ture", "https://candroid.dev"),
        PackageNameWidget(defaultPackageNameParameter)
    )

    thumb = { getImage("CMPTemplate", "qpw-cmp") }

    recipe = { data: TemplateData ->
        composeMultiplatformProjectRecipe(
            moduleData = data as ModuleTemplateData,
            packageName = data.packageName,
            isAndroidEnable = isAndroidEnable.value,
            isIosEnable = isIosEnable.value,
            isDesktopEnable = isDesktopEnable.value,
            selectedNetworkLibrary = selectedNetworkLibrary.value,
            isRoomEnable = false,
            selectedImageLibrary = selectedImageLibrary.value,
            isKoinEnable = isKoinEnable.value,
            isNavigationEnable = isNavigationEnable.value,
            isDataDomainDiUiEnable = isDataDomainDiUiEnable.value,
            screens = screens.value,
        )
    }
}
