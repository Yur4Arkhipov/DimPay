package com.example.dimpay.core.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AddCardRequest(
    val cardNumber: String,
    val expireDate: String,
    val cvv: String
)