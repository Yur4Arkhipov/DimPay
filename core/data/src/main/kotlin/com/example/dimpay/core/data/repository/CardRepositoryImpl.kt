package com.example.dimpay.core.data.repository

import com.example.dimpay.core.data.local.dao.CardDao
import com.example.dimpay.core.data.local.entities.CardEntity
import com.example.dimpay.core.data.local.entities.toDomain
import com.example.dimpay.core.data.remote.dto.AddCardRequest
import com.example.dimpay.core.data.remote.dto.AddCardResponse
import com.example.dimpay.core.data.remote.service.CustomerApi
import com.example.dimpay.core.domain.model.Card
import com.example.dimpay.core.domain.repository.CardRepository
import com.example.dimpay.core.domain.secure.CardSecureStorage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class CardRepositoryImpl @Inject constructor(
    private val api: CustomerApi,
    private val dao: CardDao,
    private val secureStorage: CardSecureStorage
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
//        val response = api.addPaymentCard(
//            AddCardRequest(
//                cardNumber = cardNumber,
//                expireDate = expireDate,
//                cvv = cvv
//            )
//        )
        delay(1000)
        val response = AddCardResponse(
            cardId = UUID.randomUUID().toString(),
            cardInstance = UUID.randomUUID().toString()
        )
        dao.insertCard(
            CardEntity(
                cardId = response.cardId,
                cardName = cardName
            )
        )
        secureStorage.saveCardInstance(
            cardId = response.cardId,
            cardInstance = response.cardInstance
        )
    }

    override suspend fun deleteCard(cardId: String) {
        dao.deleteCard(cardId)
        secureStorage.removeCardInstance(cardId)
    }

//    override suspend fun getCardInstance(
//        cardId: String
//    ): String? {
//        return secureStorage.getCardInstance(cardId)
//    }
}