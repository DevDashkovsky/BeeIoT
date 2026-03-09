package com.app.mobile.presentation.ui.screens.notification.list.models

data class NotificationListActions(
    val onNotificationClick: (String) -> Unit,
    val onDeleteAll: () -> Unit,
    val onQueenClick: (String) -> Unit
)

