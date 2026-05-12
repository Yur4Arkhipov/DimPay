package com.example.dimpay.core.data.secure

import com.example.dimpay.core.data.local.dao.PaymentTokenDao
import com.example.dimpay.core.data.local.entities.EncryptedPaymentTokenEntity
import com.example.dimpay.core.domain.model.EncryptedData
import com.example.dimpay.core.domain.model.PaymentToken
import com.example.dimpay.core.domain.secure.SecureTokenStorage

class SecureTokenStorageImpl(
    private val cryptoManager: CryptoManager,
    private val dao: PaymentTokenDao
) : SecureTokenStorage {

    companion object {
        private const val KEY_ALIAS = "card_storage_key"
    }

    override suspend fun saveTokens(
        cardInstance: String,
        tokens: List<PaymentToken>
    ) {
        cryptoManager.createKeyIfNotExists(KEY_ALIAS)
        dao.deleteForCard(cardInstance)
        val entities = tokens.map { token ->
            val encrypted = cryptoManager.encrypt(
                alias = KEY_ALIAS,
                data = token.key.toByteArray()
            )
            EncryptedPaymentTokenEntity(
                tokenId = token.tokenId,
                cardInstance = cardInstance,
                index = token.index,
                encryptedKey = encrypted.ciphertext,
                iv = encrypted.iv
            )
        }
        dao.insertAll(entities)
    }
    override suspend fun getTokens(
        cardInstance: String
    ): List<PaymentToken> {

        val entities =
            dao.getTokens(cardInstance)

        return entities.map { entity ->

            val decrypted =
                cryptoManager.decrypt(
                    alias = KEY_ALIAS,
                    encryptedData = EncryptedData(
                        ciphertext = entity.encryptedKey,
                        iv = entity.iv
                    )
                )

            PaymentToken(
                tokenId = entity.tokenId,
                index = entity.index,
                key = decrypted.decodeToString()
            )
        }
    }
}