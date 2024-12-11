package com.vivek.studyapp.presentation.component

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.vivek.studyapp.domain.model.Subject

@Composable
fun AddSubjectDialog(
    isOpen: Boolean,
    subjectName: String,
    goalHours: String,
    selectedColors: List<Color>,
    onColorChange: (List<Color>) -> Unit,
    onSubjectNameChange: (String?) -> Unit,
    onGoalHoursChange: (String?) -> Unit,
    onDismissRequest: () -> Unit,
    onConfirmBtnClick: () -> Unit
) {
    var subjectNameError by rememberSaveable {
        mutableStateOf<String?>(null)
    }
    var goalHourError by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    subjectNameError = when {
        subjectName.isBlank() -> "Please enter the subject name"
        subjectName.length < 2 -> "Subject name is too short"
        subjectName.length > 20 -> "Subject name is too long"
        else -> null
    }

    goalHourError = when {
        goalHours.isBlank() -> "Please enter the goal study hours"
        goalHours.toFloatOrNull() == null -> "Invalid study hours"
        goalHours.toFloat() < 1 -> "Please set at least 1 hour"
        goalHours.toFloat() > 1000f -> "Please set maximum of 1000 hours"
        else -> null
    }

    if (isOpen) {
        AlertDialog(onDismissRequest = onDismissRequest,
            title = { Text(text = "Add/Update Subject") },
            text = {
                Column {
                    Row {
                        Subject.subjectColorCard.forEach { gradientColors ->
                            Box(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .clickable { onColorChange(gradientColors) }
                                    .background(brush = Brush.verticalGradient(gradientColors))
                                    .border(
                                        width = 1.dp,
                                        color = if (selectedColors == gradientColors) Color.Black else Color.Transparent,
                                        shape = CircleShape

                                    )
                            )

                        }
                    }
                    OutlinedTextField(
                        value = subjectName,
                        onValueChange = onSubjectNameChange,
                        label = { Text(text = "Subject Name") },
                        singleLine = true,
                        isError = subjectNameError != null && subjectName.isNotBlank(),
                        supportingText = { Text(text = subjectNameError.orEmpty()) }

                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = goalHours,
                        onValueChange = onGoalHoursChange,
                        label = { Text(text = "Goal Study Hours") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = goalHourError != null && goalHours.isNotBlank(),
                        supportingText = { Text(text = goalHourError.orEmpty()) }
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(text = "Cancel")
                }
            },
            confirmButton = {
                TextButton(
                    onClick = onConfirmBtnClick,
                    enabled = goalHourError == null && subjectNameError == null
                ) {
                    Text(text = "Save")
                }
            })
    }
}