package com.example.dimpay.core.data.secure

import android.content.Context
import android.util.Base64
import com.example.dimpay.core.domain.model.EncryptedData
import com.example.dimpay.core.domain.secure.CardSecureStorage
import androidx.core.content.edit

class CardSecureStorageImpl(
    context: Context,
    private val cryptoManager: CryptoManager
) : CardSecureStorage {

    companion object {
        private const val PREF_NAME = "secure_cards"
        private const val KEY_ALIAS = "card_storage_key"
    }

    private val prefs =
        context.getSharedPreferences(
            PREF_NAME,
            Context.MODE_PRIVATE
        )

    init {
        cryptoManager.createKeyIfNotExists(KEY_ALIAS)
    }

    override suspend fun saveCardInstance(
        cardId: String,
        cardInstance: String
    ) {

        val encryptedData = cryptoManager.encrypt(
            alias = KEY_ALIAS,
            data = cardInstance.toByteArray()
        )

        prefs.edit {
            putString(
                "${cardId}_cipher",
                Base64.encodeToString(
                    encryptedData.ciphertext,
                    Base64.DEFAULT
                )
            )
                .putString(
                    "${cardId}_iv",
                    Base64.encodeToString(
                        encryptedData.iv,
                        Base64.DEFAULT
                    )
                )
        }
    }

    override suspend fun getCardInstance(
        cardId: String
    ): String? {

        val cipherTextBase64 =
            prefs.getString("${cardId}_cipher", null)
                ?: return null

        val ivBase64 =
            prefs.getString("${cardId}_iv", null)
                ?: return null

        val encryptedData = EncryptedData(
            ciphertext = Base64.decode(
                cipherTextBase64,
                Base64.DEFAULT
            ),
            iv = Base64.decode(
                ivBase64,
                Base64.DEFAULT
            )
        )

        val decryptedBytes =
            cryptoManager.decrypt(
                alias = KEY_ALIAS,
                encryptedData = encryptedData
            )

        return decryptedBytes.decodeToString()
    }

    override suspend fun removeCardInstance(
        cardId: String
    ) {

        prefs.edit {
            remove("${cardId}_cipher")
                .remove("${cardId}_iv")
        }
    }
}