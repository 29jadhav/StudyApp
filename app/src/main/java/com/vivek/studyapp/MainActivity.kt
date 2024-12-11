package com.vivek.studyapp

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.navigation.dependency
import com.vivek.studyapp.presentation.NavGraphs
import com.vivek.studyapp.presentation.destinations.StudySessionScreenRouteDestination
import com.vivek.studyapp.presentation.session.StudySessionTimerService
import com.vivek.studyapp.presentation.theme.StudyAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var isBound by mutableStateOf(false)

    private lateinit var timerService: StudySessionTimerService

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, binder: IBinder?) {
            val binderService = binder as StudySessionTimerService.StudySessionBinder
            timerService = binderService.getService()
            isBound = true
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            isBound = false
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        requestNotificationPermission()
        setContent {
            if (isBound) {
                StudyAppTheme {
                    DestinationsNavHost(navGraph = NavGraphs.root, dependenciesContainerBuilder = {
                        dependency(StudySessionScreenRouteDestination) {
                            timerService
                        }
                    })
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Intent(this, StudySessionTimerService::class.java).also { intent ->
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        unbindService(serviceConnection)
        isBound = false
        super.onStop()
    }

    private fun requestNotificationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
            0
        )
    }
}
