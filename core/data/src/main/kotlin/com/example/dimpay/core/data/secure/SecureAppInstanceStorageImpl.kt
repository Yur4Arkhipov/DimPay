package com.example.dimpay.core.data.secure

import android.util.Base64
import com.example.dimpay.core.data.datastore.AppInstanceDataStore
import com.example.dimpay.core.domain.model.EncryptedData
import com.example.dimpay.core.domain.secure.SecureAppInstanceStorage
import javax.inject.Inject

class SecureAppInstanceStorageImpl @Inject constructor(
    private val dataStore: AppInstanceDataStore,
    private val cryptoManager: CryptoManager
) : SecureAppInstanceStorage {

    companion object {
        private const val KEY_ALIAS = "app_instance_key"
    }

    init {
        cryptoManager.createKeyIfNotExists(KEY_ALIAS)
    }

    override suspend fun saveToken(token: String) {
        val encrypted = cryptoManager.encrypt(
            alias = KEY_ALIAS,
            data = token.toByteArray()
        )
        val cipherText = Base64.encodeToString(
            encrypted.ciphertext,
            Base64.DEFAULT
        )
        val iv = Base64.encodeToString(
            encrypted.iv,
            Base64.DEFAULT
        )
        dataStore.saveEncryptedToken(
            cipherText = cipherText,
            iv = iv
        )
    }

    override suspend fun getToken(): String? {
        val encrypted = dataStore.getEncryptedToken()
                ?: return null

        val decrypted = cryptoManager.decrypt(
            alias = KEY_ALIAS,
            encryptedData = EncryptedData(
                ciphertext = Base64.decode(
                    encrypted.cipherText,
                    Base64.DEFAULT
                ),
                iv = Base64.decode(
                    encrypted.iv,
                    Base64.DEFAULT
                )
            )
        )

        return decrypted.decodeToString()
    }

    override suspend fun clear() {
        dataStore.clear()
    }
}