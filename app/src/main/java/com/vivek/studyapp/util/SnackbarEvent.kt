package com.vivek.studyapp.util

import androidx.compose.material3.SnackbarDuration

sealed class SnackbarEvent {
    data class ShowSnackbar(
        val message: String,
        val duration: SnackbarDuration = SnackbarDuration.Short
    ): SnackbarEvent()
}