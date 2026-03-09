package com.app.mobile.presentation.ui.screens.notification.list.viewmodel

import com.app.mobile.presentation.models.notification.NotificationPreviewModel
import com.app.mobile.presentation.models.queen.QueenPreviewModel

sealed interface NotificationListUiState {
    data object Loading : NotificationListUiState
    data class Content(
        val nearestQueens: List<QueenPreviewModel>,
        val notifications: List<NotificationPreviewModel>
    ) : NotificationListUiState
    data class Error(val message: String) : NotificationListUiState
}

