package com.survey.mark.domain.repository

import com.survey.mark.data.dao.ControlPointDao
import com.survey.mark.data.entity.ControlPointEntity
import com.survey.mark.data.mappers.toDomain
import com.survey.mark.data.mappers.toEntity
import com.survey.mark.di.SurveyMarkApi
import com.survey.mark.domain.model.bearing.BearingCalculator
import com.survey.mark.domain.model.point.ControlPoint
import com.survey.mark.domain.model.point.ControlPointType
import com.survey.mark.domain.model.point.OrderClass
import com.survey.mark.domain.model.status.ConditionStatus
import javax.inject.Inject
import javax.inject.Singleton
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

    fun observeAllSortedByDistance(
        userLat: Double,
        userLng: Double
    ): Flow<List<ControlPoint>> =
        dao.observeAll().map { list ->
            list.map { entity ->
                val dist = BearingCalculator.distanceMeters(
                    userLat, userLng,
                    entity.latitude, entity.longitude
                )
                entity.toDomain(distanceMeters = dist)
            }.sortedBy { it.distanceMeters }
        }


    suspend fun getById(id: String): ControlPoint? =
        dao.getById(id)?.toDomain()


    suspend fun upsert(controlPoint: ControlPoint): Long =
        dao.upsert(controlPoint.toEntity())

    suspend fun upsertAll(entities: List<ControlPoint>) =
        dao.upsertAll(entities.map { it.toEntity() })

    suspend fun countByCondition(condition: ConditionStatus) =
        dao.countByCondition(condition)

    suspend fun markSynced(id: String) {
        dao.markSynced(id)
        Timber.d("Control point marked as synced: $id")
    }

    suspend fun delete(id: String) {
        dao.deleteById(id)
        Timber.d("Control point deleted: $id")
    }

    suspend fun seedIfEmpty() {
        if (dao.count() == 0) {
            Timber.d("Seeding ${EswatiniSeedData.points.size} control points")
            dao.upsertAll(EswatiniSeedData.points)
        }
    }

    suspend fun syncFromServer(): Result<Int> = runCatching {
        val response = api.getControlPoints()
        if (response.isSuccessful) {
            val body = response.body() ?: return@runCatching 0
            val entities = body.items.map { dto ->
                com.survey.mark.data.entity.ControlPointEntity(
                    id               = dto.id,
                    name             = dto.name,
                    type             = ControlPointType.valueOf(dto.type),
                    orderClass       = OrderClass.valueOf(dto.orderClass),
                    latitude         = dto.latitude,
                    longitude        = dto.longitude,
                    ellipsoidalHeight = dto.ellipsoidalHeight,
                    orthometricHeight = dto.orthometricHeight,
                    geoidUndulation  = dto.geoidUndulation,
                    datumName        = dto.datumName,
                    epochYear        = dto.epochYear,
                    description      = dto.description,
                    accessNotes      = dto.accessNotes,
                    districtName     = dto.regionName,
                    tinkhundlaName   = dto.inkhundlaName,
                    establishedDate  = dto.establishedDate,
                    lastVerifiedDate = dto.lastVerifiedDate,
                    condition        = ConditionStatus.valueOf(dto.condition),
                    photoUri         = dto.photoUrl,
                    sketchPhotoUri   = dto.sketchPhotoUrl,
                    isSynced         = true
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
}


private object EswatiniSeedData {
    val points: List<ControlPointEntity> = listOf(
        ControlPointEntity(
            id               = "SZ-TG-001",
            name             = "MDZIMBA TRIG",
            type             = ControlPointType.TRIG,
            orderClass       = OrderClass.FIRST,
            latitude         = -26.3311,
            longitude        = 31.1204,
            ellipsoidalHeight = 1843.21,
            orthometricHeight = 1862.44,
            geoidUndulation  = -19.23,
            datumName        = "WGS84",
            epochYear        = 2000,
            description      = "Concrete pillar on summit of Mdzimba Mountain.",
            accessNotes      = "4WD required. 45-min hike to summit.",
            districtName     = "Hhohho",
            tinkhundlaName   = "Mdzimba",
            establishedDate  = "1956-03-14",
            lastVerifiedDate = "2024-11-12",
            condition        = ConditionStatus.INTACT,
            photoUri         = null,
            sketchPhotoUri   = null,
            isSynced         = true
        ),
        ControlPointEntity(
            id               = "SZ-GPS-001",
            name             = "MBABANE CORS",
            type             = ControlPointType.BENCHMARK,
            orderClass       = OrderClass.FIRST,
            latitude         = -26.3186,
            longitude        = 31.1367,
            ellipsoidalHeight = 1159.33,
            orthometricHeight = 1177.88,
            geoidUndulation  = -18.55,
            datumName        = "ITRF2014",
            epochYear        = 2015,
            description      = "CORS on roof of Surveyor General building.",
            accessNotes      = "Government compound — security clearance required.",
            districtName     = "Hhohho",
            tinkhundlaName   = "Mbabane West",
            establishedDate  = "2008-06-01",
            lastVerifiedDate = "2025-01-20",
            condition        = ConditionStatus.INTACT,
            photoUri         = null,
            sketchPhotoUri   = null,
            isSynced         = true
        ),
        ControlPointEntity(
            id               = "SZ-TG-002",
            name             = "LOBAMBA TRIG",
            type             = ControlPointType.TRIG,
            orderClass       = OrderClass.SECOND,
            latitude         = -26.4561,
            longitude        = 31.2103,
            ellipsoidalHeight = 934.12,
            orthometricHeight = 952.67,
            geoidUndulation  = -18.55,
            datumName        = "WGS84",
            epochYear        = 2000,
            description      = "Concrete beacon on hilltop above Lobamba royal valley.",
            accessNotes      = "Permission from traditional authority required.",
            districtName     = "Hhohho",
            tinkhundlaName   = "Lobamba",
            establishedDate  = "1963-08-22",
            lastVerifiedDate = "2024-09-03",
            condition        = ConditionStatus.INTACT,
            photoUri         = null,
            sketchPhotoUri   = null,
            isSynced         = true
        ),
        ControlPointEntity(
            id               = "SZ-BM-007",
            name             = "MANZINI BM-7",
            type             = ControlPointType.REFERENCE_MARK,
            orderClass       = OrderClass.SECOND,
            latitude         = -26.4992,
            longitude        = 31.3707,
            ellipsoidalHeight = null,
            orthometricHeight = 568.22,
            geoidUndulation  = null,
            datumName        = "MSL",
            epochYear        = null,
            description      = "Brass bracket in Manzini City Council building wall.",
            accessNotes      = "On public footpath. Easily accessible.",
            districtName     = "Manzini",
            tinkhundlaName   = "Manzini North",
            establishedDate  = "1971-04-30",
            lastVerifiedDate = "2024-07-22",
            condition        = ConditionStatus.DISTURBED,
            photoUri         = null,
            sketchPhotoUri   = null,
            isSynced         = true
        ),
        ControlPointEntity(
            id               = "SZ-TG-003",
            name             = "HLANE TRIG",
            type             = ControlPointType.TRIG,
            orderClass       = OrderClass.THIRD,
            latitude         = -26.1024,
            longitude        = 31.8745,
            ellipsoidalHeight = 283.55,
            orthometricHeight = 301.10,
            geoidUndulation  = -17.55,
            datumName        = "WGS84",
            epochYear        = 2000,
            description      = "Beacon within Hlane Royal National Park.",
            accessNotes      = "Entry fee required. Check for wildlife.",
            districtName     = "Lubombo",
            tinkhundlaName   = "Siphofaneni",
            establishedDate  = "1968-07-15",
            lastVerifiedDate = "2023-06-18",
            condition        = ConditionStatus.DESTROYED,
            photoUri         = null,
            sketchPhotoUri   = null,
            isSynced         = true
        )
    )
}