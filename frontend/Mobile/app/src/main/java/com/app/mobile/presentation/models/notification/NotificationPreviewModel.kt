package com.app.mobile.presentation.models.notification

data class NotificationPreviewModel(
    val id: String,
    val title: String,
    val message: String,
    val hiveName: String,
    val date: String,
    val isUrgent: Boolean = false,
    val isNew: Boolean = false
)

