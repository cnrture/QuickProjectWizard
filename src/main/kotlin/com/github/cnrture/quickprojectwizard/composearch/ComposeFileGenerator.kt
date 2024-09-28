package com.github.cnrture.quickprojectwizard.composearch

import com.github.cnrture.quickprojectwizard.cmparch.ComposeTemp
import com.github.cnrture.quickprojectwizard.cmparch.FileGenerator
import com.github.cnrture.quickprojectwizard.cmparch.Template
import com.github.cnrture.quickprojectwizard.util.FileUtils
import com.intellij.ide.fileTemplates.FileTemplateManager
import com.intellij.ide.starters.local.GeneratorAsset
import com.intellij.ide.starters.local.GeneratorTemplateFile
import com.intellij.openapi.vfs.VirtualFile

class ComposeFileGenerator(
    params: ComposeConfigModel,
    private val dataModel: MutableMap<String, Any>,
    private val virtualFile: VirtualFile,
) : FileGenerator<ComposeConfigModel>(params) {
    override fun generate(ftManager: FileTemplateManager, packageName: String): List<GeneratorAsset> {
        val list = mutableListOf<GeneratorAsset>()
        return list.apply {
            addAll(
                listOf(
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
                        "app/src/main/AndroidManifest.xml",
                        ftManager.getCodeTemplate(Template.ANDROID_MANIFEST)
                    ),
                    GeneratorTemplateFile(
                        "app/src/main/res/values/strings.xml",
                        ftManager.getCodeTemplate(Template.ANDROID_VALUES_XML)
                    ),
                    GeneratorTemplateFile(
                        "app/src/main/java/$packageName/ui/MainActivity.kt",
                        ftManager.getCodeTemplate(ComposeTemp.COMPOSE_MAIN_ACTIVITY)
                    ),
                    GeneratorTemplateFile(
                        "app/src/main/java/$packageName/ui/theme/Color.kt",
                        ftManager.getCodeTemplate(ComposeTemp.COMPOSE_COLOR)
                    ),
                    GeneratorTemplateFile(
                        "app/src/main/java/$packageName/ui/theme/Theme.kt",
                        ftManager.getCodeTemplate(ComposeTemp.COMPOSE_THEME)
                    ),
                    GeneratorTemplateFile(
                        "app/src/main/java/$packageName/ui/theme/Type.kt",
                        ftManager.getCodeTemplate(ComposeTemp.COMPOSE_TYPE)
                    ),
                )
            )

            if (params.isDetektEnable) {
                add(
                    GeneratorTemplateFile(
                        "detekt/detektConfig.yml",
                        ftManager.getCodeTemplate(ComposeTemp.DETEKT_CONFIG)
                    )
                )
            }

            if (params.isDataDomainDiUiEnable) {
                params.screens.forEach {
                    val screenLowerCase = it.lowercase()
                    dataModel["SCREEN"] = it
                    dataModel["SCREEN_LOWERCASE"] = screenLowerCase
                    FileUtils.generateFileFromTemplate(
                        dataModel,
                        virtualFile,
                        GeneratorTemplateFile(
                            "app/src/main/java/$packageName/ui/$screenLowerCase/${it}Screen.kt",
                            ftManager.getCodeTemplate(ComposeTemp.SCREEN)
                        )
                    )
                    FileUtils.generateFileFromTemplate(
                        dataModel,
                        virtualFile,
                        GeneratorTemplateFile(
                            "app/src/main/java/$packageName/ui/$screenLowerCase/${it}ViewModel.kt",
                            ftManager.getCodeTemplate(ComposeTemp.VIEW_MODEL)
                        )
                    )
                    FileUtils.generateFileFromTemplate(
                        dataModel,
                        virtualFile,
                        GeneratorTemplateFile(
                            "app/src/main/java/$packageName/ui/$screenLowerCase/${it}Contract.kt",
                            ftManager.getCodeTemplate(ComposeTemp.CONTRACT)
                        )
                    )
                    FileUtils.generateFileFromTemplate(
                        dataModel,
                        virtualFile,
                        GeneratorTemplateFile(
                            "app/src/main/java/$packageName/ui/$screenLowerCase/${it}ScreenPreviewProvider.kt",
                            ftManager.getCodeTemplate(ComposeTemp.COMPOSE_PREVIEW_PROVIDER)
                        )
                    )
                }
                addAll(
                    listOf(
                        GeneratorTemplateFile(
                            "composeApp/src/commonMain/kotlin/$packageName/delegation/MVI.kt",
                            ftManager.getCodeTemplate(ComposeTemp.MVI)
                        ),
                        GeneratorTemplateFile(
                            "composeApp/src/commonMain/kotlin/$packageName/delegation/MVIDelegate.kt",
                            ftManager.getCodeTemplate(ComposeTemp.MVI_DELEGATE)
                        ),
                        GeneratorTemplateFile(
                            "app/src/main/java/$packageName/common/Constants.kt",
                            ftManager.getCodeTemplate(ComposeTemp.CONSTANTS)
                        ),
                        GeneratorTemplateFile(
                            "app/src/main/java/$packageName/common/CollectExtension.kt",
                            ftManager.getCodeTemplate(ComposeTemp.COMPOSE_COLLECT_EXTENSION)
                        ),
                        GeneratorTemplateFile(
                            "app/src/main/java/$packageName/ui/components/EmptyScreen.kt",
                            ftManager.getCodeTemplate(ComposeTemp.COMPOSE_EMPTY_SCREEN)
                        ),
                        GeneratorTemplateFile(
                            "app/src/main/java/$packageName/ui/components/LoadingBar.kt",
                            ftManager.getCodeTemplate(ComposeTemp.COMPOSE_LOADING_BAR)
                        ),
                        GeneratorTemplateFile(
                            "app/src/main/java/$packageName/domain/repository/MainRepository.kt",
                            ftManager.getCodeTemplate(ComposeTemp.MAIN_REPOSITORY)
                        ),
                        GeneratorTemplateFile(
                            "app/src/main/java/$packageName/data/repository/MainRepositoryImpl.kt",
                            ftManager.getCodeTemplate(ComposeTemp.MAIN_REPOSITORY_IMPL)
                        ),
                    )
                )

                if (params.isHiltEnable) {
                    add(
                        GeneratorTemplateFile(
                            "app/src/main/java/$packageName/MainApp.kt",
                            ftManager.getCodeTemplate(ComposeTemp.MAIN_APP)
                        )
                    )
                    add(
                        GeneratorTemplateFile(
                            "app/src/main/java/$packageName/di/RepositoryModule.kt",
                            ftManager.getCodeTemplate(ComposeTemp.REPOSITORY_MODULE)
                        )
                    )
                }

                if (params.isNavigationEnable) {
                    add(
                        GeneratorTemplateFile(
                            "app/src/main/java/$packageName/navigation/Navigation.kt",
                            ftManager.getCodeTemplate(ComposeTemp.COMPOSE_NAVIGATION)
                        )
                    )
                    add(
                        GeneratorTemplateFile(
                            "app/src/main/java/$packageName/navigation/Screen.kt",
                            ftManager.getCodeTemplate(ComposeTemp.COMPOSE_NAVIGATION_SCREEN)
                        )
                    )
                }

                if (params.isRetrofitEnable || params.isKtorEnable) {
                    add(
                        GeneratorTemplateFile(
                            "app/src/main/java/$packageName/data/source/remote/MainService.kt",
                            ftManager.getCodeTemplate(ComposeTemp.MAIN_SERVICE)
                        )
                    )

                    if (params.isHiltEnable) {
                        add(
                            GeneratorTemplateFile(
                                "app/src/main/java/$packageName/di/NetworkModule.kt",
                                ftManager.getCodeTemplate(ComposeTemp.NETWORK_MODULE)
                            )
                        )
                    }

                    if (params.isKtorEnable) {
                        add(
                            GeneratorTemplateFile(
                                "app/src/main/java/$packageName/data/source/remote/KtorApi.kt",
                                ftManager.getCodeTemplate(ComposeTemp.KTOR_API)
                            )
                        )
                    }
                }

                if (params.isRoomEnable) {
                    add(
                        GeneratorTemplateFile(
                            "app/src/main/java/$packageName/data/model/MainEntityModel.kt",
                            ftManager.getCodeTemplate(ComposeTemp.MAIN_ENTITY_MODEL)
                        )
                    )
                    add(
                        GeneratorTemplateFile(
                            "app/src/main/java/$packageName/data/source/local/MainDao.kt",
                            ftManager.getCodeTemplate(ComposeTemp.MAIN_DAO)
                        )
                    )
                    add(
                        GeneratorTemplateFile(
                            "app/src/main/java/$packageName/data/source/local/MainRoomDB.kt",
                            ftManager.getCodeTemplate(ComposeTemp.MAIN_ROOM_DB)
                        )
                    )
                    if (params.isHiltEnable) {
                        add(
                            GeneratorTemplateFile(
                                "app/src/main/java/$packageName/di/LocalModule.kt",
                                ftManager.getCodeTemplate(ComposeTemp.LOCAL_MODULE)
                            )
                        )
                    }
                }
            }
        }
    }
}