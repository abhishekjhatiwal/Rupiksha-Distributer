package com.rupiksha.distributer.data.remote.api

import com.rupiksha.distributer.data.remote.model.OtpResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class OtpApiService(private val client: HttpClient) {
    
    private val apiKey = "TWO_FACTOR_API_KEY" // Placeholder, should ideally come from BuildConfig

    suspend fun sendOtp(phoneNumber: String): OtpResponseDto =
        client.get("https://2factor.in/API/V1/$apiKey/SMS/$phoneNumber/AUTOGEN").body()

    suspend fun verifyOtp(sessionId: String, otp: String): OtpResponseDto =
        client.get("https://2factor.in/API/V1/$apiKey/SMS/VERIFY/$sessionId/$otp").body()
}
