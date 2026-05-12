package com.example.dimpay.feature.home.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dimpay.core.domain.repository.CardRepository
import com.example.dimpay.feature.home.model.BankCardUi
import com.example.dimpay.feature.home.model.PaymentDialogState
import dagger.hilt.android.lifecycle.HiltViewModel
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
                    isLoading = true
                )
            }

            runCatching {
                repository.generateQr(card.cardId)
            }.onSuccess { qr ->
                _paymentDialogState.update {
                    it.copy(
                        isLoading = false,
                        qrValue = qr
                    )
                }
            }.onFailure { error ->
                _paymentDialogState.update {
                    it.copy(
                        isLoading = false,
                        error = error.message
                    )
                }
            }
        }
    }



//    private val _cards = MutableStateFlow(
//        listOf(
//            BankCardUi(
//                id = 1,
//                cardName = "Тинькофф",
//                cardType = "Дебетовая карта",
//                lastNumbers = "1234",
////                icon = AppIcons.Home
//            ),
//            BankCardUi(
//                id = 2,
//                cardName = "Сбербанк",
//                cardType = "Дебетовая карта",
//                lastNumbers = "9101",
////                icon = Icons.Default.CheckCircle
//            ),
//            BankCardUi(
//                id = 3,
//                cardName = "Альфа-Банк",
//                cardType = "Кредитная карта",
//                lastNumbers = "3456",
////                icon = Icons.Default.Favorite
//            )
//        )
//    )
//
//    val cards = _cards.asStateFlow()
}