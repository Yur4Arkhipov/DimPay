package com.example.dimpay.core.domain.model

data class EncryptedData(
    val ciphertext: ByteArray,
    val iv: ByteArray
)