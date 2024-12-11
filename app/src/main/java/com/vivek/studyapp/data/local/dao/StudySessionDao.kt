package com.vivek.studyapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.vivek.studyapp.domain.model.StudySession
import kotlinx.coroutines.flow.Flow

@Dao
interface StudySessionDao {

    @Insert
    suspend fun insertSession(session: StudySession)

    @Delete
    suspend fun deleteSession(session: StudySession)

    @Query("SELECT * FROM StudySession")
    fun getAllSessions(): Flow<List<StudySession>>

    @Query("SELECT * FROM StudySession WHERE subjectSessionId=:sessionSubjectId")
    fun getRecentSessionForSubject(sessionSubjectId: Long): Flow<List<StudySession>>

    @Query("SELECT SUM(duration) FROM StudySession WHERE subjectSessionId=:sessionSubjectId")
    fun getTotalDurationOfSessionBySubjectId(sessionSubjectId: Long): Flow<Float>

    @Query("SELECT SUM(duration) FROM StudySession")
    fun getTotalSessionDuration(): Flow<Float>

    @Query("DELETE FROM StudySession WHERE subjectSessionId=:sessionSubjectId")
    suspend fun deleteSessionBySubjectId(sessionSubjectId: Long)

    @Query("SELECT * FROM StudySession ORDER BY date DESC LIMIT 5")
    fun getRecentSessions(): Flow<List<StudySession>>
}