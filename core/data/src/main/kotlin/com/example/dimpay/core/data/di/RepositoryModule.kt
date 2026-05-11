package com.example.dimpay.core.data.di

import com.example.dimpay.core.data.repository.AppInstanceRepositoryImpl
import com.example.dimpay.core.domain.repository.AppInstanceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindAppInstanceRepository(
        impl: AppInstanceRepositoryImpl
    ): AppInstanceRepository
}