package com.example.dimpay.core.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.dimpay.core.domain.model.StoredEncryptedToken
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class AppInstanceDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        val CIPHER_TEXT_KEY = stringPreferencesKey("app_instance_cipher")
        val IV_KEY = stringPreferencesKey("app_instance_iv")
    }

    suspend fun saveEncryptedToken(
        cipherText: String,
        iv: String
    ) {
        dataStore.edit { prefs ->
            prefs[CIPHER_TEXT_KEY] = cipherText
            prefs[IV_KEY] = iv
        }
    }

    suspend fun getEncryptedToken(): StoredEncryptedToken? {
        val prefs = dataStore.data.first()
        val cipherText = prefs[CIPHER_TEXT_KEY] ?: return null
        val iv = prefs[IV_KEY] ?: return null

        return StoredEncryptedToken(
            cipherText = cipherText,
            iv = iv
        )
    }

    suspend fun clear() {
        dataStore.edit {
            it.remove(CIPHER_TEXT_KEY)
            it.remove(IV_KEY)
        }
    }
}