package com.vivek.studyapp.data.local.repository

import com.vivek.studyapp.data.local.dao.StudySessionDao
import com.vivek.studyapp.data.local.dao.SubjectDao
import com.vivek.studyapp.data.local.dao.TaskDao
import com.vivek.studyapp.domain.model.Subject
import com.vivek.studyapp.domain.repository.SubjectRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SubjectRepositoryImpl @Inject constructor(
    private val subjectDao: SubjectDao,
    private val taskDao: TaskDao,
    private val sessionDao: StudySessionDao
) : SubjectRepository {

    override suspend fun upsertSubject(subject: Subject) {
        subjectDao.upsertSubject(subject)
    }

    override fun getTotalSubjects(): Flow<Int> {
        return subjectDao.getTotalSubjects()
    }

    override suspend fun deleteSubject(subjectId: Long) {
        taskDao.deleteTaskBySubjectTaskId(subjectId)
        sessionDao.deleteSessionBySubjectId(subjectId)
        subjectDao.deleteSubject(subjectId)
    }

    override fun getAllSubjects(): Flow<List<Subject>> {
        return subjectDao.getAllSubjects()
    }

    override suspend fun getSubjectById(subjectId: Long): Subject? {
        return subjectDao.getSubjectById(subjectId)
    }

    override fun getGoalHours(): Flow<Float> {
        return subjectDao.getGoalHours()
    }
}