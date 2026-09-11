package com.zenith.focus.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * Data model for storing focus session history
 */
@Entity(tableName = "zen_sessions")
data class ZenSession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val durationMinutes: Int,
    val startTime: Long,
    val endTime: Long? = null,
    val completed: Boolean = false,
    val hasAudio: Boolean = false,
    val audioFile: String? = null,
    val dndEnabled: Boolean = false,
    val rewardBreakEnabled: Boolean = false,
    val allowedApps: String = "" // JSON serialized list
)

/**
 * UI State for timer display
 */
data class TimerUIState(
    val minutes: Int = 0,
    val seconds: Int = 0,
    val totalDurationMinutes: Int = 0,
    val isRunning: Boolean = false,
    val isLocked: Boolean = false,
    val progressPercent: Float = 0f
) {
    val formattedTime: String get() = String.format("%02d:%02d", minutes, seconds)
}

/**
 * Configuration for Zen Mode session
 */
data class ZenConfig(
    val durationMinutes: Int = 15,
    val enableAudio: Boolean = false,
    val audioFilePath: String? = null,
    val enableDND: Boolean = true,
    val enableRewardBreak: Boolean = false,
    val allowedApps: List<String> = emptyList()
)

/**
 * App model for whitelist selection
 */
data class AppInfo(
    val packageName: String,
    val appName: String,
    val icon: String? = null,
    val isSelected: Boolean = false
)

/**
 * Permission status tracker
 */
data class PermissionStatus(
    val notificationAccess: Boolean = false,
    val screenOverlay: Boolean = false,
    val batteryOptimization: Boolean = false,
    val backgroundExecution: Boolean = false
) {
    val allGranted: Boolean
        get() = notificationAccess && screenOverlay && batteryOptimization && backgroundExecution
}
