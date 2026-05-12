package com.example.dimpay.core.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ConfirmationDetailsResponse(
    val success: Boolean,
    val response: ConfirmationDetailsDto
)