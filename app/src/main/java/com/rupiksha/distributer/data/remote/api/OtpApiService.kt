package com.rupiksha.distributer.data.remote.api

import com.rupiksha.distributer.BuildConfig.TWO_FACTOR_API_KEY
import com.rupiksha.distributer.data.remote.model.OtpResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class OtpApiService(private val client: HttpClient) {

    // Sanitize the API key: remove whitespace and any "API Key " prefix common in 2Factor dashboard
    private val apiKey = TWO_FACTOR_API_KEY

    suspend fun sendOtp(phoneNumber: String): OtpResponseDto {
        val url = "https://2factor.in/API/V1/$apiKey/SMS/$phoneNumber/AUTOGEN"
        return client.get(url).body()
    }

    suspend fun verifyOtp(sessionId: String, otp: String): OtpResponseDto {
        val url = "https://2factor.in/API/V1/$apiKey/SMS/VERIFY/$sessionId/$otp"
        return client.get(url).body()
    }
}
