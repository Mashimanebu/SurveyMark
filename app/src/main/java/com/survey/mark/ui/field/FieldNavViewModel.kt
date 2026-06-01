package com.survey.mark.ui.field

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.survey.mark.domain.model.bearing.BearingCalculator
import com.survey.mark.domain.model.location.LocationRepository
import com.survey.mark.domain.model.location.LocationState
import com.survey.mark.domain.model.point.ControlPoint
import com.survey.mark.domain.repository.ControlPointRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class FieldNavViewModel @Inject constructor(
    private val cpRepo: ControlPointRepository,
    private val locationRepo: LocationRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {


    private val cpId: String = checkNotNull(savedStateHandle["controlPointId"])

    private val _location = MutableStateFlow<LocationState?>(null)
    private val _target = MutableStateFlow<ControlPoint?>(null)

    private val _deviceHeading = MutableStateFlow(0f)

    val uiState: StateFlow<FieldNavState> = combine(_target, _location, _deviceHeading) { target, loc, heading ->
        if (target == null || loc == null) {
            FieldNavState(targetPoint = target, userLocation = loc)
        } else {
            val bearing = BearingCalculator.bearing(
                loc.latitude, loc.longitude, target.latitude, target.longitude
            )
            val distance = BearingCalculator.distanceMeters(
                loc.latitude, loc.longitude, target.latitude, target.longitude
            )
            FieldNavState(
                targetPoint = target,
                userLocation = loc,
                bearingDeg = (bearing - heading + 360f) % 360f,
                distanceMeters = distance,
                isArrived = BearingCalculator.isArrived(distance)
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), FieldNavState())

    fun updateHeading(degrees: Float) {
        _deviceHeading.value = degrees
    }

    init {
        viewModelScope.launch {
            cpRepo.observeById(cpId).collect { _target.value = it }
        }
        viewModelScope.launch {
            locationRepo.observeLocation(highAccuracy = true).collect { _location.value = it }
        }
    }

}