package com.rupiksha.distributer.di

import android.util.Log
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
import com.rupiksha.distributer.domain.usecase.LoginUseCase
import com.rupiksha.distributer.domain.usecase.SendOtpUseCase
import com.rupiksha.distributer.domain.usecase.VerifyOtpUseCase
import com.rupiksha.distributer.presentation.auth.login.LoginViewModel
import com.rupiksha.distributer.presentation.auth.register.RegistrationViewModel
import com.rupiksha.distributer.presentation.dashboard.DashboardViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.*
import org.koin.dsl.module

val AppModule = module {
    single<SupabaseClient> {
        createSupabaseClient(
            supabaseUrl = SUPABASE_URL,
            supabaseKey = SUPABASE_KEY
        ) {
            install(Auth)
            install(Postgrest)
            install(Storage)
        }
    }

    single<HttpClient> {
        HttpClient(OkHttp) {
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
        }
    }

    singleOf(::DistributorApiService)
    singleOf(::OtpApiService)

    singleOf(::DistributorRepositoryImpl) { bind<DistributorRepository>() }
    singleOf(::OtpRepositoryImpl) { bind<OtpRepository>() }
    singleOf(::RegisterRepositoryImpl) { bind<RegisterRepository>() }

    factoryOf(::GetPincodeDetailsUseCase)
    factoryOf(::SendOtpUseCase)
    factoryOf(::VerifyOtpUseCase)
    factoryOf(::LoginUseCase)

    viewModelOf(::LoginViewModel)
    viewModelOf(::RegistrationViewModel)
    viewModelOf(::DashboardViewModel)
}
