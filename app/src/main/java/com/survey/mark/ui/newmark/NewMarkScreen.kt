package com.survey.mark.ui.newmark

import android.Manifest
import android.graphics.fonts.FontFamily
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.survey.mark.domain.model.ObservationMethod
import com.survey.mark.domain.model.point.ControlPointType
import com.survey.mark.ui.newmark.components.GpsAccuracyChip
import com.survey.mark.ui.newmark.components.PhotoBox
import com.survey.mark.ui.newmark.components.SurveyCard
import com.survey.mark.ui.newmark.components.SurveyPrimaryButton
import com.survey.mark.ui.newmark.components.SurveyTextField


@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun NewMarkScreen(
    onBack: () -> Unit,
    onSubmitSuccess: () -> Unit,
    vm: NewMarkViewModel = hiltViewModel()
) {
    val form by vm.form.collectAsState()
    val cameraPerm = rememberPermissionState(Manifest.permission.CAMERA)

    var pendingMonumentUri by remember { mutableStateOf<Uri?>(null) }
    var pendingSketchUri by remember { mutableStateOf<Uri?>(null) }

    val monumentCamera =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { ok ->
            if (ok) vm.setMonumentPhoto(pendingMonumentUri?.toString())
        }
    val sketchCamera =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { ok ->
            if (ok) vm.setSketchPhoto(pendingSketchUri?.toString())
        }

    LaunchedEffect(form.submitSuccess) {
        if (form.submitSuccess) onSubmitSuccess()
    }

    Column(Modifier.fillMaxSize().statusBarsPadding()) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, end = 16.dp, top = 4.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                "New Control Mark",
                style = MaterialTheme.typography.headlineSmall
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
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier
                            .size(18.dp)
                            .padding(top = 2.dp)
                    )
                    Text(
                        "Submitted marks are provisional until reviewed and gazetted by the Surveyor General. " +
                                "You will be notified when your submission is approved or requires changes.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            SurveyCard(Modifier.fillMaxWidth()) {
                Text("Mark Details", style = MaterialTheme.typography.labelSmall)
                Spacer(Modifier.height(8.dp))
                SurveyTextField(
                    form.proposedName,
                    vm::setProposedName,
                    "Proposed Name *",
                    placeholder = "e.g. NHLANGANO TRIG or BM-044"
                )
                Spacer(Modifier.height(8.dp))

                var typeExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = typeExpanded,
                    onExpandedChange = { typeExpanded = it }
                ) {
                    OutlinedTextField(
                        value = form.markType.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Mark Type", fontSize = 12.sp) },
                        modifier = Modifier
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(typeExpanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            cursorColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = typeExpanded,
                        onDismissRequest = { typeExpanded = false },
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        ControlPointType.entries.forEach { t ->
                            DropdownMenuItem(
                                text = {
                                    Text(t.name, color = MaterialTheme.colorScheme.onSurface)
                                },
                                onClick = { vm.setMarkType(t); typeExpanded = false }
                            )
                        }
                    }
                }
            }

            SurveyCard(Modifier.fillMaxWidth()) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Coordinates (WGS84)", style = MaterialTheme.typography.labelSmall)
                    TextButton(
                        onClick = vm::fillGpsCoordinates,
                        enabled = form.userLocation != null
                    ) {
                        Icon(
                            Icons.Default.MyLocation,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = if (form.userLocation != null)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "Use GPS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (form.userLocation != null)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SurveyTextField(
                        form.latitudeStr, vm::setLatitude, "Latitude °",
                        placeholder = "-26.5012", modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                    SurveyTextField(
                        form.longitudeStr, vm::setLongitude, "Longitude °",
                        placeholder = "31.2044", modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                }
                Spacer(Modifier.height(8.dp))
                SurveyTextField(
                    form.heightStr, vm::setHeight, "Ellipsoidal Height (m) *",
                    placeholder = "Derived from GNSS observation",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )

                form.userLocation?.let { loc ->
                    Spacer(Modifier.height(6.dp))
                    GpsAccuracyChip(loc.accuracyMeters)
                }
            }

            SurveyCard(Modifier.fillMaxWidth()) {
                Text("Monument", style = MaterialTheme.typography.labelSmall)
                Spacer(Modifier.height(8.dp))
                SurveyTextField(
                    form.monumentDescription, vm::setMonumentDescription,
                    "Monument Description",
                    placeholder = "Concrete pillar, plate, rod",
                    singleLine = false, minLines = 2, maxLines = 4
                )
                Spacer(Modifier.height(8.dp))
                SurveyTextField(
                    form.accessDescription, vm::setAccessDescription,
                    "Access / Location Notes",
                    placeholder = "Directions, distance from road, landmarks…",
                    singleLine = false, minLines = 2, maxLines = 4
                )
            }

            SurveyCard(Modifier.fillMaxWidth()) {
                Text("Observation", style = MaterialTheme.typography.labelSmall)
                Spacer(Modifier.height(8.dp))
                var obsExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = obsExpanded,
                    onExpandedChange = { obsExpanded = it }
                ) {
                    OutlinedTextField(
                        value = form.observationMethod.label,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Method", fontSize = 12.sp) },
                        modifier = Modifier
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(obsExpanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            cursorColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = obsExpanded,
                        onDismissRequest = { obsExpanded = false },
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        ObservationMethod.entries.forEach { m ->
                            DropdownMenuItem(
                                text = {
                                    Text(m.label, color = MaterialTheme.colorScheme.onSurface)
                                },
                                onClick = { vm.setObservationMethod(m); obsExpanded = false }
                            )
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                SurveyTextField(
                    form.durationHoursStr, vm::setDurationHours, "Duration (hours)",
                    placeholder = "e.g. 4.5",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
            }

            SurveyCard(Modifier.fillMaxWidth()) {
                Text("Photos & Sketch", style = MaterialTheme.typography.labelSmall)
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PhotoBox(
                        label = "Monument Photo",
                        uri = form.monumentPhotoUri,
                        onCapture = {
                            if (cameraPerm.status.isGranted) {
                                val uri = vm.createPhotoFile("monument")
                                pendingMonumentUri = uri
                                monumentCamera.launch(uri)
                            } else cameraPerm.launchPermissionRequest()
                        },
                        onClear = { vm.setMonumentPhoto(null) },
                        modifier = Modifier.weight(1f)
                    )
                    PhotoBox(
                        label = "Locality Sketch",
                        uri = form.sketchPhotoUri,
                        onCapture = {
                            if (cameraPerm.status.isGranted) {
                                val uri = vm.createPhotoFile("sketch")
                                pendingSketchUri = uri
                                sketchCamera.launch(uri)
                            } else cameraPerm.launchPermissionRequest()
                        },
                        onClear = { vm.setSketchPhoto(null) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            SurveyCard(Modifier.fillMaxWidth()) {
                Text("Submitting Surveyor", style = MaterialTheme.typography.labelSmall)
                Spacer(Modifier.height(8.dp))
                SurveyTextField(
                    form.surveyorName,
                    vm::setSurveyorName,
                    "Full Name",
                    placeholder = "e.g. Sihlangu Maphanga"
                )
                Spacer(Modifier.height(8.dp))
                SurveyTextField(
                    form.licenceNo,
                    vm::setLicenceNo,
                    "Licence No. *",
                    placeholder = "e.g. SD-0045"
                )
            }

            SurveyTextField(
                form.fieldNotes, vm::setFieldNotes, "Additional Field Notes",
                placeholder = "Any information to assist the Surveyor General's review…",
                singleLine = false, minLines = 3, maxLines = 6
            )

            form.errorMessage?.let { msg ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.errorContainer)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.ErrorOutline,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        msg,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = vm::clearError, modifier = Modifier.size(20.dp)) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            SurveyPrimaryButton(
                text = if (form.isSubmitting) "Submitting…" else "Submit for Surveyor General Review",
                onClick = vm::submit,
                enabled = !form.isSubmitting,
                icon = Icons.Default.Send
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}