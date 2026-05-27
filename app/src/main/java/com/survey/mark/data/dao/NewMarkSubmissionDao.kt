package com.survey.mark.data.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.survey.mark.data.entity.NewMarkSubmissionEntity
import com.survey.mark.domain.model.ReviewStatus
import com.survey.mark.domain.model.status.SyncStatus
import kotlinx.coroutines.flow.Flow

interface NewMarkSubmissionDao {
    @Query("SELECT * FROM new_mark_submissions ORDER BY submittedAt DESC")
    fun observeAll(): Flow<List<NewMarkSubmissionEntity>>

    @Query("SELECT * FROM new_mark_submissions WHERE syncStatus = :status")
    suspend fun getPending(status: SyncStatus = SyncStatus.PENDING): List<NewMarkSubmissionEntity>

    @Query("SELECT * FROM new_mark_submissions WHERE id = :id")
    suspend fun getById(id: Long): NewMarkSubmissionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: NewMarkSubmissionEntity): Long

    @Query("UPDATE new_mark_submissions SET syncStatus = :sync, reviewStatus = :review WHERE id = :id")
    suspend fun updateStatus(id: Long, sync: SyncStatus, review: ReviewStatus)

    @Delete
    suspend fun delete(entity: NewMarkSubmissionEntity)
}
