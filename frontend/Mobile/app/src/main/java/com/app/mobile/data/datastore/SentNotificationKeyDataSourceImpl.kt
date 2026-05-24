package com.app.mobile.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json

private val SENT_KEYS_KEY = stringPreferencesKey("sent_keys_json")
private const val MAX_KEYS = 200

class SentNotificationKeyDataSourceImpl(
    private val dataStore: DataStore<Preferences>,
    private val json: Json
) : SentNotificationKeyDataSource {

    override suspend fun containsKey(key: String): Boolean {
        val prefs = dataStore.data.first()
        val raw = prefs[SENT_KEYS_KEY] ?: return false
        val keys = runCatching { json.decodeFromString<List<String>>(raw) }.getOrDefault(emptyList())
        return keys.contains(key)
    }

    override suspend fun addKey(key: String) {
        dataStore.edit { prefs ->
            val existing = prefs[SENT_KEYS_KEY]
                ?.let { runCatching { json.decodeFromString<List<String>>(it) }.getOrDefault(emptyList()) }
                ?: emptyList()
            val updated = (listOf(key) + existing).take(MAX_KEYS)
            prefs[SENT_KEYS_KEY] = json.encodeToString(updated)
        }
    }
}
