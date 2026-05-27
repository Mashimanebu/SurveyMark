package com.survey.mark.domain.model.newmark

import com.survey.mark.domain.model.ObservationMethod
import com.survey.mark.domain.model.ReviewStatus
import com.survey.mark.domain.model.point.ControlPointType
import com.survey.mark.domain.model.status.SyncStatus
import java.time.LocalDateTime

data class NewMarkSubmission(
    val id: Long = 0,
    val proposedName: String,
    val proposedType: ControlPointType,
    val latitude: Double,
    val longitude: Double,
    val ellipsoidalHeight: Double,
    val monumentDescription: String,
    val accessDescription: String,
    val observationMethod: ObservationMethod,
    val observationDurationHours: Double,
    val surveyorLicenceNo: String,
    val surveyorName: String,
    val monumentPhotoUri: String?,
    val sketchPhotoUri: String?,
    val fieldNotes: String,
    val submittedAt: LocalDateTime,
    val syncStatus: SyncStatus,
    val reviewStatus: ReviewStatus
)
