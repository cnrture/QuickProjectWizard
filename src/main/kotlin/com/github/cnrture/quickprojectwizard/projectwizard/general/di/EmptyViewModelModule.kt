package com.github.cnrture.quickprojectwizard.projectwizard.general.di

fun emptyViewModelModule(packageName: String, screenList: List<String>) = """
package $packageName.di

${screenList.joinToString("\n") { "import $packageName.ui.${it.lowercase()}.${it}ViewModel" }}
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
${screenList.joinToString("\n") { "    viewModelOf(::${it}ViewModel)" }}
}
""".trimIndent()
