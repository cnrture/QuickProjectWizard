package com.github.cnrture.quickprojectwizard.general.domain

fun emptyMainRepository(packageName: String) = """
package $packageName.domain.repository
            
interface MainRepository
""".trimIndent()
