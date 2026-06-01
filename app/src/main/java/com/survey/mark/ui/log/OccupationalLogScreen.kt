package com.survey.mark.ui.log

import com.survey.mark.domain.model.log.OccupationType
import com.survey.mark.ui.detailScreen.SyncStatusChip
import com.survey.mark.ui.newmark.components.SurveyCard
import com.survey.mark.ui.newmark.components.SurveyPrimaryButton
import com.survey.mark.ui.newmark.components.SurveyTextField
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OccupationLogScreen(
    preselectedControlPointId: String?,
    onBack: () -> Unit,
    vm: OccupationLogViewModel = hiltViewModel()
) {
    val form by vm.form.collectAsState()

    LaunchedEffect(form.submitSuccess) {
        if (form.submitSuccess) onBack()
    }

    Column(Modifier
        .fillMaxSize()
        .statusBarsPadding()) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, end = 16.dp, top = 4.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, "Back", tint = MaterialTheme.colorScheme.onSurface)
            }
            Text(
                "Occupation Log",
                style = MaterialTheme.typography.headlineSmall,
                fontFamily = FontFamily.Monospace
            )
        }

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            SurveyCard(Modifier.fillMaxWidth()) {
                Text("Control Point Used", style = MaterialTheme.typography.labelSmall)
                Spacer(Modifier.height(8.dp))
                var cpExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = cpExpanded,
                    onExpandedChange = { cpExpanded = it }) {
                    OutlinedTextField(
                        value = form.selectedPoint?.let { "${it.name} (${it.id})" } ?: "Select…",
                        onValueChange = {}, readOnly = true,
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(cpExpanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = cpExpanded,
                        onDismissRequest = { cpExpanded = false },
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        form.allPoints.forEach { p ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        "${p.name} (${p.id})",
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                },
                                onClick = { vm.selectPoint(p); cpExpanded = false }
                            )
                        }
                    }
                }
            }

            SurveyCard(Modifier.fillMaxWidth()) {
                Text("Surveyor", style = MaterialTheme.typography.labelSmall)
                Spacer(Modifier.height(8.dp))
                SurveyTextField(
                    form.surveyorName,
                    vm::setSurveyorName,
                    "Full Name",
                    placeholder = "e.g. Nhlanhla Dlamini"
                )
                Spacer(Modifier.height(8.dp))
                SurveyTextField(
                    form.licenceNo,
                    vm::setLicenceNo,
                    "Licence No. *",
                    placeholder = "e.g. SZ-LS-0042"
                )
            }

            SurveyCard(Modifier.fillMaxWidth()) {
                Text("Equipment", style = MaterialTheme.typography.labelSmall)
                Spacer(Modifier.height(8.dp))
                var eqExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = eqExpanded,
                    onExpandedChange = { eqExpanded = it }) {
                    OutlinedTextField(
                        value = form.equipmentType, onValueChange = {}, readOnly = true,
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(eqExpanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = eqExpanded,
                        onDismissRequest = { eqExpanded = false },
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        OccupationEquipment.entries.forEach { eq ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        eq.label,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                },
                                onClick = { vm.setEquipment(eq.label); eqExpanded = false }
                            )
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                SurveyTextField(
                    form.equipmentSerial,
                    vm::setEquipmentSerial,
                    "Serial Number",
                    placeholder = "Optional"
                )
            }

            SurveyCard(Modifier.fillMaxWidth()) {
                Text("Occupation Type", style = MaterialTheme.typography.labelSmall)
                Spacer(Modifier.height(8.dp))
                var otExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = otExpanded,
                    onExpandedChange = { otExpanded = it }) {
                    OutlinedTextField(
                        value = form.occupationType.label, onValueChange = {}, readOnly = true,
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(otExpanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = otExpanded,
                        onDismissRequest = { otExpanded = false },
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        OccupationType.entries.forEach { ot ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        ot.label,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                },
                                onClick = { vm.setOccupationType(ot); otExpanded = false }
                            )
                        }
                    }
                }
            }

            SurveyCard(Modifier.fillMaxWidth()) {
                Text("Session Time", style = MaterialTheme.typography.labelSmall)
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    TimePickerBox(
                        label = "Start", hour = form.startHour, minute = form.startMinute,
                        onHourChange = vm::setStartHour, onMinuteChange = vm::setStartMinute,
                        modifier = Modifier.weight(1f)
                    )
                    TimePickerBox(
                        label = "End", hour = form.endHour, minute = form.endMinute,
                        onHourChange = vm::setEndHour, onMinuteChange = vm::setEndMinute,
                        modifier = Modifier.weight(1f)
                    )
                }
                val durationMin =
                    (form.endHour * 60 + form.endMinute) - (form.startHour * 60 + form.startMinute)
                if (durationMin > 0) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Duration: ${durationMin / 60}h ${durationMin % 60}m",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            SurveyTextField(
                form.purposeNotes, vm::setPurposeNotes, "Purpose / Notes",
                placeholder = "Describe survey purpose, any anomalies…",
                singleLine = false, minLines = 3, maxLines = 5
            )

            form.errorMessage?.let { msg ->
                val errorColor = MaterialTheme.colorScheme.error
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.ErrorOutline,
                        null,
                        tint = errorColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        msg,
                        style = MaterialTheme.typography.bodyMedium.copy(color = errorColor),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            SurveyPrimaryButton(
                "Save Occupation Record",
                vm::submit,
                enabled = !form.isSubmitting,
                icon = Icons.Default.Save
            )

            if (form.recentLogs.isNotEmpty()) {
                SectionLabel("Previous Occupations")
                form.recentLogs.take(5).forEach { log ->
                    SurveyCard(Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                log.occupationType.label,
                                style = MaterialTheme.typography.labelLarge
                            )
                            Text(
                                log.sessionStartTime.toLocalDate().toString(),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "${log.surveyorName} · ${log.surveyorLicenceNo}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            "${log.equipmentType} · ${log.durationMinutes} min",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                        SyncStatusChip(
                            status = log.syncStatus,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SectionLabel(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier.padding(top = 12.dp, bottom = 4.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerBox(
    label: String, hour: Int, minute: Int,
    onHourChange: (Int) -> Unit, onMinuteChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Text(label, style = MaterialTheme.typography.labelSmall)
        Spacer(Modifier.height(6.dp))
        val textFieldColors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.outline,
            cursorColor = MaterialTheme.colorScheme.primary
        )
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = "%02d".format(hour),
                onValueChange = { it.toIntOrNull()?.takeIf { n -> n in 0..23 }?.let(onHourChange) },
                modifier = Modifier.weight(1f),
                singleLine = true,
                label = { Text("HH", fontSize = 10.sp) },
                colors = textFieldColors,
                textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace),
                shape = RoundedCornerShape(8.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Text(
                ":",
                fontFamily = FontFamily.Monospace,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            OutlinedTextField(
                value = "%02d".format(minute),
                onValueChange = {
                    it.toIntOrNull()?.takeIf { n -> n in 0..59 }?.let(onMinuteChange)
                },
                modifier = Modifier.weight(1f),
                singleLine = true,
                label = { Text("MM", fontSize = 10.sp) },
                colors = textFieldColors,
                textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace),
                shape = RoundedCornerShape(8.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }
    }
}