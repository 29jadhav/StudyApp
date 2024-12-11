package com.vivek.studyapp.domain.model

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.vivek.studyapp.presentation.theme.gradient1
import com.vivek.studyapp.presentation.theme.gradient2
import com.vivek.studyapp.presentation.theme.gradient3
import com.vivek.studyapp.presentation.theme.gradient4
import com.vivek.studyapp.presentation.theme.gradient5

@Entity
data class Subject(
    @PrimaryKey(autoGenerate = true)
    val subjectId: Long? = null,
    val name: String,
    val goalHours: Float,
    val colors: List<Color>
) {
    companion object {
        val subjectColorCard = listOf(gradient1, gradient2, gradient3, gradient4, gradient5)
    }
}
