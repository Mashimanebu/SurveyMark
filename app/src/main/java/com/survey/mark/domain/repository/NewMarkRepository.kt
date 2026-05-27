package com.survey.mark.domain.repository

import com.survey.mark.data.dao.NewMarkSubmissionDao
import com.survey.mark.data.mappers.toDomain
import com.survey.mark.data.mappers.toEntity
import com.survey.mark.data.remote.NewMarkRequestDto
import com.survey.mark.di.SurveyMarkApi
import com.survey.mark.domain.model.ReviewStatus
import com.survey.mark.domain.model.newmark.NewMarkSubmission
import com.survey.mark.domain.model.status.SyncStatus
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Singleton
class NewMarkRepository @Inject constructor(
    private val dao: NewMarkSubmissionDao, private val api: SurveyMarkApi
) {
    fun observeAll(): Flow<List<NewMarkSubmission>> =
        dao.observeAll().map { it.map { e -> e.toDomain() } }

    suspend fun submit(mark: NewMarkSubmission): Long {
        val entity = mark.copy(
            submittedAt = LocalDateTime.now(),
            syncStatus = SyncStatus.PENDING,
            reviewStatus = ReviewStatus.PENDING
        ).toEntity()
        val id = dao.insert(entity)
        trySync(id, mark)
        return id
    }

    private suspend fun trySync(id: Long, mark: NewMarkSubmission) {
        try {
            val dto = NewMarkRequestDto(
                proposedName = mark.proposedName,
                proposedType = mark.proposedType.name,
                latitude = mark.latitude,
                longitude = mark.longitude,
                ellipsoidalHeight = mark.ellipsoidalHeight,
                monumentDescription = mark.monumentDescription,
                accessDescription = mark.accessDescription,
                observationMethod = mark.observationMethod.name,
                observationDurationHours = mark.observationDurationHours,
                surveyorLicenceNo = mark.surveyorLicenceNo,
                surveyorName = mark.surveyorName,
                monumentPhotoUrl = mark.monumentPhotoUri,
                sketchPhotoUrl = mark.sketchPhotoUri,
                fieldNotes = mark.fieldNotes,
                submittedAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            )
            val response = api.submitNewMark(dto)
            val syncStatus = if (response.isSuccessful) SyncStatus.SYNCED else SyncStatus.FAILED
            dao.updateStatus(id, syncStatus, ReviewStatus.PENDING)
        } catch (e: Exception) {
            Timber.w(e, "New mark immediate sync failed")
            dao.updateStatus(id, SyncStatus.FAILED, ReviewStatus.PENDING)
        }
    }

    suspend fun syncPending() {
        dao.getPending().forEach { entity -> trySync(entity.id, entity.toDomain()) }
    }
}