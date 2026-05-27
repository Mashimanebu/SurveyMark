package com.survey.mark.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.survey.mark.domain.model.log.OccupationType
import com.survey.mark.domain.model.status.SyncStatus
import java.time.LocalDateTime

@Entity(
    tableName = "occupation_logs",
    indices = [Index("controlPointId")]
)
data class OccupationLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val controlPointId: String,
    val controlPointName: String,
    val surveyorLicenceNo: String,
    val surveyorName: String,
    val equipmentType: String,
    val equipmentSerialNo: String,
    val occupationType: OccupationType,
    val sessionStartTime: LocalDateTime,
    val sessionEndTime: LocalDateTime,
    val purposeNotes: String,
    val meanSolutionLatitude: Double?,
    val meanSolutionLongitude: Double?,
    val meanSolutionHeight: Double?,
    val pdop: Float?,
    val syncStatus: SyncStatus
)
