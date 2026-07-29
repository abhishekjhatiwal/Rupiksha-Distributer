package com.rupiksha.distributer.data.repository

import android.util.Log
import com.rupiksha.distributer.data.remote.api.OtpApiService
import com.rupiksha.distributer.domain.repository.OtpRepository
import com.rupiksha.distributer.util.ErrorUtils
import com.rupiksha.distributer.util.Resource
import javax.inject.Inject

class OtpRepositoryImpl @Inject constructor(private val apiService: OtpApiService) : OtpRepository {
    
    override suspend fun sendOtp(phoneNumber: String): Resource<String> {
        return try {
            Log.d("OtpRepository", "Sending OTP to $phoneNumber")
            val response = apiService.sendOtp(phoneNumber)
            Log.d("OtpRepository", "Response: status=${response.status}, details=${response.details}")
            if (response.status == "Success") {
                Resource.Success(response.details) // Session ID
            } else {
                Resource.Error(response.details)
            }
        } catch (e: Exception) {
            Log.e("OtpRepository", "Error sending OTP", e)
            Resource.Error(ErrorUtils.sanitizeError(e.message))
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
            Resource.Error(ErrorUtils.sanitizeError(e.message))
        }
    }
}
