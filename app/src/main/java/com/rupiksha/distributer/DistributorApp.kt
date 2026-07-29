package com.rupiksha.distributer

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class DistributorApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
