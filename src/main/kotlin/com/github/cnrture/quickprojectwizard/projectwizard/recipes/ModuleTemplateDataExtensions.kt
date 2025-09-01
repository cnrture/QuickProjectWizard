package com.github.cnrture.quickprojectwizard.projectwizard.recipes

import com.android.tools.idea.wizard.template.ModuleTemplateData

fun ModuleTemplateData.getMinApiLevel(): Int {
    return try {
        this.projectTemplateData.androidXSupport
        when {
            this.toString().contains("minApi") -> {
                val apiMatch = Regex("minApi[=:]\\s*(\\d+)").find(this.toString())
                apiMatch?.groupValues?.get(1)?.toIntOrNull() ?: 23
            }

            this.projectTemplateData.toString().contains("minApi") -> {
                val apiMatch = Regex("minApi[=:]\\s*(\\d+)").find(this.projectTemplateData.toString())
                apiMatch?.groupValues?.get(1)?.toIntOrNull() ?: 23
            }

            else -> 23
        }
    } catch (_: Exception) {
        23
    }
}
