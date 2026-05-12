package com.example.dimpay.core.domain.repository

import com.example.dimpay.core.domain.model.PaymentToken

interface TokenRepository {

    suspend fun getTokens(
        cardInstance: String
    ): List<PaymentToken>
}