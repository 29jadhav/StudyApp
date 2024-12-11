package com.vivek.studyapp.presentation.task

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.vivek.studyapp.domain.model.Subject
import com.vivek.studyapp.presentation.component.DeleteDialog
import com.vivek.studyapp.presentation.component.SubjectListBottomSheet
import com.vivek.studyapp.presentation.component.TaskCheckBox
import com.vivek.studyapp.presentation.component.TaskDatePickerDialog
import com.vivek.studyapp.util.Priority
import com.vivek.studyapp.util.SnackbarEvent
import com.vivek.studyapp.util.fromMilliToDateString
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.Instant

data class TaskScreenNavArgs(
    val taskId: Long?,
    val subjectId: Long?
)

@Destination(navArgsDelegate = TaskScreenNavArgs::class)
@Composable
fun TaskScreenRoute(navigator: DestinationsNavigator) {
    val taskViewModel: TaskViewModel = hiltViewModel()
    val state by taskViewModel.state.collectAsStateWithLifecycle()

    TaskScreen(
        onEvent = taskViewModel::onEvent,
        snackbarEvent = taskViewModel.snackbarEventFlow,
        state = state,
        onBackBtnClick = { navigator.navigateUp() })
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskScreen(
    onBackBtnClick: () -> Unit,
    onEvent: (TaskEvent) -> Unit,
    snackbarEvent: SharedFlow<SnackbarEvent>,
    state: TaskState
) {

    var taskTitleError by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    var isShowDeleteDialog by rememberSaveable {
        mutableStateOf(false)
    }

    var isDatePickerDialogOpen by rememberSaveable {
        mutableStateOf(false)
    }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = state.dueDate ?: Instant.now().toEpochMilli()
    )
    val bottomSheetState = rememberModalBottomSheetState()
    var isBottomSheetOpen by rememberSaveable {
        mutableStateOf(false)
    }
    val scope = rememberCoroutineScope();

    taskTitleError = when {
        state.title.isBlank() -> "Please enter the title"
        state.title.length < 4 -> "Task title is too short"
        state.title.length > 30 -> "Task title is too long"
        else -> null
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

    LaunchedEffect(key1 = state.navigateUp) {
        if (state.navigateUp) {
            onBackBtnClick()
        }
    }

    DeleteDialog(
        isOpen = isShowDeleteDialog,
        title = "Delete Task?",
        bodyText = "Are you sure, you want to delete this task. This action can't be undone.",
        onDismissClick = { isShowDeleteDialog = false },
        onConfirmClick = {
            onEvent(TaskEvent.DeleteTask)
            isShowDeleteDialog = false
        })

    TaskDatePickerDialog(
        state = datePickerState,
        isOpen = isDatePickerDialogOpen,
        onConfirmBtnClick = {
            onEvent(TaskEvent.OnDateSelect(datePickerState.selectedDateMillis ?: 0))
            isDatePickerDialogOpen = false
        },
        onDismissBtnClick = { isDatePickerDialogOpen = false },
    )

    SubjectListBottomSheet(
        sheetState = bottomSheetState,
        isOpen = isBottomSheetOpen,
        onDismissRequest = { isBottomSheetOpen = false },
        onSubjectSelected = {
            onEvent(TaskEvent.OnSelectOfRelatedToSubject(it))
            scope.launch {
                bottomSheetState.hide()
            }.invokeOnCompletion {
                if (!bottomSheetState.isVisible) isBottomSheetOpen = false
            }
        },
        subjects = state.subjects
    )

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            TaskScreenTopBar(
                isTaskCompleted = state.isCompleted,
                onBackBtnClick = onBackBtnClick,
                onDeleteBtnClick = { isShowDeleteDialog = true },
                isTaskExist = state.id != null,
                checkBoxBorderColor = state.priority.color,
                onCheckBoxChecked = { onEvent(TaskEvent.OnIsCompleteStatusUpdate) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .verticalScroll(
                    state = rememberScrollState()
                )
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 12.dp)
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.title,
                onValueChange = { onEvent(TaskEvent.OnTitleChange(it)) },
                singleLine = true,
                isError = taskTitleError != null && state.title.isNotBlank(),
                supportingText = { Text(text = taskTitleError.orEmpty()) },
                label = { Text(text = "Title") })
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.description,
                onValueChange = { onEvent(TaskEvent.OnDescriptionChange(it)) },
                label = { Text(text = "Description") })
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "Due Date", style = MaterialTheme.typography.bodySmall)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = state.dueDate.fromMilliToDateString(),
                    style = MaterialTheme.typography.bodyLarge
                )
                IconButton(onClick = { isDatePickerDialogOpen = true }) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Select due data"
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Priority.entries.forEach { priority ->
                    TaskPriorityBtn(
                        modifier = Modifier.weight(1f),
                        priority = priority,
                        borderColor = if (priority == state.priority) {
                            Color.White
                        } else {
                            Color.Transparent
                        },
                        backgroundColor = priority.color,
                        onPriorityClick = {
                            onEvent(TaskEvent.OnPriorityChange(priority))
                        },
                        labelColor = if (priority == state.priority) {
                            Color.White
                        } else {
                            Color.White.copy(alpha = 0.7f)
                        }
                    )

                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "Select Subject", style = MaterialTheme.typography.bodySmall)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val firstSubject = state.subjects.firstOrNull()?.name ?: ""
                Text(
                    text = state.relatedToSubject ?: firstSubject,
                    style = MaterialTheme.typography.bodyLarge
                )
                IconButton(onClick = { isBottomSheetOpen = true }) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Select subject dropdown"
                    )
                }
            }

            Button(
                onClick = { onEvent(TaskEvent.SaveTask) },
                enabled = taskTitleError == null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(text = "Save")
            }
        }
    }
}

@Composable
fun TaskPriorityBtn(
    modifier: Modifier = Modifier,
    priority: Priority,
    labelColor: Color,
    backgroundColor: Color,
    borderColor: Color,
    onPriorityClick: () -> Unit
) {
    Box(
        modifier = modifier
            .background(color = backgroundColor)
            .clickable { onPriorityClick() }
            .padding(5.dp)
            .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(5.dp))
            .padding(5.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = priority.title, style = MaterialTheme.typography.bodyMedium, color = labelColor)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskScreenTopBar(
    isTaskExist: Boolean,
    isTaskCompleted: Boolean,
    onBackBtnClick: () -> Unit,
    onDeleteBtnClick: () -> Unit,
    onCheckBoxChecked: () -> Unit,
    checkBoxBorderColor: Color
) {
    TopAppBar(
        title = { Text(text = "Task") },
        navigationIcon = {
            IconButton(onClick = onBackBtnClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = "Navigate to back"
                )
            }
        },
        actions = {
            if (isTaskExist) {
                TaskCheckBox(
                    isCompleted = isTaskCompleted,
                    borderColor = checkBoxBorderColor,
                    onCheckBoxClick = onCheckBoxChecked
                )
                IconButton(onClick = onDeleteBtnClick) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Task")

                }
            }
        }
    )
}