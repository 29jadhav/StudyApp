package com.vivek.studyapp.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import com.vivek.studyapp.domain.model.StudySession

fun LazyListScope.studySessionList(
    sectionTitle: String,
    studySessionList: List<StudySession>,
    emptySessionTitle: String,
    onDeleteSessionClick:(StudySession)->Unit
) {
    item {
        Text(
            text = sectionTitle,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(12.dp)
        )
    }
    item {
        if (studySessionList.isNullOrEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp, 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                Image(
                    modifier = Modifier.size(120.dp),
                    painter = painterResource(id = R.drawable.img_lamp),
                    contentDescription = ""
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = emptySessionTitle,
                    color = Color.Gray,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
    items(studySessionList) { studySession ->
        SessionItem(
            modifier = Modifier.padding(12.dp, 12.dp),
            studySession = studySession,
            onDeleteSessionClick = {onDeleteSessionClick(studySession)})

    }
}