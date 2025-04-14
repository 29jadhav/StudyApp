package com.vivek.studyapp.presentation.session

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.DeepLink
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.vivek.studyapp.R
import com.vivek.studyapp.presentation.component.DeleteDialog
import com.vivek.studyapp.presentation.component.SubjectListBottomSheet
import com.vivek.studyapp.presentation.component.studySessionList
import com.vivek.studyapp.presentation.theme.Red
import com.vivek.studyapp.util.ServiceConstants.ACTION_SERVICE_CANCEL
import com.vivek.studyapp.util.ServiceConstants.ACTION_SERVICE_START
import com.vivek.studyapp.util.ServiceConstants.ACTION_SERVICE_STOP
import com.vivek.studyapp.util.ServiceConstants.SESSION_SCREEN_URI
import com.vivek.studyapp.util.SnackbarEvent
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.time.DurationUnit

@Destination(
    deepLinks = [
        DeepLink(
            action = Intent.ACTION_VIEW,
            uriPattern = SESSION_SCREEN_URI
        )
    ]
)

@Composable
fun StudySessionScreenRoute(
    navigator: DestinationsNavigator,
    timerService: StudySessionTimerService
) {
    val studySessionViewModel: StudySessionViewModel = hiltViewModel()
    val studySessionState by studySessionViewModel.state.collectAsStateWithLifecycle()
    StudySessionScreen(
        timerService = timerService,
        onBackBtnClick = {
            navigator.navigateUp()
        },
        state = studySessionState,
        event = studySessionViewModel::onEvent,
        snackbarEvent = studySessionViewModel.snackbarEventFlow
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudySessionScreen(
    onBackBtnClick: () -> Unit,
    timerService: StudySessionTimerService,
    snackbarEvent: SharedFlow<SnackbarEvent>,
    event: (StudySessionEvent) -> Unit,
    state: StudySessionState
) {

    val hours by timerService.hour
    val minutes by timerService.minute
    val seconds by timerService.second
    val currentTimerState by timerService.currentTimerState

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    var isBottomSheetOpen by rememberSaveable {
        mutableStateOf(false)
    }
    var isDeleteDialogOpen by rememberSaveable {
        mutableStateOf(false)
    }
    val snackbarHostState = remember {
        SnackbarHostState()
    }

    SubjectListBottomSheet(
        sheetState = sheetState,
        isOpen = isBottomSheetOpen,
        subjects = state.subjectList,
        onDismissRequest = { isBottomSheetOpen = false },
        onSubjectSelected = {
            event(StudySessionEvent.OnRelatedSubjectChange(it))
            timerService.subjectId.value = it.subjectId
            Log.d("TAG", "created timerService.subjectId=" + timerService.subjectId.value)
            scope.launch {
                sheetState.hide()
                if (!sheetState.isVisible) isBottomSheetOpen = false
            }
        }
    )

    LaunchedEffect(key1 = true) {
        snackbarEvent.collectLatest {
            when (it) {
                is SnackbarEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(message = it.message, duration = it.duration)
                }
            }
        }
    }

    LaunchedEffect(key1 = state.subjectList) {
        Log.d("TAG", "timerService.subjectId=" + timerService.subjectId.value)
        val subjectId = timerService.subjectId.value ?: return@LaunchedEffect
        event(
            StudySessionEvent.UpdateSubjectIdAndRelatedSubject(
                subjectId = subjectId,
                relatedSubject = state.subjectList.let { subjects -> subjects.find { subjectId == it.subjectId }?.name })
        )
    }

    DeleteDialog(
        isOpen = isDeleteDialogOpen,
        title = "Delete Session?",
        bodyText = "Are you sure, you want to delete this session? Your studied hours will be reduced by this session time. This action can't be undone.",
        onDismissClick = { isDeleteDialogOpen = false },
        onConfirmClick = {
            event(StudySessionEvent.DeleteSession)
            isDeleteDialogOpen = false
        })

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = { SessionScreenTopBar(onBackBtnClick) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                TimerSection(
                    modifier = Modifier
                        .fillMaxSize()
                        .aspectRatio(1f),
                    h = hours,
                    m = minutes,
                    s = seconds
                )
            }

            item {
                RelatedToSubject(
                    state = state,
                    modifier = Modifier.fillMaxWidth(),
                    onDropDownClick = { isBottomSheetOpen = true })
            }

            item {
                SessionButtonSection(
                    modifier = Modifier.fillMaxWidth(),
                    onCancelBtnClick = {
                        ServiceHelper.triggerForegroundService(
                            action = ACTION_SERVICE_CANCEL,
                            context = context
                        )
                    },
                    onFinishBtnClick = {
                        ServiceHelper.triggerForegroundService(
                            action = ACTION_SERVICE_CANCEL,
                            context = context
                        )
                        event(
                            StudySessionEvent.SaveSession(
                                duration = timerService.duration.toLong(
                                    DurationUnit.MILLISECONDS
                                )
                            )
                        )
                    },
                    onStartBtnClick = {
                        if (state.relatedToSubject.isNotEmpty()) {
                            ServiceHelper.triggerForegroundService(
                                action = if (currentTimerState == TimerState.STARTED) {
                                    ACTION_SERVICE_STOP
                                } else ACTION_SERVICE_START,
                                context = context
                            )
                        } else {
                            event(StudySessionEvent.NotifyToUpdateSubject(context.getString(R.string.choose_related_to_subject)))
                        }
                    },
                    s = seconds,
                    timerState = currentTimerState
                )
            }

            studySessionList(
                sectionTitle = context.getString(R.string.study_sessions_history),
                emptySessionTitle = context.getString(R.string.start_a_study_session_msg),
                studySessionList = state.sessionList,
                onDeleteSessionClick = {
                    event(StudySessionEvent.OnDeleteSessionBtnClick(it))
                    isDeleteDialogOpen = true
                }
            )
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SessionScreenTopBar(onBackBtnClick: () -> Unit) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onBackBtnClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = "Navigate to back"
                )
            }
        },
        title = {
            Text(
                text = "Study Session",
                style = MaterialTheme.typography.headlineSmall
            )
        })
}

