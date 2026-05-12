package com.example.dimpay.feature.home.model

import com.example.dimpay.core.domain.model.ConfirmationDetails

data class PaymentDialogState(
    val card: BankCardUi? = null,
    val isLoading: Boolean = false,
    val sessionId: String? = null,
    val qrValue: String? = null,
    val confirmation: ConfirmationDetails? = null,
    val error: String? = null,
    val navigateToOfflineQr: Boolean = false
)