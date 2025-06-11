package com.github.cnrture.quickprojectwizard.projectwizard.gradle.network

import kotlinx.serialization.Serializable

@Serializable
data class VersionModel(
    val name: String,
    val value: String,
)
