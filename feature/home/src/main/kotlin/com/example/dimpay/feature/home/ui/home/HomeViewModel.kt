package com.example.dimpay.feature.home.ui.home

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dimpay.core.domain.repository.CardRepository
import com.example.dimpay.feature.home.model.BankCardUi
import com.example.dimpay.feature.home.model.PaymentDialogState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
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

    private var confirmationJob: Job? = null

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

    private fun generateQr() {
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
        confirmationJob?.cancel()
        confirmationJob = viewModelScope.launch {
            repeat(30) { _ ->
                ensureActive()
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

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun onPayClick(context: Context) {
        if (hasInternet(context)) {
            generateQr()
        } else {
            _paymentDialogState.update {
                it.copy(
                    navigateToOfflineQr = true
                )
            }
        }
    }

    fun resetOfflineNavigation() {
        _paymentDialogState.update {
            it.copy(
                navigateToOfflineQr = false
            )
        }
    }

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    private fun hasInternet(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE)
                    as ConnectivityManager

        val network = connectivityManager.activeNetwork
            ?: return false

        val capabilities =
            connectivityManager.getNetworkCapabilities(network)
                ?: return false

        return capabilities.hasCapability(
            NetworkCapabilities.NET_CAPABILITY_INTERNET
        )
    }

    fun cancelPayment() {
        val sessionId = paymentDialogState.value.sessionId
        confirmationJob?.cancel()
        confirmationJob = null
        viewModelScope.launch {
            if (sessionId != null) {
                repository.cancelPayment(sessionId)
            }
            _paymentDialogState.update {
                it.copy(
                    qrValue = null,
                    confirmation = null,
                    error = null,
                    sessionId = null,
                    isLoading = false
                )
            }
            closePaymentDialog()
        }
    }

    fun confirmPayment() {
        val sessionId = paymentDialogState.value.sessionId ?: return
        viewModelScope.launch {
            repository.confirmPayment(sessionId)
                .onSuccess {
                    closePaymentDialog()
                }
                .onFailure { error ->
                    _paymentDialogState.update {
                        it.copy(
                            error = error.message
                        )
                    }
                }
        }
    }
}