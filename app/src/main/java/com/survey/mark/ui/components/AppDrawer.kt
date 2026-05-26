package com.survey.mark.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.DismissibleDrawerSheet
import androidx.compose.material3.DismissibleNavigationDrawer
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.survey.mark.routing.Routes


data class DrawerItem(
    val route: String,
    val label: String,
    val description: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector = icon
)

val drawerItems = listOf(
    DrawerItem(
        route = Routes.DIRECTORY,
        label = "Directory",
        description = "Browse & search all control marks",
        icon = Icons.Default.Home
    ),
    DrawerItem(
        route = Routes.FIELD_NAV,
        label = "Occupation Log",
        description = "Record a base station session",
        icon = Icons.Default.Explore
    ),
    DrawerItem(
        route = Routes.REPORT,
        label = "Condition Report",
        description = "File a beacon condition report",
        icon = Icons.Default.CameraAlt
    ),
    DrawerItem(
        route = Routes.LOG,
        label = "Occupation Log",
        description = "Record a base station session",
        icon = Icons.Default.EditNote
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDrawer(
    currentRoute: String,
    drawerState: DrawerState,
    onNavigate: (String) -> Unit,
    content: @Composable () -> Unit,
) {

    DismissibleNavigationDrawer(
        drawerState = drawerState,

        drawerContent = {

            DismissibleDrawerSheet(
                modifier = Modifier.fillMaxWidth(0.8f),
                drawerShape = RoundedCornerShape(
                    topEnd = 28.dp,
                    bottomEnd = 28.dp
                ),
                drawerContainerColor = MaterialTheme.colorScheme.surface
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.primaryContainer
                                )
                            )
                        )
                        .padding(24.dp)
                ) {

                    Column {

                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.15f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Map,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier
                                    .padding(16.dp)
                                    .size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "SurveyMark Eswatini",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onPrimary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Field navigation system",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                drawerItems.forEach { item ->

                    val selected = currentRoute == item.route

                    NavigationDrawerItem(
                        modifier = Modifier
                            .padding(horizontal = 12.dp, vertical = 4.dp),

                        selected = selected,

                        onClick = {
                            onNavigate(item.route)
                        },

                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label
                            )
                        },

                        label = {

                            Column {

                                Text(
                                    text = item.label,
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = item.description,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        },

                        shape = RoundedCornerShape(18.dp),

                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor =
                                MaterialTheme.colorScheme.primaryContainer,

                            selectedIconColor =
                                MaterialTheme.colorScheme.primary,

                            selectedTextColor =
                                MaterialTheme.colorScheme.onSurface
                        )
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {

                        Text(
                            text = "GNSS Ready",
                            style = MaterialTheme.typography.labelLarge
                        )

                        Text(
                            text = "Survey tools active",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    ) {

        content()
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun AppDrawerPreview() {

    val drawerState = rememberDrawerState(
        initialValue = DrawerValue.Open
    )

    MaterialTheme {

        AppDrawer(
            currentRoute = Routes.DIRECTORY,
            drawerState = drawerState,
            onNavigate = {}
        ) {

            Scaffold(

                topBar = {

                    TopAppBar(
                        title = {
                            Text("Survey Control")
                        }
                    )
                }

            ) { padding ->

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = "Home Screen Content",
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }
        }
    }
}
