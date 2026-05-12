package com.example.dimpay.feature.home.model

data class PaymentDialogState(
    val card: BankCardUi? = null,
    val isLoading: Boolean = false,
    val qrValue: String? = null,
    val error: String? = null
)