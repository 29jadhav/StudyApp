package com.vivek.studyapp.data.local.repository

import com.vivek.studyapp.data.local.dao.TaskDao
import com.vivek.studyapp.domain.model.Task
import com.vivek.studyapp.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(private val taskDao: TaskDao) : TaskRepository {
    override suspend fun upsertTask(task: Task) {
        taskDao.upsertTask(task)
    }

    override suspend fun deleteTaskById(taskId: Long) {
        taskDao.deleteTaskById(taskId)
    }

    override suspend fun deleteTaskBySubjectTaskId(subjectTaskId: Long) {
        taskDao.deleteTaskBySubjectTaskId(subjectTaskId)
    }

    override fun getAllTasks(): Flow<List<Task>> {
        return taskDao.getAllTasks()
    }

    override suspend fun getTaskById(taskId: Long): Task {
        return taskDao.getTaskById(taskId)
    }

    override fun getTaskForSubject(subjectTaskId: Long): Flow<List<Task>> {
        return taskDao.getTaskForSubject(subjectTaskId)
    }

    override fun getUpcomingTasks(): Flow<List<Task>> {
        return taskDao.getUpcomingTasks()
    }
    
    override fun getCompletedTasks(): Flow<List<Task>> {
        return taskDao.getCompletedTasks()
    }

    override fun getUpcomingTasksForSubject(subjectTaskId: Long): Flow<List<Task>> {
        return taskDao.getUpcomingTaskForSubject(subjectTaskId)
    }

    override fun getCompletedTasksForSubject(subjectTaskId: Long): Flow<List<Task>> {
        return taskDao.getCompletedTaskForSubject(subjectTaskId)
    }
}