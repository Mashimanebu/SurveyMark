package com.survey.mark.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.survey.mark.domain.model.point.ControlPointType
import com.survey.mark.domain.model.point.OrderClass
import com.survey.mark.domain.model.status.ConditionStatus
import java.time.LocalDateTime

@Entity(tableName = "control_points")
data class ControlPointEntity(
    @PrimaryKey val id: String,
    val name: String,
    val type: ControlPointType,
    val orderClass: OrderClass,
    val latitude: Double,
    val longitude: Double,
    val ellipsoidalHeight: Double?,
    val orthometricHeight: Double?,
    val geoidUndulation: Double?,
    val datumName: String,
    val epochYear: Int?,
    val description: String,
    val accessNotes: String,
    val districtName: String,
    val tinkhundlaName: String,
    val establishedDate: String?,
    val lastVerifiedDate: String?,
    val condition: ConditionStatus,
    val photoUri: String?,
    val sketchPhotoUri: String?,
    val isSynced: Boolean,
    val updatedAt: LocalDateTime = LocalDateTime.now()
)