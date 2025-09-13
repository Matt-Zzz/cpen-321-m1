package com.cpen321.usermanagement.ui.screens

import Button
import Icon
import MenuButtonItem
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.cpen321.usermanagement.R
import com.cpen321.usermanagement.ui.components.UpcomingMilestonesCard
import com.cpen321.usermanagement.ui.components.TodaysScheduleCard
import com.cpen321.usermanagement.data.remote.dto.CreateMilestoneRequest
import com.cpen321.usermanagement.data.remote.dto.CreateTaskRequest
import com.cpen321.usermanagement.ui.components.MessageSnackbar
import com.cpen321.usermanagement.ui.components.MessageSnackbarState
import com.cpen321.usermanagement.ui.viewmodels.AuthViewModel
import com.cpen321.usermanagement.ui.viewmodels.CalendarViewModel
import com.cpen321.usermanagement.ui.viewmodels.ProfileUiState
import com.cpen321.usermanagement.ui.viewmodels.ProfileViewModel
import com.cpen321.usermanagement.ui.theme.LocalSpacing

private data class ProfileDialogState(
    val showDeleteDialog: Boolean = false
)

data class ProfileScreenActions(
    val onBackClick: () -> Unit,
    val onManageProfileClick: () -> Unit,
    val onManageHobbiesClick: () -> Unit,
    val onSignOutClick: () -> Unit,
    val onAccountDeleted: () -> Unit
)

private data class ProfileScreenCallbacks(
    val onBackClick: () -> Unit,
    val onManageProfileClick: () -> Unit,
    val onManageHobbiesClick: () -> Unit,
    val onSignOutClick: () -> Unit,
    val onDeleteAccountClick: () -> Unit,
    val onDeleteDialogDismiss: () -> Unit,
    val onDeleteDialogConfirm: () -> Unit,
    val onSuccessMessageShown: () -> Unit,
    val onErrorMessageShown: () -> Unit
)

