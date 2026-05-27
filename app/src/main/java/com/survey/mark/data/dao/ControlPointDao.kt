package com.survey.mark.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.survey.mark.data.entity.ControlPointEntity
import com.survey.mark.domain.model.point.ControlPointType
import com.survey.mark.domain.model.status.ConditionStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface ControlPointDao {
    @Query("SELECT * FROM control_points ORDER BY name ASC")
    fun observeAll(): Flow<List<ControlPointEntity>>

    @Query(
        """
        SELECT * FROM control_points
        WHERE name LIKE '%' || :query || '%'
           OR id LIKE '%' || :query || '%'
           OR districtName LIKE '%' || :query || '%'
           OR tinkhundlaName LIKE '%' || :query || '%'
        ORDER BY name ASC
    """
    )
    fun observeBySearch(query: String): Flow<List<ControlPointEntity>>

    @Query("SELECT * FROM control_points WHERE type = :type ORDER BY name ASC")
    fun observeByType(type: ControlPointType): Flow<List<ControlPointEntity>>

    @Query("SELECT * FROM control_points WHERE condition = :condition ORDER BY name ASC")
    fun observeByCondition(condition: ConditionStatus): Flow<List<ControlPointEntity>>

    @Query("SELECT * FROM control_points WHERE id = :id")
    fun observeById(id: String): Flow<ControlPointEntity?>

    @Query("SELECT * FROM control_points WHERE id = :id")
    suspend fun getById(id: String): ControlPointEntity?

    @Query("SELECT COUNT(*) FROM control_points")
    suspend fun count(): Int

    @Query("SELECT COUNT(*) FROM control_points WHERE condition = :condition")
    suspend fun countByCondition(condition: ConditionStatus): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: ControlPointEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(entities: List<ControlPointEntity>)

    @Query("UPDATE control_points SET condition = :condition, lastVerifiedDate = :date, isSynced = 0 WHERE id = :id")
    suspend fun updateCondition(id: String, condition: ConditionStatus, date: String)

    @Delete
    suspend fun delete(entity: ControlPointEntity)
}