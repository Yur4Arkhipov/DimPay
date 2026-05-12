package com.example.dimpay.core.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ConfirmationDetailsDto(
    val sessionId: String,
    val merchantName: String,
    val amount: Double
)