package com.survey.mark.ui.newmark

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.survey.mark.domain.model.ObservationMethod
import com.survey.mark.domain.model.ReviewStatus
import com.survey.mark.domain.model.bearing.BearingCalculator
import com.survey.mark.domain.model.location.LocationRepository
import com.survey.mark.domain.model.newmark.NewMarkSubmission
import com.survey.mark.domain.model.point.ControlPointType
import com.survey.mark.domain.model.status.SyncStatus
import com.survey.mark.domain.repository.NewMarkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.collections.copy

@HiltViewModel
class NewMarkViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: NewMarkRepository,
    private val locationRepo: LocationRepository
) : ViewModel() {

    private val _form = MutableStateFlow(NewMarkState())
    val form = _form.asStateFlow()

    init {
        viewModelScope.launch {
            locationRepo.observeLocation(highAccuracy = true).collect { loc ->
                _form.update { s ->
                    val updated = s.copy(userLocation = loc)
                    if (s.useGpsCoordinates) {
                        updated.copy(
                            latitudeStr = "%.8f".format(loc.latitude),
                            longitudeStr = "%.8f".format(loc.longitude),
                            heightStr = loc.altitudeMeters?.let { "%.3f".format(it) } ?: s.heightStr
                        )
                    } else updated
                }
            }
        }
    }

    fun setProposedName(s: String) = _form.update { it.copy(proposedName = s) }
    fun setMarkType(t: ControlPointType) = _form.update { it.copy(markType = t) }
    fun setLatitude(s: String) = _form.update { it.copy(latitudeStr = s, useGpsCoordinates = false) }
    fun setLongitude(s: String) = _form.update { it.copy(longitudeStr = s, useGpsCoordinates = false) }
    fun setHeight(s: String) = _form.update { it.copy(heightStr = s) }
    fun setMonumentDescription(s: String) = _form.update { it.copy(monumentDescription = s) }
    fun setAccessDescription(s: String) = _form.update { it.copy(accessDescription = s) }
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
                heightStr = loc.altitudeMeters?.let { h -> "%.3f".format(h) } ?: it.heightStr,
                useGpsCoordinates = true
            )
        }
    }

    fun createPhotoFile(prefix: String): Uri {
        val dir = File(context.filesDir, "new_mark_photos").also { it.mkdirs() }
        val file = File(dir, "${prefix}_${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    fun submit() {
        val s = _form.value
        if (s.proposedName.isBlank()) { _form.update { it.copy(errorMessage = "Proposed name is required") }; return }
        val lat = s.latitudeStr.toDoubleOrNull() ?: run { _form.update { it.copy(errorMessage = "Invalid latitude") }; return }
        val lng = s.longitudeStr.toDoubleOrNull() ?: run { _form.update { it.copy(errorMessage = "Invalid longitude") }; return }
        val ht = s.heightStr.toDoubleOrNull() ?: run { _form.update { it.copy(errorMessage = "Invalid height") }; return }
        if (!BearingCalculator.isWithinEswatini(lat, lng)) {
            _form.update { it.copy(errorMessage = "Coordinates appear to be outside Eswatini") }
            return
        }
        if (s.licenceNo.isBlank()) { _form.update { it.copy(errorMessage = "Licence number required") }; return }

        viewModelScope.launch {
            _form.update { it.copy(isSubmitting = true) }
            val mark = NewMarkSubmission(
                proposedName = s.proposedName,
                proposedType = s.markType,
                latitude = lat, longitude = lng, ellipsoidalHeight = ht,
                monumentDescription = s.monumentDescription,
                accessDescription = s.accessDescription,
                observationMethod = s.observationMethod,
                observationDurationHours = s.durationHoursStr.toDoubleOrNull() ?: 0.0,
                surveyorLicenceNo = s.licenceNo,
                surveyorName = s.surveyorName,
                monumentPhotoUri = s.monumentPhotoUri,
                sketchPhotoUri = s.sketchPhotoUri,
                fieldNotes = s.fieldNotes,
                submittedAt = LocalDateTime.now(),
                syncStatus = SyncStatus.PENDING,
                reviewStatus = ReviewStatus.PENDING
            )
            runCatching { repository.submit(mark) }
                .onSuccess {
                    Timber.d("✅ Submit success")
                    _form.update { it.copy(isSubmitting = false, submitSuccess = true) }
                }
                .onFailure { e ->
                    Timber.e(e, "❌ Submit failed: ${e.message}")
                    _form.update { it.copy(isSubmitting = false, errorMessage = e.message ?: "Unknown error") }
                }
        }
    }
}