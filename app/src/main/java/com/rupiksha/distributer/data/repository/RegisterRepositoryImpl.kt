package com.rupiksha.distributer.data.repository

import com.rupiksha.distributer.data.remote.api.DistributorApiService
import com.rupiksha.distributer.domain.model.PincodeInfo
import com.rupiksha.distributer.domain.repository.RegisterRepository
import com.rupiksha.distributer.util.Resource

class RegisterRepositoryImpl(
    private val apiService: DistributorApiService
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
            Resource.Error(e.message ?: "An error occurred fetching pincode details")
        }
    }
}
