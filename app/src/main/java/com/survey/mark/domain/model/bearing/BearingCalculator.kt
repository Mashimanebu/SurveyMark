package com.survey.mark.domain.model.bearing

import android.location.Location

object BearingCalculator {
    fun distanceMeters(
        fromLat: Double, fromLng: Double,
        toLat: Double, toLng: Double
    ): Double {
        val result = FloatArray(1)
        Location.distanceBetween(fromLat, fromLng, toLat, toLng, result)
        return result[0].toDouble()
    }

    fun bearing(
        fromLat: Double, fromLng: Double,
        toLat: Double, toLng: Double
    ): Float {
        val result = FloatArray(2)
        Location.distanceBetween(fromLat, fromLng, toLat, toLng, result)
        return (result[1] + 360f) % 360f
    }

    fun formatBearing(degrees: Float): String {
        val cardinal = when {
            degrees < 22.5f || degrees >= 337.5f -> "N"
            degrees < 67.5f -> "NE"
            degrees < 112.5f -> "E"
            degrees < 157.5f -> "SE"
            degrees < 202.5f -> "S"
            degrees < 247.5f -> "SW"
            degrees < 292.5f -> "W"
            else -> "NW"
        }
        return "$cardinal ${degrees.toInt()}°"
    }

    fun formatDistance(metres: Double): String = when {
        metres < 10 -> "%.1f m".format(metres)
        metres < 1000 -> "%.0f m".format(metres)
        metres < 10_000 -> "%.2f km".format(metres / 1000.0)
        else -> "%.1f km".format(metres / 1000.0)
    }

    fun isWithinEswatini(lat: Double, lng: Double): Boolean {
        return lat in -27.35..-25.70 && lng in 30.78..32.15
    }

    fun isArrived(distanceMeters: Double, thresholdMeters: Double = 20.0): Boolean =
        distanceMeters <= thresholdMeters
}