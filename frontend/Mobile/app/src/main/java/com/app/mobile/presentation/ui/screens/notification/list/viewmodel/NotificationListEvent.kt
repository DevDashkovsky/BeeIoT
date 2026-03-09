package com.app.mobile.presentation.ui.screens.notification.list.viewmodel

sealed interface NotificationListEvent {
    data class NavigateToNotification(val notificationId: String) : NotificationListEvent
    data class ShowSnackBar(val message: String) : NotificationListEvent
}

