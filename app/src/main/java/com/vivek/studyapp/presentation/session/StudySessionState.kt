package com.vivek.studyapp.presentation.session

import com.vivek.studyapp.domain.model.StudySession
import com.vivek.studyapp.domain.model.Subject

data class StudySessionState(
    val subjectList: List<Subject> = emptyList(),
    val sessionList: List<StudySession> = emptyList(),
    val relatedToSubject: String = "",
    val session: StudySession? = null,
    val subjectId: Long? = null
)
