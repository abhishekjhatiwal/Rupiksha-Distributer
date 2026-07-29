package com.rupiksha.distributer.domain.repository

import com.rupiksha.distributer.domain.model.*
import com.rupiksha.distributer.util.Resource

interface DistributorRepository {
    suspend fun getDashboardData(): Resource<DashboardData>
    suspend fun getRetailers(): Resource<List<Retailer>>
}
