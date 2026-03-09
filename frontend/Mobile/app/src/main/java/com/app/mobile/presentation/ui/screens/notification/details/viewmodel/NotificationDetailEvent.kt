package com.app.mobile.presentation.ui.screens.notification.details.viewmodel

sealed interface NotificationDetailEvent {
    data object NavigateBack : NotificationDetailEvent
    data class ShowSnackBar(val message: String) : NotificationDetailEvent
}

