package com.survey.mark.domain.repository

import com.survey.mark.data.dao.OccupationLogDao
import com.survey.mark.data.mappers.toDomain
import com.survey.mark.data.mappers.toEntity
import com.survey.mark.data.remote.OccupationLogRequestDto
import com.survey.mark.di.SurveyMarkApi
import com.survey.mark.domain.model.log.OccupationLog
import com.survey.mark.domain.model.status.SyncStatus
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.time.format.DateTimeFormatter

@Singleton
class OccupationLogRepository @Inject constructor(
    private val dao: OccupationLogDao, private val api: SurveyMarkApi
) {
    fun observeAll(): Flow<List<OccupationLog>> =
        dao.observeAll().map { it.map { e -> e.toDomain() } }

    fun observeForControlPoint(cpId: String): Flow<List<OccupationLog>> =
        dao.observeByControlPoint(cpId).map { it.map { e -> e.toDomain() } }

    fun observeForSurveyor(licNo: String): Flow<List<OccupationLog>> =
        dao.observeBySurveyor(licNo).map { it.map { e -> e.toDomain() } }

    suspend fun saveLog(log: OccupationLog): Long {
        val entity = log.copy(syncStatus = SyncStatus.PENDING).toEntity()
        val id = dao.insert(entity)
        trySync(id, log)
        return id
    }

    private suspend fun trySync(id: Long, log: OccupationLog) {
        try {
            val dto = OccupationLogRequestDto(
                controlPointId = log.controlPointId,
                surveyorLicenceNo = log.surveyorLicenceNo,
                surveyorName = log.surveyorName,
                equipmentType = log.equipmentType,
                equipmentSerialNo = log.equipmentSerialNo,
                occupationType = log.occupationType.name,
                sessionStartTime = log.sessionStartTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                sessionEndTime = log.sessionEndTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                purposeNotes = log.purposeNotes,
                meanSolutionLatitude = log.meanSolutionLatitude,
                meanSolutionLongitude = log.meanSolutionLongitude,
                meanSolutionHeight = log.meanSolutionHeight,
                pdop = log.pdop
            )
            val response = api.submitOccupationLog(dto)
            val status = if (response.isSuccessful) SyncStatus.SYNCED else SyncStatus.FAILED
            dao.updateSyncStatus(id, status)
        } catch (e: Exception) {
            Timber.w(e, "Occupation log immediate sync failed")
            dao.updateSyncStatus(id, SyncStatus.FAILED)
        }
    }

    suspend fun syncPending() {
        dao.getPending().forEach { entity -> trySync(entity.id, entity.toDomain()) }
    }
}