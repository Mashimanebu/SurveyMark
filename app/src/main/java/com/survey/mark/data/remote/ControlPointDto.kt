package com.survey.mark.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ControlPointDto(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "type") val type: String,
    @Json(name = "order_class") val orderClass: String,
    @Json(name = "latitude") val latitude: Double,
    @Json(name = "longitude") val longitude: Double,
    @Json(name = "ellipsoidal_height") val ellipsoidalHeight: Double?,
    @Json(name = "orthometric_height") val orthometricHeight: Double?,
    @Json(name = "geoid_undulation") val geoidUndulation: Double?,
    @Json(name = "datum_name") val datumName: String,
    @Json(name = "epoch_year") val epochYear: Int?,
    @Json(name = "description") val description: String,
    @Json(name = "access_notes") val accessNotes: String,
    @Json(name = "district_name") val districtName: String,
    @Json(name = "tinkhundla_name") val tinkhundlaName: String,
    @Json(name = "established_date") val establishedDate: String?,
    @Json(name = "last_verified_date") val lastVerifiedDate: String?,
    @Json(name = "condition") val condition: String,
    @Json(name = "photo_url") val photoUrl: String?
)

@JsonClass(generateAdapter = true)
data class ConditionReportRequestDto(
    @Json(name = "control_point_id") val controlPointId: String,
    @Json(name = "condition") val condition: String,
    @Json(name = "field_notes") val fieldNotes: String,
    @Json(name = "photo_url") val photoUrl: String?,
    @Json(name = "reporter_licence_no") val reporterLicenceNo: String,
    @Json(name = "reporter_name") val reporterName: String,
    @Json(name = "verified_latitude") val verifiedLatitude: Double,
    @Json(name = "verified_longitude") val verifiedLongitude: Double,
    @Json(name = "gps_accuracy_meters") val gpsAccuracyMeters: Float,
    @Json(name = "reported_at") val reportedAt: String
)

@JsonClass(generateAdapter = true)
data class OccupationLogRequestDto(
    @Json(name = "control_point_id") val controlPointId: String,
    @Json(name = "surveyor_licence_no") val surveyorLicenceNo: String,
    @Json(name = "surveyor_name") val surveyorName: String,
    @Json(name = "equipment_type") val equipmentType: String,
    @Json(name = "equipment_serial_no") val equipmentSerialNo: String,
    @Json(name = "occupation_type") val occupationType: String,
    @Json(name = "session_start_time") val sessionStartTime: String,
    @Json(name = "session_end_time") val sessionEndTime: String,
    @Json(name = "purpose_notes") val purposeNotes: String,
    @Json(name = "mean_solution_latitude") val meanSolutionLatitude: Double?,
    @Json(name = "mean_solution_longitude") val meanSolutionLongitude: Double?,
    @Json(name = "mean_solution_height") val meanSolutionHeight: Double?,
    @Json(name = "pdop") val pdop: Float?
)

@JsonClass(generateAdapter = true)
data class NewMarkRequestDto(
    @Json(name = "proposed_name") val proposedName: String,
    @Json(name = "proposed_type") val proposedType: String,
    @Json(name = "latitude") val latitude: Double,
    @Json(name = "longitude") val longitude: Double,
    @Json(name = "ellipsoidal_height") val ellipsoidalHeight: Double,
    @Json(name = "monument_description") val monumentDescription: String,
    @Json(name = "access_description") val accessDescription: String,
    @Json(name = "observation_method") val observationMethod: String,
    @Json(name = "observation_duration_hours") val observationDurationHours: Double,
    @Json(name = "surveyor_licence_no") val surveyorLicenceNo: String,
    @Json(name = "surveyor_name") val surveyorName: String,
    @Json(name = "monument_photo_url") val monumentPhotoUrl: String?,
    @Json(name = "sketch_photo_url") val sketchPhotoUrl: String?,
    @Json(name = "field_notes") val fieldNotes: String,
    @Json(name = "submitted_at") val submittedAt: String
)

@JsonClass(generateAdapter = true)
data class ApiResponse<T>(
    @Json(name = "success") val success: Boolean,
    @Json(name = "data") val data: T?,
    @Json(name = "message") val message: String?
)

@JsonClass(generateAdapter = true)
data class PagedResponse<T>(
    @Json(name = "items") val items: List<T>,
    @Json(name = "total") val total: Int,
    @Json(name = "page") val page: Int,
    @Json(name = "page_size") val pageSize: Int
)
