package com.example.dimpay.core.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.dimpay.core.data.local.dao.CardDao
import com.example.dimpay.core.data.local.dao.PaymentTokenDao
import com.example.dimpay.core.data.local.entities.CardEntity
import com.example.dimpay.core.data.local.entities.EncryptedPaymentTokenEntity

@Database(
    entities = [
        CardEntity::class,
        EncryptedPaymentTokenEntity::class
    ],
    version = 2
)
abstract class CardDatabase : RoomDatabase() {
    abstract fun cardDao(): CardDao
    abstract fun paymentTokenDao(): PaymentTokenDao
}