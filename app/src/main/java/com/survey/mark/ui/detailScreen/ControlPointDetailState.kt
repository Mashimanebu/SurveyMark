package com.survey.mark.ui.detailScreen

import com.survey.mark.domain.model.point.ControlPoint

data class ControlPointDetailState(
    val point: ControlPoint? = null,
    val distanceMetres: Double? = null,
    val bearingDegrees: Double? = null,
    val isLoading: Boolean = true
)

