package com.rupiksha.distributer.data.repository

import com.rupiksha.distributer.domain.model.AuthTokens
import com.rupiksha.distributer.domain.model.UserProfile
import com.rupiksha.distributer.domain.repository.LoginRepository
import com.rupiksha.distributer.util.ErrorUtils
import com.rupiksha.distributer.util.Resource
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val supabase: SupabaseClient
) : LoginRepository {

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
                Resource.Error(ErrorUtils.sanitizeError(message))
            }
        }
    }
}
