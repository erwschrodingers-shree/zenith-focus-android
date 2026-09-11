package com.zenith.focus.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.zenith.focus.service.ZenModeService

/**
 * Boot Completed Receiver: Resilience for device reboot
 * Restarts Zen Mode service if it was running during reboot
 */
class BootCompletedReceiver : BroadcastReceiver() {

    companion object {
        const val TAG = "ZenthBootReceiver"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d(TAG, "Device boot completed, checking for active session")

            context?.let {
                val prefs = it.getSharedPreferences("zenith_prefs", Context.MODE_PRIVATE)
                val wasSessionActive = prefs.getBoolean("session_active", false)

                if (wasSessionActive) {
                    val remainingMinutes = prefs.getInt("remaining_minutes", 0)
                    if (remainingMinutes > 0) {
                        // Restart the Zen Mode service
                        val serviceIntent = Intent(it, ZenModeService::class.java).apply {
                            action = ZenModeService.ACTION_START
                            putExtra(ZenModeService.EXTRA_DURATION, remainingMinutes)
                            putExtra(ZenModeService.EXTRA_REWARD_BREAK, prefs.getBoolean("reward_break", false))
                        }
                        it.startForegroundService(serviceIntent)
                        Log.d(TAG, "Restarted Zen Mode service with $remainingMinutes minutes")
                    }
                }
            }
        }
    }
}
