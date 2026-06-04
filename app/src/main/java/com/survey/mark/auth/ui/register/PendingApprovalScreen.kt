package com.survey.mark.auth.ui.register

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PendingApprovalScreen(
    onSignOut: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Text(
                text = "Account Pending Approval",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(Modifier.height(12.dp))

            Text(
                "Your account is waiting for Surveyor General approval."
            )

            Spacer(Modifier.height(24.dp))

            Button(onClick = onSignOut) {
                Text("Sign Out")
            }
        }
    }
}