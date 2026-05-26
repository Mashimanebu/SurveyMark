package com.survey.mark.domain.model.point

import com.survey.mark.domain.model.point.ControlPointType
import com.survey.mark.domain.model.status.ConditionStatus

data class ControlPoint(

    val name: String,
    val refNumber: String,
    val type: ControlPointType,
    val order: Int,
    val latitude: Double,
    val longitude: Double,
    val ellipsoidalHeight: Double?,
    val orthometricHeight: Double?,
    val description: String,
    val district: String,
    val region: String,
    val establishedDate: String?,
    val lastVerifiedDate: String?,
    val conditionStatus: ConditionStatus,
    val epoch: String? = null,
    val uncertaintyMm: Int? = null,
    val occupationCount: Int = 0,
    val isSynced: Boolean = true,
    val distanceMeters: Double? = null,
    val serverRevision: Long = 0L,
    val lastModified: Long = System.currentTimeMillis()
)

fun ControlPointType.displayName(): String = when (this) {
    ControlPointType.TRIG -> "Trigonometric Station"
    ControlPointType.TOWN_SURVEY_MARK -> "Town Survey Mark"
    ControlPointType.REFERENCE_MARK -> "Reference Mark"
    ControlPointType.BENCHMARK -> "Benchmark"
}

fun ControlPointType.shortCode(): String = when (this) {
    ControlPointType.TRIG -> "TRIG"
    ControlPointType.TOWN_SURVEY_MARK -> "TSM"
    ControlPointType.REFERENCE_MARK -> "RM"
    ControlPointType.BENCHMARK -> "BM"
}

fun ControlPointType.hasHorizontalControl(): Boolean =
    this == ControlPointType.TRIG ||
            this == ControlPointType.TOWN_SURVEY_MARK

fun ControlPointType.hasVerticalControl(): Boolean =
    this == ControlPointType.BENCHMARK ||
            this == ControlPointType.TRIG

fun ControlPoint.formattedLatitude(): String {
    val abs = Math.abs(latitude)
    val deg = abs.toInt()
    val minFull = (abs - deg) * 60
    val min = minFull.toInt()
    val sec = (minFull - min) * 60
    val dir = if (latitude >= 0) "N" else "S"
    return "%d° %02d' %05.2f\" %s".format(deg, min, sec, dir)
}


fun ControlPoint.formattedLongitude(): String {
    val abs = Math.abs(longitude)
    val deg = abs.toInt()
    val minFull = (abs - deg) * 60
    val min = minFull.toInt()
    val sec = (minFull - min) * 60
    val dir = if (longitude >= 0) "E" else "W"
    return "%d° %02d' %05.2f\" %s".format(deg, min, sec, dir)
}

fun ControlPoint.formattedDistance(): String {
    return when {
        distanceMeters == null -> "—"
        distanceMeters < 1000 -> "%.0f m".format(distanceMeters)
        else -> "%.1f km".format(distanceMeters / 1000.0)
    }
}
