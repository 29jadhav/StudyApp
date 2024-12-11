package com.vivek.studyapp.presentation.session

import com.vivek.studyapp.domain.model.StudySession
import com.vivek.studyapp.domain.model.Subject

sealed class StudySessionEvent {
    data object DeleteSession : StudySessionEvent()
    data class OnRelatedSubjectChange(val subject: Subject) : StudySessionEvent()
    data class SaveSession(val duration: Long) : StudySessionEvent()
    data class OnDeleteSessionBtnClick(val session: StudySession) : StudySessionEvent()
    data object NotifyToUpdateSubject : StudySessionEvent()
    data class UpdateSubjectIdAndRelatedSubject(val subjectId: Long?, val relatedSubject: String?) :
        StudySessionEvent()


}