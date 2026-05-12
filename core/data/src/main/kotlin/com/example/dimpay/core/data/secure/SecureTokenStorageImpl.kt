package com.example.dimpay.core.data.secure

import com.example.dimpay.core.data.local.dao.PaymentTokenDao
import com.example.dimpay.core.data.local.entities.EncryptedPaymentTokenEntity
import com.example.dimpay.core.domain.model.EncryptedPaymentToken
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
        cardId: String,
        tokens: List<PaymentToken>
    ) {
        cryptoManager.createKeyIfNotExists(KEY_ALIAS)
        dao.deleteForCard(cardId)
        val entities = tokens.map { token ->
            val encrypted = cryptoManager.encrypt(
                alias = KEY_ALIAS,
                data = token.key.toByteArray()
            )
            EncryptedPaymentTokenEntity(
                tokenId = token.tokenId,
                cardId = cardId,
                index = token.index,
                encryptedKey = encrypted.ciphertext,
                iv = encrypted.iv
            )
        }
        dao.insertAll(entities)
    }

    override suspend fun deleteToken(tokenId: String) {
        dao.deleteToken(tokenId)
    }

    override suspend fun getNextToken(cardId: String): EncryptedPaymentToken? {
        val entity = dao.getNextToken(cardId)
            ?: return null

        return EncryptedPaymentToken(
            tokenId = entity.tokenId,
            cardId = entity.cardId,
            index = entity.index,
            encryptedKey = entity.encryptedKey,
            iv = entity.iv
        )
    }
}