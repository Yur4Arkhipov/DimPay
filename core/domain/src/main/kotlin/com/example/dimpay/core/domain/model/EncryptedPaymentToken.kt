package com.example.dimpay.core.domain.model

data class EncryptedPaymentToken(
    val tokenId: String,
    val cardId: String,
    val index: Int,
    val encryptedKey: ByteArray,
    val iv: ByteArray
)