@Composable
private fun TimerSection(modifier: Modifier, h: String, m: String, s: String) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(250.dp)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$h:$m:$s",
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 45.sp)
            )
        }
    }
}

@Composable
private fun RelatedToSubject(
    modifier: Modifier,
    onDropDownClick: () -> Unit,
    state: StudySessionState
) {
    Column(modifier = modifier.padding(8.dp)) {
        Text(text = "Related To Subject", style = MaterialTheme.typography.titleSmall)
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = state.relatedToSubject, style = MaterialTheme.typography.bodyLarge)
            IconButton(onClick = onDropDownClick) {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Open subject bottom sheet"
                )
            }
        }
    }
}

@Composable
private fun SessionButtonSection(
    modifier: Modifier,
    onStartBtnClick: () -> Unit,
    onCancelBtnClick: () -> Unit,
    onFinishBtnClick: () -> Unit,
    timerState: TimerState,
    s: String
) {
    Row(
        modifier = modifier.padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onCancelBtnClick,
            enabled = s != "00" && timerState != TimerState.STARTED
        ) {
            Text(
                modifier = Modifier.padding(12.dp, 5.dp),
                text = "Cancel"
            )
        }
        Button(
            onClick = onStartBtnClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (timerState == TimerState.STARTED) Red else MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            )
        ) {
            Text(
                modifier = Modifier.padding(12.dp, 5.dp), text = when (timerState) {
                    TimerState.IDLE -> "Start"
                    TimerState.STARTED -> "Stop"
                    TimerState.STOPPED -> "Resume"
                }
            )
        }
        Button(
            onClick = onFinishBtnClick,
            enabled = s != "00" && timerState != TimerState.STARTED
        ) {
            Text(modifier = Modifier.padding(12.dp, 5.dp), text = "Finish")
        }
    }
}