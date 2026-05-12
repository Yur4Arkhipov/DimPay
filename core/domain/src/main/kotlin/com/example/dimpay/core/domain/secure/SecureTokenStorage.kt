package com.example.dimpay.core.domain.secure

import com.example.dimpay.core.domain.model.PaymentToken

interface SecureTokenStorage {
    suspend fun saveTokens(
        cardInstance: String,
        tokens: List<PaymentToken>
    )
    suspend fun getTokens(
        cardInstance: String
    ): List<PaymentToken>
}