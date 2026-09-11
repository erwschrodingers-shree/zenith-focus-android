package com.zenith.focus.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * Break Alarm Receiver: Triggers reward break timer callback
 */
class BreakAlarmReceiver : BroadcastReceiver() {

    companion object {
        const val TAG = "ZenthBreakAlarm"
        const val ACTION_BREAK_TIME = "com.zenith.focus.ACTION_BREAK_TIME"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            ACTION_BREAK_TIME -> {
                Log.d(TAG, "Break time reached!")
                // Play gentle chime
                // Broadcast to UI to show break screen
                val breakIntent = Intent("com.zenith.focus.BREAK_TIME")
                context?.sendBroadcast(breakIntent)
            }
        }
    }
}
