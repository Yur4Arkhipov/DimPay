package com.example.dimpay.core.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [
        Index(value = ["cardInstance"])
    ]
)
data class EncryptedPaymentTokenEntity(
    @PrimaryKey
    val tokenId: String,
    val cardInstance: String,
    val index: Int,
    val encryptedKey: ByteArray,
    val iv: ByteArray
)