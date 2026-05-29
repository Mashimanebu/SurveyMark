package com.survey.mark.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.survey.mark.domain.model.location.LocationRepository
import com.survey.mark.domain.model.location.LocationState
import com.survey.mark.domain.model.point.ControlPoint
import com.survey.mark.domain.model.point.ControlPointType
import com.survey.mark.domain.model.status.ConditionStatus
import com.survey.mark.domain.repository.ControlPointRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.collections.emptyList

sealed class DirectoryFilter(val label: String) {
    object All : DirectoryFilter("All")
    data class ByType(val type: ControlPointType) : DirectoryFilter(type.name)
    data class ByCondition(val condition: ConditionStatus) : DirectoryFilter(condition.name)
}


@HiltViewModel
class DirectoryViewModel @Inject constructor(
    private val repository: ControlPointRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    private val _filter = MutableStateFlow<DirectoryFilter>(DirectoryFilter.All)
    val filter = _filter.asStateFlow()

    private val _location = MutableStateFlow<LocationState?>(null)
    val location = _location.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val controlPoints: StateFlow<List<ControlPoint>> =
        combine(_query, _filter, _location) { query, filter, loc ->
            Triple(query, filter, loc)
        }.flatMapLatest { (query, filter, loc) ->
            when {
                query.isNotBlank() -> repository.observeSearch(query)
                filter is DirectoryFilter.ByType -> repository.observeByType(filter.type)
                filter is DirectoryFilter.ByCondition -> repository.observeByCondition(filter.condition)
                loc != null -> repository.observeAllSortedByDistance(loc.latitude, loc.longitude)
                else -> repository.observeAll()
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        viewModelScope.launch { repository.seedIfEmpty() }
        viewModelScope.launch {
            locationRepository.observeLocation(highAccuracy = false)
                .catch { Timber.w(it, "Location flow error — GPS unavailable") }
                .collect { _location.value = it }
        }
    }

    fun onQueryChange(q: String) {
        _query.value = q
    }

    fun onFilterChange(f: DirectoryFilter) {
        _filter.value = f; _query.value = ""
    }
}
