package com.rupiksha.distributer.data.repository

import com.rupiksha.distributer.data.remote.api.OtpApiService
import com.rupiksha.distributer.domain.repository.OtpRepository
import com.rupiksha.distributer.util.Resource

class OtpRepositoryImpl(private val apiService: OtpApiService) : OtpRepository {
    
    override suspend fun sendOtp(phoneNumber: String): Resource<String> {
        return try {
            val response = apiService.sendOtp(phoneNumber)
            if (response.status == "Success") {
                Resource.Success(response.details) // Session ID
            } else {
                Resource.Error(response.details)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred while sending OTP")
        }
    }

    override suspend fun verifyOtp(sessionId: String, otp: String): Resource<Boolean> {
        return try {
            val response = apiService.verifyOtp(sessionId, otp)
            if (response.status == "Success" && response.details == "OTP Matched") {
                Resource.Success(true)
            } else {
                Resource.Error(response.details)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred while verifying OTP")
        }
    }
}
