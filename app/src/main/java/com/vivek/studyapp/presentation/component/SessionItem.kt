package com.vivek.studyapp.presentation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.vivek.studyapp.domain.model.StudySession
import com.vivek.studyapp.util.fromMilliToDateString
import com.vivek.studyapp.util.fromMilliToHour

@Composable
fun SessionItem(
    modifier: Modifier = Modifier,
    studySession: StudySession,
    onDeleteSessionClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text(
                    text = studySession.relatedToSubject,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = studySession.date.fromMilliToDateString(),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "${studySession.duration.fromMilliToHour()} hr",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Gray
            )
            IconButton(onClick = { onDeleteSessionClick() }) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete the session")
            }
        }
    }

}