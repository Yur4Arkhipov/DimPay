package com.example.dimpay.core.data.repository

import com.example.dimpay.core.data.remote.service.CustomerApi
import com.example.dimpay.core.domain.repository.AppInstanceRepository
import com.example.dimpay.core.domain.secure.SecureAppInstanceStorage
import javax.inject.Inject

class AppInstanceRepositoryImpl @Inject constructor(
    private val api: CustomerApi,
    private val secureStorage: SecureAppInstanceStorage
) : AppInstanceRepository {

    override suspend fun getOrFetchToken(): String {
        val cached = secureStorage.getToken()
        if (cached != null) return cached
        val response = try {
            api.setAppInstance()
        } catch (e: Exception) {
            throw NoInternetException()
        }
        if (!response.success) {
            throw AppInstanceException(
                "Server rejected app instance"
            )
        }
        val token = response.response
        secureStorage.saveToken(token)

        return token
    }
}

class NoInternetException : Exception()
class AppInstanceException(message: String) : Exception(message)