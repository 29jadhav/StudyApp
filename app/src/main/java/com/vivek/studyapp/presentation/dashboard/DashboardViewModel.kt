package com.vivek.studyapp.presentation.dashboard

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vivek.studyapp.domain.model.StudySession
import com.vivek.studyapp.domain.model.Subject
import com.vivek.studyapp.domain.model.Task
import com.vivek.studyapp.domain.repository.StudySessionRepository
import com.vivek.studyapp.domain.repository.SubjectRepository
import com.vivek.studyapp.domain.repository.TaskRepository
import com.vivek.studyapp.util.SnackbarEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val subjectRepository: SubjectRepository,
    private val sessionRepository: StudySessionRepository,
    private val taskRepository: TaskRepository
) : ViewModel() {
    private val _state = MutableStateFlow(DashboardState())
    val state = combine(
        _state,
        subjectRepository.getTotalSubjects(),
        subjectRepository.getGoalHours(),
        subjectRepository.getAllSubjects(),
        sessionRepository.getTotalSessionDuration(),
    ) { state, subjectCount, totalGoalHours, subjects, studiedHours ->
        state.copy(
            studiedHours = studiedHours,
            subjectCount = subjectCount,
            goalStudyHoursOfAllSessions = totalGoalHours,
            subjects = subjects,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardState()
    )

    val tasks: StateFlow<List<Task>> = taskRepository.getUpcomingTasks().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val sessions: StateFlow<List<StudySession>> = sessionRepository.getRecentFiveSessions().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _snackbarEventFlow = MutableSharedFlow<SnackbarEvent>()
    val snackbarEventFlow = _snackbarEventFlow.asSharedFlow()

    fun onEvent(event: DashboardEvent) {
        Log.d("TAG","onEvent called.....")
        when (event) {
            DashboardEvent.DeleteSession -> {
                _state
            }

            is DashboardEvent.OnDeleteSessionBtnClick -> {
                _state.update { dashboardState ->
                    dashboardState.copy(session = event.session)

                }
            }

            DashboardEvent.SaveSubject -> saveSubject()
            is DashboardEvent.OnSubjectCardColorChange -> _state.update { dashboardState ->
                dashboardState.copy(subjectColorCard = event.colors)
            }

            is DashboardEvent.OnSubjectNameChange -> _state.update { dashboardState ->
                dashboardState.copy(subjectName = event.name)
            }

            is DashboardEvent.OnGoalStudyHourChange -> _state.update { dashboardState ->
                dashboardState.copy(goalStudyHours = event.hours)
            }

            is DashboardEvent.OnTaskIsCompleteChange -> {
                Log.d("TAG","onEvent OnTaskIsCompleteChange called.....")
                updateTask(event.task)
            }

        }
    }

    private fun updateTask(task: Task) {
        viewModelScope.launch {
            try {
                taskRepository.upsertTask(task = task.copy(isCompleted = !task.isCompleted))
                _snackbarEventFlow.emit(SnackbarEvent.ShowSnackbar("Task status updated successfully"))
            } catch (e: Exception) {
                _snackbarEventFlow.emit(SnackbarEvent.ShowSnackbar("Failed to update task status${e.message} "))
            }
        }
    }

    private fun saveSubject() {
        viewModelScope.launch {
            try {
                subjectRepository.upsertSubject(
                    subject = Subject(
                        name = state.value.subjectName,
                        goalHours = state.value.goalStudyHours.toFloatOrNull() ?: 1f,
                        colors = state.value.subjectColorCard
                    )
                )
                _state.update {
                    it.copy(
                        subjectName = "",
                        goalStudyHours = "",
                        subjectColorCard = Subject.subjectColorCard.random()
                    )
                }
                _snackbarEventFlow.emit(SnackbarEvent.ShowSnackbar("Subject saved successfully"))
            } catch (e: Exception) {
                _snackbarEventFlow.emit(SnackbarEvent.ShowSnackbar("Failed to save subject. ${e.message}"))
            }
        }

    }
}