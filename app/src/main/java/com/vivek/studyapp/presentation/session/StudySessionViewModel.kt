package com.vivek.studyapp.presentation.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vivek.studyapp.domain.model.StudySession
import com.vivek.studyapp.domain.repository.StudySessionRepository
import com.vivek.studyapp.domain.repository.SubjectRepository
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
class StudySessionViewModel @Inject constructor(
    private val studySessionRepository: StudySessionRepository,
    private val subjectRepository: SubjectRepository
) : ViewModel() {
    private val _state = MutableStateFlow(StudySessionState())

    private val _snackbarEventFlow = MutableSharedFlow<SnackbarEvent>()
    val snackbarEventFlow = _snackbarEventFlow.asSharedFlow()

    val state = combine(
        _state,
        studySessionRepository.getAllSessions(),
        subjectRepository.getAllSubjects()
    ) { state, sessions, subjects ->
        state.copy(sessionList = sessions, subjectList = subjects)
    }.stateIn(
        viewModelScope,
        initialValue = StudySessionState(),
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000)
    )

    fun onEvent(event: StudySessionEvent) {
        when (event) {
            StudySessionEvent.DeleteSession -> deleteSession()
            StudySessionEvent.NotifyToUpdateSubject -> notifyToSelectSubject()
            is StudySessionEvent.OnDeleteSessionBtnClick -> {
                _state.update {
                    it.copy(session = event.session)
                }
            }

            is StudySessionEvent.OnRelatedSubjectChange -> {
                _state.update {
                    it.copy(
                        relatedToSubject = event.subject.name,
                        subjectId = event.subject.subjectId
                    )
                }
            }

            is StudySessionEvent.SaveSession -> saveSession(event.duration)
            is StudySessionEvent.UpdateSubjectIdAndRelatedSubject -> {
                _state.update {
                    it.copy(
                        relatedToSubject = event.relatedSubject ?: "",
                        subjectId = event.subjectId
                    )
                }
            }
        }

    }

    private fun notifyToSelectSubject() {
        viewModelScope.launch {
            _snackbarEventFlow.emit(SnackbarEvent.ShowSnackbar(message = "Related to subject can not be empty. Please choose related to subject."))
        }
    }

    private fun deleteSession() {
        viewModelScope.launch {
            try {
                _state.value.session?.let {
                    studySessionRepository.deleteSession(session = it)
                }
                _snackbarEventFlow.emit(SnackbarEvent.ShowSnackbar(message = "Session deleted successfully"))
            } catch (e: Exception) {
                _snackbarEventFlow.emit(SnackbarEvent.ShowSnackbar(message = "Failed to delete session ${e.message}"))
            }
        }
    }

    private fun saveSession(duration: Long) {
        viewModelScope.launch {
            try {
                if (_state.value.relatedToSubject.isNullOrEmpty()) {
                    _snackbarEventFlow.emit((SnackbarEvent.ShowSnackbar(message = "Related to subject can not be empty. Please choose related to subject.")))
                    return@launch
                }
                studySessionRepository.insertSession(
                    session = StudySession(
                        duration = duration,
                        relatedToSubject = _state.value.relatedToSubject,
                        subjectSessionId = _state.value.subjectId ?: -1L,
                        date = Instant.now().toEpochMilli()
                    )
                )
                _snackbarEventFlow.emit((SnackbarEvent.ShowSnackbar(message = "Session saved successfully")))
            } catch (e: Exception) {
                _snackbarEventFlow.emit(SnackbarEvent.ShowSnackbar(message = "Failed to save session ${e.message}"))
            }
        }
    }
}