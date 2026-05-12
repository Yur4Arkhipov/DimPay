package com.example.dimpay.core.domain.repository

import com.example.dimpay.core.domain.model.Card
import com.example.dimpay.core.domain.model.ConfirmationDetails
import com.example.dimpay.core.domain.model.PaymentSession
import kotlinx.coroutines.flow.Flow

interface CardRepository {
    fun getCards(): Flow<List<Card>>
    suspend fun addCard(
        cardName: String,
        cardNumber: String,
        expireDate: String,
        cvv: String
    )
    suspend fun deleteCard(cardId: String)
    suspend fun generateQr(cardId: String): Result<PaymentSession>
    suspend fun getConfirmationDetails(
        sessionId: String
    ): Result<ConfirmationDetails>
    suspend fun cancelPayment(sessionId: String): Result<Unit>
    suspend fun confirmPayment(sessionId: String): Result<Unit>
}