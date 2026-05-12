package com.example.dimpay.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.example.dimpay.core.data.local.entities.EncryptedPaymentTokenEntity

@Dao
interface PaymentTokenDao {
    @Insert(onConflict = REPLACE)
    suspend fun insertAll(
        tokens: List<EncryptedPaymentTokenEntity>
    )

    @Query("""
        SELECT * FROM EncryptedPaymentTokenEntity
        WHERE cardInstance = :cardInstance
        ORDER BY `index`
    """)
    suspend fun getTokens(
        cardInstance: String
    ): List<EncryptedPaymentTokenEntity>

    @Query("""
        DELETE FROM EncryptedPaymentTokenEntity
        WHERE cardInstance = :cardInstance
    """)
    suspend fun deleteForCard(
        cardInstance: String
    )
}