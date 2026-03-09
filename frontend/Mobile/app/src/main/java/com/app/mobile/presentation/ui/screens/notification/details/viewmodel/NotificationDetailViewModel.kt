package com.app.mobile.presentation.ui.screens.notification.details.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.app.mobile.presentation.models.notification.NotificationDetailModel
import com.app.mobile.presentation.ui.components.BaseViewModel
import com.app.mobile.presentation.ui.screens.notification.details.NotificationDetailRoute

class NotificationDetailViewModel(
    savedStateHandle: SavedStateHandle
) : BaseViewModel<NotificationDetailUiState, NotificationDetailEvent>(NotificationDetailUiState.Loading) {

    private val notificationId: String = savedStateHandle.toRoute<NotificationDetailRoute>().notificationId

    override fun handleError(exception: Throwable) {
        updateState { NotificationDetailUiState.Error(exception.message ?: "Unknown error") }
    }

    fun loadNotification() {
        updateState { NotificationDetailUiState.Loading }
        launch {
            // Stub data — заменить на use case позже
            val notification = NotificationDetailModel(
                id = notificationId,
                title = "Медведь съел улей",
                hiveName = "игорь",
                date = "2024.05.03",
                fullText = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.",
                isUrgent = false
            )

            updateState { NotificationDetailUiState.Content(notification) }
        }
    }

    fun onDeleteClick() {
        // Заглушка — позже добавить use case удаления
        sendEvent(NotificationDetailEvent.NavigateBack)
    }

    fun resetError() = loadNotification()
}

