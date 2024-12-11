package com.vivek.studyapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.vivek.studyapp.data.local.dao.StudySessionDao
import com.vivek.studyapp.data.local.dao.SubjectDao
import com.vivek.studyapp.data.local.dao.TaskDao
import com.vivek.studyapp.domain.model.StudySession
import com.vivek.studyapp.domain.model.Subject
import com.vivek.studyapp.domain.model.Task

@Database(entities = [StudySession::class, Subject::class, Task::class], version = 1)
@TypeConverters(value = [ColorListConvertor::class])
abstract class StudyAppDatabase : RoomDatabase() {

    abstract fun subjectDao(): SubjectDao

    abstract fun taskDao(): TaskDao

    abstract fun studySessionDao(): StudySessionDao

}