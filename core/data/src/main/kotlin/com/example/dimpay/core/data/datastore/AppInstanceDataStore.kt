package com.example.dimpay.core.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class AppInstanceDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private val KEY = stringPreferencesKey("app_instance_token")

    suspend fun saveToken(token: String) {
        dataStore.edit { prefs ->
            prefs[KEY] = token
        }
    }

    suspend fun getToken(): String? {
        return dataStore.data.first()[KEY]
    }

    suspend fun clear() {
        dataStore.edit { it.remove(KEY) }
    }
}