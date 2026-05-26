package com.survey.mark.domain.model.log

import com.survey.mark.domain.model.status.SyncStatus
import java.time.LocalDateTime
data class OccupationLog(
    val id: Long = 0,
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
) {
    val durationMinutes: Long get() =
        java.time.Duration.between(sessionStartTime, sessionEndTime).toMinutes()
}

enum class OccupationType(val label: String) {
    CADASTRAL_BASE("Cadastral Survey — Base Station"),
    ENGINEERING("Engineering Survey"),
    TOPOGRAPHIC("Topographic Survey"),
    NETWORK_CHECK("Network Adjustment Check"),
    CONDITION_VERIFY("Condition Verification"),
    MONITORING("Structural Monitoring"),
    OTHER("Other")
}