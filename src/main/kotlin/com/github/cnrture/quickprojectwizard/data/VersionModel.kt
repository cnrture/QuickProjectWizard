package com.github.cnrture.quickprojectwizard.data

import kotlinx.serialization.Serializable

@Serializable
data class VersionModel(
    val name: String,
    val value: String,
)