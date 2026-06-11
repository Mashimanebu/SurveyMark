package com.survey.mark.data.entity

import androidx.room.TypeConverter
import com.survey.mark.domain.model.ObservationMethod
import com.survey.mark.domain.model.ReviewStatus
import com.survey.mark.domain.model.log.OccupationType
import com.survey.mark.domain.model.point.ControlPointType
import com.survey.mark.domain.model.point.OrderClass
import com.survey.mark.domain.model.status.ConditionStatus
import com.survey.mark.domain.model.status.SyncStatus
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ControlPointTypeConverters {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): String? = value?.format(formatter)

    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? =
        value?.let { LocalDateTime.parse(it, formatter) }

    @TypeConverter
    fun fromMarkType(value: ControlPointType): String = value.name

    @TypeConverter
    fun toMarkType(value: String): ControlPointType = ControlPointType.valueOf(value)

    @TypeConverter
    fun fromConditionStatus(value: ConditionStatus): String = value.name

    @TypeConverter
    fun toConditionStatus(value: String): ConditionStatus = ConditionStatus.valueOf(value)

    @TypeConverter
    fun fromOrderClass(value: OrderClass): String = value.name

    @TypeConverter
    fun toOrderClass(value: String): OrderClass = OrderClass.valueOf(value)

    @TypeConverter
    fun fromSyncStatus(value: SyncStatus): String = value.name

    @TypeConverter
    fun toSyncStatus(value: String): SyncStatus = SyncStatus.valueOf(value)

    @TypeConverter
    fun fromOccupationType(value: OccupationType): String = value.name

    @TypeConverter
    fun toOccupationType(value: String): OccupationType = OccupationType.valueOf(value)

    @TypeConverter
    fun fromObservationMethod(value: ObservationMethod): String = value.name

    @TypeConverter
    fun toObservationMethod(value: String): ObservationMethod = ObservationMethod.valueOf(value)

    @TypeConverter
    fun fromReviewStatus(value: ReviewStatus): String = value.name

    @TypeConverter
    fun toReviewStatus(value: String): ReviewStatus = ReviewStatus.valueOf(value)

}