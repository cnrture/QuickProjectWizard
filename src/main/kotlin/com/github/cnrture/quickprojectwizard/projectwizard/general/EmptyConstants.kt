package com.github.cnrture.quickprojectwizard.projectwizard.general

fun emptyConstants(packageName: String) = """
package $packageName.common

object Constants
""".trimIndent()
