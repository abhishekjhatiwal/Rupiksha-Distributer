package com.rupiksha.distributer.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PincodeResponseDto(
    @SerialName("Message") val message: String?,
    @SerialName("Status") val status: String,
    @SerialName("PostOffice") val postOffices: List<PostOfficeDto>?
)

@Serializable
data class PostOfficeDto(
    @SerialName("Name") val name: String,
    @SerialName("Description") val description: String?,
    @SerialName("BranchType") val branchType: String?,
    @SerialName("DeliveryStatus") val deliveryStatus: String?,
    @SerialName("Circle") val circle: String?,
    @SerialName("District") val district: String,
    @SerialName("Division") val division: String?,
    @SerialName("Region") val region: String?,
    @SerialName("State") val state: String,
    @SerialName("Country") val country: String?,
    @SerialName("Pincode") val pincode: String
)
