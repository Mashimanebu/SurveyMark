package com.survey.mark.ui.log

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.survey.mark.auth.domain.AuthRepository
import com.survey.mark.domain.model.log.OccupationLog
import com.survey.mark.domain.model.log.OccupationType
import com.survey.mark.domain.model.point.ControlPoint
import com.survey.mark.domain.model.status.SyncStatus
import com.survey.mark.domain.repository.ControlPointRepository
import com.survey.mark.domain.repository.OccupationLogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class OccupationLogViewModel @Inject constructor(
    private val cpRepo: ControlPointRepository,
    private val logRepo: OccupationLogRepository,
    private val authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val preselectedId: String? = savedStateHandle["controlPointId"]
    private val _form = MutableStateFlow(LogFormState())
    val form = _form.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.getCurrentUser()?.let { user ->
                _form.update {
                    it.copy(
                        surveyorName = user.displayName,
                        licenceNo = user.licenceNo
                    )
                }
                Timber.d("Pre-filled surveyor: ${user.displayName} (${user.licenceNo})")
            }
        }

        viewModelScope.launch {
            cpRepo.observeAll().collect { points ->
                val selected =
                    if (preselectedId != null) points.firstOrNull { it.id == preselectedId }
                    else points.firstOrNull()
                _form.update {
                    it.copy(
                        allPoints = points,
                        selectedPoint = it.selectedPoint ?: selected
                    )
                }
                loadLogsForSelected(selected?.id)
            }
        }
    }

    private fun loadLogsForSelected(cpId: String?) {
        if (cpId == null) return
        viewModelScope.launch {
            logRepo.observeForControlPoint(cpId).take(1).collect { logs ->
                _form.update { it.copy(recentLogs = logs) }
            }
        }
    }

    fun selectPoint(p: ControlPoint) {
        _form.update { it.copy(selectedPoint = p) }
        loadLogsForSelected(p.id)
    }

    fun setSurveyorName(s: String) = _form.update { it.copy(surveyorName = s) }
    fun setLicenceNo(s: String) = _form.update { it.copy(licenceNo = s) }
    fun setEquipment(s: String) = _form.update { it.copy(equipmentType = s) }
    fun setEquipmentSerial(s: String) = _form.update { it.copy(equipmentSerial = s) }
    fun setOccupationType(t: OccupationType) = _form.update { it.copy(occupationType = t) }
    fun setStartHour(h: Int) = _form.update { it.copy(startHour = h) }
    fun setStartMinute(m: Int) = _form.update { it.copy(startMinute = m) }
    fun setEndHour(h: Int) = _form.update { it.copy(endHour = h) }
    fun setEndMinute(m: Int) = _form.update { it.copy(endMinute = m) }
    fun setPurposeNotes(s: String) = _form.update { it.copy(purposeNotes = s) }
    fun clearError() = _form.update { it.copy(errorMessage = null) }

    fun submit() {
        val s = _form.value
        val point = s.selectedPoint
            ?: run { _form.update { it.copy(errorMessage = "Select a control point") }; return }
        if (s.licenceNo.isBlank()) {
            _form.update { it.copy(errorMessage = "Licence number required") }; return
        }

        viewModelScope.launch {
            _form.update { it.copy(isSubmitting = true) }
            val start = LocalDateTime.of(s.sessionDate, LocalTime.of(s.startHour, s.startMinute))
            val end = LocalDateTime.of(s.sessionDate, LocalTime.of(s.endHour, s.endMinute))
            val log = OccupationLog(
                controlPointId = point.id,
                controlPointName = point.name,
                surveyorLicenceNo = s.licenceNo,
                surveyorName = s.surveyorName,
                equipmentType = s.equipmentType,
                equipmentSerialNo = s.equipmentSerial,
                occupationType = s.occupationType,
                sessionStartTime = start,
                sessionEndTime = end,
                purposeNotes = s.purposeNotes,
                meanSolutionLatitude = null,
                meanSolutionLongitude = null,
                meanSolutionHeight = null,
                pdop = null,
                syncStatus = SyncStatus.PENDING
            )
            runCatching { logRepo.saveLog(log) }
                .onSuccess {
                    _form.update { it.copy(isSubmitting = false, submitSuccess = true) }
                }
                .onFailure { e ->
                    Timber.e(e, "Log submission failed")
                    _form.update { it.copy(isSubmitting = false, errorMessage = e.message) }
                }
        }
    }
}