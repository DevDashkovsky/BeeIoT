package com.app.mobile.domain.repository.notifications

interface SentNotificationKeyRepository {
    suspend fun isAlreadySent(key: String): Boolean
    suspend fun markAsSent(key: String)
}
