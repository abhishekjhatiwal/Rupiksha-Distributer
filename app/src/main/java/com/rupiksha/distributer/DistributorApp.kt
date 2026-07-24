package com.rupiksha.distributer

import android.app.Application
import com.rupiksha.distributer.di.AppContainer

class DistributorApp : Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = AppContainer()
    }
}
