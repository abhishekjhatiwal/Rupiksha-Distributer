package com.rupiksha.distributer.data.repository

import com.rupiksha.distributer.data.remote.api.DistributorApiService
import com.rupiksha.distributer.domain.model.DashboardData
import com.rupiksha.distributer.domain.model.Retailer
import com.rupiksha.distributer.domain.repository.DistributorRepository
import com.rupiksha.distributer.util.ErrorUtils
import com.rupiksha.distributer.util.Resource
import kotlinx.coroutines.delay
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

class DistributorRepositoryImpl @Inject constructor(
    private val apiService: DistributorApiService
) : DistributorRepository {

    override suspend fun getDashboardData(): Resource<DashboardData> {
        return try {
            delay(1000.milliseconds)
            Resource.Success(
                DashboardData(
                    totalRetailers = 42,
                    mtdCommission = 15400.50,
                    walletBalance = 50000.0
                )
            )
        } catch (e: Exception) {
            Resource.Error(ErrorUtils.sanitizeError(e.message))
        }
    }

    override suspend fun getRetailers(): Resource<List<Retailer>> {
        return try {
            delay(1000.milliseconds)
            Resource.Success(
                listOf(
                    Retailer("1", "Ravi Kumar", "9876543210", 1500.0, "Ravi Telecom", "Active"),
                    Retailer("2", "Suresh Patel", "9876543211", 500.0, "Patel Mobile Shop", "Active")
                )
            )
        } catch (e: Exception) {
            Resource.Error(ErrorUtils.sanitizeError(e.message))
        }
    }
}
