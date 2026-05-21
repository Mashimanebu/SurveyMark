package com.survey.mark.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

object Routes {
    const val DIRECTORY = "directory"
    const val DETAIL = "detail/{controlPointId}"
    const val FIELD_NAV = "field_nav/{controlPointId}"
    const val REPORT = "report?controlPointId={controlPointId}"
    const val LOG = "log?controlPointId={controlPointId}"

    fun detail(id: String) = "detail/$id"
    fun fieldNav(id: String) = "field_nav/$id"
    fun report(id: String? = null) = if (id != null) "report?controlPointId=$id" else "report"
    fun log(id: String? = null) = if (id != null) "log?controlPointId=$id" else "log"
}

@Composable
fun SurveyDrawer() {

}

data class DrawerItem(
    val route: String,
    val label: String,
    val description: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector = icon,
    val badge: String? = null
)