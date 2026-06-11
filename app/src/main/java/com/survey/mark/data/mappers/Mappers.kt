package com.survey.mark.data.mappers

import com.survey.mark.data.entity.ConditionReportEntity
import com.survey.mark.data.entity.ControlPointEntity
import com.survey.mark.data.entity.NewMarkSubmissionEntity
import com.survey.mark.data.entity.OccupationLogEntity
import com.survey.mark.domain.model.log.OccupationLog
import com.survey.mark.domain.model.newmark.NewMarkSubmission
import com.survey.mark.domain.model.point.ControlPoint
import com.survey.mark.domain.model.report.ConditionReport

fun ControlPointEntity.toDomain(distanceMeters: Double? = null) = ControlPoint(
    id = id,
    name = name,
    type = type,
    orderClass = orderClass,
    latitude = latitude,
    longitude = longitude,
    ellipsoidalHeight = ellipsoidalHeight,
    orthometricHeight = orthometricHeight,
    geoidUndulation = geoidUndulation,
    datumName = datumName,
    epochYear = epochYear,
    description = description,
    accessNotes = accessNotes,
    regionName = districtName,
    inkhundlaName = tinkhundlaName,
    establishedDate = establishedDate,
    lastVerifiedDate = lastVerifiedDate,
    condition = condition,
    photoUri = photoUri,
    sketchPhotoUri = sketchPhotoUri,
    isSynced = isSynced,
    distanceMeters = distanceMeters
)

fun ControlPoint.toEntity() = ControlPointEntity(
    id = id,
    name = name,
    type = type,
    orderClass = orderClass,
    latitude = latitude,
    longitude = longitude,
    ellipsoidalHeight = ellipsoidalHeight,
    orthometricHeight = orthometricHeight,
    geoidUndulation = geoidUndulation,
    datumName = datumName,
    epochYear = epochYear,
    description = description,
    accessNotes = accessNotes,
    districtName = regionName,
    tinkhundlaName = inkhundlaName,
    establishedDate = establishedDate,
    lastVerifiedDate = lastVerifiedDate,
    condition = condition,
    photoUri = photoUri,
    sketchPhotoUri = sketchPhotoUri,
    isSynced = isSynced
)

fun OccupationLogEntity.toDomain() = OccupationLog(
    id = id,
    controlPointId = controlPointId,
    controlPointName = controlPointName,
    surveyorLicenceNo = surveyorLicenceNo,
    surveyorName = surveyorName,
    equipmentType = equipmentType,
    equipmentSerialNo = equipmentSerialNo,
    occupationType = occupationType,
    sessionStartTime = sessionStartTime,
    sessionEndTime = sessionEndTime,
    purposeNotes = purposeNotes,
    meanSolutionLatitude = meanSolutionLatitude,
    meanSolutionLongitude = meanSolutionLongitude,
    meanSolutionHeight = meanSolutionHeight,
    pdop = pdop,
    syncStatus = syncStatus
)

fun OccupationLog.toEntity() = OccupationLogEntity(
    id = id,
    controlPointId = controlPointId,
    controlPointName = controlPointName,
    surveyorLicenceNo = surveyorLicenceNo,
    surveyorName = surveyorName,
    equipmentType = equipmentType,
    equipmentSerialNo = equipmentSerialNo,
    occupationType = occupationType,
    sessionStartTime = sessionStartTime,
    sessionEndTime = sessionEndTime,
    purposeNotes = purposeNotes,
    meanSolutionLatitude = meanSolutionLatitude,
    meanSolutionLongitude = meanSolutionLongitude,
    meanSolutionHeight = meanSolutionHeight,
    pdop = pdop,
    syncStatus = syncStatus
)


fun NewMarkSubmissionEntity.toDomain() = NewMarkSubmission(
    id = id,
    proposedName = proposedName,
    proposedType = proposedType,
    latitude = latitude,
    longitude = longitude,
    ellipsoidalHeight = ellipsoidalHeight,
    monumentDescription = monumentDescription,
    accessDescription = accessDescription,
    observationMethod = observationMethod,
    observationDurationHours = observationDurationHours,
    surveyorLicenceNo = surveyorLicenceNo,
    surveyorName = surveyorName,
    monumentPhotoUri = monumentPhotoUri,
    sketchPhotoUri = sketchPhotoUri,
    fieldNotes = fieldNotes,
    submittedAt = submittedAt,
    syncStatus = syncStatus,
    reviewStatus = reviewStatus
)

fun NewMarkSubmission.toEntity() = NewMarkSubmissionEntity(
    id = id,
    proposedName = proposedName,
    proposedType = proposedType,
    latitude = latitude,
    longitude = longitude,
    ellipsoidalHeight = ellipsoidalHeight,
    monumentDescription = monumentDescription,
    accessDescription = accessDescription,
    observationMethod = observationMethod,
    observationDurationHours = observationDurationHours,
    surveyorLicenceNo = surveyorLicenceNo,
    surveyorName = surveyorName,
    monumentPhotoUri = monumentPhotoUri,
    sketchPhotoUri = sketchPhotoUri,
    fieldNotes = fieldNotes,
    submittedAt = submittedAt,
    syncStatus = syncStatus,
    reviewStatus = reviewStatus
)

fun ConditionReportEntity.toDomain() = ConditionReport(
    id = id,
    controlPointId = controlPointId,
    controlPointName = controlPointName,
    condition = condition,
    fieldNotes = fieldNotes,
    photoUri = photoUri,
    reporterLicenceNo = reporterLicenceNo,
    reporterName = reporterName,
    verifiedLatitude = verifiedLatitude,
    verifiedLongitude = verifiedLongitude,
    gpsAccuracyMeters = gpsAccuracyMeters,
    reportedAt = reportedAt,
    syncStatus = syncStatus
)

fun ConditionReport.toEntity() = ConditionReportEntity(
    id = id,
    controlPointId = controlPointId,
    controlPointName = controlPointName,
    condition = condition,
    fieldNotes = fieldNotes,
    photoUri = photoUri,
    reporterLicenceNo = reporterLicenceNo,
    reporterName = reporterName,
    verifiedLatitude = verifiedLatitude,
    verifiedLongitude = verifiedLongitude,
    gpsAccuracyMeters = gpsAccuracyMeters,
    reportedAt = reportedAt,
    syncStatus = syncStatus
)