package com.github.cnrture.quickprojectwizard.arch.common

fun emptyConstants(packageName: String) = """
package $packageName.common

object Constants
""".trimIndent()
