package com.survey.mark.auth.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.survey.mark.auth.domain.AuthRepository
import com.survey.mark.auth.domain.SurveyUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SurveyorGeneralState(
    val currentUser: SurveyUser? = null,
    val pendingUsers: List<SurveyUser> = emptyList(),
    val allUsers: List<SurveyUser> = emptyList(),
    val activeCount: Int = 0,
    val rejectedCount: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SurveyorGeneralState())
    val state: StateFlow<SurveyorGeneralState> = _state.asStateFlow()

    init {
        loadCurrentUser()
        loadPendingUsers()
        loadAllUsers()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    currentUser = authRepository.getCurrentUser()
                )
            }
        }
    }

    fun loadPendingUsers() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            authRepository.getPendingUsers()
                .onSuccess { users ->
                    _state.update {
                        it.copy(
                            pendingUsers = users,
                            isLoading = false
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Failed to load pending users"
                        )
                    }
                }
        }
    }

    fun loadAllUsers() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            authRepository.getAllUsers()
                .onSuccess { users ->

                    val activeCount =
                        users.count { user ->
                            user.status.name == "ACTIVE"
                        }

                    val rejectedCount =
                        users.count { user ->
                            user.status.name == "REJECTED"
                        }

                    _state.update {
                        it.copy(
                            allUsers = users,
                            activeCount = activeCount,
                            rejectedCount = rejectedCount,
                            isLoading = false
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Failed to load users"
                        )
                    }
                }
        }
    }

    fun approveUser(uid: String) {
        viewModelScope.launch {
            authRepository.approveUser(uid)
                .onSuccess {
                    loadPendingUsers()
                    loadAllUsers()
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            errorMessage = error.message ?: "Failed to approve user"
                        )
                    }
                }
        }
    }

    fun rejectUser(
        uid: String,
        reason: String
    ) {
        viewModelScope.launch {
            authRepository.rejectUser(uid, reason)
                .onSuccess {
                    loadPendingUsers()
                    loadAllUsers()
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            errorMessage = error.message ?: "Failed to reject user"
                        )
                    }
                }
        }
    }

    fun suspendUser(
        uid: String,
        reason: String
    ) {
        viewModelScope.launch {
            authRepository.suspendUser(uid, reason)
                .onSuccess {
                    loadAllUsers()
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            errorMessage = error.message ?: "Failed to suspend user"
                        )
                    }
                }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }

    fun clearError() {
        _state.update {
            it.copy(errorMessage = null)
        }
    }
}


@Composable
fun AdminDashboardScreen(
    onSignOut: () -> Unit,
    vm: AdminViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Admin Dashboard",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(12.dp))

        Text("Pending users: ${state.pendingUsers.size}")
        Text("All users: ${state.allUsers.size}")
        Text("Active: ${state.activeCount}")
        Text("Rejected: ${state.rejectedCount}")

        Spacer(Modifier.height(16.dp))

        Button(onClick = onSignOut) {
            Text("Sign Out")
        }

        Spacer(Modifier.height(16.dp))

        HorizontalDivider()

        Spacer(Modifier.height(12.dp))

        Text("Pending Users", style = MaterialTheme.typography.titleMedium)

        LazyColumn {
            items(state.pendingUsers) { user ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(user.displayName)
                        Text(user.email)

                        Row {
                            Button(onClick = { vm.approveUser(user.uid) }) {
                                Text("Approve")
                            }

                            Spacer(Modifier.width(8.dp))

                            Button(onClick = {
                                vm.rejectUser(user.uid, "Rejected by admin")
                            }) {
                                Text("Reject")
                            }
                        }
                    }
                }
            }
        }
    }
}