package com.github.cnrture.quickprojectwizard.arch.domain

fun emptyMainRepository(packageName: String) = """
package $packageName.domain.repository
            
interface MainRepository
""".trimIndent()
