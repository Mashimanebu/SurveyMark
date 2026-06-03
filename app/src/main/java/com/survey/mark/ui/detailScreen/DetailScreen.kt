package com.survey.mark.ui.detailScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.survey.mark.domain.model.log.OccupationLog
import com.survey.mark.domain.model.point.ControlPoint
import com.survey.mark.domain.model.report.ConditionReport
import com.survey.mark.domain.model.status.SyncStatus
import com.survey.mark.ui.home.components.ConditionBadge
import com.survey.mark.ui.newmark.components.SurveyCard

@Composable
fun ControlPointDetailScreen(
    markId: String,
    onNavigateClick: () -> Unit,
    onReportClick: () -> Unit,
    onLogClick: () -> Unit,
    onBack: () -> Unit,
    vm: DetailViewModel = hiltViewModel()
) {
    val cp by vm.controlPoint.collectAsState()
    val reports by vm.recentReports.collectAsState()
    val logs by vm.recentLogs.collectAsState()
    val proximity by vm.proximity.collectAsState()

    cp?.let { point ->
        DetailContent(
            point = point,
            recentReports = reports,
            recentLogs = logs,
            proximity = proximity,
            onNavigateClick = onNavigateClick,
            onReportClick = onReportClick,
            onLogClick = onLogClick,
            onBack = onBack
        )
    } ?: Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun DetailContent(
    point: ControlPoint,
    recentReports: List<ConditionReport>,
    recentLogs: List<OccupationLog>,
    proximity: ProximityState,
    onNavigateClick: () -> Unit,
    onReportClick: () -> Unit,
    onLogClick: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        // ── Top bar ───────────────────────────────────────────────────
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, "Back", tint = MaterialTheme.colorScheme.onSurface)
            }
            Spacer(Modifier.weight(1f))
            SyncStatusChip(
                status = if (point.isSynced) SyncStatus.SYNCED else SyncStatus.PENDING,
                modifier = Modifier.padding(end = 16.dp)
            )
        }

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // ── Hero card: name, coords, distance, direction ──────────
            SurveyCard(Modifier.fillMaxWidth()) {
                Text(point.name, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(2.dp))
                Text(
                    "${point.type.name} · ${point.orderClass.name}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(10.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(Modifier.height(10.dp))

                // Coordinates
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CoordChip("Latitude", "%.6f°".format(point.latitude), Modifier.weight(1f))
                    CoordChip("Longitude", "%.6f°".format(point.longitude), Modifier.weight(1f))
                }
                point.ellipsoidalHeight?.let { h ->
                    Spacer(Modifier.height(6.dp))
                    CoordChip("Ellipsoidal Height", "%.3f m".format(h), Modifier.fillMaxWidth())
                }

                Spacer(Modifier.height(10.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(Modifier.height(10.dp))

                // Distance + direction — live from user location
                val distText = proximity.distanceMeters?.let { d ->
                    if (d >= 1000f) "${"%.2f".format(d / 1000f)} km"
                    else "${d.toInt()} m"
                } ?: "Acquiring…"

                val dirText = if (proximity.cardinalDirection != null && proximity.bearingDegrees != null)
                    "${proximity.cardinalDirection} · ${proximity.bearingDegrees.toInt()}°"
                else "—"

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CoordChip("Distance", distText, Modifier.weight(1f))
                    CoordChip("Direction", dirText, Modifier.weight(1f))
                }

                Spacer(Modifier.height(10.dp))
                ConditionBadge(point.condition)
            }

            // ── Meta card ─────────────────────────────────────────────
            SurveyCard(Modifier.fillMaxWidth()) {
                MetaRow(
                    Icons.Default.LocationCity,
                    "District",
                    "${point.regionName}, ${point.inkhundlaName}"
                )
                if (!point.establishedDate.isNullOrBlank()) {
                    HorizontalDivider(
                        Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                    MetaRow(Icons.Default.CalendarToday, "Established", point.establishedDate)
                }
                if (!point.lastVerifiedDate.isNullOrBlank()) {
                    HorizontalDivider(
                        Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                    MetaRow(Icons.Default.Verified, "Last Verified", point.lastVerifiedDate)
                }
                HorizontalDivider(
                    Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                MetaRow(Icons.Default.Description, "Description", point.description)
                if (point.accessNotes.isNotBlank()) {
                    HorizontalDivider(
                        Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                    MetaRow(Icons.Default.DirectionsCar, "Access", point.accessNotes)
                }
            }

            // ── Action buttons ────────────────────────────────────────
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onNavigateClick,
                    modifier = Modifier.weight(1f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Navigation, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Navigate", style = MaterialTheme.typography.labelLarge)
                }
                OutlinedButton(
                    onClick = onReportClick,
                    modifier = Modifier.weight(1f).height(48.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = SolidColor(MaterialTheme.colorScheme.outlineVariant)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        Icons.Default.CameraAlt, null, Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "Report",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            OutlinedButton(
                onClick = onLogClick,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = SolidColor(MaterialTheme.colorScheme.outlineVariant)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    Icons.Default.EditNote, null, Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    "Log Occupation",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // ── Recent reports ────────────────────────────────────────
            if (recentReports.isNotEmpty()) {
                SectionLabel("Recent Condition Reports")
                recentReports.forEach { report ->
                    SurveyCard(Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ConditionBadge(report.condition)
                            Text(
                                report.reportedAt.toLocalDate().toString(),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        if (report.fieldNotes.isNotBlank()) {
                            Spacer(Modifier.height(6.dp))
                            Text(
                                report.fieldNotes,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "By ${report.reporterName} (${report.reporterLicenceNo})",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            // ── Recent occupations ────────────────────────────────────
            if (recentLogs.isNotEmpty()) {
                SectionLabel("Recent Occupations")
                recentLogs.forEach { log ->
                    SurveyCard(Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                        Text(log.occupationType.label, style = MaterialTheme.typography.labelLarge)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "${log.surveyorName} (${log.surveyorLicenceNo})",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            "${log.equipmentType} · ${log.durationMinutes} min",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                        Text(
                            log.sessionStartTime.toLocalDate().toString(),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

// ── Private helpers ───────────────────────────────────────────────────────────

@Composable
private fun CoordChip(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier.padding(top = 4.dp, bottom = 4.dp)
    )
}

@Composable
private fun MetaRow(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(18.dp).padding(top = 2.dp)
        )
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall)
            Text(
                value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}