package com.vivek.studyapp.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vivek.studyapp.R
import com.vivek.studyapp.domain.model.Task


fun LazyListScope.taskList(
    sectionTitle: String,
    emptyTaskListText: String,
    tasks: List<Task>,
    onTaskItemClick: (Long?) -> Unit,
    onCheckBoxClick: (Task) -> Unit
) {
    item {
        Text(
            text = sectionTitle,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(12.dp)
        )
    }
    if (tasks.isNullOrEmpty()) {
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    modifier = Modifier.size(120.dp),
                    painter = painterResource(id = R.drawable.img_tasks),
                    contentDescription = ""
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = emptyTaskListText,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }

        }
    } else {
        items(tasks) { task ->
            TaskItem(
                modifier = Modifier.padding(12.dp, 12.dp),
                task = task,
                onCheckBoxClick = { onCheckBoxClick(task) },
                onClick = {onTaskItemClick(task.taskId)})

        }
    }
}