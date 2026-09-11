package com.zenith.focus.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

/**
 * Notification Listener Service: Intercepts and suppresses notifications during Zen Mode
 * Requires: android.permission.BIND_NOTIFICATION_LISTENER_SERVICE
 */
class ZenthNotificationListenerService : NotificationListenerService() {

    companion object {
        const val TAG = "ZenthNotificationListener"
        private var isZenModeActive = false

        fun setZenModeActive(active: Boolean) {
            isZenModeActive = active
        }
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)

        // If Zen Mode is active, suppress notification
        if (isZenModeActive && sbn != null) {
            Log.d(TAG, "Intercepting notification: ${sbn.packageName}")
            cancelNotification(sbn.key)
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        Log.d(TAG, "Notification removed: ${sbn?.packageName}")
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.d(TAG, "Notification Listener Connected")
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        Log.d(TAG, "Notification Listener Disconnected")
    }
}
