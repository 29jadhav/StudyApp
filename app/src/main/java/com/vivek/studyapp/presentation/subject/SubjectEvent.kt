package com.vivek.studyapp.presentation.subject

import androidx.compose.ui.graphics.Color
import com.vivek.studyapp.domain.model.StudySession
import com.vivek.studyapp.domain.model.Task

sealed class SubjectEvent {
    data object UpdateSubject : SubjectEvent()

    data object DeleteSubject : SubjectEvent()

    data object DeleteSession : SubjectEvent()

    data object UpdateProgress : SubjectEvent()

    data class OnSubjectNameChange(val name: String) : SubjectEvent()

    data class OnIsTaskCompletedChange(val task: Task) : SubjectEvent()

    data class OnGoalStudyHourChange(val goalStudyHours: String) : SubjectEvent()

    data class OnSubjectColorCardChange(val colorCard: List<Color>) : SubjectEvent()

    data class OnDeleteSessionBtnClick(val session: StudySession) : SubjectEvent()
}