package com.example.dimpay.core.data.di

import android.content.Context
import com.example.dimpay.core.data.datastore.AppInstanceDataStore
import com.example.dimpay.core.data.secure.CardSecureStorageImpl
import com.example.dimpay.core.data.secure.CryptoManager
import com.example.dimpay.core.data.secure.SecureAppInstanceStorageImpl
import com.example.dimpay.core.domain.secure.CardSecureStorage
import com.example.dimpay.core.domain.secure.SecureAppInstanceStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SecureStorageModule {

    @Provides
    @Singleton
    fun provideCardSecureStorage(
        @ApplicationContext context: Context,
        cryptoManager: CryptoManager
    ): CardSecureStorage {
        return CardSecureStorageImpl(context, cryptoManager)
    }

    @Provides
    @Singleton
    fun provideAppInstanceSecureStorage(
        dataStore: AppInstanceDataStore,
        cryptoManager: CryptoManager
    ): SecureAppInstanceStorage {
        return SecureAppInstanceStorageImpl(
            dataStore = dataStore,
            cryptoManager = cryptoManager
        )
    }
}