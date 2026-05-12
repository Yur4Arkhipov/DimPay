package com.example.dimpay.core.data.repository

import com.example.dimpay.core.data.remote.dto.toDomain
import com.example.dimpay.core.data.remote.service.CustomerApi
import com.example.dimpay.core.domain.model.PaymentToken
import com.example.dimpay.core.domain.repository.TokenRepository
import retrofit2.HttpException
import javax.inject.Inject

class TokenRepositoryImpl @Inject constructor(
    private val api: CustomerApi
) : TokenRepository {

    override suspend fun getTokens(
        cardInstance: String
    ): List<PaymentToken> {
        val response = api.getTokens(cardInstance)
        if (!response.isSuccessful) {
            throw HttpException(response)
        }
        val body = response.body()
            ?: throw IllegalStateException("Tokens response body is null")
        return body.response.map { dto ->
            dto.toDomain()
        }
    }
}