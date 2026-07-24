package com.rupiksha.distributer.data.remote.api

import com.rupiksha.distributer.data.remote.model.PincodeResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class DistributorApiService(private val client: HttpClient) {
    suspend fun login(request: Map<String, String>): Map<String, String> =
        client.post("login") {
            setBody(request)
        }.body()

    suspend fun getDashboard(): Map<String, Any> =
        client.get("dashboard").body()

    suspend fun getPincodeDetails(pincode: String): List<PincodeResponseDto> =
        client.get("https://api.postalpincode.in/pincode/$pincode").body()
}
