package com.survey.mark.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.survey.mark.data.entity.OccupationLogEntity
import com.survey.mark.domain.model.status.SyncStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface OccupationLogDao {
    @Query("SELECT * FROM occupation_logs ORDER BY sessionStartTime DESC")
    fun observeAll(): Flow<List<OccupationLogEntity>>

    @Query("SELECT * FROM occupation_logs WHERE controlPointId = :cpId ORDER BY sessionStartTime DESC")
    fun observeByControlPoint(cpId: String): Flow<List<OccupationLogEntity>>

    @Query("SELECT * FROM occupation_logs WHERE surveyorLicenceNo = :licNo ORDER BY sessionStartTime DESC")
    fun observeBySurveyor(licNo: String): Flow<List<OccupationLogEntity>>

    @Query("SELECT * FROM occupation_logs WHERE syncStatus = :status")
    suspend fun getPending(status: SyncStatus = SyncStatus.PENDING): List<OccupationLogEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: OccupationLogEntity): Long

    @Query("UPDATE occupation_logs SET syncStatus = :status WHERE id = :id")
    suspend fun updateSyncStatus(id: Long, status: SyncStatus)

    @Delete
    suspend fun delete(entity: OccupationLogEntity)
}