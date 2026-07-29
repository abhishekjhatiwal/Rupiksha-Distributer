package com.rupiksha.distributer.domain.repository

import com.rupiksha.distributer.domain.model.AuthTokens
import com.rupiksha.distributer.util.Resource

interface LoginRepository {
    suspend fun login(username: String, pin: String): Resource<AuthTokens>
}
