package com.github.cnrture.quickprojectwizard.data

import kotlinx.serialization.Serializable

@Serializable
data class QPWEvent(
    val eventName: String,
    val timestamp: String = "",
)