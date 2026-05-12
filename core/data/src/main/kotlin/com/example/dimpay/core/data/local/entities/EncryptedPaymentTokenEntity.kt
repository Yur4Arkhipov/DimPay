package com.example.dimpay.core.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [
        Index(value = ["cardId"])
    ]
)
data class EncryptedPaymentTokenEntity(
    @PrimaryKey
    val tokenId: String,
    val cardId: String,
    val index: Int,
    val encryptedKey: ByteArray,
    val iv: ByteArray
)