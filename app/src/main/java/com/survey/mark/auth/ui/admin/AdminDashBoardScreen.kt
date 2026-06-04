package com.survey.mark.auth.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.survey.mark.auth.domain.AccountStatus
import com.survey.mark.auth.domain.AuthRepository
import com.survey.mark.auth.domain.SurveyUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

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
            _state.update { it.copy(currentUser = authRepository.getCurrentUser()) }
        }
    }

    fun loadPendingUsers() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            authRepository.getPendingUsers().onSuccess { users ->
                    _state.update { it.copy(pendingUsers = users, isLoading = false) }
                }.onFailure { error ->
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
            authRepository.getAllUsers().onSuccess { users ->
                    _state.update {
                        it.copy(
                            allUsers = users,
                            activeCount = users.count { u -> u.status == AccountStatus.ACTIVE },
                            rejectedCount = users.count { u -> u.status == AccountStatus.REJECTED },
                            suspendedCount = users.count { u -> u.status == AccountStatus.SUSPENDED },
                            isLoading = false
                        )
                    }
                }.onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Failed to load users"
                        )
                    }
                }
        }
    }

    private fun refresh() {
        loadPendingUsers()
        loadAllUsers()
    }

    fun approveUser(uid: String) {
        viewModelScope.launch {
            authRepository.approveUser(uid).onSuccess { refresh() }.onFailure { e ->
                    _state.update {
                        it.copy(
                            errorMessage = e.message ?: "Failed to approve user"
                        )
                    }
                }
        }
    }

    fun rejectUser(uid: String, reason: String = "Rejected by admin") {
        viewModelScope.launch {
            authRepository.rejectUser(uid, reason).onSuccess { refresh() }.onFailure { e ->
                    _state.update {
                        it.copy(
                            errorMessage = e.message ?: "Failed to reject user"
                        )
                    }
                }
        }
    }

    fun suspendUser(uid: String, reason: String = "Suspended by admin") {
        viewModelScope.launch {
            authRepository.suspendUser(uid, reason).onSuccess { loadAllUsers() }.onFailure { e ->
                    _state.update {
                        it.copy(
                            errorMessage = e.message ?: "Failed to suspend user"
                        )
                    }
                }
        }
    }

    fun restoreUser(uid: String) {
        viewModelScope.launch {
            authRepository.approveUser(uid).onSuccess { loadAllUsers() }.onFailure { e ->
                    _state.update {
                        it.copy(
                            errorMessage = e.message ?: "Failed to restore user"
                        )
                    }
                }
        }
    }

    fun signOut() {
        viewModelScope.launch { authRepository.signOut() }
    }

    fun clearError() {
        _state.update { it.copy(errorMessage = null) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onSignOut: () -> Unit, vm: AdminViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()
    var activeTab by remember { mutableStateOf(AdminTab.OVERVIEW) }

    if (state.errorMessage != null) {
        LaunchedEffect(state.errorMessage) {
            delay(3000)
            vm.clearError()
        }
    }

    Scaffold(
        topBar = {
        TopAppBar(
            title = {
            Column {
                Text(
                    text = when (activeTab) {
                        AdminTab.OVERVIEW -> "Overview"
                        AdminTab.ALL_USERS -> "All users"
                        AdminTab.PENDING -> "Pending approvals"
                        AdminTab.CONTROL_POINTS -> "Control points"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = state.currentUser?.displayName ?: "Surveyor General",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(.5f)
                )
            }
        }, actions = {

            if (state.pendingUsers.isNotEmpty()) {
                Badge(containerColor = MaterialTheme.colorScheme.error) {
                    Text(
                        "${state.pendingUsers.size}",
                        color = MaterialTheme.colorScheme.onError
                    )
                }
                Spacer(Modifier.width(8.dp))
            }
            IconButton(onClick = { vm.signOut(); onSignOut() }) {
                Icon(
                    Icons.Outlined.Logout,
                    contentDescription = "Sign out",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }, colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
        )
    }, bottomBar = {
        NavigationBar {
            NavigationBarItem(
                selected = activeTab == AdminTab.OVERVIEW,
                onClick = { activeTab = AdminTab.OVERVIEW },
                icon = { Icon(Icons.Outlined.Dashboard, null) },
                label = { Text("Overview", style = MaterialTheme.typography.labelSmall) })
            NavigationBarItem(
                selected = activeTab == AdminTab.PENDING,
                onClick = { activeTab = AdminTab.PENDING },
                icon = {
                    BadgedBox(
                        badge = {
                            if (state.pendingUsers.isNotEmpty()) {
                                Badge { Text("${state.pendingUsers.size}") }
                            }
                        }) {
                        Icon(Icons.Outlined.Schedule, null)
                    }
                },
                label = { Text("Pending", style = MaterialTheme.typography.labelSmall) })
            NavigationBarItem(
                selected = activeTab == AdminTab.ALL_USERS,
                onClick = { activeTab = AdminTab.ALL_USERS },
                icon = { Icon(Icons.Outlined.Group, null) },
                label = { Text("Users", style = MaterialTheme.typography.labelSmall) })
            NavigationBarItem(
                selected = activeTab == AdminTab.CONTROL_POINTS,
                onClick = { activeTab = AdminTab.CONTROL_POINTS },
                icon = { Icon(Icons.Outlined.PinDrop, null) },
                label = { Text("Points", style = MaterialTheme.typography.labelSmall) })
        }
    }, containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->

        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            state.errorMessage?.let { msg ->
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Outlined.ErrorOutline,
                            null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            msg,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodySmall
                        )
                        IconButton(onClick = { vm.clearError() }, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Outlined.Close, null, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }

            when (activeTab) {
                AdminTab.OVERVIEW -> OverviewTab(state, vm)
                AdminTab.PENDING -> PendingTab(state, vm)
                AdminTab.ALL_USERS -> AllUsersTab(state, vm)
                AdminTab.CONTROL_POINTS -> ControlPointsTab()
            }
        }
    }
}

@Composable
private fun OverviewTab(state: SurveyorGeneralState, vm: AdminViewModel) {
    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatCard(
                        "Total users",
                        state.allUsers.size.toString(),
                        MaterialTheme.colorScheme.primary,
                        Modifier.weight(1f)
                    )
                    StatCard(
                        "Active",
                        state.activeCount.toString(),
                        Color(0xFF3B6D11),
                        Modifier.weight(1f)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatCard(
                        "Pending",
                        state.pendingUsers.size.toString(),
                        Color(0xFF854F0B),
                        Modifier.weight(1f)
                    )
                    StatCard(
                        "Suspended",
                        state.suspendedCount.toString(),
                        Color(0xFF5F5E5A),
                        Modifier.weight(1f)
                    )
                }
            }
        }

        item {
            SectionLabel("Pending approvals", state.pendingUsers.size)
        }

        if (state.pendingUsers.isEmpty()) {
            item { EmptyState("No pending users") }
        } else {
            items(state.pendingUsers, key = { it.uid }) { user ->
                UserCard(user) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilledTonalButton(
                            onClick = { vm.approveUser(user.uid) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = Color(0xFFEAF3DE), contentColor = Color(0xFF27500A)
                            )
                        ) {
                            Icon(Icons.Outlined.Check, null, Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Approve")
                        }
                        OutlinedButton(
                            onClick = { vm.rejectUser(user.uid) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(Icons.Outlined.Close, null, Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Reject")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PendingTab(state: SurveyorGeneralState, vm: AdminViewModel) {
    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item { SectionLabel("Awaiting your approval", state.pendingUsers.size) }

        if (state.pendingUsers.isEmpty()) {
            item { EmptyState("All caught up — no pending users") }
        } else {
            items(state.pendingUsers, key = { it.uid }) { user ->
                UserCard(user) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilledTonalButton(
                            onClick = { vm.approveUser(user.uid) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = Color(0xFFEAF3DE), contentColor = Color(0xFF27500A)
                            )
                        ) {
                            Icon(Icons.Outlined.Check, null, Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Approve")
                        }
                        OutlinedButton(
                            onClick = { vm.rejectUser(user.uid) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(Icons.Outlined.Close, null, Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Reject")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AllUsersTab(state: SurveyorGeneralState, vm: AdminViewModel) {
    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item { SectionLabel("All registered users", state.allUsers.size) }

        if (state.allUsers.isEmpty()) {
            item { EmptyState("No users found") }
        } else {
            items(state.allUsers, key = { it.uid }) { user ->
                UserCard(user) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        when (user.status) {
                            AccountStatus.PENDING -> {
                                FilledTonalButton(
                                    onClick = { vm.approveUser(user.uid) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.filledTonalButtonColors(
                                        containerColor = Color(0xFFEAF3DE),
                                        contentColor = Color(0xFF27500A)
                                    )
                                ) { Text("Approve") }
                                OutlinedButton(
                                    onClick = { vm.rejectUser(user.uid) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                                ) { Text("Reject") }
                            }

                            AccountStatus.ACTIVE -> {
                                OutlinedButton(
                                    onClick = { vm.suspendUser(user.uid) },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = Color(
                                            0xFF854F0B
                                        )
                                    )
                                ) {
                                    Icon(Icons.Outlined.Block, null, Modifier.size(16.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Suspend account")
                                }
                            }

                            AccountStatus.SUSPENDED -> {
                                FilledTonalButton(
                                    onClick = { vm.restoreUser(user.uid) },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        Icons.Outlined.RestoreFromTrash, null, Modifier.size(16.dp)
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text("Restore account")
                                }
                            }

                            else -> {}
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ControlPointsTab() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                Icons.Outlined.PinDrop,
                null,
                Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(.25f)
            )
            Text(
                "Control points coming soon",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(.4f)
            )
            Text(
                "Add a ControlPointRepository and wire it into AdminViewModel to populate this tab.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(.3f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun UserCard(
    user: SurveyUser, actions: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                UserAvatar(user.displayName)
                Column(Modifier.weight(1f)) {
                    Text(
                        user.displayName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        user.email,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(.55f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                StatusChip(user.status)
            }

            if (user.region.isNotBlank() || user.licenceNo.isNotBlank()) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (user.region.isNotBlank()) {
                        InfoPill(Icons.Outlined.LocationOn, user.region)
                    }
                    if (user.licenceNo.isNotBlank()) {
                        InfoPill(Icons.Outlined.Badge, user.licenceNo)
                    }
                }
            }

            HorizontalDivider()
            actions()
        }
    }
}

@Composable
private fun InfoPill(icon: ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .background(
                MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(6.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Icon(icon, null, Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurface.copy(.5f))
        Text(
            text,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(.6f)
        )
    }
}

@Composable
private fun SectionLabel(title: String, count: Int) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
        Surface(
            color = MaterialTheme.colorScheme.secondaryContainer, shape = CircleShape
        ) {
            Text(
                "$count",
                Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
private fun StatCard(
    label: String, value: String, valueColor: Color, modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(.5f)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = valueColor
            )
        }
    }
}

@Composable
private fun UserAvatar(name: String) {
    val initials =
        name.split(" ").mapNotNull { it.firstOrNull()?.uppercaseChar() }.take(2).joinToString("")
    Box(
        Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Text(
            initials,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun StatusChip(status: AccountStatus) {
    val (bg, fg, label) = when (status) {
        AccountStatus.ACTIVE -> Triple(Color(0xFFEAF3DE), Color(0xFF27500A), "Active")
        AccountStatus.PENDING -> Triple(Color(0xFFFAEEDA), Color(0xFF633806), "Pending")
        AccountStatus.REJECTED -> Triple(Color(0xFFFCEBEB), Color(0xFF791F1F), "Rejected")
        AccountStatus.SUSPENDED -> Triple(Color(0xFFF1EFE8), Color(0xFF5F5E5A), "Suspended")
    }
    Surface(color = bg, shape = RoundedCornerShape(20.dp)) {
        Text(
            label,
            Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = fg,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun EmptyState(text: String) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp), contentAlignment = Alignment.Center
    ) {
        Text(
            text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(.38f)
        )
    }
}

