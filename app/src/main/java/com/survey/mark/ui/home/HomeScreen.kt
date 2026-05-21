package com.survey.mark.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.survey.mark.ui.components.SurveyTopAppBar
import com.survey.mark.ui.home.components.FilterChip
import com.survey.mark.ui.home.components.FilterChipType
import com.survey.mark.ui.home.components.SearchBar


@Composable
fun HomeScreen() {

    var selectedFilter by remember {
        mutableStateOf(FilterChipType.ALL)
    }
    Scaffold(
        topBar = {
            SurveyTopAppBar(
                title = "Home",
                subtitle = "SurveyMark Eswatini",
                locationAccuracy = null
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {}
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "New Mark"
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
                .padding(top = 8.dp)
                .padding(horizontal = 8.dp)
        ) {
            SearchBar(
                searchQuery = "Manzini",
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


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}
