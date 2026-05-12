package com.example.dimpay.core.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CardDetailsDto(
    val cardNumber: String,
    val expiryDate: String,
    val cvv: String
)