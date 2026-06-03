package com.survey.mark.ui.report

import com.survey.mark.domain.model.status.ConditionStatus
import com.survey.mark.ui.newmark.components.GpsAccuracyChip
import com.survey.mark.ui.newmark.components.SurveyCard
import com.survey.mark.ui.newmark.components.SurveyPrimaryButton
import com.survey.mark.ui.newmark.components.SurveyTextField


import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ConditionReportScreen(
    preselectedControlPointId: String?,
    onBack: () -> Unit,
    onSubmitSuccess: () -> Unit,
    vm: ConditionReportViewModel = hiltViewModel()
) {
    val form by vm.form.collectAsState()
    val context = LocalContext.current

    val cameraPerm = rememberPermissionState(Manifest.permission.CAMERA)

    var pendingPhotoUri by remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) vm.setPhotoUri(pendingPhotoUri?.toString())
    }

    LaunchedEffect(form.submitSuccess) {
        if (form.submitSuccess) onSubmitSuccess()
    }

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        // Header
        Row(
            Modifier.fillMaxWidth().padding(start = 4.dp, end = 16.dp, top = 4.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, "Back", tint = MaterialTheme.colorScheme.onSurface)
            }
            Column {
                Text("Condition Report", style = MaterialTheme.typography.headlineSmall, fontFamily = FontFamily.Monospace)
                form.location?.let { GpsAccuracyChip(it.accuracyMeters) }
            }
        }

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Control point selector
            SurveyCard(Modifier.fillMaxWidth()) {
                Text("Control Point", style = MaterialTheme.typography.labelSmall)
                Spacer(Modifier.height(8.dp))
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = form.selectedPoint?.let { "${it.name} (${it.id})" } ?: "Select…",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
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
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        form.allPoints.forEach { point ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        "${point.name} (${point.id})",
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                },
                                onClick = { vm.selectPoint(point); expanded = false }
                            )
                        }
                    }
                }
            }

            SurveyCard(Modifier.fillMaxWidth()) {
                Text("Condition", style = MaterialTheme.typography.labelSmall)
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ConditionButton(
                        label = "Intact",
                        icon = Icons.Default.CheckCircle,
                        color = MaterialTheme.colorScheme.primary,
                        selected = form.condition == ConditionStatus.INTACT,
                        onClick = { vm.setCondition(ConditionStatus.INTACT) },
                        modifier = Modifier.weight(1f)
                    )
                    ConditionButton(
                        label = "Disturbed",
                        icon = Icons.Default.Warning,
                        color = MaterialTheme.colorScheme.tertiary,
                        selected = form.condition == ConditionStatus.DISTURBED,
                        onClick = { vm.setCondition(ConditionStatus.DISTURBED) },
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ConditionButton(
                        label = "Destroyed",
                        icon = Icons.Default.LocalFireDepartment,
                        color = MaterialTheme.colorScheme.error,
                        selected = form.condition == ConditionStatus.DESTROYED,
                        onClick = { vm.setCondition(ConditionStatus.DESTROYED) },
                        modifier = Modifier.weight(1f)
                    )
                    ConditionButton(
                        label = "Not Found",
                        icon = Icons.Default.SearchOff,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        selected = form.condition == ConditionStatus.NOT_FOUND,
                        onClick = { vm.setCondition(ConditionStatus.NOT_FOUND) },
                        modifier = Modifier.weight(1f)
                    )
                }

                if (form.condition == ConditionStatus.DESTROYED) {
                    Spacer(Modifier.height(8.dp))
                    val errorColor = MaterialTheme.colorScheme.error
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.errorContainer)
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.NotificationsActive,
                            null,
                            tint = errorColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            "Surveyor General will be alerted immediately",
                            style = MaterialTheme.typography.labelMedium.copy(color = errorColor)
                        )
                    }
                }
            }

            SurveyCard(Modifier.fillMaxWidth()) {
                Text("Photo Evidence", style = MaterialTheme.typography.labelSmall)
                Spacer(Modifier.height(8.dp))
                if (form.photoUri != null) {
                    Box(Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(10.dp))) {
                        AsyncImage(
                            model = form.photoUri,
                            contentDescription = "Beacon photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        IconButton(
                            onClick = { vm.setPhotoUri(null) },
                            modifier = Modifier.align(Alignment.TopEnd)
                        ) {
                            Icon(Icons.Default.Cancel, "Remove photo", tint = Color.White)
                        }
                    }
                } else {
                    val outlineColor = MaterialTheme.colorScheme.outline
                    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
                    val outlineVariant = MaterialTheme.colorScheme.outlineVariant
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(surfaceVariant)
                            .border(1.5.dp, outlineVariant, RoundedCornerShape(10.dp))
                            .clickable {
                                if (cameraPerm.status.isGranted) {
                                    val uri = vm.createPhotoFile()
                                    pendingPhotoUri = uri
                                    cameraLauncher.launch(uri)
                                } else {
                                    cameraPerm.launchPermissionRequest()
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.CameraAlt,
                                null,
                                tint = outlineColor,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(Modifier.height(6.dp))
                            Text("Tap to photograph beacon", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            SurveyTextField(
                value = form.fieldNotes,
                onValueChange = vm::setFieldNotes,
                label = "Field Notes",
                placeholder = "Access conditions, vegetation, offset from original position…",
                singleLine = false,
                minLines = 3,
                maxLines = 6
            )

            SurveyCard(Modifier.fillMaxWidth()) {
                Text("Surveyor Details", style = MaterialTheme.typography.labelSmall)
                Spacer(Modifier.height(8.dp))
                SurveyTextField(
                    value = form.reporterName,
                    onValueChange = vm::setReporterName,
                    label = "Full Name",
                    placeholder = "e.g. Sihlangu Maphanga"
                )
                Spacer(Modifier.height(8.dp))
                SurveyTextField(
                    value = form.reporterLicenceNo,
                    onValueChange = vm::setLicenceNo,
                    label = "Licence Number *",
                    placeholder = "e.g. SD-007"
                )
            }

            form.location?.let { loc ->
                val primaryColor = MaterialTheme.colorScheme.primary
                SurveyCard(Modifier.fillMaxWidth()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            null,
                            tint = primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text("GPS Stamp (auto)", style = MaterialTheme.typography.labelSmall)
                            Text(
                                "%.6f°, %.6f° ±%.1fm".format(loc.latitude, loc.longitude, loc.accuracyMeters),
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                color = primaryColor
                            )
                        }
                    }
                }
            }

            form.errorMessage?.let { msg ->
                val errorColor = MaterialTheme.colorScheme.error
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.errorContainer)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.ErrorOutline, null, tint = errorColor, modifier = Modifier.size(18.dp))
                    Text(
                        msg,
                        style = MaterialTheme.typography.bodyMedium.copy(color = errorColor),
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = vm::clearError, modifier = Modifier.size(20.dp)) {
                        Icon(Icons.Default.Close, null, tint = errorColor)
                    }
                }
            }

            SurveyPrimaryButton(
                text = if (form.isSubmitting) "Submitting…" else "Submit Condition Report",
                onClick = vm::submit,
                enabled = !form.isSubmitting,
                icon = Icons.Default.Send
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ConditionButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val outlineVariant = MaterialTheme.colorScheme.outlineVariant
    val outline = MaterialTheme.colorScheme.outline

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) color.copy(alpha = 0.15f) else surfaceVariant)
            .border(1.5.dp, if (selected) color else outlineVariant, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            icon,
            contentDescription = label,
            tint = if (selected) color else outline,
            modifier = Modifier.size(26.dp)
        )
        Spacer(Modifier.height(5.dp))
        Text(
            label,
            style = MaterialTheme.typography.labelMedium.copy(
                color = if (selected) color else outline
            )
        )
    }
}