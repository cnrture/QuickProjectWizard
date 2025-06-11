package com.github.cnrture.quickprojectwizard.projectwizard.general.domain

fun emptyMainRepository(packageName: String) = """
package $packageName.domain.repository
            
interface MainRepository
""".trimIndent()
