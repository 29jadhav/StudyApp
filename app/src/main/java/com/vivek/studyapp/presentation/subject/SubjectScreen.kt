package com.vivek.studyapp.presentation.subject

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.vivek.studyapp.presentation.component.AddSubjectDialog
import com.vivek.studyapp.presentation.component.CountCard
import com.vivek.studyapp.presentation.component.DeleteDialog
import com.vivek.studyapp.presentation.component.studySessionList
import com.vivek.studyapp.presentation.component.taskList
import com.vivek.studyapp.presentation.destinations.TaskScreenRouteDestination
import com.vivek.studyapp.presentation.task.TaskScreenNavArgs
import com.vivek.studyapp.util.SnackbarEvent
import com.vivek.studyapp.util.fromMilliToHour
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest


data class SubjectScreenNavArgs(
    val subjectId: Long
)

@Destination(navArgsDelegate = SubjectScreenNavArgs::class)
@Composable
fun SubjectScreenRoute(
    navigator: DestinationsNavigator
) {
    val subjectViewModel: SubjectViewModel = hiltViewModel()
    val state by subjectViewModel.state.collectAsStateWithLifecycle()
    SubjectScreen(
        subjectEvent = subjectViewModel::onEvent,
        snackbarEvent = subjectViewModel.snackbarEventFlow,
        subjectState = state,
        subjectViewModel = subjectViewModel,
        onBackBtnClick = {
            navigator.navigateUp()
        }, onTaskBtnClick = { taskId ->
            val navArgs = TaskScreenNavArgs(taskId = taskId, subjectId = state.subjectId)
            navigator.navigate(TaskScreenRouteDestination(navArgs))

        }, onAddTaskBtnClick = {
            val navArgs = TaskScreenNavArgs(taskId = null, subjectId = null)
            navigator.navigate(TaskScreenRouteDestination(navArgs))
        })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectScreen(
    subjectViewModel: SubjectViewModel,
    snackbarEvent: SharedFlow<SnackbarEvent>,
    subjectState: SubjectState,
    subjectEvent: (SubjectEvent) -> Unit,
    onBackBtnClick: () -> Unit,
    onAddTaskBtnClick: () -> Unit,
    onTaskBtnClick: (Long?) -> Unit

) {
    Log.d("TAG", "studyHours=" + subjectState.studyHours)
    Log.d("TAG", "goalStudyHours=" + subjectState.goalStudyHours)
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val listState = rememberLazyListState()
    val isFABExpanded by remember {
        derivedStateOf { listState.firstVisibleItemIndex == 0 }
    }
    var isAddSubjectDialogOpen by rememberSaveable {
        mutableStateOf(false)
    }

    val snackbarHostState = remember {
        SnackbarHostState()
    }
    LaunchedEffect(key1 = true) {
        snackbarEvent.collectLatest {
            when (it) {
                is SnackbarEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(message = it.message, duration = it.duration)
                }
            }
        }
    }

    LaunchedEffect(key1 = subjectState.goalStudyHours, key2 = subjectState.studyHours) {
        Log.d("TAG", "LaunchedEffect studyHours=" + subjectState.studyHours)
        Log.d("TAG", "LaunchedEffect goalStudyHours=" + subjectState.goalStudyHours)
        subjectEvent(SubjectEvent.UpdateProgress)
    }
    AddSubjectDialog(
        isOpen = isAddSubjectDialogOpen,
        goalHours = subjectState.goalStudyHours,
        onSubjectNameChange = { subjectEvent(SubjectEvent.OnSubjectNameChange(it!!)) },
        onGoalHoursChange = { subjectEvent(SubjectEvent.OnGoalStudyHourChange(it!!)) },
        onColorChange = { subjectEvent(SubjectEvent.OnSubjectColorCardChange(it)) },
        selectedColors = subjectState.subjectCardColor,
        subjectName = subjectState.subjectName,
        onDismissRequest = { isAddSubjectDialogOpen = false },
        onConfirmBtnClick = {
            subjectEvent(SubjectEvent.UpdateSubject)
            isAddSubjectDialogOpen = false
        })

    var isDeleteDialogOpen by rememberSaveable {
        mutableStateOf(false)
    }

    DeleteDialog(
        isOpen = isDeleteDialogOpen,
        title = "Delete Session?",
        bodyText = "Are you sure, you want to delete this session? Your studied hours will be reduced by this session time. This action can't be undone.",
        onDismissClick = {
            isDeleteDialogOpen = false
        },
        onConfirmClick = {
            subjectEvent(SubjectEvent.DeleteSession)
            isDeleteDialogOpen = false
        })

    var isDeleteSubjectDialogOpen by rememberSaveable {
        mutableStateOf(false)
    }


    DeleteDialog(
        isOpen = isDeleteSubjectDialogOpen,
        title = "Delete Subject?",
        bodyText = "Are you sure, you want to delete this subject? All related task and study session  will be permanently removed. This action can't be undone.",
        onDismissClick = { isDeleteSubjectDialogOpen = false },
        onConfirmClick = {
            subjectEvent(SubjectEvent.DeleteSubject)
            isDeleteSubjectDialogOpen = false
            if (subjectState.isLoading.not()) {
                onBackBtnClick()
            }
        })
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SubjectScreenTopBar(
                modifier = Modifier,
                title = subjectState.subjectName,
                onDeleteBtnClick = { isDeleteSubjectDialogOpen = true },
                onBackBtnClick = onBackBtnClick,
                onEditBtnClick = { isAddSubjectDialogOpen = true },
                scrollBehaviour = scrollBehavior
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(text = "Add Task ") },
                icon = { Icon(imageVector = Icons.Default.Add, contentDescription = "Add Task") },
                onClick = onAddTaskBtnClick,
                expanded = isFABExpanded
            )
        }
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                Log.d("TAG", "LazyColumn studyHours=" + subjectState.studyHours)
                Log.d("TAG", "LazyColumn goalStudyHours=" + subjectState.goalStudyHours)
                SubjectOverviewSection(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    studyHours = subjectState.studyHours.toString(),
                    studyGoalHours = subjectState.goalStudyHours,
                    progress = subjectState.progress
                )
            }
            taskList(
                sectionTitle = "Upcoming Tasks",
                emptyTaskListText = "You don't have any upcoming tasks.\n Click the checkbox to complete the task.",
                tasks = subjectState.upcomingTask,
                onCheckBoxClick = {
                    subjectEvent(SubjectEvent.OnIsTaskCompletedChange(it))
                },
                onTaskItemClick = onTaskBtnClick
            )

            taskList(
                sectionTitle = "Completed Tasks",
                emptyTaskListText = "You don't have any completed tasks.\n Click the + in subject screen to complete new task .",
                tasks = subjectState.completedTask,
                onCheckBoxClick = {
                    subjectEvent(SubjectEvent.OnIsTaskCompletedChange(it))
                },
                onTaskItemClick = onTaskBtnClick
            )
            studySessionList(
                sectionTitle = "Recent Study Sessions",
                emptySessionTitle = "You don't have any recent session.\n Start a study session to begin recording your progress",
                studySessionList = subjectState.recentSessions,
                onDeleteSessionClick = {
                    subjectEvent(SubjectEvent.OnDeleteSessionBtnClick(it))
                    isDeleteDialogOpen = true
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectScreenTopBar(
    modifier: Modifier,
    title: String,
    onBackBtnClick: () -> Unit,
    onDeleteBtnClick: () -> Unit,
    onEditBtnClick: () -> Unit,
    scrollBehaviour: TopAppBarScrollBehavior
) {
    LargeTopAppBar(
        scrollBehavior = scrollBehaviour,
        navigationIcon = {
            IconButton(onClick = onBackBtnClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = "Navigate to back"
                )
            }
        },
        actions = {
            IconButton(onClick = onDeleteBtnClick) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Subject")
            }
            IconButton(onClick = onEditBtnClick) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Subject")
            }

        },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        })
}

