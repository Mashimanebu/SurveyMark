package com.survey.mark.domain.model.point

import com.survey.mark.domain.model.point.ControlPointType
import com.survey.mark.domain.model.status.ConditionStatus

data class ControlPoint(
    val id: String,
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
    val regionName: String,
    val inkhundlaName: String,
    val establishedDate: String?,
    val lastVerifiedDate: String?,
    val condition: ConditionStatus,
    val photoUri: String?,
    val isSynced: Boolean,
    val distanceMeters: Double? = null
)

fun ControlPointType.displayName(): String = when (this) {
    ControlPointType.TRIG -> "Trigonometric Station"
    ControlPointType.TOWN_SURVEY_MARK -> "Town Survey Mark"
    ControlPointType.REFERENCE_MARK -> "Reference Mark"
    ControlPointType.BENCHMARK -> "Benchmark"
    ControlPointType.GPS_BASE_STATION -> "GPS Base Station"
}

fun ControlPointType.shortCode(): String = when (this) {
    ControlPointType.TRIG -> "TRIG"
    ControlPointType.TOWN_SURVEY_MARK -> "TSM"
    ControlPointType.REFERENCE_MARK -> "RM"
    ControlPointType.BENCHMARK -> "BM"
    ControlPointType.GPS_BASE_STATION -> "GPS"
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
