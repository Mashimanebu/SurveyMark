package com.survey.mark.ui.field

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.survey.mark.domain.model.bearing.BearingCalculator
import com.survey.mark.ui.newmark.components.GpsAccuracyChip
import com.survey.mark.ui.newmark.components.SurveyCard
import com.survey.mark.ui.newmark.components.SurveyPrimaryButton
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun FieldNavScreen(
    controlPointId: String,
    onBack: () -> Unit,
    onArrived: () -> Unit,
    vm: FieldNavViewModel = hiltViewModel()
) {
    val state by vm.uiState.collectAsState()
    val context = LocalContext.current

    val locationPerm = rememberPermissionState(android.Manifest.permission.ACCESS_FINE_LOCATION)

    LaunchedEffect(Unit) {
        if (!locationPerm.status.isGranted) locationPerm.launchPermissionRequest()
    }

    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        val gravity = FloatArray(3)
        val geomagnetic = FloatArray(3)
        var hasGravity = false
        var hasMag = false

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                when (event.sensor.type) {
                    Sensor.TYPE_ACCELEROMETER -> {
                        val alpha = 0.8f
                        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0]
                        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1]
                        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2]
                        hasGravity = true
                    }
                    Sensor.TYPE_MAGNETIC_FIELD -> {
                        val alpha = 0.5f
                        geomagnetic[0] = alpha * geomagnetic[0] + (1 - alpha) * event.values[0]
                        geomagnetic[1] = alpha * geomagnetic[1] + (1 - alpha) * event.values[1]
                        geomagnetic[2] = alpha * geomagnetic[2] + (1 - alpha) * event.values[2]
                        hasMag = true
                    }
                }
                if (!hasGravity || !hasMag) return

                val rotationMatrix = FloatArray(9)
                val remappedMatrix = FloatArray(9)
                val orientationAngles = FloatArray(3)

                if (SensorManager.getRotationMatrix(rotationMatrix, null, gravity, geomagnetic)) {
                    SensorManager.remapCoordinateSystem(
                        rotationMatrix,
                        SensorManager.AXIS_X,
                        SensorManager.AXIS_Z,
                        remappedMatrix
                    )
                    SensorManager.getOrientation(remappedMatrix, orientationAngles)
                    val azimuthDeg = Math.toDegrees(orientationAngles[0].toDouble()).toFloat()
                    vm.updateHeading((azimuthDeg + 360f) % 360f)
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        if (accelerometer != null && magnetometer != null) {
            sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_GAME)
            sensorManager.registerListener(listener, magnetometer, SensorManager.SENSOR_DELAY_GAME)
        }

        onDispose { sensorManager.unregisterListener(listener) }
    }

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {

        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, "Back", tint = MaterialTheme.colorScheme.onSurface)
            }
            Column(Modifier.weight(1f)) {
                Text("Navigating to", style = MaterialTheme.typography.labelSmall)
                Text(
                    state.targetPoint?.name ?: "…",
                    style = MaterialTheme.typography.headlineSmall,
                    fontFamily = FontFamily.Monospace
                )
            }
            state.userLocation?.let {
                GpsAccuracyChip(it.accuracyMeters, Modifier.padding(end = 12.dp))
            }
        }

        if (!locationPerm.status.isGranted) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.LocationOff, null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    "Location permission required for navigation",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = { locationPerm.launchPermissionRequest() }) {
                    Text("Grant")
                }
            }
        }

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(8.dp))

            CompassRose(
                bearingDeg = state.bearingDeg,
                trueBearingDeg = state.trueBearingDeg,
                isArrived = state.isArrived,
                modifier = Modifier.size(240.dp)
            )

            Spacer(Modifier.height(16.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MetricCard(
                    label = "Distance",
                    value = if (state.userLocation != null)
                        BearingCalculator.formatDistance(state.distanceMeters)
                    else "—",
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    label = "Bearing",
                    value = if (state.userLocation != null)
                        BearingCalculator.formatBearing(state.trueBearingDeg)  // true bearing
                    else "—",
                    valueColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(10.dp))

            state.targetPoint?.let { target ->
                SurveyCard(Modifier.fillMaxWidth()) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                "Your Position",
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            state.userLocation?.let { loc ->
                                Text(
                                    "%.6f°".format(loc.latitude),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    "%.6f°".format(loc.longitude),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            } ?: Text(
                                "Acquiring…",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                        VerticalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant,
                            modifier = Modifier.height(60.dp)
                        )
                        Column(Modifier.weight(1f)) {
                            Text(
                                "Target",
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            Text(
                                "%.6f°".format(target.latitude),
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                "%.6f°".format(target.longitude),
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            state.targetPoint?.accessNotes?.takeIf { it.isNotBlank() }?.let { notes ->
                SurveyCard(Modifier.fillMaxWidth()) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Icon(
                            Icons.Default.DirectionsCar, null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp).padding(top = 2.dp)
                        )
                        Column {
                            Text("Access Notes", style = MaterialTheme.typography.labelSmall)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                notes,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            if (state.isArrived) {
                SurveyPrimaryButton(
                    text = "Arrived — File Condition Report",
                    onClick = onArrived,
                    icon = Icons.Default.CameraAlt
                )
            } else {
                OutlinedButton(
                    onClick = onArrived,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(
                            MaterialTheme.colorScheme.outlineVariant
                        )
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        Icons.Default.CameraAlt, null,
                        Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "File Report Now",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun CompassRose(
    bearingDeg: Float,
    trueBearingDeg: Float,
    isArrived: Boolean,
    modifier: Modifier = Modifier
) {
    val animatedBearing by animateFloatAsState(
        targetValue = bearingDeg,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 60f),
        label = "compass_bearing"
    )

    val primaryColor = MaterialTheme.colorScheme.primary
    val errorColor = MaterialTheme.colorScheme.error
    val outlineVariant = MaterialTheme.colorScheme.outlineVariant
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
    val tertiaryColor = MaterialTheme.colorScheme.tertiary
    val surface = MaterialTheme.colorScheme.surface

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(surface)
            .border(2.dp, if (isArrived) tertiaryColor else outlineVariant, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val radius = size.minDimension / 2f

            // Tick marks
            for (i in 0 until 72) {
                val angle = Math.toRadians((i * 5).toDouble())
                val inner = when {
                    i % 18 == 0 -> radius * 0.70f
                    i % 9 == 0  -> radius * 0.80f
                    else        -> radius * 0.88f
                }
                val x1 = cx + (inner * sin(angle)).toFloat()
                val y1 = cy - (inner * cos(angle)).toFloat()
                val x2 = cx + (radius * sin(angle)).toFloat()
                val y2 = cy - (radius * cos(angle)).toFloat()
                drawLine(
                    color = if (i % 18 == 0) onSurfaceVariant else outlineVariant,
                    start = Offset(x1, y1),
                    end = Offset(x2, y2),
                    strokeWidth = if (i % 18 == 0) 2.5f else 1f
                )
            }

            drawCircle(color = surfaceVariant, radius = radius * 0.62f, center = Offset(cx, cy))
            drawCircle(
                color = outlineVariant,
                radius = radius * 0.62f,
                center = Offset(cx, cy),
                style = Stroke(1.5f)
            )

            val needleLen = radius * 0.55f
            val needleWidth = radius * 0.04f
            val bearingRad = Math.toRadians(animatedBearing.toDouble())

            val nTipX = cx + (needleLen * sin(bearingRad)).toFloat()
            val nTipY = cy - (needleLen * cos(bearingRad)).toFloat()
            val sBaseX = cx - (needleLen * 0.35f * sin(bearingRad)).toFloat()
            val sBaseY = cy + (needleLen * 0.35f * cos(bearingRad)).toFloat()

            drawLine(
                errorColor,
                Offset(cx, cy),
                Offset(nTipX, nTipY),
                needleWidth * 2.5f,
                StrokeCap.Round
            )
            drawLine(
                onSurfaceVariant,
                Offset(cx, cy),
                Offset(sBaseX, sBaseY),
                needleWidth * 2f,
                StrokeCap.Round
            )

            drawCircle(
                color = if (isArrived) tertiaryColor else primaryColor,
                radius = needleWidth * 2.5f,
                center = Offset(cx, cy)
            )
        }

        listOf("N" to 0f, "E" to 90f, "S" to 180f, "W" to 270f).forEach { (letter, angle) ->
            val rad = Math.toRadians(angle.toDouble())
            val r = 90.dp.value * 0.38f
            Box(
                Modifier.offset(
                    x = (r * sin(rad)).dp,
                    y = -(r * cos(rad)).dp
                )
            ) {
                Text(
                    letter,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = if (letter == "N") errorColor else onSurfaceVariant
                )
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "${trueBearingDeg.toInt()}°",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                color = if (isArrived) tertiaryColor else MaterialTheme.colorScheme.onSurface
            )
            Text(
                if (isArrived) "ARRIVED" else "BEARING",
                style = MaterialTheme.typography.labelSmall,
                color = if (isArrived) tertiaryColor else MaterialTheme.colorScheme.outline,
                letterSpacing = 2.sp
            )
        }
    }
}

@Composable
private fun MetricCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    SurveyCard(modifier) {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(4.dp))
        Text(
            value,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = valueColor,
            textAlign = TextAlign.Start
        )
    }
}