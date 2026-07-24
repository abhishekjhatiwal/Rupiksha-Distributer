package com.rupiksha.distributer.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class AuthTokens(
    val headerKey: String,
    val headerToken: String
)

@Serializable
data class UserProfile(
    val username: String,
    val email: String
)

data class DashboardData(
    val totalRetailers: Int,
    val mtdCommission: Double,
    val walletBalance: Double
)

data class Retailer(
    val id: String,
    val name: String,
    val phone: String,
    val wallet: Double,
    val shopName: String,
    val status: String
)

data class Commission(
    val txnId: String,
    val productType: String,
    val commissionAmt: Double
)

data class Transaction(
    val txnId: String,
    val amount: Double,
    val status: String,
    val createdAt: String
)
