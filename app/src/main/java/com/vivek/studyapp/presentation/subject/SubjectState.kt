package com.vivek.studyapp.presentation.subject

import androidx.compose.ui.graphics.Color
import com.vivek.studyapp.domain.model.StudySession
import com.vivek.studyapp.domain.model.Subject
import com.vivek.studyapp.domain.model.Task

data class SubjectState(
    val subjectId: Long? = null,
    val goalStudyHours: String = "",
    val subjectName: String = "",
    val studyHours: Float = 0f,
    val subjectCardColor: List<Color> = Subject.subjectColorCard.random(),
    val progress: Float = 0f,
    val completedTask: List<Task> = emptyList(),
    val upcomingTask: List<Task> = emptyList(),
    val recentSessions: List<StudySession> = emptyList(),
    val session: StudySession?=null,
    val isLoading: Boolean = false
)
