package com.rupiksha.distributer.domain.repository

import com.rupiksha.distributer.domain.model.PincodeInfo
import com.rupiksha.distributer.util.Resource

interface RegisterRepository {
    suspend fun getPincodeDetails(pincode: String): Resource<PincodeInfo>
}
