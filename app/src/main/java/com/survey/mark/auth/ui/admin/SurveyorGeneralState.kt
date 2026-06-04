package com.survey.mark.auth.ui.admin

import com.survey.mark.auth.domain.SurveyUser
import com.survey.mark.domain.model.point.ControlPoint


data class SurveyorGeneralState(
    val currentUser: SurveyUser? = null,
    val pendingUsers: List<SurveyUser> = emptyList(),
    val allUsers: List<SurveyUser> = emptyList(),
    val activeCount: Int = 0,
    val rejectedCount: Int = 0,
    val suspendedCount: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val allPoints: List<ControlPoint> = emptyList(),
    val destroyedPoints: List<ControlPoint> = emptyList(),
    val newPoints: List<ControlPoint> = emptyList(),
    val pendingPoints: List<ControlPoint> = emptyList(),
)

enum class AdminTab { OVERVIEW, ALL_USERS, PENDING, CONTROL_POINTS }