package com.example.dimpay.core.domain.secure

interface CardSecureStorage {

    suspend fun saveCardInstance(
        cardId: String,
        cardInstance: String
    )

    suspend fun getCardInstance(
        cardId: String
    ): String?

    suspend fun removeCardInstance(
        cardId: String
    )
}