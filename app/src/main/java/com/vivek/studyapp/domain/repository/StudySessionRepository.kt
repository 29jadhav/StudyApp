package com.vivek.studyapp.domain.repository

import com.vivek.studyapp.domain.model.StudySession
import kotlinx.coroutines.flow.Flow

interface StudySessionRepository {
    suspend fun insertSession(session: StudySession)

    suspend fun deleteSession(session: StudySession)

    fun getAllSessions(): Flow<List<StudySession>>

    fun getRecentSessionForSubject(sessionSubjectId: Long): Flow<List<StudySession>>

    fun getRecentFiveSessions(): Flow<List<StudySession>>

    fun getTotalDurationOfSessionBySubjectId(sessionSubjectId: Long): Flow<Float>

    fun getTotalSessionDuration(): Flow<Float>

    suspend fun deleteSessionBySubjectId(sessionSubjectId: Long)
}