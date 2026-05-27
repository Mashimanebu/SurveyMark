package com.survey.mark.domain.repository

import com.survey.mark.data.dao.ControlPointDao
import com.survey.mark.data.entity.ControlPointEntity
import com.survey.mark.data.mappers.toDomain
import com.survey.mark.di.SurveyMarkApi
import com.survey.mark.domain.model.bearing.BearingCalculator
import com.survey.mark.domain.model.point.ControlPoint
import com.survey.mark.domain.model.point.ControlPointType
import com.survey.mark.domain.model.point.OrderClass
import com.survey.mark.domain.model.status.ConditionStatus
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber


@Singleton
class ControlPointRepository @Inject constructor(
    private val dao: ControlPointDao,
    private val api: SurveyMarkApi
) {
    fun observeAll(): Flow<List<ControlPoint>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    fun observeSearch(query: String): Flow<List<ControlPoint>> =
        dao.observeBySearch(query).map { list -> list.map { it.toDomain() } }

    fun observeByType(type: ControlPointType): Flow<List<ControlPoint>> =
        dao.observeByType(type).map { list -> list.map { it.toDomain() } }

    fun observeByCondition(condition: ConditionStatus): Flow<List<ControlPoint>> =
        dao.observeByCondition(condition).map { list -> list.map { it.toDomain() } }

    fun observeById(id: String): Flow<ControlPoint?> =
        dao.observeById(id).map { it?.toDomain() }

    fun observeAllSortedByDistance(userLat: Double, userLng: Double): Flow<List<ControlPoint>> =
        dao.observeAll().map { list ->
            list.map { entity ->
                val dist = BearingCalculator.distanceMeters(
                    userLat, userLng, entity.latitude, entity.longitude
                )
                entity.toDomain(distanceMeters = dist)
            }.sortedBy { it.distanceMeters }
        }

    suspend fun syncFromServer(): Result<Int> = runCatching {
        val response = api.getControlPoints()
        if (response.isSuccessful) {
            val body = response.body() ?: return@runCatching 0
            val entities = body.items.map { dto ->
                ControlPointEntity(
                    id = dto.id, name = dto.name,
                    type = MarkType.valueOf(dto.type),
                    orderClass = OrderClass.valueOf(dto.orderClass),
                    latitude = dto.latitude, longitude = dto.longitude,
                    ellipsoidalHeight = dto.ellipsoidalHeight,
                    orthometricHeight = dto.orthometricHeight,
                    geoidUndulation = dto.geoidUndulation,
                    datumName = dto.datumName, epochYear = dto.epochYear,
                    description = dto.description, accessNotes = dto.accessNotes,
                    districtName = dto.regionName, tinkhundlaName = dto.inkhundlaName,
                    establishedDate = dto.establishedDate,
                    lastVerifiedDate = dto.lastVerifiedDate,
                    condition = ConditionStatus.valueOf(dto.condition),
                    photoUri = dto.photoUrl, isSynced = true
                )
            }
            dao.upsertAll(entities)
            Timber.d("Synced ${entities.size} control points from server")
            entities.size
        } else {
            Timber.w("Server sync failed: ${response.code()}")
            0
        }
    }

    suspend fun seedIfEmpty() {
        if (dao.count() == 0) {
            Timber.d("Database empty — seeding with ${EswatiniSeedData.controlPoints.size} control points")
            dao.upsertAll(EswatiniSeedData.controlPoints.map { it.toEntity() })
        }
    }

    suspend fun countByCondition(condition: ConditionStatus) = dao.countByCondition(condition)
}