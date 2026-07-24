package com.rupiksha.distributer.domain.usecase

import com.rupiksha.distributer.domain.repository.OtpRepository
import com.rupiksha.distributer.util.Resource

class SendOtpUseCase(private val repository: OtpRepository) {
    suspend operator fun invoke(phoneNumber: String): Resource<String> =
        repository.sendOtp(phoneNumber)
}

class VerifyOtpUseCase(private val repository: OtpRepository) {
    suspend operator fun invoke(sessionId: String, otp: String): Resource<Boolean> =
        repository.verifyOtp(sessionId, otp)
}
