package com.survey.mark.ui.report

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.survey.mark.domain.model.location.LocationRepository
import com.survey.mark.domain.model.point.ControlPoint
import com.survey.mark.domain.model.report.ConditionReport
import com.survey.mark.domain.model.status.ConditionStatus
import com.survey.mark.domain.model.status.SyncStatus
import com.survey.mark.domain.repository.ConditionReportRepository
import com.survey.mark.domain.repository.ControlPointRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class ConditionReportViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val cpRepo: ControlPointRepository,
    private val reportRepo: ConditionReportRepository,
    private val locationRepo: LocationRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val preselectedId: String? = savedStateHandle["controlPointId"]
    private val _form = MutableStateFlow(ReportState())
    val form = _form.asStateFlow()

    init {
        viewModelScope.launch {
            cpRepo.observeAll().collect { points ->
                val selected = if (preselectedId != null) {
                    points.firstOrNull { it.id == preselectedId }
                } else points.firstOrNull()
                _form.update { it.copy(allPoints = points, selectedPoint = it.selectedPoint ?: selected) }
            }
        }
        viewModelScope.launch {
            locationRepo.observeLocation(highAccuracy = true).collect { loc ->
                _form.update { it.copy(location = loc) }
            }
        }
    }

    fun selectPoint(point: ControlPoint) = _form.update { it.copy(selectedPoint = point) }
    fun setCondition(c: ConditionStatus) = _form.update { it.copy(condition = c) }
    fun setFieldNotes(s: String) = _form.update { it.copy(fieldNotes = s) }
    fun setReporterName(s: String) = _form.update { it.copy(reporterName = s) }
    fun setLicenceNo(s: String) = _form.update { it.copy(reporterLicenceNo = s) }
    fun setPhotoUri(uri: String?) = _form.update { it.copy(photoUri = uri) }
    fun clearError() = _form.update { it.copy(errorMessage = null) }

    fun createPhotoFile(): Uri {
        val dir = File(context.filesDir, "beacon_photos").also { it.mkdirs() }
        val file = File(dir, "beacon_${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    fun submit() {
        val state = _form.value
        val point = state.selectedPoint ?: run {
            _form.update { it.copy(errorMessage = "Select a control point") }
            return
        }
        val condition = state.condition ?: run {
            _form.update { it.copy(errorMessage = "Select a condition") }
            return
        }
        val loc = state.location ?: run {
            _form.update { it.copy(errorMessage = "GPS location not yet acquired") }
            return
        }
        if (state.reporterLicenceNo.isBlank()) {
            _form.update { it.copy(errorMessage = "Enter your surveyor licence number") }
            return
        }

        viewModelScope.launch {
            _form.update { it.copy(isSubmitting = true) }
            val report = ConditionReport(
                controlPointId = point.id,
                controlPointName = point.name,
                condition = condition,
                fieldNotes = state.fieldNotes,
                photoUri = state.photoUri,
                reporterLicenceNo = state.reporterLicenceNo,
                reporterName = state.reporterName,
                verifiedLatitude = loc.latitude,
                verifiedLongitude = loc.longitude,
                gpsAccuracyMeters = loc.accuracyMeters,
                reportedAt = LocalDateTime.now(),
                syncStatus = SyncStatus.PENDING
            )
            runCatching { reportRepo.submitReport(report) }
                .onSuccess { _form.update { it.copy(isSubmitting = false, submitSuccess = true) } }
                .onFailure { e -> _form.update { it.copy(isSubmitting = false, errorMessage = e.message) } }
        }
    }
}