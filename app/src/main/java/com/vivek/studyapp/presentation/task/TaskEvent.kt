package com.vivek.studyapp.presentation.task

import com.vivek.studyapp.domain.model.Subject
import com.vivek.studyapp.util.Priority

sealed class TaskEvent {
    data object SaveTask : TaskEvent()

    data object DeleteTask : TaskEvent()

    data class OnTitleChange(val title: String) : TaskEvent()

    data class OnDescriptionChange(val description: String) : TaskEvent()

    data class OnDateSelect(val date: Long) : TaskEvent()

    data class OnSelectOfRelatedToSubject(val subject: Subject) : TaskEvent()

    data object OnIsCompleteStatusUpdate : TaskEvent()

    data class OnPriorityChange(val priority: Priority) : TaskEvent()

}