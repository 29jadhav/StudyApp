package com.vivek.studyapp.presentation.task

import com.vivek.studyapp.domain.model.Subject
import com.vivek.studyapp.util.Priority

data class TaskState(
    val id: Long? = null,
    val title: String = "",
    val description: String = "",
    val isCompleted: Boolean = false,
    val dueDate: Long? = null,
    val priority: Priority = Priority.LOW,
    val relatedToSubject: String? = null,
    val subjects: List<Subject> = emptyList(),
    val subjectId: Long? = null,
    val navigateUp: Boolean =false

)
