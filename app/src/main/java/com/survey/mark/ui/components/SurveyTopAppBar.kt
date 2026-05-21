package com.survey.mark.ui.components

import android.util.Log.i
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurveyTopAppBar(
    title: String,
    subtitle: String? = null,
    locationAccuracy: Float? = null
) {
    TopAppBar(
        title = {
            Column(
                modifier = Modifier
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium
                )

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    subtitle?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Spacer(modifier = Modifier.weight(1.0f))

                        locationAccuracy?.let {
                            TODO()
                        }
                    }
                }
            }
        },

        actions = {
            IconButton(
                onClick = {

                }
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Survey Options"
                )
            }
        }
    )
}

@Preview
@Composable
fun SurveyTopAppBarPreview() {
    SurveyTopAppBar(
        subtitle = "Eswatini SurveyMark",
        title = "Home",
        locationAccuracy = null
    )
}
