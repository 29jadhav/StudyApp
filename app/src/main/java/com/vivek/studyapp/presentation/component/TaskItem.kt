package com.vivek.studyapp.presentation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.vivek.studyapp.domain.model.Task
import com.vivek.studyapp.util.Priority
import com.vivek.studyapp.util.fromMilliToDateString

@Composable
fun TaskItem(
    modifier: Modifier = Modifier,
    task: Task,
    onCheckBoxClick: () -> Unit,
    onClick: () -> Unit
) {

    ElevatedCard(modifier = modifier, onClick = { onClick() }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TaskCheckBox(
                task.isCompleted,
                borderColor = Priority.fromInt(task.priority).color,
                onCheckBoxClick = {})
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    //modifier = Modifier.padding(12.dp, 12.dp),
                    text = task.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textDecoration = if (task.isCompleted) {
                        TextDecoration.LineThrough
                    } else TextDecoration.None,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    // modifier = Modifier.padding(12.dp, 12.dp),
                    text = task.dueDate.fromMilliToDateString(),
                    textAlign = TextAlign.Left
                )
            }
        }
    }
}