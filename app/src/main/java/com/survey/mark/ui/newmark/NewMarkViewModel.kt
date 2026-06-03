package com.survey.mark.ui.newmark

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.survey.mark.domain.model.ObservationMethod
import com.survey.mark.domain.model.ReviewStatus
import com.survey.mark.domain.model.location.LocationRepository
import com.survey.mark.domain.model.newmark.NewMarkSubmission
import com.survey.mark.domain.model.point.ControlPoint
import com.survey.mark.domain.model.point.ControlPointType
import com.survey.mark.domain.model.point.OrderClass
import com.survey.mark.domain.model.status.ConditionStatus
import com.survey.mark.domain.model.status.SyncStatus
import com.survey.mark.domain.repository.ControlPointRepository
import com.survey.mark.domain.repository.NewMarkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject


@HiltViewModel
class NewMarkViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val newMarkRepository: NewMarkRepository,
    private val controlPointRepository: ControlPointRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _form = MutableStateFlow(
        NewMarkState(
            refNumber = generateRefNumber(ControlPointType.TRIG),
            submissionDate = LocalDate.now()
        )
    )
    val form = _form.asStateFlow()

    init {
        viewModelScope.launch {
            locationRepository.getLastKnownLocation()?.let { last ->
                _form.update { it.copy(userLocation = last) }
                Timber.d("Seeded with last known location: ${last.latitude}, ${last.longitude}")
            }
        }

        viewModelScope.launch {
            locationRepository.observeLocation(highAccuracy = true)
                .catch { Timber.w(it, "Location unavailable in NewMark") }
                .collect { loc -> _form.update { it.copy(userLocation = loc) } }
        }
    }

    private fun generateRefNumber(type: ControlPointType): String {
        val date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)
        val suffix = (1000..9999).random()
        return "PROV-${type.name}-$date-$suffix"
    }

    fun setProposedName(s: String) = _form.update { it.copy(proposedName = s) }

    fun setMarkType(t: ControlPointType) = _form.update {
        it.copy(markType = t, refNumber = generateRefNumber(t))
    }

    fun setLatitude(s: String) = _form.update { it.copy(latitudeStr = s) }
    fun setLongitude(s: String) = _form.update { it.copy(longitudeStr = s) }
    fun setHeight(s: String) = _form.update { it.copy(heightStr = s) }
    fun setMonumentDescription(s: String) = _form.update { it.copy(monumentDescription = s) }
    fun setAccessDescription(s: String) = _form.update { it.copy(accessDescription = s) }
    fun setDatumName(s: String) = _form.update { it.copy(datumName = s) }
    fun setRegionName(s: String) = _form.update { it.copy(regionName = s) }
    fun setInkhundlaName(s: String) = _form.update { it.copy(inkhundlaName = s) }
    fun setObservationMethod(m: ObservationMethod) = _form.update { it.copy(observationMethod = m) }
    fun setDurationHours(s: String) = _form.update { it.copy(durationHoursStr = s) }
    fun setSurveyorName(s: String) = _form.update { it.copy(surveyorName = s) }
    fun setLicenceNo(s: String) = _form.update { it.copy(licenceNo = s) }
    fun setMonumentPhoto(uri: String?) = _form.update { it.copy(monumentPhotoUri = uri) }
    fun setSketchPhoto(uri: String?) = _form.update { it.copy(sketchPhotoUri = uri) }
    fun setFieldNotes(s: String) = _form.update { it.copy(fieldNotes = s) }
    fun clearError() = _form.update { it.copy(errorMessage = null) }

    fun fillGpsCoordinates() {
        val loc = _form.value.userLocation ?: return
        _form.update {
            it.copy(
                latitudeStr = "%.8f".format(loc.latitude),
                longitudeStr = "%.8f".format(loc.longitude),
                heightStr = loc.altitudeMeters?.let { h -> "%.3f".format(h) } ?: it.heightStr
            )
        }
    }

    fun createPhotoFile(prefix: String): Uri {
        val dir = File(context.filesDir, "new_mark_photos").also { it.mkdirs() }
        val file = File(dir, "${prefix}_${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    fun submit() {
        val s = _form.value

        if (s.proposedName.isBlank()) {
            _form.update { it.copy(errorMessage = "Proposed name is required") }
            return
        }
        val lat = s.latitudeStr.toDoubleOrNull() ?: run {
            _form.update { it.copy(errorMessage = "Enter a valid latitude") }
            return
        }
        val lng = s.longitudeStr.toDoubleOrNull() ?: run {
            _form.update { it.copy(errorMessage = "Enter a valid longitude") }
            return
        }
        val ht = s.heightStr.toDoubleOrNull()
        val hours = s.durationHoursStr.toDoubleOrNull() ?: 0.0

        if (s.regionName.isBlank()) {
            _form.update { it.copy(errorMessage = "Region name is required") }
            return
        }
        if (s.licenceNo.isBlank()) {
            _form.update { it.copy(errorMessage = "Licence number is required") }
            return
        }

        viewModelScope.launch {
            _form.update { it.copy(isSubmitting = true) }

            runCatching {
                val provisionalPoint = ControlPoint(
                    id = s.refNumber,
                    name = s.proposedName,
                    type = s.markType,
                    orderClass = OrderClass.FOURTH,
                    latitude = lat,
                    longitude = lng,
                    ellipsoidalHeight = ht,
                    orthometricHeight = null,
                    geoidUndulation = null,
                    datumName = s.datumName,
                    epochYear = null,
                    description = s.monumentDescription,
                    accessNotes = s.accessDescription,
                    regionName = s.regionName,
                    inkhundlaName = s.inkhundlaName,
                    establishedDate = null,
                    lastVerifiedDate = null,
                    condition = ConditionStatus.UNKNOWN,
                    photoUri = s.monumentPhotoUri,
                    isSynced = false
                )
                controlPointRepository.upsert(provisionalPoint)
                Timber.d("Provisional point saved: ${s.proposedName}")

                val submission = NewMarkSubmission(
                    proposedName = s.proposedName,
                    proposedType = s.markType,
                    latitude = lat,
                    longitude = lng,
                    ellipsoidalHeight = ht ?: 0.0,
                    monumentDescription = s.monumentDescription,
                    accessDescription = s.accessDescription,
                    observationMethod = s.observationMethod,
                    observationDurationHours = hours,
                    surveyorLicenceNo = s.licenceNo,
                    surveyorName = s.surveyorName,
                    monumentPhotoUri = s.monumentPhotoUri,
                    sketchPhotoUri = s.sketchPhotoUri,
                    fieldNotes = s.fieldNotes,
                    submittedAt = LocalDateTime.now(),
                    syncStatus = SyncStatus.PENDING,
                    reviewStatus = ReviewStatus.PENDING
                )
                newMarkRepository.submit(submission)
                Timber.d("Submission queued for SG review: ${s.proposedName}")
            }
                .onSuccess {
                    _form.update { it.copy(isSubmitting = false, submitSuccess = true) }
                }
                .onFailure { e ->
                    Timber.e(e, "Submission failed")
                    _form.update { it.copy(isSubmitting = false, errorMessage = e.message) }
                }
        }
    }
}