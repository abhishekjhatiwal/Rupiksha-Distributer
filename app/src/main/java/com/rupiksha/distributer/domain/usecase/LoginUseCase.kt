package com.rupiksha.distributer.domain.usecase

import com.rupiksha.distributer.domain.model.AuthTokens
import com.rupiksha.distributer.domain.repository.LoginRepository
import com.rupiksha.distributer.util.Resource
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: LoginRepository
) {
    suspend operator fun invoke(username: String, pin: String): Resource<AuthTokens> {
        if(username.isBlank() || pin.isBlank()) {
            return Resource.Error("Username or PIN cannot be empty")
        }
        return repository.login(username, pin)
    }
}
