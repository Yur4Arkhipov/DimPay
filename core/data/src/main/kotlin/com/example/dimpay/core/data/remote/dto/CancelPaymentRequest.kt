package com.example.dimpay.core.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CancelPaymentRequest(
    val sessionId: String
)