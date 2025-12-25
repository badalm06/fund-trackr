package com.badal.fundtrackr.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsDataStore(context: Context) {
    private val dataStore = context.dataStore

    companion object {
        val DAILY_REMINDER_KEY = booleanPreferencesKey("daily_reminder_enabled")
    }

    suspend fun saveReminderEnabled(isEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[DAILY_REMINDER_KEY] = isEnabled
        }
    }


    val reminderEnabledFlow: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[DAILY_REMINDER_KEY] ?: false
        }
}