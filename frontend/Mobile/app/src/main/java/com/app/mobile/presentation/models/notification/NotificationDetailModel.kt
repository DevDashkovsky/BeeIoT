package com.app.mobile.presentation.models.notification

data class NotificationDetailModel(
    val id: String,
    val title: String,
    val hiveName: String,
    val date: String,
    val fullText: String,
    val isUrgent: Boolean = false
)

