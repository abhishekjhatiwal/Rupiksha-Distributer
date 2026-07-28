package com.rupiksha.distributer

import android.app.Application
import com.rupiksha.distributer.di.AppModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class DistributorApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@DistributorApp)
            modules(AppModule)
        }
    }
}
