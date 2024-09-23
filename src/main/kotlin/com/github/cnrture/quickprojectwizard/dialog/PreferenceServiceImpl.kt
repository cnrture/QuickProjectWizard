package com.github.cnrture.quickprojectwizard.dialog

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil.copyBean
import kotlinx.serialization.Serializable
import org.jetbrains.annotations.Nullable

@State(name = "PreferenceService", storages = [(Storage("module_maker_preferences.xml"))])
class PreferenceServiceImpl : PersistentStateComponent<PreferenceServiceImpl.Companion.State>, PreferenceService {

    private var state = State()

    override var preferenceState: State
        get() = this.state
        set(value) {
            this.state = value
        }

    @Nullable
    override fun getState(): State {
        return this.preferenceState
    }

    override fun loadState(from: State) {
        copyBean(from, this.preferenceState)
    }

    companion object {

        @Serializable
        data class State(
            var androidTemplate: String = "",
            var kotlinTemplate: String = "",
            var apiTemplate: String = "",
            var apiModuleName: String = DEFAULT_API_MODULE_NAME,
            var glueTemplate: String = "",
            var glueModuleName: String = DEFAULT_GLUE_MODULE_NAME,
            var implTemplate: String = "",
            var implModuleName: String = DEFAULT_IMPL_MODULE_NAME,
            var gitignoreTemplate: String = "",
            var packageName: String = DEFAULT_BASE_PACKAGE_NAME,
            var includeProjectKeyword: String = DEFAULT_INCLUDE_KEYWORD,
            var refreshOnModuleAdd: Boolean = DEFAULT_REFRESH_ON_MODULE_ADD,
            var threeModuleCreationDefault: Boolean = DEFAULT_THREE_MODULE_CREATION,
            var useKtsFileExtension: Boolean = DEFAULT_USE_KTS_FILE_EXTENSION,
            var gradleFileNamedAfterModule: Boolean = DEFAULT_GRADLE_FILE_NAMED_AFTER_MODULE,
            var addReadme: Boolean = DEFAULT_ADD_README,
            var addGitIgnore: Boolean = DEFAULT_ADD_GIT_IGNORE
        )

        @JvmStatic
        val instance: PreferenceServiceImpl
            get() = ApplicationManager.getApplication().getService(PreferenceServiceImpl::class.java)

        const val DEFAULT_BASE_PACKAGE_NAME = "com.company.app"
        const val DEFAULT_INCLUDE_KEYWORD = ""
        const val DEFAULT_REFRESH_ON_MODULE_ADD = true
        const val DEFAULT_THREE_MODULE_CREATION = false
        const val DEFAULT_USE_KTS_FILE_EXTENSION = true
        const val DEFAULT_GRADLE_FILE_NAMED_AFTER_MODULE = false
        const val DEFAULT_ADD_README = true
        const val DEFAULT_ADD_GIT_IGNORE = false
        const val DEFAULT_API_MODULE_NAME = "api"
        const val DEFAULT_GLUE_MODULE_NAME = "glue"
        const val DEFAULT_IMPL_MODULE_NAME = "impl"

    }
}