@Composable
fun SubjectOverviewSection(
    modifier: Modifier,
    studyGoalHours: String,
    studyHours: String,
    progress: Float
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        val goalHours = studyGoalHours.toDoubleOrNull()
        var percentageProgress = 0
        if(goalHours!=null) {
            percentageProgress = rememberSaveable(progress) {
                (((studyHours.toDouble().toLong().fromMilliToHour()
                        / goalHours).coerceIn(0.0, 1.0)) * 100).toInt().coerceIn(0, 100)
            }
        }
        CountCard(
            modifier = Modifier.weight(1f),
            headlineText = "Goal Study Hours",
            count = studyGoalHours
        )
        Spacer(modifier = Modifier.width(10.dp))
        CountCard(
            modifier = Modifier.weight(1f),
            headlineText = "Study Hours",
            count = studyHours.toDouble().toLong().fromMilliToHour().toString()
        )
        Spacer(modifier = Modifier.width(10.dp))
        Box(modifier = Modifier.size(75.dp), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = { 1f },
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.surfaceVariant,
                strokeWidth = 4.dp,
                strokeCap = StrokeCap.Round,
            )
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxSize(),
                strokeWidth = 4.dp,
                strokeCap = StrokeCap.Round,
            )
            Text(text = "$percentageProgress%")
        }

    }
}