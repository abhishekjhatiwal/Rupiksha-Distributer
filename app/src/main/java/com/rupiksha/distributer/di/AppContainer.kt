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
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import android.content.Context
import android.util.Log
import io.github.jan.supabase.storage.Storage
import kotlinx.serialization.json.Json

class AppContainer(private val context: Context) {

    val supabaseClient: SupabaseClient = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_KEY
    ) {
        install(Auth)
        install(Postgrest)
        install(Storage)
    }

    private val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    Log.d("Ktor", message)
                }
            }
            level = LogLevel.ALL
        }
        defaultRequest {
            // Remove mock base URL if we are using full URLs in services
            // url("https://api.rupiksha.com/")
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
        RegisterRepositoryImpl(apiService, supabaseClient, context)
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
