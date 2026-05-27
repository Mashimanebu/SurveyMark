package com.survey.mark.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.survey.mark.domain.model.status.ConditionStatus
import com.survey.mark.domain.model.status.SyncStatus
import java.time.LocalDateTime

@Entity(
    tableName = "condition_reports",
    indices = [Index("controlPointId")]
)
data class ConditionReportEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val controlPointId: String,
    val controlPointName: String,
    val condition: ConditionStatus,
    val fieldNotes: String,
    val photoUri: String?,
    val reporterLicenceNo: String,
    val reporterName: String,
    val verifiedLatitude: Double,
    val verifiedLongitude: Double,
    val gpsAccuracyMeters: Float,
    val reportedAt: LocalDateTime,
    val syncStatus: SyncStatus
)
