package com.rupiksha.distributer.domain.repository

import com.rupiksha.distributer.util.Resource

interface OtpRepository {
    suspend fun sendOtp(phoneNumber: String): Resource<String>
    suspend fun verifyOtp(sessionId: String, otp: String): Resource<Boolean>
}
