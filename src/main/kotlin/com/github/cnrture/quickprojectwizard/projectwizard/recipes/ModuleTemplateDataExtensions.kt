package com.github.cnrture.quickprojectwizard.projectwizard.recipes

import com.android.tools.idea.wizard.template.ModuleTemplateData

fun ModuleTemplateData.getMinApiLevel(): Int {
    return try {
        val minApi = this.apis.minApi
        val apiLevelMethod = minApi.javaClass.getDeclaredMethod("getApiLevel")
        apiLevelMethod.invoke(minApi) as Int
    } catch (_: Exception) {
        try {
            val minApi = this.apis.minApi
            val apiMethod = minApi.javaClass.getDeclaredMethod("getApi")
            apiMethod.invoke(minApi) as Int
        } catch (_: Exception) {
            try {
                val minApi = this.apis.minApi
                val apiLevelField = minApi.javaClass.getDeclaredField("apiLevel")
                apiLevelField.isAccessible = true
                apiLevelField.get(minApi) as Int
            } catch (_: Exception) {
                try {
                    val minApi = this.apis.minApi
                    val apiField = minApi.javaClass.getDeclaredField("api")
                    apiField.isAccessible = true
                    apiField.get(minApi) as Int
                } catch (_: Exception) {
                    23
                }
            }
        }
    }
}
