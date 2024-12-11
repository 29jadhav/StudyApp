package com.vivek.studyapp.domain.repository

import com.vivek.studyapp.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    suspend fun upsertTask(task: Task)

    suspend fun deleteTaskById(taskId: Long)

    suspend fun deleteTaskBySubjectTaskId(subjectTaskId: Long)

    fun getAllTasks(): Flow<List<Task>>

    suspend fun getTaskById(taskId: Long): Task

    fun getTaskForSubject(subjectTaskId: Long): Flow<List<Task>>

    fun getUpcomingTasks(): Flow<List<Task>>

    fun getUpcomingTasksForSubject(subjectTaskId: Long): Flow<List<Task>>

    fun getCompletedTasks(): Flow<List<Task>>

    fun getCompletedTasksForSubject(subjectTaskId: Long): Flow<List<Task>>
}