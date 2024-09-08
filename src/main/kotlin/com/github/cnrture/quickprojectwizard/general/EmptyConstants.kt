package com.github.cnrture.quickprojectwizard.general

fun emptyConstants(packageName: String) = """
package $packageName.common

object Constants
""".trimIndent()
