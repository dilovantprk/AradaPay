package com.ardabank.aradapay.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ardabank.aradapay.domain.util.SecurityUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "aradapay_security_prefs")

@Singleton
class SecurityPreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val PIN_HASH = stringPreferencesKey("pin_hash")
        val IS_PIN_ENABLED = booleanPreferencesKey("is_pin_enabled")
        val IS_DATA_LOCKED = booleanPreferencesKey("is_data_locked")
        val HIDE_PHONE_IN_CONTACTS = booleanPreferencesKey("hide_phone_in_contacts")
        val USER_NAME = stringPreferencesKey("user_name")
    }

    val userNameFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.USER_NAME] ?: "Mehmet Dilovan"
    }

    val pinHashFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[Keys.PIN_HASH]
    }

    val isPinEnabledFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.IS_PIN_ENABLED] ?: false
    }

    val isDataLockedFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.IS_DATA_LOCKED] ?: false
    }

    val hidePhoneFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.HIDE_PHONE_IN_CONTACTS] ?: false
    }

    suspend fun setPin(pin: String) {
        val hash = SecurityUtils.hashPin(pin)
        context.dataStore.edit { prefs ->
            prefs[Keys.PIN_HASH] = hash
            prefs[Keys.IS_PIN_ENABLED] = true
            prefs[Keys.IS_DATA_LOCKED] = true
        }
    }

    suspend fun clearPin() {
        context.dataStore.edit { prefs ->
            prefs.remove(Keys.PIN_HASH)
            prefs[Keys.IS_PIN_ENABLED] = false
            prefs[Keys.IS_DATA_LOCKED] = false
        }
    }

    suspend fun toggleLock(locked: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.IS_DATA_LOCKED] = locked
        }
    }

    suspend fun setHidePhone(hide: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.HIDE_PHONE_IN_CONTACTS] = hide
        }
    }

    suspend fun setUserName(name: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.USER_NAME] = name
        }
    }
}
