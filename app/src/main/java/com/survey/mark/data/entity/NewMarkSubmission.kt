package com.survey.mark.data.entity

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.survey.mark.domain.model.ObservationMethod
import com.survey.mark.domain.model.ReviewStatus
import com.survey.mark.domain.model.point.ControlPointType
import com.survey.mark.domain.model.status.SyncStatus
import java.time.LocalDateTime


@Entity(tableName = "new_mark_submissions")
data class NewMarkSubmissionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
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
