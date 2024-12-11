package com.vivek.studyapp.presentation.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun DeleteDialog(
    isOpen: Boolean,
    title: String,
    bodyText: String,
    onDismissClick: () -> Unit,
    onConfirmClick: () -> Unit
) {
    if (isOpen) {
        AlertDialog(
            title = { Text(text = title) },
            text = {
                Text(text = bodyText)
            },
            onDismissRequest = onDismissClick,
            dismissButton = {
                TextButton(onClick = onDismissClick) {
                    Text(text = "Cancel")
                }
            },
            confirmButton = {
                TextButton(onClick = onConfirmClick) {
                    Text(text = "Delete")
                }

            })
    }

}