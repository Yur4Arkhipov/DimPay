package com.example.dimpay.feature.home.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dimpay.core.domain.repository.CardRepository
import com.example.dimpay.feature.home.model.BankCardUi
import com.example.dimpay.feature.home.model.PaymentDialogState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: CardRepository
) : ViewModel() {

    private val _paymentDialogState = MutableStateFlow(PaymentDialogState())
    val paymentDialogState = _paymentDialogState.asStateFlow()

    val cards = repository.getCards()
        .map { cards ->
            cards.map { card ->
                BankCardUi(
                    cardId = card.cardId,
                    cardName = card.cardName,
                    cardType = "Дебетовая карта",
                    lastNumbers = "1234"
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun deleteCard(cardId: String) {
        viewModelScope.launch {
            repository.deleteCard(cardId)
        }
    }

    fun openPaymentDialog(card: BankCardUi) {
        _paymentDialogState.value =
            PaymentDialogState(
                card = card
            )
    }

    fun closePaymentDialog() {
        _paymentDialogState.value = PaymentDialogState()
    }

    fun generateQr() {
        val card = paymentDialogState.value.card ?: return
        viewModelScope.launch {
            _paymentDialogState.update {
                it.copy(
                    isLoading = true,
                    error = null
                )
            }

            repository.generateQr(card.cardId)
                .onSuccess { session ->
                    _paymentDialogState.update {
                        it.copy(
                            isLoading = false,
                            sessionId = session.sessionId,
                            qrValue = session.sessionId
                        )
                    }
                    waitForConfirmation(session.sessionId)
                }
                .onFailure { error ->
                    _paymentDialogState.update {
                        it.copy(
                            isLoading = false,
                            qrValue = null,
                            error = error.message
                        )
                    }
                }
        }
    }

    fun waitForConfirmation(sessionId: String) {
        viewModelScope.launch {
            repeat(30) { _ ->
                val result = repository.getConfirmationDetails(sessionId)
                result.onSuccess { details ->
                    _paymentDialogState.update {
                        it.copy(
                            qrValue = null,
                            confirmation = details,
                            error = null
                        )
                    }
                    return@launch
                }
                delay(1000)
            }
            _paymentDialogState.update {
                it.copy(
                    qrValue = null,
                    error = "Время ожидания истекло"
                )
            }
        }
    }
}