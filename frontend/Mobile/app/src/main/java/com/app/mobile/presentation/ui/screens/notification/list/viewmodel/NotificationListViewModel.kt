package com.app.mobile.presentation.ui.screens.notification.list.viewmodel

import com.app.mobile.presentation.models.hive.QueenStageUi
import com.app.mobile.presentation.models.notification.NotificationPreviewModel
import com.app.mobile.presentation.models.queen.QueenPreviewModel
import com.app.mobile.presentation.ui.components.BaseViewModel

class NotificationListViewModel :
    BaseViewModel<NotificationListUiState, NotificationListEvent>(NotificationListUiState.Loading) {

    override fun handleError(exception: Throwable) {
        updateState { NotificationListUiState.Error(exception.message ?: "Unknown error") }
    }

    fun loadNotifications() {
        updateState { NotificationListUiState.Loading }
        launch {
            // Stub data — заменить на use case позже
            val queens = listOf(
                QueenPreviewModel(
                    id = "q1",
                    name = "Контроль кладки",
                    stage = QueenStageUi(
                        title = "День 5",
                        description = "Личинка: Кормление",
                        progress = 0.85f,
                        isActionRequired = true,
                        remainingDays = "Осталось 1 день"
                    ),
                    hiveName = "Игорь"
                ),
                QueenPreviewModel(
                    id = "q2",
                    name = "Контроль кладки",
                    stage = QueenStageUi(
                        title = "День 3",
                        description = "Яйцо: Инкубация",
                        progress = 0.7f,
                        isActionRequired = true,
                        remainingDays = "Осталось 2 дня"
                    ),
                    hiveName = "Игорь"
                )
            )

            val notifications = listOf(
                NotificationPreviewModel(
                    id = "n1",
                    title = "Медведь съел улей",
                    message = "Жестко там все, полныйфывфывфывфыв",
                    hiveName = "игорь",
                    date = "2024.05.06",
                    isUrgent = false,
                    isNew = true
                ),
                NotificationPreviewModel(
                    id = "n2",
                    title = "Медведь съел улей",
                    message = "Жестко там все, полныйфывфывфывфыв фывфывфывр ыфвыр выфв выф вфывфыв выфвфы выф вф ывф ыв...",
                    hiveName = "игорьвыаываывавыв",
                    date = "2024.05.06",
                    isUrgent = true,
                    isNew = false
                ),
                NotificationPreviewModel(
                    id = "n3",
                    title = "Медведь съел улей",
                    message = "Жестко там все, полныйфывфывфывфывфыв",
                    hiveName = "игорь",
                    date = "2024.05.06",
                    isUrgent = false,
                    isNew = false
                ),
                NotificationPreviewModel(
                    id = "n4",
                    title = "Медведь съел улей",
                    message = "Жестко там все, полныйфывфывфывфывфыв",
                    hiveName = "игорь",
                    date = "2024.05.06",
                    isUrgent = false,
                    isNew = false
                ),
                NotificationPreviewModel(
                    id = "n5",
                    title = "Медведь съел улей",
                    message = "Жестко там все, полныйфывфывфывфывфыв",
                    hiveName = "игорь",
                    date = "2024.05.06",
                    isUrgent = false,
                    isNew = false
                ),
                NotificationPreviewModel(
                    id = "n6",
                    title = "Медведь съел улей",
                    message = "Жестко там все, полныйфывфывфывфывфыв",
                    hiveName = "игорь",
                    date = "2024.05.06",
                    isUrgent = false,
                    isNew = false
                )
            )

            updateState { NotificationListUiState.Content(queens, notifications) }
        }
    }

    fun onNotificationClick(notificationId: String) {
        if (currentState is NotificationListUiState.Content) {
            sendEvent(NotificationListEvent.NavigateToNotification(notificationId))
        }
    }

    fun onDeleteAll() {
        // Заглушка — позже добавить use case
        updateState {
            if (it is NotificationListUiState.Content) {
                it.copy(notifications = emptyList())
            } else it
        }
    }

    fun onSwipeDismiss(notificationId: String) {
        // Заглушка — пока без функционала удаления
    }

    fun resetError() = loadNotifications()
}

