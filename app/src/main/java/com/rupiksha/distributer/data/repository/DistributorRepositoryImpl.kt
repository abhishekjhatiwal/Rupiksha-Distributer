package com.rupiksha.distributer.data.repository

import com.rupiksha.distributer.data.remote.api.DistributorApiService
import com.rupiksha.distributer.domain.model.AuthTokens
import com.rupiksha.distributer.domain.model.DashboardData
import com.rupiksha.distributer.domain.model.PincodeInfo
import com.rupiksha.distributer.domain.model.Retailer
import com.rupiksha.distributer.domain.model.UserProfile
import com.rupiksha.distributer.domain.repository.DistributorRepository
import com.rupiksha.distributer.util.Resource
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.delay

class DistributorRepositoryImpl(
    private val apiService: DistributorApiService,
    private val supabase: SupabaseClient
) : DistributorRepository {

    override suspend fun login(username: String, pin: String): Resource<AuthTokens> {
        return try {
            val emailToUse = if (username.contains("@")) {
                // If input is already an email, use it directly
                username
            } else {
                // 1. Lookup email by username in 'app_user_profiles' table
                val profile = supabase.from("app_user_profiles")
                    .select {
                        filter {
                            eq("username", username)
                        }
                    }.decodeSingleOrNull<UserProfile>() ?: return Resource.Error("Username '$username' not found. Please use your email address to log in.")
                profile.email
            }

            // 2. Sign in with the fetched email and pin (as password)
            supabase.auth.signInWith(Email) {
                email = emailToUse
                password = pin
            }

            // 3. Get session
            val session = supabase.auth.currentSessionOrNull()
            if (session != null) {
                Resource.Success(AuthTokens("Authorization", "Bearer ${session.accessToken}"))
            } else {
                Resource.Error("Login failed: Session not established")
            }
        } catch (e: Exception) {
            val message = e.message ?: "An error occurred during login"
            if (message.contains("public.app_user_profiles", ignoreCase = true)) {
                Resource.Error("Username lookup table 'app_user_profiles' is missing. Please run the setup SQL in Supabase or log in using your email address.")
            } else {
                Resource.Error(message)
            }
        }
    }

    override suspend fun getDashboardData(): Resource<DashboardData> {
        return try {
            delay(1000)
            Resource.Success(
                DashboardData(
                    totalRetailers = 42,
                    mtdCommission = 15400.50,
                    walletBalance = 50000.0
                )
            )
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun getRetailers(): Resource<List<Retailer>> {
        return try {
            delay(1000)
            Resource.Success(
                listOf(
                    Retailer("1", "Ravi Kumar", "9876543210", 1500.0, "Ravi Telecom", "Active"),
                    Retailer("2", "Suresh Patel", "9876543211", 500.0, "Patel Mobile Shop", "Active")
                )
            )
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
}
