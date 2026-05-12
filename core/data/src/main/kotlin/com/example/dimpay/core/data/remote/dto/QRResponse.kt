package com.example.dimpay.core.data.remote.dto

import kotlinx.serialization.Serializable
import retrofit2.Response

@Serializable
data class QRResponse(
    val success: Boolean,
    val response: String
)