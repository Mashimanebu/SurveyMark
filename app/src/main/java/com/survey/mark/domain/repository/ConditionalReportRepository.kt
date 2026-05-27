package com.survey.mark.domain.repository

import com.survey.mark.data.dao.ConditionReportDao
import com.survey.mark.data.dao.ControlPointDao
import com.survey.mark.data.mappers.toDomain
import com.survey.mark.data.mappers.toEntity
import com.survey.mark.data.remote.ConditionReportRequestDto
import com.survey.mark.di.SurveyMarkApi
import com.survey.mark.domain.model.report.ConditionReport
import com.survey.mark.domain.model.status.SyncStatus
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Singleton
class ConditionReportRepository @Inject constructor(
    private val reportDao: ConditionReportDao,
    private val cpDao: ControlPointDao,
    private val api: SurveyMarkApi
) {
    fun observeAll(): Flow<List<ConditionReport>> =
        reportDao.observeAll().map { it.map { e -> e.toDomain() } }

    fun observeForControlPoint(cpId: String): Flow<List<ConditionReport>> =
        reportDao.observeByControlPoint(cpId).map { it.map { e -> e.toDomain() } }

    suspend fun submitReport(report: ConditionReport): Long {
        val entity = report.copy(
            reportedAt = LocalDateTime.now(),
            syncStatus = SyncStatus.PENDING
        ).toEntity()
        val id = reportDao.insert(entity)

        cpDao.updateCondition(
            id = report.controlPointId,
            condition = report.condition,
            date = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        )

        trySync(id, report)
        return id
    }

    private suspend fun trySync(id: Long, report: ConditionReport) {
        try {
            val dto = ConditionReportRequestDto(
                controlPointId = report.controlPointId,
                condition = report.condition.name,
                fieldNotes = report.fieldNotes,
                photoUrl = report.photoUri,
                reporterLicenceNo = report.reporterLicenceNo,
                reporterName = report.reporterName,
                verifiedLatitude = report.verifiedLatitude,
                verifiedLongitude = report.verifiedLongitude,
                gpsAccuracyMeters = report.gpsAccuracyMeters,
                reportedAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            )
            val response = api.submitConditionReport(dto)
            val status = if (response.isSuccessful) SyncStatus.SYNCED else SyncStatus.FAILED
            reportDao.updateSyncStatus(id, status)
        } catch (e: Exception) {
            Timber.w(e, "Immediate sync failed — will retry via WorkManager")
            reportDao.updateSyncStatus(id, SyncStatus.FAILED)
        }
    }

    suspend fun syncPending() {
        reportDao.getPending().forEach { entity ->
            val report = entity.toDomain()
            trySync(entity.id, report)
        }
    }
}