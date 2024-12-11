package com.vivek.studyapp.data.di

import com.vivek.studyapp.data.local.repository.StudySessionRepositoryImpl
import com.vivek.studyapp.data.local.repository.SubjectRepositoryImpl
import com.vivek.studyapp.data.local.repository.TaskRepositoryImpl
import com.vivek.studyapp.domain.repository.StudySessionRepository
import com.vivek.studyapp.domain.repository.SubjectRepository
import com.vivek.studyapp.domain.repository.TaskRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindSubjectRepository(subjectRepositoryImpl: SubjectRepositoryImpl): SubjectRepository

    @Singleton
    @Binds
    abstract fun bindTaskRepository(taskRepositoryImpl: TaskRepositoryImpl): TaskRepository

    @Singleton
    @Binds
    abstract fun bindStudySessionRepository(studySessionRepositoryImpl: StudySessionRepositoryImpl): StudySessionRepository
}