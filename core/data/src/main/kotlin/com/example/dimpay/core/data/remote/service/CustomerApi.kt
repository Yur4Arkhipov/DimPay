package com.example.dimpay.core.data.remote.service

import com.example.dimpay.core.data.remote.dto.AddCardRequest
import com.example.dimpay.core.data.remote.dto.AddCardResponse
import com.example.dimpay.core.data.remote.dto.AppInstanceDto
import com.example.dimpay.core.data.remote.dto.QRRequest
import com.example.dimpay.core.data.remote.dto.QRResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface CustomerApi {

    @POST("customer/appinstance")
    suspend fun setAppInstance(): AppInstanceDto

    @POST("customer/paymentcard")
    suspend fun addPaymentCard(
        @Body request: AddCardRequest
    ): AddCardResponse

    @POST("customer/onlinepayment")
    suspend fun generateQR(
        @Body request: QRRequest
    ): QRResponse
}