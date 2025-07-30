package com.example.aa_example.model

import android.location.Location

data class PlaceInfo(
    val type: PlaceType,
    val name: String,
    val latitude: Double,
    val longitude: Double,
) {
    fun distance(currentLat: Double, currentLng: Double): Double {
        val results = FloatArray(size = 1)
        Location.distanceBetween(
            currentLat,
            currentLng,
            latitude,
            longitude,
            results,
        )
        return results.first() / 1000.0
    }
}
