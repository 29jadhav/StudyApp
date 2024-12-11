package com.vivek.studyapp.presentation.component

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDatePickerDialog(
    state: DatePickerState,
    isOpen: Boolean,
    onConfirmBtnClick: () -> Unit,
    onDismissBtnClick: () -> Unit,
    confirmBtnText: String = "Ok",
    dismissBtnText: String = "Cancel"
) {
    if (isOpen) {
        DatePickerDialog(
            onDismissRequest = onDismissBtnClick,
            confirmButton = {
                TextButton(onClick = onConfirmBtnClick) {
                    Text(text = confirmBtnText)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissBtnClick) {
                    Text(text = dismissBtnText)
                }
            },
            content = {
                DatePicker(
                    state = state,
                )
            }
        )
    }
}