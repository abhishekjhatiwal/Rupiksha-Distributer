package com.rupiksha.distributer.di

import com.rupiksha.distributer.BuildConfig.SUPABASE_KEY
import com.rupiksha.distributer.BuildConfig.SUPABASE_URL
import com.rupiksha.distributer.data.remote.api.DistributorApiService
import com.rupiksha.distributer.data.remote.api.OtpApiService
import com.rupiksha.distributer.data.repository.DistributorRepositoryImpl
import com.rupiksha.distributer.data.repository.OtpRepositoryImpl
import com.rupiksha.distributer.data.repository.RegisterRepositoryImpl
import com.rupiksha.distributer.domain.repository.DistributorRepository
import com.rupiksha.distributer.domain.repository.OtpRepository
import com.rupiksha.distributer.domain.repository.RegisterRepository
import com.rupiksha.distributer.domain.usecase.GetPincodeDetailsUseCase
import com.rupiksha.distributer.domain.usecase.SendOtpUseCase
import com.rupiksha.distributer.domain.usecase.VerifyOtpUseCase
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class AppContainer {

    val supabaseClient: SupabaseClient = createSupabaseClient(
        supabaseUrl = SUPABASE_URL, // Replace with your URL or use BuildConfig
        supabaseKey = SUPABASE_KEY // Replace with your Key or use BuildConfig
    ) {
        install(Auth)
        install(Postgrest)
    }

    private val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        defaultRequest {
            url("https://api.example.com/") // Mock base URL
        }
    }

    private val apiService = DistributorApiService(client)
    private val otpApiService = OtpApiService(client)

    val repository: DistributorRepository by lazy {
        DistributorRepositoryImpl(apiService, supabaseClient)
    }

    val otpRepository: OtpRepository by lazy {
        OtpRepositoryImpl(otpApiService)
    }

    val registerRepository: RegisterRepository by lazy {
        RegisterRepositoryImpl(apiService)
    }

    val getPincodeDetailsUseCase: GetPincodeDetailsUseCase by lazy {
        GetPincodeDetailsUseCase(registerRepository)
    }

    val sendOtpUseCase: SendOtpUseCase by lazy {
        SendOtpUseCase(otpRepository)
    }

    val verifyOtpUseCase: VerifyOtpUseCase by lazy {
        VerifyOtpUseCase(otpRepository)
    }
}
