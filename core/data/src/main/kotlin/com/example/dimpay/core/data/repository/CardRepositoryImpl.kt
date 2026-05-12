package com.example.dimpay.core.data.repository

import android.util.Log
import com.example.dimpay.core.data.local.dao.CardDao
import com.example.dimpay.core.data.local.entities.CardEntity
import com.example.dimpay.core.data.local.entities.toDomain
import com.example.dimpay.core.data.remote.dto.AddCardRequest
import com.example.dimpay.core.data.remote.dto.CancelPaymentRequest
import com.example.dimpay.core.data.remote.dto.CardDetailsDto
import com.example.dimpay.core.data.remote.dto.ConfirmPaymentRequest
import com.example.dimpay.core.data.remote.dto.QRRequest
import com.example.dimpay.core.data.remote.service.CustomerApi
import com.example.dimpay.core.domain.model.Card
import com.example.dimpay.core.domain.model.ConfirmationDetails
import com.example.dimpay.core.domain.model.PaymentSession
import com.example.dimpay.core.domain.repository.CardRepository
import com.example.dimpay.core.domain.secure.CardSecureStorage
import com.example.dimpay.core.domain.secure.SecureAppInstanceStorage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.util.UUID
import javax.inject.Inject

class CardRepositoryImpl @Inject constructor(
    private val api: CustomerApi,
    private val dao: CardDao,
    private val secureCardStorage: CardSecureStorage,
    private val secureAppStorage: SecureAppInstanceStorage
) : CardRepository {

    override fun getCards(): Flow<List<Card>> {
        return dao.getAllCards()
            .map { list ->
                list.map { it.toDomain() }
            }
    }

    override suspend fun addCard(
        cardName: String,
        cardNumber: String,
        expireDate: String,
        cvv: String
    ) {
        val appInstanceId = secureAppStorage.getToken()
            ?: error("AppInstanceId not found")

        val response = api.addPaymentCard(
            AddCardRequest(
                appInstanceId = appInstanceId,
                cardDetails = CardDetailsDto(
                    cardNumber = cardNumber,
                    expiryDate = expireDate,
                    cvv = cvv
                )
            )
        )
        Log.d("CardRepository", "Card id: $response")

        val cardId = UUID.randomUUID().toString()

        dao.insertCard(
            CardEntity(
                cardId = cardId,
                cardName = cardName
            )
        )
        secureCardStorage.saveCardInstance(
            cardId = cardId,
            cardInstance = response.response
        )
    }

    override suspend fun generateQr(
        cardId: String
    ): Result<PaymentSession> {
        return runCatching {
            val cardInstanceId =
                secureCardStorage.getCardInstance(cardId)
                    ?: error("Card instance not found")
            val response = api.generateQR(
                QRRequest(
                    cardInstanceId = cardInstanceId
                )
            )
            PaymentSession(
                sessionId = response.response
            )
        }
    }

    override suspend fun deleteCard(cardId: String) {
        dao.deleteCard(cardId)
        secureCardStorage.removeCardInstance(cardId)
    }

    override suspend fun getConfirmationDetails(
        sessionId: String
    ): Result<ConfirmationDetails> {
        try {
            Log.d("CardRepository", "Before request")
            val response = api.getConfirmationDetails(sessionId)
            Log.d("CardRepository", "After request")
            Log.d("CardRepository", "Response: $response")
            if (!response.success) {
                throw IllegalStateException("Confirmation failed")
            }
            return Result.success(
                ConfirmationDetails(
                    sessionId = response.response.sessionId,
                    merchantName = response.response.merchantName,
                    amount = response.response.amount
                )
            )
        } catch (e: Exception) {
            Log.e("CardRepository", "Request failed", e)
            return Result.failure(e)
        }
    }

    override suspend fun cancelPayment(
        sessionId: String
    ): Result<Unit> {
        return runCatching {
            val response = api.cancelPayment(
                CancelPaymentRequest(
                    sessionId = sessionId
                )
            )

            if (!response.isSuccessful) {
                error("Cancel payment failed")
            }
        }
    }

    override suspend fun confirmPayment(
        sessionId: String
    ): Result<Unit> {
        return runCatching {
            val response = api.confirmPayment(
                ConfirmPaymentRequest(
                    sessionId = sessionId
                )
            )
            if (!response.isSuccessful) {
                error("Confirm failed")
            }
        }
    }
}