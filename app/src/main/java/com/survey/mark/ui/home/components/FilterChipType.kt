package com.survey.mark.ui.home.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

enum class FilterChipType(val label: String) {
    ALL("All"),
    TRIGONOMETRIC("Trigonometric"),
    GPS_BASE_STATION("GPS Base Station"),
    DESTROYED("Destroyed")
}
@Composable
fun FilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
){
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(52.dp),
        color = if (selected) MaterialTheme.colorScheme.primary
        else
            MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = if (selected) 4.dp else 0.dp
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            color = if (selected)
                MaterialTheme.colorScheme.onPrimary
            else
                MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
