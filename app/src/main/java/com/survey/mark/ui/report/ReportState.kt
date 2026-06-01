package com.survey.mark.ui.report

import com.survey.mark.domain.model.location.LocationState
import com.survey.mark.domain.model.point.ControlPoint
import com.survey.mark.domain.model.status.ConditionStatus


data class ReportState(
    val allPoints: List<ControlPoint> = emptyList(),
    val selectedPoint: ControlPoint? = null,
    val condition: ConditionStatus? = null,
    val fieldNotes: String = "",
    val photoUri: String? = null,
    val reporterName: String = "",
    val reporterLicenceNo: String = "",
    val location: LocationState? = null,
    val isSubmitting: Boolean = false,
    val submitSuccess: Boolean = false,
    val errorMessage: String? = null
)