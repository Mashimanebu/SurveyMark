package com.survey.mark.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocation
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.survey.mark.auth.domain.AuthState
import com.survey.mark.auth.ui.AuthViewModel
import com.survey.mark.domain.model.point.ControlPoint
import com.survey.mark.domain.model.point.ControlPointType
import com.survey.mark.domain.model.point.formattedDistance
import com.survey.mark.domain.model.status.ConditionStatus
import com.survey.mark.routing.Routes
import com.survey.mark.ui.components.AppDrawer
import com.survey.mark.ui.components.SurveyTopAppBar
import com.survey.mark.ui.home.components.ConditionBadge
import com.survey.mark.ui.home.components.EmptyState
import com.survey.mark.ui.home.components.FilterChip
import com.survey.mark.ui.home.components.FilterChipType
import com.survey.mark.ui.home.components.MarkTypeIcon
import com.survey.mark.ui.home.components.SearchBar

@Composable
fun HomeScreen(
    navController: NavHostController,
    onControlPointClick: (String) -> Unit,
    vm: DirectoryViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val query by vm.query.collectAsState()
    val filter by vm.filter.collectAsState()
    val points by vm.controlPoints.collectAsState()
    val location by vm.location.collectAsState()
    val authState by authViewModel.authState.collectAsState()


    val user = (authState as? AuthState.Authenticated)?.user

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    LaunchedEffect(authState) {

        when (authState) {

            is AuthState.Unauthenticated -> {

                navController.navigate(Routes.LOGIN) {
                    popUpTo(0) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }

            else -> Unit
        }
    }

    AppDrawer(
        currentRoute = Routes.DIRECTORY,

        drawerState = drawerState,

        userName = user?.displayName ?: "",

        userEmail = user?.email ?: "",

        userRole = user?.role?.name ?: "",

        onNavigate = { route ->

            navController.navigate(route) {
                launchSingleTop = true
            }
        },

        onSignOut = {

            authViewModel.signOut()

            navController.navigate(Routes.LOGIN) {
                popUpTo(0) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
    ) {
        Scaffold(
            topBar = {
                SurveyTopAppBar(
                    title = "Directory",
                    subtitle = "SurveyMark Eswatini",
                    locationAccuracy = location?.accuracyMeters,
                    drawerState = drawerState
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navController.navigate(Routes.NEW_MARK) },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = Icons.Default.AddLocation,
                        contentDescription = "Submit New Mark"
                    )
                }
            }
        ) { padding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(top = 8.dp)
                ) {
                    SearchBar(
                        searchQuery = query,
                        onSearchQueryChange = vm::onQueryChange,
                        onImeSearch = {}
                    )

                    Spacer(Modifier.height(8.dp))

                    val currentFilter = filter
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(FilterChipType.entries) { chipType ->

                            val isSelected = when (chipType) {
                                FilterChipType.ALL -> currentFilter is DirectoryFilter.All
                                FilterChipType.TRIGONOMETRIC -> {
                                    currentFilter is DirectoryFilter.ByType &&
                                            currentFilter.type == ControlPointType.TRIG
                                }

                                FilterChipType.TOWN_SURVEY -> {
                                    currentFilter is DirectoryFilter.ByType &&
                                            currentFilter.type == ControlPointType.TOWN_SURVEY_MARK
                                }

                                FilterChipType.REFERENCE -> {
                                    currentFilter is DirectoryFilter.ByType &&
                                            currentFilter.type == ControlPointType.REFERENCE_MARK
                                }

                                FilterChipType.BENCHMARK -> {
                                    currentFilter is DirectoryFilter.ByType &&
                                            currentFilter.type == ControlPointType.BENCHMARK
                                }

                                FilterChipType.DESTROYED -> {
                                    currentFilter is DirectoryFilter.ByCondition &&
                                            currentFilter.condition == ConditionStatus.DESTROYED
                                }

                                FilterChipType.GPS_BASE_STATION -> {
                                    currentFilter is DirectoryFilter.ByType &&
                                            currentFilter.type == ControlPointType.GPS_BASE_STATION
                                }
                            }

                            FilterChip(
                                text = chipType.label,
                                selected = isSelected,
                                onClick = {
                                    vm.onFilterChange(
                                        when (chipType) {
                                            FilterChipType.ALL -> DirectoryFilter.All
                                            FilterChipType.TRIGONOMETRIC -> DirectoryFilter.ByType(
                                                ControlPointType.TRIG
                                            )

                                            FilterChipType.TOWN_SURVEY -> DirectoryFilter.ByType(
                                                ControlPointType.TOWN_SURVEY_MARK
                                            )

                                            FilterChipType.REFERENCE -> DirectoryFilter.ByType(
                                                ControlPointType.REFERENCE_MARK
                                            )

                                            FilterChipType.BENCHMARK -> DirectoryFilter.ByType(
                                                ControlPointType.BENCHMARK
                                            )

                                            FilterChipType.DESTROYED -> DirectoryFilter.ByCondition(
                                                ConditionStatus.DESTROYED
                                            )

                                            FilterChipType.GPS_BASE_STATION -> DirectoryFilter.ByType(
                                                ControlPointType.GPS_BASE_STATION
                                            )
                                        }
                                    )
                                }
                            )
                        }
                    }

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = "${points.size} control point${if (points.size != 1) "s" else ""}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                    )
                }

                HorizontalDivider(thickness = 0.5.dp)

                if (points.isEmpty()) {
                    EmptyState(
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 88.dp)
                    ) {
                        items(
                            items = points,
                            key = { it.id }
                        ) { point ->
                            ControlPointRow(
                                point = point,
                                onClick = { onControlPointClick(point.id) }
                            )
                            HorizontalDivider(
                                thickness = 0.5.dp,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ControlPointRow(
    point: ControlPoint,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        MarkTypeIcon(type = point.type)

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = point.name,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${point.id} · Order ${point.orderClass}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 1.dp)
            )
            Spacer(Modifier.height(5.dp))
            ConditionBadge(condition = point.condition)
        }

        Column(horizontalAlignment = Alignment.End) {
            if (point.distanceMeters != null) {
                Text(
                    text = point.formattedDistance(),
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "away",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {

}



