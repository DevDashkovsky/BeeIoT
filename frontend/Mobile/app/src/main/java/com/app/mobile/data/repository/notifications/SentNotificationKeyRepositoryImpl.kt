package com.app.mobile.data.repository.notifications

import com.app.mobile.data.datastore.SentNotificationKeyDataSource
import com.app.mobile.domain.repository.notifications.SentNotificationKeyRepository

class SentNotificationKeyRepositoryImpl(
    private val dataSource: SentNotificationKeyDataSource
) : SentNotificationKeyRepository {

    override suspend fun isAlreadySent(key: String): Boolean = dataSource.containsKey(key)

    override suspend fun markAsSent(key: String) = dataSource.addKey(key)
}
