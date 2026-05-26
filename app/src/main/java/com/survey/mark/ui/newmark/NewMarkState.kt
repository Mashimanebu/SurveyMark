package com.survey.mark.ui.newmark

import com.survey.mark.domain.model.ControlPointType

data class NewMarkState(
    val proposedName: String = "",
    val markType: ControlPointType = ControlPointType.TOWN_SURVEY_MARK,
    val latitudeStr: String = "",
    val longitudeStr: String = "",
    val heightStr: String = "",
    val monumentDescription: String = "",
    val accessDescription: String = "",
    //val observationMethod: ObservationMethod = ObservationMethod.GNSS_STATIC_4H,
    val durationHoursStr: String = "",
    val surveyorName: String = "",
    val licenceNo: String = "",
    val monumentPhotoUri: String? = null,
    val sketchPhotoUri: String? = null,
    val fieldNotes: String = "",
   // val userLocation: LocationState? = null,
    val isSubmitting: Boolean = false,
    val submitSuccess: Boolean = false,
    val errorMessage: String? = null,
    val useGpsCoordinates: Boolean = false
)