package com.survey.mark.ui.field

import com.survey.mark.domain.model.location.LocationState
import com.survey.mark.domain.model.point.ControlPoint

data class FieldNavState(
    val targetPoint: ControlPoint? = null,
    val userLocation: LocationState? = null,
    val bearingDeg: Float = 0f,
    val trueBearingDeg: Float = 0f,
    val distanceMeters: Double = 0.0,
    val isArrived: Boolean = false
)