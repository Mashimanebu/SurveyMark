package com.survey.mark.ui.home.components

import android.R.attr.type
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChangeHistory
import androidx.compose.material.icons.filled.HorizontalRule
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.SatelliteAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.survey.mark.domain.model.point.ControlPointType

@Composable
fun MarkTypeIcon(
    type    : ControlPointType,
    size    : Int = 44,
    modifier: Modifier = Modifier
) {
    val (icon, containerColor, contentColor) = when (type) {
        ControlPointType.TRIG ->
            Triple(
                Icons.Default.ChangeHistory,
                MaterialTheme.colorScheme.primaryContainer,
                MaterialTheme.colorScheme.primary
            )
        ControlPointType.TOWN_SURVEY_MARK ->
            Triple(
                Icons.Default.LocationCity,
                MaterialTheme.colorScheme.secondaryContainer,
                MaterialTheme.colorScheme.secondary
            )
        ControlPointType.REFERENCE_MARK ->
            Triple(
                Icons.Default.MyLocation,
                MaterialTheme.colorScheme.tertiaryContainer,
                MaterialTheme.colorScheme.tertiary
            )
        ControlPointType.BENCHMARK ->
            Triple(
                Icons.Default.HorizontalRule,
                MaterialTheme.colorScheme.surfaceVariant,
                MaterialTheme.colorScheme.onSurfaceVariant
            )
        ControlPointType.GPS_BASE_STATION ->
            Triple(
                Icons.Default.SatelliteAlt,
                MaterialTheme.colorScheme.errorContainer,
                MaterialTheme.colorScheme.error
            )
    }


    Box(
        modifier = modifier
            .size(size.dp)
            .clip(RoundedCornerShape((size / 3).dp))
            .background(containerColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector        = icon,
            contentDescription = type.name,
            tint               = contentColor,
            modifier           = Modifier.size((size * 0.5).dp)
        )
    }
}