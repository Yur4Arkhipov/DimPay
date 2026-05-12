package com.example.dimpay.core.data.repository

import com.example.dimpay.core.data.datastore.AppInstanceDataStore
import com.example.dimpay.core.data.remote.service.CustomerApi
import com.example.dimpay.core.domain.repository.AppInstanceRepository
import javax.inject.Inject

class AppInstanceRepositoryImpl @Inject constructor(
    private val api: CustomerApi,
    private val dataStore: AppInstanceDataStore
) : AppInstanceRepository {

    override suspend fun getOrFetchToken(): String {
        val cached = dataStore.getToken()
        if (cached != null) return cached
        val response = try {
            api.setAppInstance()
        } catch (e: Exception) {
            throw NoInternetException()
        }
        if (!response.success) {
            throw AppInstanceException("Server rejected app instance")
        }
        val token = response.response
        dataStore.saveToken(token)
        return token
    }

    override suspend fun hasToken(): Boolean {
        return dataStore.getToken() != null
    }

    override suspend fun clearToken() {
        dataStore.clear()
    }
}

class NoInternetException : Exception()
class AppInstanceException(message: String) : Exception(message)