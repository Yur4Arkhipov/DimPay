package com.example.dimpay.core.data.usecase

import android.util.Log
import com.example.dimpay.core.domain.model.PaymentToken
import com.example.dimpay.core.domain.repository.CardRepository
import com.example.dimpay.core.domain.repository.TokenRepository
import com.example.dimpay.core.domain.secure.CardSecureStorage
import com.example.dimpay.core.domain.secure.SecureTokenStorage
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SyncPaymentTokensUseCase @Inject constructor(
    private val cardsRepository: CardRepository,
    private val tokenRepository: TokenRepository,
    private val secureStorage: SecureTokenStorage,
    private val cardSecureStorage: CardSecureStorage
) {

    suspend operator fun invoke() {

        val cards = cardsRepository.getCards().first()

        var totalSaved = 0
        var totalCards = 0

        cards.forEach { card ->
            totalCards++
            val cardInstance =
                cardSecureStorage.getCardInstance(card.cardId)

            if (cardInstance == null) {
                Log.e("SyncTokens", "Missing cardInstance for ${card.cardId}")
                return@forEach
            }
            Log.d("SyncTokens", "Fetching tokens for card=${card.cardId}")
            val tokens = tokenRepository.getTokens(cardInstance)
            Log.d("SyncTokens", "Received ${tokens.size} tokens for ${card.cardId}")
            secureStorage.saveTokens(cardInstance, tokens)
            totalSaved += tokens.size
            Log.d(
                "SyncTokens",
                "Saved ${tokens.size} tokens for ${card.cardId}"
            )
        }
        Log.i(
            "SyncTokens",
            "SYNC DONE: cards=$totalCards, tokensSaved=$totalSaved"
        )
    }
}