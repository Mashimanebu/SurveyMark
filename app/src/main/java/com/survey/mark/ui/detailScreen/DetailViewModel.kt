package com.survey.mark.ui.detailScreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.survey.mark.domain.model.log.OccupationLog
import com.survey.mark.domain.model.point.ControlPoint
import com.survey.mark.domain.model.report.ConditionReport
import com.survey.mark.domain.repository.ConditionReportRepository
import com.survey.mark.domain.repository.ControlPointRepository
import com.survey.mark.domain.repository.OccupationLogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val cpRepo: ControlPointRepository,
    private val reportRepo: ConditionReportRepository,
    private val logRepo: OccupationLogRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val cpId: String = checkNotNull(savedStateHandle["controlPointId"])

    val controlPoint: StateFlow<ControlPoint?> =
        cpRepo.observeById(cpId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val recentReports: StateFlow<List<ConditionReport>> =
        reportRepo.observeForControlPoint(cpId)
            .map { it.take(5) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val recentLogs: StateFlow<List<OccupationLog>> =
        logRepo.observeForControlPoint(cpId)
            .map { it.take(5) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}