package com.survey.mark.routing


object Routes {
    const val DIRECTORY = "directory"
    const val DETAIL = "detail/{markId}"
    const val FIELD_NAV = "field_nav/{controlPointId}"
    const val REPORT = "report?controlPointId={controlPointId}"
    const val LOG = "log?controlPointId={controlPointId}"
    const val NEW_MARK = "new_mark"

    fun detail(markId: String) = "detail/$markId"
    fun fieldNav(id: String) = "field_nav/$id"
    fun report(id: String? = null) = if (id != null) "report?controlPointId=$id" else "report"
    fun log(id: String? = null) = if (id != null) "log?controlPointId=$id" else "log"
}

