package com.vivek.studyapp.presentation.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.vivek.studyapp.R
import com.vivek.studyapp.domain.model.StudySession
import com.vivek.studyapp.domain.model.Subject
import com.vivek.studyapp.domain.model.Task
import com.vivek.studyapp.presentation.component.AddSubjectDialog
import com.vivek.studyapp.presentation.component.CountCard
import com.vivek.studyapp.presentation.component.DeleteDialog
import com.vivek.studyapp.presentation.component.SubjectCard
import com.vivek.studyapp.presentation.component.studySessionList
import com.vivek.studyapp.presentation.component.taskList
import com.vivek.studyapp.presentation.destinations.StudySessionScreenRouteDestination
import com.vivek.studyapp.presentation.destinations.SubjectScreenRouteDestination
import com.vivek.studyapp.presentation.destinations.TaskScreenRouteDestination
import com.vivek.studyapp.presentation.subject.SubjectScreenNavArgs
import com.vivek.studyapp.presentation.task.TaskScreenNavArgs
import com.vivek.studyapp.util.SnackbarEvent
import com.vivek.studyapp.util.fromMilliToHour
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest

@RootNavGraph(start = true)
@Destination
@Composable
fun DashboardScreenRoute(
    navigator: DestinationsNavigator
) {
    val dashboardViewModel: DashboardViewModel = hiltViewModel()
    val dashboardState by dashboardViewModel.state.collectAsStateWithLifecycle()
    val tasks by dashboardViewModel.tasks.collectAsStateWithLifecycle()
    val studySessions by dashboardViewModel.sessions.collectAsStateWithLifecycle()

    DashboardScreen(
        onEvent = dashboardViewModel::onEvent,
        tasks = tasks,
        studySessions = studySessions,
        snackbarEvent = dashboardViewModel.snackbarEventFlow,
        dashboardState = dashboardState,
        onSubjectCardClick = { subjectId ->
            subjectId?.let {
                val navArgs = SubjectScreenNavArgs(subjectId = subjectId)
                navigator.navigate(SubjectScreenRouteDestination(navArgs))
            }
        },
        onTaskCardClick = { taskId ->
            val navArgs = TaskScreenNavArgs(taskId = taskId, subjectId = null)
            navigator.navigate(TaskScreenRouteDestination(navArgs))

        },
        onStartSessionBtnClick = {
            navigator.navigate((StudySessionScreenRouteDestination))
        })
}

