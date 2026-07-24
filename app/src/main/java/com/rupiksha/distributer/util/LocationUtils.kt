package com.rupiksha.distributer.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task

object LocationUtils {

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(context: Context, onLocationResult: (String) -> Unit) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    onLocationResult("${location.latitude}, ${location.longitude}")
                } else {
                    onLocationResult("Location not available")
                }
            }
            .addOnFailureListener {
                onLocationResult("Error fetching location")
            }
    }
}
