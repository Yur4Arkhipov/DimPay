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
        WHERE cardId = :cardId
        ORDER BY `index`
    """)
    suspend fun getTokens(
        cardId: String
    ): List<EncryptedPaymentTokenEntity>

    @Query("""
        DELETE FROM EncryptedPaymentTokenEntity
        WHERE cardId = :cardId
    """)
    suspend fun deleteForCard(
        cardId: String
    )

    @Query("""
        DELETE FROM EncryptedPaymentTokenEntity
        WHERE tokenId = :tokenId
    """)
    suspend fun deleteToken(
        tokenId: String
    )

    @Query("""
        SELECT * FROM EncryptedPaymentTokenEntity
        WHERE cardId = :cardId
        ORDER BY `index`
        LIMIT 1
    """)
    suspend fun getNextToken(cardId: String): EncryptedPaymentTokenEntity?
}