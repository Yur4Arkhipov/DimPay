package com.example.dimpay.core.data.remote.service

import com.example.dimpay.core.data.remote.dto.AppInstanceDto
import retrofit2.http.POST

interface CustomerApi {

    @POST("customer/appinstance")
    suspend fun setAppInstance(): AppInstanceDto
}