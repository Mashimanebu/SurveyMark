package com.survey.mark.domain.model.location

data class LocationState(
    val latitude: Double,
    val longitude: Double,
    val accuracyMeters: Float,
    val altitudeMeters: Double?,
    val bearingDegrees: Float?,
    val speedMps: Float?,
    val isFromGps: Boolean
)
