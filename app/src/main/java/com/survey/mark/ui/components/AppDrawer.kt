package com.survey.mark.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.DismissibleDrawerSheet
import androidx.compose.material3.DismissibleNavigationDrawer
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
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
        label = "Navigation",
        description = "Navigate to control point",
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
    userName: String,
    userEmail: String,
    userRole: String,
    onNavigate: (String) -> Unit,
    onSignOut: () -> Unit,
    content: @Composable () -> Unit
) {

    DismissibleNavigationDrawer(

        drawerState = drawerState,

        drawerContent = {

            DismissibleDrawerSheet(
                modifier = Modifier.fillMaxWidth(0.82f),
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

                            Box(
                                modifier = Modifier.size(72.dp),
                                contentAlignment = Alignment.Center
                            ) {

                                Text(
                                    text = userName
                                        .split(" ")
                                        .take(2)
                                        .map {
                                            it.firstOrNull()?.uppercase() ?: ""
                                        }
                                        .joinToString(""),
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = userName,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimary
                        )

                        Text(
                            text = userEmail,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = userRole,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))


                drawerItems.forEach { item ->

                    NavigationDrawerItem(

                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 4.dp
                        ),

                        selected = currentRoute == item.route,

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
                                MaterialTheme.colorScheme.primary
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

                NavigationDrawerItem(

                    modifier = Modifier.padding(
                        horizontal = 12.dp,
                        vertical = 8.dp
                    ),

                    selected = false,

                    onClick = onSignOut,

                    icon = {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Sign Out"
                        )
                    },

                    label = {
                        Text("Sign Out")
                    }
                )
            }
        }
    ) {

        content()
    }
}