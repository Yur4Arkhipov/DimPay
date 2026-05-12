package com.example.dimpay.core.domain.model

data class StoredEncryptedToken(
    val cipherText: String,
    val iv: String
)