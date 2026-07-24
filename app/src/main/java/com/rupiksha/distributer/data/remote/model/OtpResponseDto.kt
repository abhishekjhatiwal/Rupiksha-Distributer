package com.rupiksha.distributer.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OtpResponseDto(
    @SerialName("Status") val status: String,
    @SerialName("Details") val details: String
)
