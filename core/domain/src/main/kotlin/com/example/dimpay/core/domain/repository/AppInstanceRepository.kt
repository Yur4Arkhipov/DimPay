package com.example.dimpay.core.domain.repository

interface AppInstanceRepository {
    suspend fun getOrFetchToken(): String
    suspend fun hasToken(): Boolean
    suspend fun clearToken()
}