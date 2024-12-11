package com.vivek.studyapp.data.di

import android.app.Application
import androidx.room.Room
import com.vivek.studyapp.data.local.StudyAppDatabase
import com.vivek.studyapp.data.local.dao.StudySessionDao
import com.vivek.studyapp.data.local.dao.SubjectDao
import com.vivek.studyapp.data.local.dao.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun providesDatabase(application: Application): StudyAppDatabase {
        return Room.databaseBuilder(application, StudyAppDatabase::class.java, "study_app.db")
            .build()
    }

    @Provides
    @Singleton
    fun providesSubjectDao(appDatabase: StudyAppDatabase): SubjectDao{
        return appDatabase.subjectDao()
    }

    @Provides
    @Singleton
    fun providesTaskDao(appDatabase: StudyAppDatabase):TaskDao{
        return appDatabase.taskDao()
    }

    @Provides
    @Singleton
    fun providesStudySessionDao(appDatabase: StudyAppDatabase): StudySessionDao{
        return appDatabase.studySessionDao()
    }
}