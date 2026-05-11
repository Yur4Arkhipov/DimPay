package com.example.dimpay.core.data.di

import com.example.dimpay.core.data.auth.BiometricAuthenticatorImpl
import com.example.dimpay.core.data.auth.SessionManagerImpl
import com.example.dimpay.core.domain.auth.BiometricAuthenticator
import com.example.dimpay.core.domain.auth.SessionManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    @Binds
    @Singleton
    abstract fun bindSessionManager(impl: SessionManagerImpl): SessionManager

    @Binds
    @Singleton
    abstract fun bindBiometricAuthenticator(impl: BiometricAuthenticatorImpl): BiometricAuthenticator
}