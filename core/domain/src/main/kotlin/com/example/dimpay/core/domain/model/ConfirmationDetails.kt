package com.example.dimpay.core.domain.model

data class ConfirmationDetails(
    val sessionId: String,
    val merchantName: String,
    val amount: Double
)