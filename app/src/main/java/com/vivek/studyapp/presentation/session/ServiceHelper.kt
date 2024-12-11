package com.vivek.studyapp.presentation.session

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.TaskStackBuilder
import androidx.core.net.toUri
import com.vivek.studyapp.MainActivity
import com.vivek.studyapp.util.ServiceConstants.CLICK_REQUEST_CODE
import com.vivek.studyapp.util.ServiceConstants.SESSION_SCREEN_URI

object ServiceHelper {

    fun triggerForegroundService(context: Context, action: String) {
        Intent(context, StudySessionTimerService::class.java).apply {
            this.action = action
            context.startService(this)
        }
    }

    fun clickPendingIntent(context: Context): PendingIntent? {
        val deepLinkIntent = Intent(
            Intent.ACTION_VIEW,
            SESSION_SCREEN_URI.toUri(),
            context,
            MainActivity::class.java
        )
        return TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(deepLinkIntent)
            getPendingIntent(CLICK_REQUEST_CODE, PendingIntent.FLAG_IMMUTABLE)
        }
    }
}