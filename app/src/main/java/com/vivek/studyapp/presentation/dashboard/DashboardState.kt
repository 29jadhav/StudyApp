package com.vivek.studyapp.presentation.dashboard

import androidx.compose.ui.graphics.Color
import com.vivek.studyapp.domain.model.StudySession
import com.vivek.studyapp.domain.model.Subject

data class DashboardState(
    val studiedHours: Float = 0f,
    val subjectCount: Int = 0,
    val goalStudyHoursOfAllSessions: Float = 0f,
    val goalStudyHours: String = "",
    val subjects: List<Subject> = emptyList(),
    val subjectColorCard: List<Color> = Subject.subjectColorCard.random(),
    val subjectName: String = "",
    val session: StudySession? = null
)
