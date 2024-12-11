package com.vivek.studyapp.presentation.subject

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vivek.studyapp.domain.model.Subject
import com.vivek.studyapp.domain.model.Task
import com.vivek.studyapp.domain.repository.StudySessionRepository
import com.vivek.studyapp.domain.repository.SubjectRepository
import com.vivek.studyapp.domain.repository.TaskRepository
import com.vivek.studyapp.presentation.navArgs
import com.vivek.studyapp.util.SnackbarEvent
import com.vivek.studyapp.util.fromMilliToHour
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubjectViewModel @Inject constructor(
    private val subjectRepository: SubjectRepository,
    private val taskRepository: TaskRepository,
    private val sessionRepository: StudySessionRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val navArgs: SubjectScreenNavArgs = savedStateHandle.navArgs()
    private val _state = MutableStateFlow(SubjectState())

    private val _snackbarEventFlow = MutableSharedFlow<SnackbarEvent>()
    val snackbarEventFlow = _snackbarEventFlow.asSharedFlow()


    init {
        fetchSubject()
    }

    val state = combine(
        _state,
        taskRepository.getUpcomingTasksForSubject(navArgs.subjectId),
        taskRepository.getCompletedTasksForSubject(navArgs.subjectId),
        sessionRepository.getRecentSessionForSubject(navArgs.subjectId),
        sessionRepository.getTotalDurationOfSessionBySubjectId(navArgs.subjectId)
    ) { state, upcomingTask, completedTask, recentSessions, totalSessionDuration ->
        state.copy(
            completedTask = completedTask,
            upcomingTask = upcomingTask,
            recentSessions = recentSessions,
            studyHours = totalSessionDuration
        )

    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SubjectState()
    )

    fun onEvent(event: SubjectEvent) {
        when (event) {
            SubjectEvent.DeleteSession -> deleteSession()
            SubjectEvent.DeleteSubject -> deleteSubject()
            is SubjectEvent.OnDeleteSessionBtnClick -> {
                _state.update {
                    it.copy(session = event.session)
                }
            }

            is SubjectEvent.OnGoalStudyHourChange -> {
                _state.update { subjectState ->
                    subjectState.copy(goalStudyHours = event.goalStudyHours)
                }
            }

            is SubjectEvent.OnIsTaskCompletedChange -> updateTask(event.task)
            is SubjectEvent.OnSubjectColorCardChange -> {
                _state.update { it.copy(subjectCardColor = event.colorCard) }
            }

            is SubjectEvent.OnSubjectNameChange -> {
                _state.update {
                    it.copy(subjectName = event.name)
                }
            }

            SubjectEvent.UpdateSubject -> updateSubject()
            SubjectEvent.UpdateProgress -> {
                val goalStudyHours = _state.value.goalStudyHours.toFloatOrNull() ?: 1f
                Log.w("TAG","_state.value.goalStudyHours="+_state.value.goalStudyHours)
                Log.w("TAG","_state.value.studyHours="+_state.value.studyHours)
                _state.update {
                    //Log.w("TAG","_state.value.studyHours="+_state.value.studyHours)
                    it.copy(
                        progress = (_state.value.studyHours.toLong().fromMilliToHour()
                            .toFloat() / goalStudyHours).coerceIn(0f, 1f)
                    )
                }
            }
        }
    }

    private fun deleteSession() {
        viewModelScope.launch {
            try {
                _state.value.session?.let {
                    sessionRepository.deleteSession(session = it)
                }
                _snackbarEventFlow.emit(SnackbarEvent.ShowSnackbar(message = "Session  deleted successfully"))
            } catch (e: Exception) {
                _snackbarEventFlow.emit(SnackbarEvent.ShowSnackbar(message = "Failed to delete session ${e.message}"))
            }
        }
    }

    private fun updateTask(task: Task) {
        viewModelScope.launch {
            try {
                taskRepository.upsertTask(task = task.copy(isCompleted = !task.isCompleted))
                _snackbarEventFlow.emit(SnackbarEvent.ShowSnackbar("Task updated successfully"))
            } catch (e: Exception) {
                _snackbarEventFlow.emit(SnackbarEvent.ShowSnackbar("Failed to updated Task ${e.message}"))
            }
        }
    }

    private fun updateSubject() {
        viewModelScope.launch {
            try {
                subjectRepository.upsertSubject(
                    subject = Subject(
                        subjectId = _state.value.subjectId,
                        name = _state.value.subjectName,
                        goalHours = _state.value.goalStudyHours.toFloatOrNull() ?: 0f,
                        colors = _state.value.subjectCardColor
                    )
                )
                _snackbarEventFlow.emit(SnackbarEvent.ShowSnackbar(message = "Subject updated successfully"))
            } catch (e: Exception) {
                _snackbarEventFlow.emit(SnackbarEvent.ShowSnackbar(message = "Failed to updated subject ${e.message}"))
            }
        }
    }

    private fun fetchSubject() {
        viewModelScope.launch {
            subjectRepository.getSubjectById(navArgs.subjectId)?.let { subject ->
                _state.update { subjectState ->
                    subjectState.copy(
                        subjectId = subject.subjectId,
                        subjectName = subject.name,
                        goalStudyHours = subject.goalHours.toString(),
                        subjectCardColor = subject.colors
                    )
                }
            }
        }
    }

    private fun deleteSubject() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                _state.value.subjectId?.let {
                    subjectRepository.deleteSubject(it)
                    _snackbarEventFlow.emit(SnackbarEvent.ShowSnackbar(message = "Subject deleted successfully"))
                }

            } catch (e: Exception) {
                _snackbarEventFlow.emit(SnackbarEvent.ShowSnackbar(message = "Failed to delete subject ${e.message}"))
            }
            _state.update { it.copy(isLoading = false) }
        }
    }
}