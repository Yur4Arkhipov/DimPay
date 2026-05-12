package com.example.dimpay.core.data.remote.dto

import com.example.dimpay.core.domain.model.PaymentToken
import kotlinx.serialization.Serializable

@Serializable
data class PaymentTokenResponse(
    val success: Boolean,
    val response: List<PaymentTokenDto>
)

@Serializable
data class PaymentTokenDto(
    val tokenId: String,
    val index: Int,
    val key: String
)

fun PaymentTokenDto.toDomain(): PaymentToken {
    return PaymentToken(
        tokenId = tokenId,
        index = index,
        key = key
    )
}