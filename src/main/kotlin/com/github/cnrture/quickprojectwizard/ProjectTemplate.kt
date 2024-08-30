package com.github.cnrture.quickprojectwizard

import com.android.tools.idea.wizard.template.*
import com.android.tools.idea.wizard.template.impl.defaultPackageNameParameter
import java.net.URL
import java.util.*

enum class ImageLibrary { None, Coil, Glide }
enum class NetworkLibrary { None, Retrofit, Ktor }
enum class UISystem { Compose, XML }

val projectTemplate
    get() = template {
        name = "Quick Project Wizard"
        description = "Quickly create a new project with libraries, tools and screens you want."
        minApi = 23
        constraints = listOf(TemplateConstraint.AndroidX, TemplateConstraint.Kotlin)
        category = Category.Application
        formFactor = FormFactor.Mobile
        screens = listOf(WizardUiContext.NewProject, WizardUiContext.NewProjectExtraDetail)

        val packageName = defaultPackageNameParameter
        val projectName = packageName.value.split(".").last()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

        val selectedUISystem = enumParameter<UISystem> {
            name = "UI System"
            default = UISystem.Compose
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

        val screens = stringParameter {
            name = "Screens"
            default = ""
        }

        val javaJvmVersion = stringParameter {
            name = "Java & JVM Version"
            default = "17"
        }

        widgets(
            EnumWidget(selectedUISystem),
            CheckBoxWidget(isRoomEnable),
            CheckBoxWidget(isHiltEnable),
            CheckBoxWidget(isNavigationEnable),
            CheckBoxWidget(isFirebaseEnable),
            CheckBoxWidget(isKtLintEnable),
            CheckBoxWidget(isDetektEnable),
            CheckBoxWidget(isWorkManagerEnable),
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
            UrlLinkWidget("Created by Caner Ture", "https://bento.me/canerture"),
            PackageNameWidget(packageName),
        )

        thumb = { Thumb { URL("https://canerture.com/quick_project_wizard_template.png") } }

        recipe = { data: TemplateData ->
            projectRecipe(
                moduleData = data as ModuleTemplateData,
                packageName = packageName.value,
                isCompose = selectedUISystem.value == UISystem.Compose,
                isRoomEnable = isRoomEnable.value,
                isWorkManagerEnable = isWorkManagerEnable.value,
                selectedNetworkLibrary = selectedNetworkLibrary.value,
                isHiltEnable = isHiltEnable.value,
                isNavigationEnable = isNavigationEnable.value,
                selectedImageLibrary = selectedImageLibrary.value,
                isKtLintEnable = isKtLintEnable.value,
                isDetektEnable = isDetektEnable.value,
                isFirebaseEnable = isFirebaseEnable.value,
                screens = screens.value,
                minApi = minApi,
                javaJvmVersion = javaJvmVersion.value,
                projectName = projectName
            )
        }
    }
