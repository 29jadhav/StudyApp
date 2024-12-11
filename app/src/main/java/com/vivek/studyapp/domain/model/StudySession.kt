package com.vivek.studyapp.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class StudySession(
    @PrimaryKey(autoGenerate = true)
    val sessionId: Long? = null,
    val duration: Long,
    val date: Long,
    val relatedToSubject: String,
    val subjectSessionId: Long
) {

}
