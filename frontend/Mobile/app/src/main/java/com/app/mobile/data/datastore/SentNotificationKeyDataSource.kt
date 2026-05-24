package com.app.mobile.data.datastore

interface SentNotificationKeyDataSource {
    suspend fun containsKey(key: String): Boolean
    suspend fun addKey(key: String)
}
