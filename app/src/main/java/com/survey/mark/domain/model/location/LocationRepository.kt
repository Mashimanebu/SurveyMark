package com.survey.mark.domain.model.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val client: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val highAccuracyRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY,
        1_000L
    ).apply {
        setMinUpdateIntervalMillis(500L)
        setMaxUpdateDelayMillis(2_000L)
        setWaitForAccurateLocation(false)
    }.build()

    private val balancedRequest = LocationRequest.Builder(
        Priority.PRIORITY_BALANCED_POWER_ACCURACY,
        5_000L
    ).apply {
        setMinUpdateIntervalMillis(2_000L)
    }.build()

    @SuppressLint("MissingPermission")
    fun observeLocation(highAccuracy: Boolean = false): Flow<LocationState> = callbackFlow {
        val request = if (highAccuracy) highAccuracyRequest else balancedRequest

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val loc = result.lastLocation ?: return
                val state = loc.toState()
                Timber.v(
                    "Location update: %.6f, %.6f ±%.1fm",
                    loc.latitude,
                    loc.longitude,
                    loc.accuracy
                )
                trySend(state)
            }

            override fun onLocationAvailability(availability: LocationAvailability) {
                Timber.d("Location available: ${availability.isLocationAvailable}")
            }
        }

        try {
            client.requestLocationUpdates(request, callback, Looper.getMainLooper())
        } catch (e: SecurityException) {
            Timber.e(e, "Location permission missing")
            close(e)
        }

        awaitClose {
            client.removeLocationUpdates(callback)
            Timber.d("Stopped location updates")
        }
    }.distinctUntilChanged { old, new ->
        val dist = FloatArray(1)
        Location.distanceBetween(
            old.latitude, old.longitude,
            new.latitude, new.longitude,
            dist
        )
        dist[0] < 0.5f && Math.abs(old.accuracyMeters - new.accuracyMeters) < 2f
    }

    @SuppressLint("MissingPermission")
    suspend fun getLastKnownLocation(): LocationState? {
        return try {
            val loc = com.google.android.gms.tasks.Tasks.await(client.lastLocation)
            loc?.toState()
        } catch (e: Exception) {
            Timber.w(e, "Could not get last known location")
            null
        }
    }

    private fun Location.toState() = LocationState(
        latitude = latitude,
        longitude = longitude,
        accuracyMeters = accuracy,
        altitudeMeters = if (hasAltitude()) altitude else null,
        bearingDegrees = if (hasBearing()) bearing else null,
        speedMps = if (hasSpeed()) speed else null,
        isFromGps = provider == "gps"
    )
}