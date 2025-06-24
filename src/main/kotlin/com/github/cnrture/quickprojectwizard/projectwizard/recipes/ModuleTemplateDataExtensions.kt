package com.github.cnrture.quickprojectwizard.projectwizard.recipes

import com.android.tools.idea.wizard.template.ModuleTemplateData

/**
 * Extension function to get minimum API level from ModuleTemplateData
 * Uses reflection to handle different API versions and changes
 */
fun ModuleTemplateData.getMinApiLevel(): Int {
    return try {
        // Try modern API first
        val minApi = this.apis.minApi

        // Try getApiLevel method
        val apiLevelMethod = minApi.javaClass.getDeclaredMethod("getApiLevel")
        apiLevelMethod.isAccessible = true
        apiLevelMethod.invoke(minApi) as Int
    } catch (_: Exception) {
        try {
            // Fallback to getApi method
            val minApi = this.apis.minApi
            val apiMethod = minApi.javaClass.getDeclaredMethod("getApi")
            apiMethod.isAccessible = true
            apiMethod.invoke(minApi) as Int
        } catch (_: Exception) {
            try {
                // Fallback to apiLevel field
                val minApi = this.apis.minApi
                val apiLevelField = minApi.javaClass.getDeclaredField("apiLevel")
                apiLevelField.isAccessible = true
                apiLevelField.get(minApi) as Int
            } catch (_: Exception) {
                try {
                    // Fallback to api field
                    val minApi = this.apis.minApi
                    val apiField = minApi.javaClass.getDeclaredField("api")
                    apiField.isAccessible = true
                    apiField.get(minApi) as Int
                } catch (_: Exception) {
                    try {
                        // Try toString parsing as last resort
                        val minApi = this.apis.minApi
                        val apiString = minApi.toString()
                        val regex = Regex("\\d+")
                        val match = regex.find(apiString)
                        match?.value?.toIntOrNull() ?: 23
                    } catch (_: Exception) {
                        // Default to API 23 (Android 6.0) as safe fallback
                        23
                    }
                }
            }
        }
    }
}
