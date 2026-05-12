package com.example.dimpay.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.dimpay.core.data.local.entities.CardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: CardEntity)

    @Query("SELECT * FROM card")
    fun getAllCards(): Flow<List<CardEntity>>

    @Query("SELECT * FROM card WHERE cardId = :cardId")
    suspend fun getCardById(cardId: String): CardEntity?

    @Query("DELETE FROM card WHERE cardId = :cardId")
    suspend fun deleteCard(cardId: String)
}