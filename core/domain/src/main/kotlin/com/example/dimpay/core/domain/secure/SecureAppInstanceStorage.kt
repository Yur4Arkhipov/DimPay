package com.example.dimpay.core.domain.secure

interface SecureAppInstanceStorage {
    suspend fun saveToken(token: String)
    suspend fun getToken(): String?
    suspend fun clear()
}