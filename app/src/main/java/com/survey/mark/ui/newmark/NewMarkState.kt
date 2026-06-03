package com.survey.mark.ui.newmark

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.survey.mark.domain.model.ObservationMethod
import com.survey.mark.domain.model.ReviewStatus
import com.survey.mark.domain.model.location.LocationRepository
import com.survey.mark.domain.model.location.LocationState
import com.survey.mark.domain.model.newmark.NewMarkSubmission
import com.survey.mark.domain.model.point.ControlPoint
import com.survey.mark.domain.model.point.ControlPointType
import com.survey.mark.domain.model.point.OrderClass
import com.survey.mark.domain.model.status.ConditionStatus
import com.survey.mark.domain.model.status.SyncStatus
import com.survey.mark.domain.repository.ControlPointRepository
import com.survey.mark.domain.repository.NewMarkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

data class NewMarkState(
    val proposedName: String = "",
    val markType: ControlPointType = ControlPointType.TRIG,
    val submissionDate: LocalDate = LocalDate.now(),
    val refNumber: String = "",
    val latitudeStr: String = "",
    val longitudeStr: String = "",
    val heightStr: String = "",
    val monumentDescription: String = "",
    val accessDescription: String = "",
    val datumName: String = "WGS84",
    val regionName: String = "",
    val inkhundlaName: String = "",
    val observationMethod: ObservationMethod = ObservationMethod.GNSS_STATIC_4H,
    val durationHoursStr: String = "",
    val surveyorName: String = "",
    val licenceNo: String = "",
    val monumentPhotoUri: String? = null,
    val sketchPhotoUri: String? = null,
    val fieldNotes: String = "",
    val userLocation: LocationState? = null,
    val isSubmitting: Boolean = false,
    val submitSuccess: Boolean = false,
    val errorMessage: String? = null
)
