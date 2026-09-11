package com.zenith.focus.domain.usecase

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.core.content.ContextCompat

class PermissionCheckUseCase(private val context: Context) {

    fun hasNotificationAccessPermission(): Boolean {
        return try {
            val enabled = Settings.Secure.getInt(
                context.contentResolver,
                "enabled_notification_listeners",
                0
            )
            enabled != 0 && isZenithNotificationListenerEnabled()
        } catch (e: Exception) {
            false
        }
    }

    private fun isZenithNotificationListenerEnabled(): Boolean {
        val cn = "com.zenith.focus/.service.ZenthNotificationListenerService"
        val notificationListeners = Settings.Secure.getString(
            context.contentResolver,
            "enabled_notification_listeners"
        ) ?: return false
        return notificationListeners.contains(cn)
    }

    fun hasScreenOverlayPermission(): Boolean {
        return Settings.canDrawOverlays(context)
    }

    fun hasIgnoreBatteryOptimizationPermission(): Boolean {
        return try {
            val pm = context.packageManager
            val batteryOptimizationIntent = Intent()
            batteryOptimizationIntent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
            batteryOptimizationIntent.data = android.net.Uri.parse("package:${context.packageName}")
            pm.resolveActivity(batteryOptimizationIntent, 0) != null
        } catch (e: Exception) {
            false
        }
    }

    fun requestNotificationAccess() {
        val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
        context.startActivity(intent)
    }

    fun requestScreenOverlayPermission() {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            android.net.Uri.parse("package:${context.packageName}")
        )
        context.startActivity(intent)
    }

    fun requestIgnoreBatteryOptimization() {
        val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
        intent.data = android.net.Uri.parse("package:${context.packageName}")
        context.startActivity(intent)
    }
}
