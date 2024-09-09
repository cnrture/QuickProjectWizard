package com.github.cnrture.quickprojectwizard.gradle.network

import kotlinx.serialization.Serializable

@Serializable
data class VersionModel(
    val name: String,
    val value: String,
)
