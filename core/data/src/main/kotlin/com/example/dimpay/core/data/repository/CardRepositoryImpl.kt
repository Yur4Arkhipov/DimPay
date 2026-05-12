package com.example.dimpay.core.data.repository

import android.util.Log
import com.example.dimpay.core.data.local.dao.CardDao
import com.example.dimpay.core.data.local.entities.CardEntity
import com.example.dimpay.core.data.local.entities.toDomain
import com.example.dimpay.core.data.remote.dto.AddCardRequest
import com.example.dimpay.core.data.remote.dto.CardDetailsDto
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
        return runCatching {
            val response = api.getConfirmationDetails(sessionId)
            if (!response.success) {
                throw IllegalStateException("Confirmation failed")
            }
            ConfirmationDetails(
                sessionId = response.response.sessionId,
                merchantName = response.response.merchantName,
                amount = response.response.amount
            )
        }
    }
}