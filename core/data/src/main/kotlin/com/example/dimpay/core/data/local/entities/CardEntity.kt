package com.example.dimpay.core.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.dimpay.core.domain.model.Card

@Entity(tableName = "card")
data class CardEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val cardId: String,
    val cardName: String,
)

fun CardEntity.toDomain(): Card =
    Card(
        cardId = cardId,
        cardName = cardName,
    )