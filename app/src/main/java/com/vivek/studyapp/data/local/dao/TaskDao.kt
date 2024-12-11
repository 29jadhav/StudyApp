package com.vivek.studyapp.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.vivek.studyapp.domain.model.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Upsert
    suspend fun upsertTask(task: Task)

    @Query("DELETE FROM Task WHERE taskId=:taskId")
    suspend fun deleteTaskById(taskId: Long)

    @Query("DELETE FROM Task WHERE subjectTaskId= :subjectTaskId")
    suspend fun deleteTaskBySubjectTaskId(subjectTaskId: Long)

    @Query("SELECT * FROM Task")
    fun getAllTasks(): Flow<List<Task>>

    @Query("SELECT * FROM Task WHERE taskId=:taskId")
    suspend fun getTaskById(taskId: Long):Task

    @Query("SELECT * FROM Task WHERE subjectTaskId=:subjectTaskId")
    fun getTaskForSubject(subjectTaskId: Long):Flow<List<Task>>

    @Query("SELECT * FROM Task WHERE isCompleted=0")
    fun getUpcomingTasks(): Flow<List<Task>>

    @Query("SELECT * FROM Task WHERE isCompleted=1")
    fun getCompletedTasks(): Flow<List<Task>>

    @Query("SELECT * FROM Task WHERE subjectTaskId=:subjectId AND isCompleted=1")
    fun getCompletedTaskForSubject(subjectId: Long): Flow<List<Task>>

    @Query("SELECT * FROM Task WHERE subjectTaskId=:subjectTaskId AND isCompleted=0")
    fun getUpcomingTaskForSubject(subjectTaskId: Long):Flow<List<Task>>

}