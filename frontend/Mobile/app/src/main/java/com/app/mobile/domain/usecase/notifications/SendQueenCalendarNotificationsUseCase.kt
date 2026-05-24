package com.app.mobile.domain.usecase.notifications

import com.app.mobile.domain.models.notifications.NotificationRecord
import com.app.mobile.domain.repository.notifications.PermissionRepository
import com.app.mobile.domain.repository.notifications.SentNotificationKeyRepository
import com.app.mobile.presentation.notifications.NotificationView
import java.util.UUID

class SendQueenCalendarNotificationsUseCase(
    private val getQueenNotificationsForTodayUseCase: GetQueenNotificationsForTodayUseCase,
    private val notificationView: NotificationView,
    private val saveNotificationUseCase: SaveNotificationUseCase,
    private val sentKeyRepository: SentNotificationKeyRepository,
    private val permissionRepository: PermissionRepository
) {

    suspend operator fun invoke() {
        if (!permissionRepository.hasAskedForPermission()) return

        val events = getQueenNotificationsForTodayUseCase()
        events.forEach { event ->
            if (event.isCritical) {
                notificationView.showCriticalNotification(event.title, event.body)
            } else {
                notificationView.showRegularNotification(event.title, event.body)
            }
            saveNotificationUseCase(
                NotificationRecord(
                    id = UUID.randomUUID().toString(),
                    title = event.title,
                    body = event.body,
                    type = if (event.isCritical) "CRITICAL" else "REGULAR",
                    timestamp = System.currentTimeMillis()
                )
            )
            sentKeyRepository.markAsSent(event.eventKey)
        }
    }
}
