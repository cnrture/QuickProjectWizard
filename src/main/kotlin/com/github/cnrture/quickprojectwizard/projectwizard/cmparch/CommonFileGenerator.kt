package com.github.cnrture.quickprojectwizard.projectwizard.cmparch

import com.github.cnrture.quickprojectwizard.common.Utils
import com.intellij.ide.fileTemplates.FileTemplateManager
import com.intellij.ide.starters.local.GeneratorAsset
import com.intellij.ide.starters.local.GeneratorTemplateFile
import com.intellij.openapi.vfs.VirtualFile

class CommonFileGenerator(
    params: CMPConfigModel,
    private val dataModel: MutableMap<String, Any>,
    private val virtualFile: VirtualFile,
) : FileGenerator(params) {
    override fun generate(ftManager: FileTemplateManager, packageName: String): List<GeneratorAsset> {
        val list = mutableListOf<GeneratorAsset>()
        return list.apply {
            addAll(
                listOf(
                    GeneratorTemplateFile(
                        ".idea/workspace.xml",
                        ftManager.getCodeTemplate(Template.IDEA_WORKSPACE)
                    ),
                    GeneratorTemplateFile(
                        "build.gradle.kts",
                        ftManager.getCodeTemplate(Template.GRADLE_KTS)
                    ),
                    GeneratorTemplateFile(
                        "settings.gradle.kts",
                        ftManager.getCodeTemplate(Template.SETTINGS_GRADLE)
                    ),
                    GeneratorTemplateFile(
                        "gradle.properties",
                        ftManager.getCodeTemplate(Template.GRADLE_PROPERTIES)
                    ),
                    GeneratorTemplateFile(
                        "gradle/wrapper/gradle-wrapper.properties",
                        ftManager.getCodeTemplate(Template.GRADLE_WRAPPER_PROPERTIES)
                    ),
                    GeneratorTemplateFile(
                        "gradle/libs.versions.toml",
                        ftManager.getCodeTemplate(Template.TOML)
                    ),
                    GeneratorTemplateFile(
                        "composeApp/src/commonMain/kotlin/$packageName/App.kt",
                        ftManager.getCodeTemplate(Template.COMMON_APP)
                    ),
                    GeneratorTemplateFile(
                        "composeApp/src/commonMain/composeResources/drawable/compose-multiplatform.xml",
                        ftManager.getCodeTemplate(Template.COMMON_COMPOSE_RESOURCES_MULTIPLATFORM_XML)
                    ),
                    GeneratorTemplateFile(
                        "composeApp/build.gradle.kts",
                        ftManager.getCodeTemplate(Template.COMPOSE_GRADLE_KTS)
                    ),
                )
            )

            if (params.isDataDomainDiUiEnable) {
                params.screens.forEach {
                    val screenLowerCase = it.lowercase()
                    dataModel["SCREEN"] = it
                    dataModel["SCREEN_LOWERCASE"] = screenLowerCase
                    Utils.generateFileFromTemplate(
                        dataModel,
                        virtualFile,
                        GeneratorTemplateFile(
                            "composeApp/src/commonMain/kotlin/$packageName/ui/$screenLowerCase/${it}Screen.kt",
                            ftManager.getCodeTemplate(Template.COMPOSE_SCREEN)
                        )
                    )
                    Utils.generateFileFromTemplate(
                        dataModel,
                        virtualFile,
                        GeneratorTemplateFile(
                            "composeApp/src/commonMain/kotlin/$packageName/ui/$screenLowerCase/${it}ViewModel.kt",
                            ftManager.getCodeTemplate(Template.COMPOSE_VIEW_MODEL)
                        )
                    )
                    Utils.generateFileFromTemplate(
                        dataModel,
                        virtualFile,
                        GeneratorTemplateFile(
                            "composeApp/src/commonMain/kotlin/$packageName/ui/$screenLowerCase/${it}Contract.kt",
                            ftManager.getCodeTemplate(Template.CONTRACT)
                        )
                    )
                }
                addAll(
                    listOf(
                        GeneratorTemplateFile(
                            "composeApp/src/commonMain/kotlin/$packageName/navigation/Screen.kt",
                            ftManager.getCodeTemplate(Template.NAVIGATION_SCREENS)
                        ),
                        GeneratorTemplateFile(
                            "composeApp/src/commonMain/kotlin/$packageName/delegation/MVI.kt",
                            ftManager.getCodeTemplate(Template.MVI)
                        ),
                        GeneratorTemplateFile(
                            "composeApp/src/commonMain/kotlin/$packageName/delegation/MVIDelegate.kt",
                            ftManager.getCodeTemplate(Template.MVI_DELEGATE)
                        ),
                        GeneratorTemplateFile(
                            "composeApp/src/commonMain/kotlin/$packageName/common/CollectExtension.kt",
                            ftManager.getCodeTemplate(Template.COLLECT_EXTENSION)
                        ),
                        GeneratorTemplateFile(
                            "composeApp/src/commonMain/kotlin/$packageName/common/Constants.kt",
                            ftManager.getCodeTemplate(Template.CONSTANTS)
                        ),
                        GeneratorTemplateFile(
                            "composeApp/src/commonMain/kotlin/$packageName/domain/repository/MainRepository.kt",
                            ftManager.getCodeTemplate(Template.REPOSITORY)
                        ),
                        GeneratorTemplateFile(
                            "composeApp/src/commonMain/kotlin/$packageName/data/repository/MainRepositoryImpl.kt",
                            ftManager.getCodeTemplate(Template.REPOSITORY_IMPL)
                        ),
                        GeneratorTemplateFile(
                            "composeApp/src/commonMain/kotlin/$packageName/ui/components/EmptyScreen.kt",
                            ftManager.getCodeTemplate(Template.EMPTY_SCREEN)
                        ),
                        GeneratorTemplateFile(
                            "composeApp/src/commonMain/kotlin/$packageName/ui/components/LoadingBar.kt",
                            ftManager.getCodeTemplate(Template.LOADING_BAR)
                        )
                    )
                )

                if (params.isKoinEnable) {
                    add(
                        GeneratorTemplateFile(
                            "composeApp/src/commonMain/kotlin/$packageName/di/AppModule.kt",
                            ftManager.getCodeTemplate(Template.APP_MODULE)
                        )
                    )
                }

                if (params.isKtorEnable) {
                    add(
                        GeneratorTemplateFile(
                            "composeApp/src/commonMain/kotlin/$packageName/data/source/remote/MainService.kt",
                            ftManager.getCodeTemplate(Template.SERVICE)
                        )
                    )
                }

                if (params.isNavigationEnable) {
                    add(
                        GeneratorTemplateFile(
                            "composeApp/src/commonMain/kotlin/$packageName/navigation/NavigationGraph.kt",
                            ftManager.getCodeTemplate(Template.NAVIGATION_GRAPH)
                        )
                    )
                }
            }
        }.toList()
    }
}