@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    profileViewModel: ProfileViewModel,
    calendarViewModel: CalendarViewModel,
    actions: ProfileScreenActions
) {
    val uiState by profileViewModel.uiState.collectAsState()
    val calendarUiState by calendarViewModel.uiState.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }

    // Dialog state
    var dialogState by remember {
        mutableStateOf(ProfileDialogState())
    }

    // Side effects
    LaunchedEffect(Unit) {
        profileViewModel.clearSuccessMessage()
        profileViewModel.clearError()
    }

            ProfileContent(
                uiState = uiState,
                calendarUiState = calendarUiState,
                calendarViewModel = calendarViewModel,
                dialogState = dialogState,
                snackBarHostState = snackBarHostState,
                callbacks = ProfileScreenCallbacks(
            onBackClick = actions.onBackClick,
            onManageProfileClick = actions.onManageProfileClick,
            onManageHobbiesClick = actions.onManageHobbiesClick,
            onSignOutClick = actions.onSignOutClick,
            onDeleteAccountClick = {
                dialogState = dialogState.copy(showDeleteDialog = true)
            },
            onDeleteDialogDismiss = {
                dialogState = dialogState.copy(showDeleteDialog = false)
            },
            onDeleteDialogConfirm = {
                dialogState = dialogState.copy(showDeleteDialog = false)
                authViewModel.handleAccountDeletion()
                actions.onAccountDeleted()
            },
            onSuccessMessageShown = profileViewModel::clearSuccessMessage,
            onErrorMessageShown = profileViewModel::clearError
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileContent(
    uiState: ProfileUiState,
    calendarUiState: com.cpen321.usermanagement.ui.viewmodels.CalendarUiState,
    calendarViewModel: CalendarViewModel,
    dialogState: ProfileDialogState,
    snackBarHostState: SnackbarHostState,
    callbacks: ProfileScreenCallbacks,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            ProfileTopBar(onBackClick = callbacks.onBackClick)
        },
        snackbarHost = {
            MessageSnackbar(
                hostState = snackBarHostState,
                messageState = MessageSnackbarState(
                    successMessage = uiState.successMessage,
                    errorMessage = uiState.errorMessage,
                    onSuccessMessageShown = callbacks.onSuccessMessageShown,
                    onErrorMessageShown = callbacks.onErrorMessageShown
                )
            )
        }
    ) { paddingValues ->
        ProfileBody(
            paddingValues = paddingValues,
            isLoading = uiState.isLoadingProfile,
            calendarUiState = calendarUiState,
            calendarViewModel = calendarViewModel,
            onManageProfileClick = callbacks.onManageProfileClick,
            onManageHobbiesClick = callbacks.onManageHobbiesClick,
            onSignOutClick = callbacks.onSignOutClick,
            onDeleteAccountClick = callbacks.onDeleteAccountClick
        )
    }

    if (dialogState.showDeleteDialog) {
        DeleteAccountDialog(
            onDismiss = callbacks.onDeleteDialogDismiss,
            onConfirm = callbacks.onDeleteDialogConfirm
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileTopBar(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = stringResource(R.string.profile),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(name = R.drawable.ic_arrow_back)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

@Composable
private fun ProfileBody(
    paddingValues: PaddingValues,
    isLoading: Boolean,
    calendarUiState: com.cpen321.usermanagement.ui.viewmodels.CalendarUiState,
    calendarViewModel: CalendarViewModel,
    onManageProfileClick: () -> Unit,
    onManageHobbiesClick: () -> Unit,
    onSignOutClick: () -> Unit,
    onDeleteAccountClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        when {
            isLoading -> {
                LoadingIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            else -> {
                ProfileMenuItems(
                    calendarUiState = calendarUiState,
                    calendarViewModel = calendarViewModel,
                    onManageProfileClick = onManageProfileClick,
                    onManageHobbiesClick = onManageHobbiesClick,
                    onSignOutClick = onSignOutClick,
                    onDeleteAccountClick = onDeleteAccountClick
                )
            }
        }
    }
}

@Composable
private fun ProfileMenuItems(
    calendarUiState: com.cpen321.usermanagement.ui.viewmodels.CalendarUiState,
    calendarViewModel: CalendarViewModel,
    onManageProfileClick: () -> Unit,
    onManageHobbiesClick: () -> Unit,
    onSignOutClick: () -> Unit,
    onDeleteAccountClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(spacing.large)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(spacing.medium)
    ) {
                // Calendar Components
                UpcomingMilestonesCard(
                    milestones = calendarUiState.milestones,
                    isLoading = calendarUiState.isLoadingMilestones,
                    onAddMilestone = {
                        // TODO: Show add milestone dialog
                        val testMilestone = CreateMilestoneRequest(
                            title = "Test Milestone",
                            description = "This is a test milestone",
                            dueDate = "2024-12-25",
                            dueTime = "12:00",
                            isAllDay = false
                        )
                        calendarViewModel.createMilestone(testMilestone)
                    },
                    onDeleteMilestone = { eventId ->
                        calendarViewModel.deleteEvent(eventId)
                    }
                )

                TodaysScheduleCard(
                    events = calendarUiState.todaysEvents,
                    isLoading = calendarUiState.isLoadingSchedule,
                    onAddTask = {
                        // TODO: Show add task dialog
                        val testTask = CreateTaskRequest(
                            title = "Test Task",
                            description = "This is a test task",
                            dueDate = "2024-12-25",
                            dueTime = "14:00",
                            isAllDay = false
                        )
                        calendarViewModel.createTask(testTask)
                    },
                    onDeleteEvent = { eventId ->
                        calendarViewModel.deleteEvent(eventId)
                    }
                )

        ProfileSection(
            onManageProfileClick = onManageProfileClick,
            onManageHobbiesClick = onManageHobbiesClick
        )

        AccountSection(
            onSignOutClick = onSignOutClick,
            onDeleteAccountClick = onDeleteAccountClick
        )
    }
}

@Composable
private fun ProfileSection(
    onManageProfileClick: () -> Unit,
    onManageHobbiesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(LocalSpacing.current.medium)
    ) {
        ManageProfileButton(onClick = onManageProfileClick)
        ManageHobbiesButton(onClick = onManageHobbiesClick)
    }
}

@Composable
private fun AccountSection(
    onSignOutClick: () -> Unit,
    onDeleteAccountClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(LocalSpacing.current.medium)
    ) {
        SignOutButton(onClick = onSignOutClick)
        DeleteAccountButton(onClick = onDeleteAccountClick)
    }
}

@Composable
private fun ManageProfileButton(
    onClick: () -> Unit,
) {
    MenuButtonItem(
        text = stringResource(R.string.manage_profile),
        iconRes = R.drawable.ic_manage_profile,
        onClick = onClick,
    )
}

@Composable
private fun ManageHobbiesButton(
    onClick: () -> Unit,
) {
    MenuButtonItem(
        text = stringResource(R.string.manage_hobbies),
        iconRes = R.drawable.ic_heart_smile,
        onClick = onClick,
    )
}

@Composable
private fun SignOutButton(
    onClick: () -> Unit,
) {
    MenuButtonItem(
        text = stringResource(R.string.sign_out),
        iconRes = R.drawable.ic_sign_out,
        onClick = onClick,
    )
}

@Composable
private fun DeleteAccountButton(
    onClick: () -> Unit,
) {
    MenuButtonItem(
        text = stringResource(R.string.delete_account),
        iconRes = R.drawable.ic_delete_forever,
        onClick = onClick,
    )
}

@Composable
private fun DeleteAccountDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        title = {
            DeleteDialogTitle()
        },
        text = {
            DeleteDialogText()
        },
        confirmButton = {
            DeleteDialogConfirmButton(onClick = onConfirm)
        },
        dismissButton = {
            DeleteDialogDismissButton(onClick = onDismiss)
        }
    )
}

@Composable
private fun DeleteDialogTitle(
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(R.string.delete_account),
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        modifier = modifier
    )
}

@Composable
private fun DeleteDialogText(
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(R.string.delete_account_confirmation),
        modifier = modifier
    )
}

@Composable
private fun DeleteDialogConfirmButton(
    onClick: () -> Unit,
) {
    Button(
        fullWidth = false,
        onClick = onClick,
    ) {
        Text(stringResource(R.string.confirm))
    }
}

@Composable
private fun DeleteDialogDismissButton(
    onClick: () -> Unit,
) {
    Button(
        fullWidth = false,
        type = "secondary",
        onClick = onClick,
    ) {
        Text(stringResource(R.string.cancel))
    }
}

@Composable
private fun LoadingIndicator(
    modifier: Modifier = Modifier
) {
    CircularProgressIndicator(modifier = modifier)
}
