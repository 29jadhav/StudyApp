package com.vivek.studyapp.data.local.repository

import com.vivek.studyapp.data.local.dao.StudySessionDao
import com.vivek.studyapp.domain.model.StudySession
import com.vivek.studyapp.domain.repository.StudySessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.take
import javax.inject.Inject

class StudySessionRepositoryImpl @Inject constructor(private val studySessionDao: StudySessionDao) :
    StudySessionRepository {
    override suspend fun insertSession(session: StudySession) {
        studySessionDao.insertSession(session)
    }

    override suspend fun deleteSession(session: StudySession) {
        studySessionDao.deleteSession(session)
    }

    override fun getAllSessions():Flow<List<StudySession>> {
        return studySessionDao.getAllSessions()
    }

    override fun getRecentSessionForSubject(sessionSubjectId: Long): Flow<List<StudySession>> {
        return studySessionDao.getRecentSessionForSubject(sessionSubjectId).take(10)
    }

    override fun getRecentFiveSessions(): Flow<List<StudySession>> {
        return studySessionDao.getRecentSessions()
    }

    override fun getTotalDurationOfSessionBySubjectId(sessionSubjectId: Long): Flow<Float> {
        return studySessionDao.getTotalDurationOfSessionBySubjectId(sessionSubjectId)
    }

    override fun getTotalSessionDuration(): Flow<Float> {
        return studySessionDao.getTotalSessionDuration()
    }

    override suspend fun deleteSessionBySubjectId(sessionSubjectId: Long) {
        studySessionDao.deleteSessionBySubjectId(sessionSubjectId)
    }
}