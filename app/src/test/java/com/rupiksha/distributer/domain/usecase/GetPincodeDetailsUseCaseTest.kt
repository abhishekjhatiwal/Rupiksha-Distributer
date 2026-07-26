package com.rupiksha.distributer.domain.usecase

import com.rupiksha.distributer.domain.model.PincodeInfo
import com.rupiksha.distributer.domain.model.RegistrationData
import com.rupiksha.distributer.domain.repository.RegisterRepository
import com.rupiksha.distributer.util.Resource
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetPincodeDetailsUseCaseTest {

    private val repository = object : RegisterRepository {
        var pincodeResult: Resource<PincodeInfo> = Resource.Error("Not initialized")
        
        override suspend fun getPincodeDetails(pincode: String): Resource<PincodeInfo> = pincodeResult

        override suspend fun registerRetailer(data: RegistrationData): Resource<Unit> {
            return Resource.Error("Not implemented")
        }
    }
    
    private val useCase = GetPincodeDetailsUseCase(repository)

    @Test
    fun `invoke with valid pincode returns success`() = runBlocking {
        val pincode = "110001"
        val expectedInfo = PincodeInfo("Delhi", "New Delhi")
        repository.pincodeResult = Resource.Success(expectedInfo)

        val result = useCase(pincode)

        assertTrue(result is Resource.Success)
        assertEquals(expectedInfo, (result as Resource.Success).data)
    }

    @Test
    fun `invoke with invalid pincode length returns error`() = runBlocking {
        val pincode = "11000"
        
        val result = useCase(pincode)

        assertTrue(result is Resource.Error)
        assertEquals("Invalid pincode", (result as Resource.Error).message)
    }
}