@Composable
private fun DashboardScreen(
    onEvent: (DashboardEvent) -> Unit,
    tasks: List<Task>,
    snackbarEvent: SharedFlow<SnackbarEvent>,
    studySessions: List<StudySession>,
    dashboardState: DashboardState,
    onSubjectCardClick: (Long?) -> Unit,
    onTaskCardClick: (Long?) -> Unit,
    onStartSessionBtnClick: () -> Unit
) {


    var isAddSubjectDialogOpen by rememberSaveable {
        mutableStateOf(false)
    }

    AddSubjectDialog(
        isOpen = isAddSubjectDialogOpen,
        goalHours = dashboardState.goalStudyHours,
        onSubjectNameChange = {
            onEvent(DashboardEvent.OnSubjectNameChange(it!!))
        },
        onGoalHoursChange = {
            if (it!!.toFloatOrNull() != null) {
                //   val goalHours = it!!.toFloat()
                onEvent(DashboardEvent.OnGoalStudyHourChange(it!!))
            }
        },
        onColorChange = { onEvent(DashboardEvent.OnSubjectCardColorChange(it)) },
        selectedColors = dashboardState.subjectColorCard,
        subjectName = dashboardState.subjectName,
        onDismissRequest = { isAddSubjectDialogOpen = false },
        onConfirmBtnClick = {
            onEvent(DashboardEvent.SaveSubject)
            isAddSubjectDialogOpen = false
        })

    var isDeleteDialogOpen by rememberSaveable {
        mutableStateOf(false)
    }

    var snackbarHostState = remember {
        SnackbarHostState()
    }

    LaunchedEffect(key1 = true) {
        snackbarEvent.collectLatest { event ->
            when (event) {
                is SnackbarEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = event.duration
                    )
                }
            }
        }
    }

    DeleteDialog(
        isOpen = isDeleteDialogOpen,
        title = "Delete Session?",
        bodyText = "Are you sure, you want to delete this session? Your studied hours will be reduced by this session time. This action can't be undone.",
        onDismissClick = { isDeleteDialogOpen = false },
        onConfirmClick = {
            onEvent(DashboardEvent.DeleteSession)
            isDeleteDialogOpen = false
        })



    Scaffold(snackbarHost = {
        SnackbarHost(hostState = snackbarHostState)
    },
        topBar = { DashboardTopBar() }) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                CountCardSection(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    subjectCount = dashboardState.subjectCount,
                    studiedHours = dashboardState.studiedHours.toLong().fromMilliToHour()
                        .toString(),
                    goalStudiedHours = dashboardState.goalStudyHoursOfAllSessions.toString()
                )
            }

            item {
                SubjectCardSection(
                    modifier = Modifier.fillMaxSize(),
                    subjects = dashboardState.subjects,
                    onAddSubjectClick = {
                        isAddSubjectDialogOpen = true
                    },
                    onSubjectCardClick = onSubjectCardClick
                )
            }
            item {
                Button(
                    onClick = onStartSessionBtnClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(30.dp, 12.dp)
                ) {
                    Text(text = "Start study session")
                }
            }
            taskList(
                sectionTitle = "Upcoming Tasks",
                emptyTaskListText = "You don't have any upcoming tasks.\n Click the + in subject screen to add new task .",
                tasks = tasks,
                onCheckBoxClick = { onEvent(DashboardEvent.OnTaskIsCompleteChange(it)) },
                onTaskItemClick = onTaskCardClick
            )

            studySessionList(
                sectionTitle = "Recent Study Sessions",
                emptySessionTitle = "You don't have any recent session.\n Start a study session to begin recording your progress",
                studySessionList = studySessions,
                onDeleteSessionClick = {
                    onEvent(DashboardEvent.OnDeleteSessionBtnClick(it))
                    isDeleteDialogOpen = true
                }
            )

        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardTopBar() {
    CenterAlignedTopAppBar(title = {
        Text(
            text = "Study App",
            style = MaterialTheme.typography.titleMedium
        )
    })
}

@Composable
fun CountCardSection(
    modifier: Modifier,
    subjectCount: Int,
    studiedHours: String,
    goalStudiedHours: String
) {

    Row(modifier = modifier) {
        CountCard(
            modifier = Modifier.weight(1f),
            headlineText = "Subject Count",
            count = "$subjectCount"
        )
        Spacer(modifier = Modifier.width(10.dp))
        CountCard(
            modifier = Modifier.weight(1f),
            headlineText = "Studied Hours",
            count = studiedHours
        )
        Spacer(modifier = Modifier.width(10.dp))
        CountCard(
            modifier = Modifier.weight(1f),
            headlineText = "Goal Study Hours",
            count = goalStudiedHours
        )
    }
}

@Composable
fun SubjectCardSection(
    modifier: Modifier,
    subjects: List<Subject>,
    emptyListText: String = "You don't have any subject.\n Click the + button to add new subject.",
    onAddSubjectClick: () -> Unit,
    onSubjectCardClick: (Long?) -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Subjects", style = MaterialTheme.typography.labelMedium)
            IconButton(onClick = { onAddSubjectClick() }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add subject button")

            }
        }
        if (subjects.isNullOrEmpty()) {
            Image(
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.CenterHorizontally),
                painter = painterResource(id = R.drawable.img_books),
                contentDescription = emptyListText
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = emptyListText,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(start = 12.dp, end = 12.dp)
            ) {
                items(subjects) { subject ->
                    SubjectCard(
                        name = subject.name,
                        gradientColor = subject.colors,
                        onClick = { onSubjectCardClick(subject.subjectId) }
                    )
                }
            }
        }
    }
}