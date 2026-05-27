package com.survey.mark.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.survey.mark.data.entity.ConditionReportEntity
import com.survey.mark.domain.model.status.SyncStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface ConditionReportDao {
    @Query("SELECT * FROM condition_reports ORDER BY reportedAt DESC")
    fun observeAll(): Flow<List<ConditionReportEntity>>

    @Query("SELECT * FROM condition_reports WHERE controlPointId = :cpId ORDER BY reportedAt DESC")
    fun observeByControlPoint(cpId: String): Flow<List<ConditionReportEntity>>

    @Query("SELECT * FROM condition_reports WHERE syncStatus = :status ORDER BY reportedAt ASC")
    suspend fun getPending(status: SyncStatus = SyncStatus.PENDING): List<ConditionReportEntity>

    @Query("SELECT * FROM condition_reports WHERE id = :id")
    suspend fun getById(id: Long): ConditionReportEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ConditionReportEntity): Long

    @Query("UPDATE condition_reports SET syncStatus = :status WHERE id = :id")
    suspend fun updateSyncStatus(id: Long, status: SyncStatus)

    @Delete
    suspend fun delete(entity: ConditionReportEntity)
}