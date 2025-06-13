package com.github.cnrture.quickprojectwizard.projectwizard

import com.android.tools.idea.wizard.template.*
import com.android.tools.idea.wizard.template.impl.defaultPackageNameParameter
import com.github.cnrture.quickprojectwizard.projectwizard.general.ImageLibrary
import com.github.cnrture.quickprojectwizard.projectwizard.general.NetworkLibrary
import com.github.cnrture.quickprojectwizard.projectwizard.gradle.network.getVersions
import com.github.cnrture.quickprojectwizard.projectwizard.recipes.xmlProjectRecipe
import kotlinx.coroutines.runBlocking
import java.net.URL

val xmlTemplate = template {
    name = "QuickProjectWizard - XML"
    description = "Quickly create a new project with libraries, tools and screens you want."
    minApi = 23
    constraints = listOf(TemplateConstraint.AndroidX, TemplateConstraint.Kotlin)
    category = Category.Application
    formFactor = FormFactor.Mobile
    screens = listOf(WizardUiContext.NewProject, WizardUiContext.NewProjectExtraDetail)

    runBlocking {
        try {
            getVersions()
        } catch (e: Exception) {
            println("Failed to fetch versions: ${e.message}")
        }
    }

    val selectedNetworkLibrary = enumParameter<NetworkLibrary> {
        name = "Network Library"
        default = NetworkLibrary.None
    }

    val isRoomEnable = booleanParameter {
        name = "Room"
        default = false
    }

    val isHiltEnable = booleanParameter {
        name = "Hilt"
        default = false
    }

    val isNavigationEnable = booleanParameter {
        name = "Navigation"
        default = false
    }

    val selectedImageLibrary = enumParameter<ImageLibrary> {
        name = "Image Library"
        default = ImageLibrary.None
    }

    val isKtLintEnable = booleanParameter {
        name = "KtLint"
        default = false
    }

    val isDetektEnable = booleanParameter {
        name = "Detekt"
        default = false
    }

    val isFirebaseEnable = booleanParameter {
        name = "Firebase"
        default = false
    }

    val isWorkManagerEnable = booleanParameter {
        name = "WorkManager"
        default = false
    }

    val dataDiDomainPresentationUiPackages = booleanParameter {
        name = "data - di - domain - presentation - ui packages"
        default = false
    }

    val screens = stringParameter {
        name = "Screens"
        default = ""
    }

    val javaJvmVersion = stringParameter {
        name = "Java & JVM Version"
        default = "17"
    }

    widgets(
        CheckBoxWidget(isRoomEnable),
        CheckBoxWidget(isHiltEnable),
        CheckBoxWidget(isNavigationEnable),
        CheckBoxWidget(isFirebaseEnable),
        CheckBoxWidget(isKtLintEnable),
        CheckBoxWidget(isDetektEnable),
        CheckBoxWidget(isWorkManagerEnable),
        CheckBoxWidget(dataDiDomainPresentationUiPackages),
        EnumWidget(selectedNetworkLibrary),
        EnumWidget(selectedImageLibrary),
        TextFieldWidget(screens),
        LabelWidget(
            "Please enter the screens you want to create. (e.g. Home, Detail, Profile)" +
                "\nNote: First item is start destination"
        ),
        LabelWidget(" "),
        TextFieldWidget(javaJvmVersion),
        LabelWidget("8 or 11 or 17 etc."),
        LabelWidget(" "),
        UrlLinkWidget("Created by Caner Ture", "https://candroid.dev"),
        PackageNameWidget(defaultPackageNameParameter),
    )

    thumb = {
        val pluginClassLoader =
            Class.forName("com.github.cnrture.quickprojectwizard.projectwizard.XMLTemplateKt").classLoader
        val imageUrl = pluginClassLoader?.getResource("images/qpw-xml.png")
        if (imageUrl != null) {
            Thumb { imageUrl }
        } else {
            Thumb { URL("https://canerture.com/qpw-xml.png") }
        }
    }

    recipe = { data: TemplateData ->
        xmlProjectRecipe(
            moduleData = data as ModuleTemplateData,
            packageName = defaultPackageNameParameter.value,
            isRoomEnable = isRoomEnable.value,
            isWorkManagerEnable = isWorkManagerEnable.value,
            selectedNetworkLibrary = selectedNetworkLibrary.value,
            isHiltEnable = isHiltEnable.value,
            isNavigationEnable = isNavigationEnable.value,
            selectedImageLibrary = selectedImageLibrary.value,
            isKtLintEnable = isKtLintEnable.value,
            isDetektEnable = isDetektEnable.value,
            isFirebaseEnable = isFirebaseEnable.value,
            dataDiDomainPresentationUiPackages = dataDiDomainPresentationUiPackages.value,
            screens = screens.value,
            javaJvmVersion = javaJvmVersion.value,
        )
    }
}
