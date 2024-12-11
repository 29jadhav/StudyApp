package com.vivek.studyapp.presentation.dashboard

import androidx.compose.ui.graphics.Color
import com.vivek.studyapp.domain.model.StudySession
import com.vivek.studyapp.domain.model.Task

sealed class DashboardEvent {
    data object SaveSubject : DashboardEvent()
    data object DeleteSession : DashboardEvent()
    data class OnDeleteSessionBtnClick(val session: StudySession) : DashboardEvent()
    data class OnTaskIsCompleteChange(val task: Task) : DashboardEvent()
    data class OnSubjectCardColorChange(val colors: List<Color>) : DashboardEvent()
    data class OnSubjectNameChange(val name: String) : DashboardEvent()
    data class OnGoalStudyHourChange(val hours: String) : DashboardEvent()
}