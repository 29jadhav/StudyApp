package com.vivek.studyapp.presentation.task

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vivek.studyapp.domain.model.Task
import com.vivek.studyapp.domain.repository.SubjectRepository
import com.vivek.studyapp.domain.repository.TaskRepository
import com.vivek.studyapp.presentation.navArgs
import com.vivek.studyapp.util.Priority
import com.vivek.studyapp.util.SnackbarEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val subjectRepository: SubjectRepository,
    stateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow(TaskState())
    private val navArgs: TaskScreenNavArgs = stateHandle.navArgs()

    val state = combine(_state, subjectRepository.getAllSubjects()) { state, subjects ->
        state.copy(subjects = subjects)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = TaskState()
    )

    private val _snackbarEventFlow = MutableSharedFlow<SnackbarEvent>()
    val snackbarEventFlow = _snackbarEventFlow.asSharedFlow()

    init {
        fetchTask()
        fetchSubject()
    }

    fun onEvent(event: TaskEvent) {
        when (event) {
            TaskEvent.DeleteTask -> deleteTask()
            is TaskEvent.OnDateSelect -> {
                _state.update {
                    it.copy(dueDate = event.date)
                }
            }

            is TaskEvent.OnDescriptionChange -> {
                _state.update { it.copy(description = event.description) }
            }

            TaskEvent.OnIsCompleteStatusUpdate -> {
                _state.update { it.copy(isCompleted = !_state.value.isCompleted) }
            }

            is TaskEvent.OnPriorityChange -> {
                _state.update { it.copy(priority = event.priority) }
            }

            is TaskEvent.OnSelectOfRelatedToSubject -> {
                _state.update {
                    it.copy(
                        relatedToSubject = event.subject.name,
                        subjectId = event.subject.subjectId
                    )
                }
            }

            is TaskEvent.OnTitleChange -> {
                _state.update { it.copy(title = event.title) }
            }

            TaskEvent.SaveTask -> saveTask()

        }
    }

    private fun deleteTask() {
        viewModelScope.launch {
            try {
                _state.update {
                    it.copy(navigateUp = false)
                }

                if (_state.value.id == null) {
                    _snackbarEventFlow.emit(SnackbarEvent.ShowSnackbar(message = "Failed to delete task. Try again later"))
                    return@launch
                }
                taskRepository.deleteTaskById(_state.value.id!!)
                _snackbarEventFlow.emit(SnackbarEvent.ShowSnackbar(message = "Task deleted successfully"))
                _state.update {
                    it.copy(navigateUp = true)
                }
            } catch (e: Exception) {
                _snackbarEventFlow.emit(SnackbarEvent.ShowSnackbar(message = "Failed to delete task ${e.message}"))
            }
        }
    }

    private fun saveTask() {
        viewModelScope.launch {
            try {
                _state.update {
                    it.copy(navigateUp = false)
                }
                if (_state.value.subjectId == null || _state.value.relatedToSubject == null) {
                    _snackbarEventFlow.emit(SnackbarEvent.ShowSnackbar(message = "Please add the subject to create the task."))
                    return@launch
                }
                taskRepository.upsertTask(
                    Task(
                        isCompleted = _state.value.isCompleted,
                        priority = _state.value.priority.value,
                        subjectTaskId = _state.value.subjectId!!,
                        title = _state.value.title,
                        dueDate = _state.value.dueDate ?: Instant.now().toEpochMilli(),
                        description = _state.value.description,
                        relatedToSubject = _state.value.relatedToSubject!!,
                        taskId = _state.value.id
                    )
                )
                _snackbarEventFlow.emit(SnackbarEvent.ShowSnackbar(message = "Successfully  saved the task."))
                _state.update {
                    it.copy(navigateUp = true)
                }
            } catch (e: Exception) {
                _snackbarEventFlow.emit(SnackbarEvent.ShowSnackbar(message = "Failed to save task. ${e.message}"))
            }
        }
    }


    private fun fetchTask() {
        viewModelScope.launch {
            navArgs.taskId?.let {
                taskRepository.getTaskById(navArgs.taskId)?.let { task ->
                    _state.update {
                        it.copy(
                            title = task.title,
                            priority = Priority.fromInt(task.priority),
                            dueDate = task.dueDate,
                            description = task.description,
                            isCompleted = task.isCompleted,
                            relatedToSubject = task.relatedToSubject,
                            id = task.taskId,
                            subjectId = task.subjectTaskId
                        )
                    }
                }
            }

        }
    }

    private fun fetchSubject() {
        viewModelScope.launch {
            navArgs.subjectId?.let {
                subjectRepository.getSubjectById(navArgs.subjectId)?.let { subject ->
                    _state.update {
                        it.copy(relatedToSubject = subject.name, subjectId = subject.subjectId)

                    }
                }
            }
        }
    }
}