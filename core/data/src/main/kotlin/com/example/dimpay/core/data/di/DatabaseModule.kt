package com.example.dimpay.core.data.di

import android.content.Context
import androidx.room.Room
import com.example.dimpay.core.data.local.dao.CardDao
import com.example.dimpay.core.data.local.dao.PaymentTokenDao
import com.example.dimpay.core.data.local.database.CardDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): CardDatabase =
        Room.databaseBuilder(
            context,
            CardDatabase::class.java,
            "card_db"
        )
            .build()

    @Provides
    fun provideCardDao(db: CardDatabase): CardDao = db.cardDao()

    @Provides
    fun providePaymentTokenDao(db: CardDatabase): PaymentTokenDao =
        db.paymentTokenDao()
}