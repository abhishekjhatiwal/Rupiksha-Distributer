package com.rupiksha.distributer.data.repository

import android.content.Context
import android.util.Log
import com.rupiksha.distributer.data.remote.api.DistributorApiService
import com.rupiksha.distributer.domain.model.PincodeInfo
import com.rupiksha.distributer.domain.model.RegistrationData
import com.rupiksha.distributer.domain.model.RetailerEntity
import com.rupiksha.distributer.domain.repository.RegisterRepository
import com.rupiksha.distributer.util.ErrorUtils
import com.rupiksha.distributer.util.Resource
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage

class RegisterRepositoryImpl(
    private val apiService: DistributorApiService,
    private val supabase: SupabaseClient,
    private val context: Context
) : RegisterRepository {

    override suspend fun getPincodeDetails(pincode: String): Resource<PincodeInfo> {
        return try {
            val response = apiService.getPincodeDetails(pincode)
            if (response.isNotEmpty() && response[0].status == "Success") {
                val postOffice = response[0].postOffices?.firstOrNull()
                if (postOffice != null) {
                    Resource.Success(
                        PincodeInfo(
                            state = postOffice.state,
                            district = postOffice.district
                        )
                    )
                } else {
                    Resource.Error("No post office found for this pincode")
                }
            } else {
                Resource.Error(response.firstOrNull()?.message ?: "Invalid pincode or API error")
            }
        } catch (e: Exception) {
            Resource.Error(ErrorUtils.sanitizeError(e.message))
        }
    }

    override suspend fun registerRetailer(data: RegistrationData): Resource<Unit> {
        Log.d("RegisterRepo", "Starting registration for: ${data.email}")
        return try {
            // 1. Sign up user in Supabase Auth
            Log.d("RegisterRepo", "Step 1: Signing up in Supabase Auth")
            val response = supabase.auth.signUpWith(Email) {
                email = data.email
                password = data.password
            }

            val userId = response?.id 
                ?: supabase.auth.currentUserOrNull()?.id
                ?: return Resource.Error("Sign up failed: User ID not found in response. If you just disabled 'Confirm Email', please restart the app.")
            
            Log.d("RegisterRepo", "User signed up successfully. ID: $userId")

            // 2. Upload images to Supabase Storage
            Log.d("RegisterRepo", "Step 2: Uploading images...")
            val adharFrontUrl = data.adharFrontUri?.let { uploadImage(it, userId, "adhar_front.jpg") }
            val adharBackUrl = data.adharBackUri?.let { uploadImage(it, userId, "adhar_back.jpg") }
            val panFrontUrl = data.panFrontUri?.let { uploadImage(it, userId, "pan_front.jpg") }
            val panBackUrl = data.panBackUri?.let { uploadImage(it, userId, "pan_back.jpg") }
            val additionalDocUrl = data.additionalDocUri?.let { uploadImage(it, userId, "additional_doc.jpg") }
            val photoWithEmployeeUrl = data.photoWithEmployeeUri?.let { uploadImage(it, userId, "photo_with_employee.jpg") }
            val shopPhotoUrl = data.shopPhotoUri?.let { uploadImage(it, userId, "shop_photo.jpg") }

            // 3. Insert retailer profile into the 'retailers' table
            Log.d("RegisterRepo", "Step 3: Inserting retailer profile into 'retailers' table")
            val entity = RetailerEntity(
                id = userId,
                full_name = data.name,
                mobile_number = data.mobile,
                email = data.email,
                shop_name = data.shopName,
                shop_address = data.shopAddress,
                permanent_address = data.permanentAddress,
                pincode = data.pincode,
                state = data.state,
                district = data.district,
                adhar_number = data.adharNumber,
                pan_number = data.panNumber,
                bank_account_holder = data.accountHolderName,
                bank_account_number = data.accountNumber,
                bank_name = data.bankName,
                bank_ifsc = data.ifscCode,
                upi_id = data.upiId,
                pin = data.pin,
                adhar_front_url = adharFrontUrl,
                adhar_back_url = adharBackUrl,
                pan_front_url = panFrontUrl,
                pan_back_url = panBackUrl,
                additional_doc_url = additionalDocUrl,
                additional_doc_type = data.additionalDocType,
                photo_with_employee_url = photoWithEmployeeUrl,
                shop_photo_url = shopPhotoUrl,
                employee_gps_location = data.employeeGpsLocation,
                shop_gps_location = data.shopGpsLocation
            )

            supabase.from("retailers").insert(entity)
            Log.d("RegisterRepo", "Registration complete!")

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(ErrorUtils.sanitizeError(e.message))
        }
    }

    private suspend fun uploadImage(uri: android.net.Uri, userId: String, fileName: String): String {
        Log.d("RegisterRepo", "Uploading image: $fileName")
        val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            ?: throw Exception("Could not read image data for $fileName")

        val path = "$userId/$fileName"
        supabase.storage.from("retailer-docs").upload(path, bytes) {
            upsert = true
        }
        Log.d("RegisterRepo", "Image uploaded: $path")
        return path
    }
}
