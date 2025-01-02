package com.eazywrite.app.data

import android.content.Context
import android.content.Intent
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.eazywrite.app.service.BillService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.settingsDataStore by preferencesDataStore(name = "settings")

object DataStoreKey {
    val KeyHideUpdateVersionCode = longPreferencesKey("key_hide_update_version_code")
    val KeyNotificationPermission = booleanPreferencesKey("key_notification_permission")
    val KeyMaxChatContextSize = intPreferencesKey("key_max_context_size")
}

val Context.notificationPermission: Flow<Boolean>
    get() = this.settingsDataStore.data.map { pref ->
        pref[DataStoreKey.KeyNotificationPermission] ?: false
    }

suspend fun Context.setNotificationPermission(enable: Boolean) {
    if (enable) {
        kotlin.runCatching {
            this.startService(Intent(this, BillService::class.java))
        }.onFailure { it.printStackTrace() }
    }
    this.settingsDataStore.edit { settings ->
        settings[DataStoreKey.KeyNotificationPermission] = enable
    }
}

val Context.hideUpdateVersionCodeFlow: Flow<Long>
    get() = this.settingsDataStore.data.map { pref ->
        pref[DataStoreKey.KeyHideUpdateVersionCode] ?: 0
    }

suspend fun Context.setHideUpdateVersionCode(version: Long) {
    this.settingsDataStore.edit { settings ->
        settings[DataStoreKey.KeyHideUpdateVersionCode] = version
    }
}

val Context.maxChatContextSize: Flow<Int>
    get() = this.settingsDataStore.data.map { pref ->
        pref[DataStoreKey.KeyMaxChatContextSize] ?: 10
    }

suspend fun Context.setMaxChatContextSize(value: Int) {
    this.settingsDataStore.edit { settings ->
        settings[DataStoreKey.KeyMaxChatContextSize] = value
    }
}