package com.survey.mark.ui.home

import com.survey.mark.domain.model.ControlPoint

data class HomeState(
    val totalPoints: Int = 0,
    val intactCount: Int = 0,
    val destroyedCount: Int = 0,
    val disturbedCount: Int = 0,
    val notFoundCount: Int = 0,
    val unknownCount: Int = 0,
    val pendingReports: Int = 0,
    val pendingOccupations: Int = 0,
    val pendingNewMarks: Int = 0,
    val recentPoints: List<ControlPoint> = emptyList(),
    val surveyorName: String = ""
)