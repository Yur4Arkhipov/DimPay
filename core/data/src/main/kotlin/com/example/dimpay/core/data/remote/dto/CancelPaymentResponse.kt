package com.example.dimpay.core.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CancelPaymentResponse(
    val success: Boolean
)