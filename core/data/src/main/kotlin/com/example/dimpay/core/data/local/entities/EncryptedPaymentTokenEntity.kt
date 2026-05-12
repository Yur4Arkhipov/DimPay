package com.example.dimpay.core.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.dimpay.core.domain.model.EncryptedPaymentToken

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

fun EncryptedPaymentTokenEntity.toDomain(): EncryptedPaymentToken =
    EncryptedPaymentToken(
        tokenId = tokenId,
        cardId = cardId,
        index = index,
        encryptedKey = encryptedKey,
        iv = iv
    )