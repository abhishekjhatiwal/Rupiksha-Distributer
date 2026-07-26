package com.rupiksha.distributer.domain.model

import android.net.Uri
import kotlinx.serialization.Serializable

data class RegistrationData(
    // Step 1: Personal
    val name: String = "",
    val mobile: String = "",
    val isMobileVerified: Boolean = false,
    val email: String = "",
    
    // Step 2: Business
    val shopName: String = "",
    val shopAddress: String = "",
    val permanentAddress: String = "",
    val pincode: String = "",
    val state: String = "",
    val district: String = "",
    
    // Step 3: KYC & Finance
    val adharNumber: String = "",
    val panNumber: String = "",
    val accountHolderName: String = "",
    val accountNumber: String = "",
    val bankName: String = "",
    val ifscCode: String = "",
    val upiId : String = "",
    
    // Step 4: Media (Uris or Paths)
    val adharFrontUri: Uri? = null,
    val adharBackUri: Uri? = null,
    val panFrontUri: Uri? = null,
    val panBackUri: Uri? = null,
    val additionalDocUri: Uri? = null,
    val additionalDocType: String? = null,
    val photoWithEmployeeUri: Uri? = null,
    val shopPhotoUri: Uri? = null,
    val employeeGpsLocation: String = "",
    val shopGpsLocation: String = "",
    
    // Step 5: Security
    val password: String = "",
    val confirmPassword: String = "",
    val pin: String = "",
    val confirmPin: String = ""
)

@Serializable
data class RetailerEntity(
    val id: String,
    val full_name: String,
    val mobile_number: String,
    val email: String,
    val shop_name: String,
    val shop_address: String,
    val permanent_address: String,
    val pincode: String,
    val state: String,
    val district: String,
    val adhar_number: String,
    val pan_number: String,
    val bank_account_holder: String,
    val bank_account_number: String,
    val bank_name: String,
    val bank_ifsc: String,
    val upi_id: String? = null,
    val pin: String? = null,
    val adhar_front_url: String? = null,
    val adhar_back_url: String? = null,
    val pan_front_url: String? = null,
    val pan_back_url: String? = null,
    val additional_doc_url: String? = null,
    val additional_doc_type: String? = null,
    val photo_with_employee_url: String? = null,
    val shop_photo_url: String? = null,
    val employee_gps_location: String? = null,
    val shop_gps_location: String? = null,
    val onboarding_status: String = "pending"
)

@Serializable
data class RegistrationRequest(
    val name: String,
    val mobile: String,
    val email: String,
    val shopName: String,
    val shopAddress: String,
    val pincode: String,
    val state: String,
    val district: String,
    val adharNumber: String,
    val panNumber: String,
    val permanentAddress: String,
    val accountDetails: BankDetails,
    val gpsLocation: String,
    val password: String,
    val pin: String
)

@Serializable
data class BankDetails(
    val holderName: String,
    val accountNumber: String,
    val bankName: String,
    val ifscCode: String
)
