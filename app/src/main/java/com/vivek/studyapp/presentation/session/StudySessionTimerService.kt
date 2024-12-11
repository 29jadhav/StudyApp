package com.vivek.studyapp.presentation.session

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.NotificationCompat
import com.vivek.studyapp.util.ServiceConstants.ACTION_SERVICE_CANCEL
import com.vivek.studyapp.util.ServiceConstants.ACTION_SERVICE_START
import com.vivek.studyapp.util.ServiceConstants.ACTION_SERVICE_STOP
import com.vivek.studyapp.util.ServiceConstants.NOTIFICATION_CHANNEL_ID
import com.vivek.studyapp.util.ServiceConstants.NOTIFICATION_CHANNEL_NAME
import com.vivek.studyapp.util.ServiceConstants.NOTIFICATION_ID
import com.vivek.studyapp.util.pad
import dagger.hilt.android.AndroidEntryPoint
import java.util.Timer
import javax.inject.Inject
import kotlin.concurrent.fixedRateTimer
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@AndroidEntryPoint
class StudySessionTimerService : Service() {

    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var notificationBuilder: NotificationCompat.Builder

    private val binder = StudySessionBinder()

    private lateinit var timer: Timer

    var duration: Duration = Duration.ZERO
        private set

    var second = mutableStateOf("00")
        private set
    var minute = mutableStateOf("00")
        private set
    var hour = mutableStateOf("00")
        private set

    var currentTimerState = mutableStateOf(TimerState.IDLE)
        private set

    var subjectId = mutableStateOf<Long?>(null);


    override fun onBind(intent: Intent) = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.action.let { action ->
            when (action) {
                ACTION_SERVICE_START -> {
                    startForeService()
                    startTimer { hour, minute, seconds ->
                        updateNotification(hour, minute, seconds)
                    }
                }

                ACTION_SERVICE_STOP -> {
                    stopTimer()
                }

                ACTION_SERVICE_CANCEL -> {
                    stopTimer()
                    cancelTimer()
                    stopForegroundService()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)

    }

    private fun startForeService() {
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }

    private fun updateNotification(h: String, m: String, s: String) {
        notificationManager.notify(
            NOTIFICATION_ID,
            notificationBuilder.setContentText("$h:$m:$s").build()
        )
    }

    private fun startTimer(onTick: (h: String, m: String, s: String) -> Unit) {
        currentTimerState.value = TimerState.STARTED
        timer = fixedRateTimer(initialDelay = 1000L, period = 1000L) {
            duration = duration.plus(1.seconds)
            updateTimeUnit()
            onTick(hour.value, minute.value, second.value)
        }
    }

    private fun updateTimeUnit() {
        duration.toComponents { hours, minutes, seconds, _ ->
            hour.value = hours.toInt().pad()
            minute.value = minutes.pad()
            second.value = seconds.pad()
        }
    }

    private fun stopTimer() {
        if (this::timer.isInitialized) {
            timer.cancel()
        }
        currentTimerState.value = TimerState.IDLE
    }

    private fun cancelTimer() {
        duration = Duration.ZERO
        updateTimeUnit()
        currentTimerState.value = TimerState.IDLE

    }

    private fun stopForegroundService() {
        notificationManager.cancel(NOTIFICATION_ID)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    inner class StudySessionBinder : Binder() {
        fun getService(): StudySessionTimerService = this@StudySessionTimerService
    }

}

enum class TimerState {
    IDLE,
    STARTED,
    STOPPED
}