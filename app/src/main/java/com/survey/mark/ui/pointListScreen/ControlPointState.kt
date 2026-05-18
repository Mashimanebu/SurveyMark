package com.survey.mark.ui.pointListScreen

import com.survey.mark.domain.model.ConditionStatus
import com.survey.mark.domain.model.ControlPoint

enum class SortMode { NAME, NUMBER, DISTRICT, PROXIMITY, CONDITION }
enum class FilterType { ALL, TRIG, TOWN_SURVEY_MARK, REFERENCE_MARK, BENCHMARK }
data class ControlPointState(
    val points: List<ControlPoint> = emptyList(),
    val query: String = "",
    val sortMode: SortMode = SortMode.NAME,
    val filterType: FilterType = FilterType.ALL,
    val filterDistrict: String = "All",
    val filterCondition: ConditionStatus? = null,
    val isLoading: Boolean = true,
    val userLocation: Pair<Double, Double>? = null
)
