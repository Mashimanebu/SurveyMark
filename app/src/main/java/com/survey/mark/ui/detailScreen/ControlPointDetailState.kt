package com.survey.mark.ui.detailScreen

import com.survey.mark.domain.model.ControlPoint

data class ControlPointDetailState(
    val point: ControlPoint? = null,
   // val recentReports: List<ConditionReport> = emptyList(),
   // val recentOccupations: List<OccupationRecord> = emptyList(),
    val distanceMetres: Double? = null,
    val bearingDegrees: Double? = null,
    val isLoading: Boolean = true
)

