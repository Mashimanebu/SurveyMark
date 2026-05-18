package com.survey.mark.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onImeSearch: () -> Unit,
    modifier: Modifier = Modifier
){
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        shape = RoundedCornerShape(32.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Black,
            focusedTextColor = Color.Black,
        ),
        singleLine = true,

        placeholder = {

        },

        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.66f)
            )
        },

        keyboardActions = KeyboardActions(
            onSearch = {
                onImeSearch()
            }
        ),

        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search
        ),

        trailingIcon = {
            AnimatedVisibility(
                visible = searchQuery.isNotBlank(),

            ) {
                IconButton(
                    onClick = {
                        onSearchQueryChange("")
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear Search"
                    )
                }
            }
        },

        modifier = modifier.fillMaxWidth()
    )
}



@Preview(showBackground = true)
@Composable
fun SearchBarPreview(){
    SearchBar(
        searchQuery = "Manzini",
        onSearchQueryChange = {},
        onImeSearch = {},
        modifier = Modifier.fillMaxWidth()
    )
}