// [S17 AUTO-REPAIRED FOR GALAXY S17 / ONE UI 7]
package com.example

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat

class KeepAliveService : Service() {
    private var wakeLock: PowerManager.WakeLock? = null
    private val CHANNEL_ID = "KeepAliveChannel"
    private val NOTIFICATION_ID = 1

    override fun onCreate() {
        super.onCreate()

        // 1) Acquire a partial wake lock so the CPU continues running
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "${'$'}{packageName}:KeepAliveLock"
        ).apply {
            // acquire without timeout; be careful to release in onDestroy
            acquire()
        }

        // 2) Create notification channel (required for Android O+)
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Background Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Keeps the app alive while running"
            }
            manager.createNotificationChannel(channel)
        }

        // 3) Build persistent notification and start foreground
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("App is running")
            .setContentText("Service keeping app alive")
            .setSmallIcon(android.R.drawable.ic_menu_info_details)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // If killed by system, try to restart (START_STICKY)
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        // Always release the wake lock when the service stops
        wakeLock?.let {
            if (it.isHeld) it.release()
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
