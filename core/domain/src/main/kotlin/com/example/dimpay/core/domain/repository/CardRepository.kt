package com.example.dimpay.core.domain.repository

import com.example.dimpay.core.domain.model.Card
import kotlinx.coroutines.flow.Flow

interface CardRepository {

    fun getCards(): Flow<List<Card>>

    suspend fun addCard(
        cardName: String,
        cardNumber: String,
        expireDate: String,
        cvv: String
    )

//    suspend fun getCardInstance(
//        cardId: String
//    ): String?
}