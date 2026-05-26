package com.survey.mark.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocation
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.survey.mark.routing.Routes
import com.survey.mark.ui.components.AppDrawer
import com.survey.mark.ui.components.SurveyTopAppBar
import com.survey.mark.ui.home.components.FilterChip
import com.survey.mark.ui.home.components.FilterChipType
import com.survey.mark.ui.home.components.SearchBar

@Composable
fun HomeScreen(navController: NavHostController) {

    var selectedFilter by remember {
        mutableStateOf(FilterChipType.ALL)
    }

    val drawerState = rememberDrawerState(
        initialValue = DrawerValue.Closed
    )

    AppDrawer(
        currentRoute = Routes.DIRECTORY,
        drawerState = drawerState,
        onNavigate = { route ->
            navController.navigate(route) {
                popUpTo(Routes.DIRECTORY)
                launchSingleTop = true
            }
        }
    ) {
        Scaffold(
            topBar = {
                SurveyTopAppBar(
                    title = "Home",
                    subtitle = "SurveyMark Eswatini",
                    locationAccuracy = null,
                    drawerState = drawerState
                )
            },

            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        println("New Mark")
                        navController.navigate(Routes.NEW_MARK)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.AddLocation,
                        contentDescription = "New Mark",
                    )
                }
            },

            ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(padding)
                    .padding(top = 8.dp)
                    .padding(horizontal = 8.dp)
            ) {
                SearchBar(
                    searchQuery = "Gables",
                    onSearchQueryChange = {},
                    onImeSearch = {}
                )

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(
                        FilterChipType.entries
                    ) { filter ->
                        FilterChip(
                            text = filter.label,
                            selected = selectedFilter == filter,
                            onClick = {
                                selectedFilter = filter
                            }
                        )
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {

}
