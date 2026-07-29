package com.rupiksha.distributer.domain.usecase

import com.rupiksha.distributer.domain.model.PincodeInfo
import com.rupiksha.distributer.domain.repository.RegisterRepository
import com.rupiksha.distributer.util.Resource
import javax.inject.Inject

class GetPincodeDetailsUseCase @Inject constructor(
    private val repository: RegisterRepository
) {
    suspend operator fun invoke(pincode: String): Resource<PincodeInfo> {
        if (pincode.length != 6) {
            return Resource.Error("Invalid pincode")
        }
        return repository.getPincodeDetails(pincode)
    }
}
