package com.survey.mark.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.survey.mark.domain.model.status.ConditionStatus

@Composable
fun ConditionBadge(
    condition: ConditionStatus,
    modifier : Modifier = Modifier
) {
    val (label, containerColor, textColor) = when (condition) {
        ConditionStatus.INTACT ->
            Triple(
                "Intact",
                MaterialTheme.colorScheme.primaryContainer,
                MaterialTheme.colorScheme.primary
            )
        ConditionStatus.DISTURBED ->
            Triple(
                "Disturbed",
                MaterialTheme.colorScheme.secondaryContainer,
                MaterialTheme.colorScheme.secondary
            )
        ConditionStatus.DESTROYED ->
            Triple(
                "Destroyed",
                MaterialTheme.colorScheme.errorContainer,
                MaterialTheme.colorScheme.error
            )
        ConditionStatus.NOT_FOUND ->
            Triple(
                "Not Found",
                MaterialTheme.colorScheme.surfaceVariant,
                MaterialTheme.colorScheme.onSurfaceVariant
            )
        ConditionStatus.UNKNOWN ->
            Triple(
                "Unverified",
                MaterialTheme.colorScheme.surfaceVariant,
                MaterialTheme.colorScheme.onSurfaceVariant
            )
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(containerColor)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text  = label,
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
    }
}
