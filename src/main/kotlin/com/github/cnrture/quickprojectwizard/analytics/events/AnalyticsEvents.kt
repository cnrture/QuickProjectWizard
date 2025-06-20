package com.github.cnrture.quickprojectwizard.analytics.events

import kotlinx.serialization.Serializable

@Serializable
data class QPWEvent(
    val eventName: String,
    val timestamp: String = "",
)