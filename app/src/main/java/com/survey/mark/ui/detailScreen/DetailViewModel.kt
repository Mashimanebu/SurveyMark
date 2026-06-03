package com.survey.mark.ui.detailScreen

import android.location.Location
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.survey.mark.domain.model.location.LocationRepository
import com.survey.mark.domain.model.location.LocationState
import com.survey.mark.domain.model.log.OccupationLog
import com.survey.mark.domain.model.point.ControlPoint
import com.survey.mark.domain.model.report.ConditionReport
import com.survey.mark.domain.repository.ConditionReportRepository
import com.survey.mark.domain.repository.ControlPointRepository
import com.survey.mark.domain.repository.OccupationLogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val cpRepo: ControlPointRepository,
    private val reportRepo: ConditionReportRepository,
    private val logRepo: OccupationLogRepository,
    private val locationRepository: LocationRepository,
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

    private val _userLocation = MutableStateFlow<LocationState?>(null)

    val proximity: StateFlow<ProximityState> =
        combine(_userLocation, controlPoint) { loc, point ->
            if (loc == null || point == null) return@combine ProximityState()
            val results = FloatArray(2)
            Location.distanceBetween(
                loc.latitude, loc.longitude,
                point.latitude, point.longitude,
                results
            )
            ProximityState(
                distanceMeters = results[0],
                bearingDegrees = results[1],
                cardinalDirection = bearingToCardinal(results[1])
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ProximityState())

    init {
        viewModelScope.launch {
            locationRepository.getLastKnownLocation()?.let { last ->
                _userLocation.update { last }
            }
        }

        viewModelScope.launch {
            locationRepository.observeLocation(highAccuracy = false) // balanced is fine here
                .catch { Timber.w(it, "Location unavailable in Detail") }
                .collect { loc -> _userLocation.update { loc } }
        }
    }

    private fun bearingToCardinal(bearing: Float): String {
        val b = (bearing + 360f) % 360f
        return when {
            b < 11.25 -> "N"
            b < 33.75 -> "NNE"
            b < 56.25 -> "NE"
            b < 78.75 -> "ENE"
            b < 101.25 -> "E"
            b < 123.75 -> "ESE"
            b < 146.25 -> "SE"
            b < 168.75 -> "SSE"
            b < 191.25 -> "S"
            b < 213.75 -> "SSW"
            b < 236.25 -> "SW"
            b < 258.75 -> "WSW"
            b < 281.25 -> "W"
            b < 303.75 -> "WNW"
            b < 326.25 -> "NW"
            b < 348.75 -> "NNW"
            else -> "N"
        }
    }
}