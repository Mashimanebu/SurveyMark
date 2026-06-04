package com.survey.mark.auth.ui.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChangeHistory
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.survey.mark.auth.domain.AuthState
import com.survey.mark.auth.domain.UserRole
import com.survey.mark.auth.ui.AuthViewModel

@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onNavigateToPending: () -> Unit,
    vm: AuthViewModel = hiltViewModel()
) {
    val authState by vm.authState.collectAsState()

    LaunchedEffect(authState) {
        when (val state = authState) {

            is AuthState.Authenticated -> {
                when (state.user.role) {
                    UserRole.SURVEYOR_GENERAL ->
                        onNavigateToAdmin()

                    UserRole.SURVEYOR ->
                        onNavigateToHome()
                }
            }

            is AuthState.PendingApproval ->
                onNavigateToPending()

            is AuthState.Unauthenticated ->
                onNavigateToLogin()

            else -> Unit
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector = Icons.Default.ChangeHistory,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(72.dp)
            )

            Spacer(Modifier.height(20.dp))

            Text(
                text = "SurveyMark",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "Eswatini",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(32.dp))

            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.dp
            )
        }
    }
}