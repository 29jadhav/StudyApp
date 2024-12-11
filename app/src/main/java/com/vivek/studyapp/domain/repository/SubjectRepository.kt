package com.vivek.studyapp.domain.repository

import com.vivek.studyapp.domain.model.Subject
import kotlinx.coroutines.flow.Flow

interface SubjectRepository {

    suspend fun upsertSubject(subject: Subject)

    fun getTotalSubjects(): Flow<Int>

    suspend fun deleteSubject(subjectId: Long)

    fun getAllSubjects(): Flow<List<Subject>>

    suspend fun getSubjectById(subjectId: Long): Subject?

    fun getGoalHours(): Flow<Float>

}