package com.app.mobile.presentation.ui.screens.notification.details.viewmodel

import com.app.mobile.presentation.models.notification.NotificationDetailModel

sealed interface NotificationDetailUiState {
    data object Loading : NotificationDetailUiState
    data class Content(val notification: NotificationDetailModel) : NotificationDetailUiState
    data class Error(val message: String) : NotificationDetailUiState
}

