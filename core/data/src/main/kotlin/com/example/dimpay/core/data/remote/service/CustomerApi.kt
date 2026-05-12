package com.example.dimpay.core.data.remote.service

import com.example.dimpay.core.data.remote.dto.AddCardRequest
import com.example.dimpay.core.data.remote.dto.AddCardResponse
import com.example.dimpay.core.data.remote.dto.AppInstanceDto
import com.example.dimpay.core.data.remote.dto.CancelPaymentRequest
import com.example.dimpay.core.data.remote.dto.CancelPaymentResponse
import com.example.dimpay.core.data.remote.dto.ConfirmPaymentRequest
import com.example.dimpay.core.data.remote.dto.ConfirmPaymentResponse
import com.example.dimpay.core.data.remote.dto.ConfirmationDetailsResponse
import com.example.dimpay.core.data.remote.dto.PaymentTokenDto
import com.example.dimpay.core.data.remote.dto.PaymentTokenResponse
import com.example.dimpay.core.data.remote.dto.QRRequest
import com.example.dimpay.core.data.remote.dto.QRResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

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

    @GET("customer/confirmationdetails")
    suspend fun getConfirmationDetails(
        @Query("sessionId") sessionId: String
    ): ConfirmationDetailsResponse

    @POST("customer/cancel")
    suspend fun cancelPayment(
        @Body request: CancelPaymentRequest
    ): Response<Unit>

    @POST("customer/confirm")
    suspend fun confirmPayment(
        @Body request: ConfirmPaymentRequest
    ): Response<Unit>

    @GET("customer/tokens")
    suspend fun getTokens(
        @Query("cardInstanceId")
        cardInstance: String
    ): Response<PaymentTokenResponse>
}