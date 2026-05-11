package com.example.dimpay.core.data.repository

import com.example.dimpay.core.domain.repository.AppInstanceRepository
import kotlinx.coroutines.delay
import javax.inject.Inject

class AppInstanceRepositoryImpl @Inject constructor() : AppInstanceRepository {

    private var cachedToken: String? = null
    var hasInternet: Boolean = false

    override suspend fun getOrFetchToken(): String {

        cachedToken?.let { return it }

        simulateNetworkDelay()

        if (!hasInternet) {
            throw NoInternetException()
        }

        val newToken = generateFakeToken()
        cachedToken = newToken

        return newToken
    }

    override suspend fun hasToken(): Boolean {
        return cachedToken != null
    }

    override suspend fun clearToken() {
        cachedToken = null
    }

    private suspend fun simulateNetworkDelay() {
        delay(800)
    }

    private fun generateFakeToken(): String {
        return "app_instance_" + java.util.UUID.randomUUID().toString()
    }
}

class NoInternetException : Exception("No internet connection")