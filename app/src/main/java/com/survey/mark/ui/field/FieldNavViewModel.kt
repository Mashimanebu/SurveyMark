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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class FieldNavViewModel @Inject constructor(
    private val cpRepo: ControlPointRepository,
    private val locationRepo: LocationRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val cpId: String = checkNotNull(savedStateHandle["controlPointId"])

    private val _location = MutableSharedFlow<LocationState?>(replay = 1)
    private val _target = MutableStateFlow<ControlPoint?>(null)
    private val _deviceHeading = MutableStateFlow(0f)

    val uiState: StateFlow<FieldNavState> =
        combine(_target, _location, _deviceHeading) { target, loc, heading ->
            Timber.d("combine fired — loc=%.6f,%.6f target=%s"
                .format(loc?.latitude ?: 0.0, loc?.longitude ?: 0.0, target?.name))
            if (target == null || loc == null) {
                FieldNavState(targetPoint = target, userLocation = loc)
            } else {
                val trueBearing = BearingCalculator.bearing(
                    loc.latitude, loc.longitude,
                    target.latitude, target.longitude
                )
                val distance = BearingCalculator.distanceMeters(
                    loc.latitude, loc.longitude,
                    target.latitude, target.longitude
                )
                val relativeBearing = (trueBearing - heading + 360f) % 360f

                FieldNavState(
                    targetPoint = target,
                    userLocation = loc,
                    trueBearingDeg = trueBearing,
                    bearingDeg = relativeBearing,
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
            locationRepo.getLastKnownLocation()?.let {
                _location.emit(it)
                Timber.d("FieldNav seeded: ${it.latitude}, ${it.longitude}")
            }
        }

        viewModelScope.launch {
            cpRepo.observeById(cpId).collect { _target.value = it }
        }
        viewModelScope.launch {
            while (true) {
                try {
                    locationRepo.observeLocation(highAccuracy = true)
                        .collect { loc ->
                            Timber.d(
                                "FieldNav location → %.6f, %.6f (±%.1fm)"
                                    .format(loc.latitude, loc.longitude, loc.accuracyMeters)
                            )
                            _location.emit(loc)
                        }
                } catch (e: Exception) {
                    Timber.w(e, "Location flow terminated, restarting in 1s…")
                    delay(1_000)
                }
            }
        }
    }
}