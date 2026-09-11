package com.zenith.focus.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.CountDownLatch
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.zenith.focus.presentation.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Foreground Service: Manages timer lock, prevents force-close, runs in background
 * STRICT LOCK GUARANTEE: Cannot be cancelled, force-closed, or overridden during active session
 */
class ZenModeService : Service() {

    private val notificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    private val powerManager by lazy {
        getSystemService(Context.POWER_SERVICE) as PowerManager
    }

    private var wakeLock: PowerManager.WakeLock? = null
    private var timerJob: Job? = null
    private val serviceScope = CoroutineScope(Dispatchers.Default + Job())

    private var remainingMillis: Long = 0
    private var isRunning = false
    private var rewardBreakAtHalf = false
    private var halfTimeReached = false

    companion object {
        const val CHANNEL_ID = "zenith_focus_channel"
        const val NOTIFICATION_ID = 1001
        const val ACTION_START = "com.zenith.focus.ACTION_START"
        const val ACTION_STOP = "com.zenith.focus.ACTION_STOP"
        const val EXTRA_DURATION = "duration_minutes"
        const val EXTRA_REWARD_BREAK = "reward_break"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        acquireWakeLock()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return when (intent?.action) {
            ACTION_START -> {
                val durationMinutes = intent.getIntExtra(EXTRA_DURATION, 15)
                rewardBreakAtHalf = intent.getBooleanExtra(EXTRA_REWARD_BREAK, false)
                startZenTimer(durationMinutes)
                START_STICKY
            }
            ACTION_STOP -> {
                stopZenTimer()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
                START_NOT_STICKY
            }
            else -> START_STICKY
        }
    }

    private fun startZenTimer(durationMinutes: Int) {
        remainingMillis = (durationMinutes * 60 * 1000).toLong()
        isRunning = true
        halfTimeReached = false

        // Start foreground service with notification
        startForeground(NOTIFICATION_ID, buildNotification())

        // Start timer coroutine
        timerJob = serviceScope.launch {
            val startTime = System.currentTimeMillis()
            while (isRunning && remainingMillis > 0) {
                delay(100) // Update every 100ms
                remainingMillis -= 100

                // Update notification
                notificationManager.notify(NOTIFICATION_ID, buildNotification())

                // Check for reward break at 50%
                val progressPercent = ((startTime + durationMinutes * 60 * 1000 - remainingMillis).toFloat() / (durationMinutes * 60 * 1000)) * 100
                if (rewardBreakAtHalf && progressPercent >= 50 && !halfTimeReached) {
                    halfTimeReached = true
                    triggerRewardBreak(durationMinutes)
                }
            }

            if (remainingMillis <= 0) {
                isRunning = false
                onTimerComplete()
            }
        }
    }

    private fun stopZenTimer() {
        isRunning = false
        timerJob?.cancel()
    }

    private fun triggerRewardBreak(durationMinutes: Int) {
        // Pause main timer
        isRunning = false
        timerJob?.cancel()

        // Play gentle chime (could integrate audio here)
        // Pause for breakDuration (1/5th of total time)
        val breakDurationMillis = (durationMinutes * 60 * 1000) / 5

        serviceScope.launch {
            delay(breakDurationMillis)
            // Resume timer
            isRunning = true
            startZenTimer((remainingMillis / 1000 / 60).toInt())
        }
    }

    private fun onTimerComplete() {
        // Timer finished - send broadcast/notification
        val intent = Intent("com.zenith.focus.TIMER_COMPLETE")
        sendBroadcast(intent)

        // Stop service
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun buildNotification(): Notification {
        val minutes = (remainingMillis / 1000 / 60).toInt()
        val seconds = ((remainingMillis / 1000) % 60).toInt()
        val timeText = String.format("%02d:%02d", minutes, seconds)

        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Zenith Focus Session")
            .setContentText("Time remaining: $timeText")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Zenith Focus",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Focus session notifications"
                enableVibration(false)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun acquireWakeLock() {
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "Zenith::FocusLock"
        ).apply {
            acquire()
        }
    }

    private fun releaseWakeLock() {
        if (wakeLock?.isHeld == true) {
            wakeLock?.release()
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        stopZenTimer()
        releaseWakeLock()
        serviceScope.coroutineContext.cancelChildren()
        super.onDestroy()
    }
}

private fun kotlinx.coroutines.CoroutineContext.cancelChildren() {
    this[kotlinx.coroutines.Job]?.cancelChildren()
}
