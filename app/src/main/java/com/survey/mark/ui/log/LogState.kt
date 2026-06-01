package com.survey.mark.ui.log

import com.survey.mark.domain.model.log.OccupationLog
import com.survey.mark.domain.model.log.OccupationType
import com.survey.mark.domain.model.point.ControlPoint
import java.time.LocalDate

data class LogFormState(
    val allPoints: List<ControlPoint> = emptyList(),
    val selectedPoint: ControlPoint? = null,
    val surveyorName: String = "",
    val licenceNo: String = "",
    val equipmentType: String = OccupationEquipment.entries.first().label,
    val equipmentSerial: String = "",
    val occupationType: OccupationType = OccupationType.CADASTRAL_BASE,
    val sessionDate: LocalDate = LocalDate.now(),
    val startHour: Int = 7,
    val startMinute: Int = 0,
    val endHour: Int = 10,
    val endMinute: Int = 0,
    val purposeNotes: String = "",
    val isSubmitting: Boolean = false,
    val submitSuccess: Boolean = false,
    val errorMessage: String? = null,
    val recentLogs: List<OccupationLog> = emptyList()
)

enum class OccupationEquipment(val label: String) {
    LEICA_GS18("Leica GS18 T (RTK GNSS)"),
    TRIMBLE_R10("Trimble R10 (Static)"),
    TOPCON_HIPER("Topcon HiPer HR"),
    STONEX_S9("Stonex S9III GNSS"),
    LEICA_TS16("Leica TS16 Total Station"),
    TRIMBLE_S7("Trimble S7 Total Station"),
    OTHER("Other (specify in notes)")
}