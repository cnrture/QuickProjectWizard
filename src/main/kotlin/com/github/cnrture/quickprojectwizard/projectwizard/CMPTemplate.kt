package com.github.cnrture.quickprojectwizard.projectwizard

import com.android.tools.idea.wizard.template.*
import com.android.tools.idea.wizard.template.impl.defaultPackageNameParameter
import com.github.cnrture.quickprojectwizard.projectwizard.cmparch.CMPImageLibrary
import com.github.cnrture.quickprojectwizard.projectwizard.gradle.network.getVersions
import com.github.cnrture.quickprojectwizard.projectwizard.recipes.composeMultiplatformProjectRecipe
import kotlinx.coroutines.runBlocking
import java.net.URL

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
            println("Versions fetched successfully")
        } catch (e: Exception) {
            println("Failed to fetch versions: ${e.message}")
        }
    }

    val packageName = defaultPackageNameParameter

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

    val isKtorServiceEnable = booleanParameter {
        name = "Ktor Service"
        default = false
    }

    /*    val isRoomEnable = booleanParameter {
            name = "Room"
            default = false
        }*/

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
    }

    widgets(
        CheckBoxWidget(isAndroidEnable),
        CheckBoxWidget(isIosEnable),
        CheckBoxWidget(isDesktopEnable),
        CheckBoxWidget(isKtorServiceEnable),
        //CheckBoxWidget(isRoomEnable),
        EnumWidget(selectedImageLibrary),
        CheckBoxWidget(isKoinEnable),
        CheckBoxWidget(isNavigationEnable),
        CheckBoxWidget(isDataDomainDiUiEnable),
        TextFieldWidget(screens),
        LabelWidget(
            "Please enter the screens you want to create. (e.g. Home, Detail, Profile)" +
                "\nNote: First item is start destination"
        ),
        UrlLinkWidget("Created by Caner Ture", "https://bento.me/canerture"),
        PackageNameWidget(packageName)
    )

    thumb = {
        val pluginClassLoader = Class.forName("com.github.cnrture.quickprojectwizard.CMPTemplateKt").classLoader
        val imageUrl = pluginClassLoader?.getResource("images/qpw-cmp.png")
        if (imageUrl != null) {
            Thumb { imageUrl }
        } else {
            Thumb { URL("https://raw.githubusercontent.com/cnrture/QuickProjectWizard/refs/heads/main/images/cmp_template.png") }
        }
    }

    recipe = { data: TemplateData ->
        composeMultiplatformProjectRecipe(
            moduleData = data as ModuleTemplateData,
            packageName = packageName.value,
            isAndroidEnable = isAndroidEnable.value,
            isIosEnable = isIosEnable.value,
            isDesktopEnable = isDesktopEnable.value,
            isKtorServiceEnable = isKtorServiceEnable.value,
            isRoomEnable = false,
            selectedImageLibrary = selectedImageLibrary.value,
            isKoinEnable = isKoinEnable.value,
            isNavigationEnable = isNavigationEnable.value,
            isDataDomainDiUiEnable = isDataDomainDiUiEnable.value,
            screens = screens.value,
        )
    }
}
