package com.rupiksha.distributer.di

import com.rupiksha.distributer.data.repository.DistributorRepositoryImpl
import com.rupiksha.distributer.data.repository.LoginRepositoryImpl
import com.rupiksha.distributer.data.repository.OtpRepositoryImpl
import com.rupiksha.distributer.data.repository.RegisterRepositoryImpl
import com.rupiksha.distributer.domain.repository.DistributorRepository
import com.rupiksha.distributer.domain.repository.LoginRepository
import com.rupiksha.distributer.domain.repository.OtpRepository
import com.rupiksha.distributer.domain.repository.RegisterRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindLoginRepository(
        loginRepositoryImpl: LoginRepositoryImpl
    ): LoginRepository

    @Binds
    @Singleton
    abstract fun bindDistributorRepository(
        distributorRepositoryImpl: DistributorRepositoryImpl
    ): DistributorRepository

    @Binds
    @Singleton
    abstract fun bindOtpRepository(
        otpRepositoryImpl: OtpRepositoryImpl
    ): OtpRepository

    @Binds
    @Singleton
    abstract fun bindRegisterRepository(
        registerRepositoryImpl: RegisterRepositoryImpl
    ): RegisterRepository
}
