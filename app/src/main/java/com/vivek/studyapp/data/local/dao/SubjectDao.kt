package com.vivek.studyapp.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.vivek.studyapp.domain.model.Subject
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectDao {
    @Upsert
    suspend fun upsertSubject(subject: Subject)

    @Query("SELECT COUNT(*) FROM Subject")
    fun getTotalSubjects(): Flow<Int>

    @Query("DELETE FROM Subject WHERE  subjectId = :subjectId")
    suspend fun deleteSubject(subjectId: Long)

    @Query("SELECT * FROM Subject")
    fun getAllSubjects(): Flow<List<Subject>>

    @Query("SELECT * FROM Subject WHERE subjectId=:subjectId")
    suspend fun getSubjectById(subjectId: Long): Subject?

    @Query("SELECT SUM(goalHours) FROM Subject ")
    fun getGoalHours(): Flow<Float>
}