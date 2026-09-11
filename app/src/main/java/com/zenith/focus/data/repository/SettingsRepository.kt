package com.zenith.focus.data.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.zenith.focus.data.model.ZenConfig
import com.zenith.focus.data.model.PermissionStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Repository for managing app settings and preferences
 */
class SettingsRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        "zenith_prefs",
        Context.MODE_PRIVATE
    )

    private val _permissionStatus = MutableStateFlow(PermissionStatus())
    val permissionStatus: StateFlow<PermissionStatus> = _permissionStatus

    private val _zenConfig = MutableStateFlow(ZenConfig())
    val zenConfig: StateFlow<ZenConfig> = _zenConfig

    // Theme preference
    fun isDarkTheme(): Boolean = prefs.getBoolean("dark_theme", false)

    fun setDarkTheme(isDark: Boolean) {
        prefs.edit { putBoolean("dark_theme", isDark) }
    }

    // Duration preferences
    fun setSelectedDuration(minutes: Int) {
        prefs.edit { putInt("selected_duration", minutes) }
        updateZenConfig(zenConfig.value.copy(durationMinutes = minutes))
    }

    fun getSelectedDuration(): Int = prefs.getInt("selected_duration", 15)

    // Audio preferences
    fun setAudioEnabled(enabled: Boolean) {
        prefs.edit { putBoolean("audio_enabled", enabled) }
        updateZenConfig(zenConfig.value.copy(enableAudio = enabled))
    }

    fun isAudioEnabled(): Boolean = prefs.getBoolean("audio_enabled", false)

    fun setAudioFilePath(path: String?) {
        if (path != null) {
            prefs.edit { putString("audio_file_path", path) }
        }
        updateZenConfig(zenConfig.value.copy(audioFilePath = path))
    }

    fun getAudioFilePath(): String? = prefs.getString("audio_file_path", null)

    // DND preferences
    fun setDNDEnabled(enabled: Boolean) {
        prefs.edit { putBoolean("dnd_enabled", enabled) }
        updateZenConfig(zenConfig.value.copy(enableDND = enabled))
    }

    fun isDNDEnabled(): Boolean = prefs.getBoolean("dnd_enabled", true)

    // Reward Break preferences
    fun setRewardBreakEnabled(enabled: Boolean) {
        prefs.edit { putBoolean("reward_break_enabled", enabled) }
        updateZenConfig(zenConfig.value.copy(enableRewardBreak = enabled))
    }

    fun isRewardBreakEnabled(): Boolean = prefs.getBoolean("reward_break_enabled", false)

    // Allowed apps (max 6)
    fun setAllowedApps(apps: List<String>) {
        val limited = apps.take(6)
        prefs.edit { putString("allowed_apps", limited.joinToString(",")) }
        updateZenConfig(zenConfig.value.copy(allowedApps = limited))
    }

    fun getAllowedApps(): List<String> {
        val csv = prefs.getString("allowed_apps", "") ?: ""
        return csv.split(",").filter { it.isNotEmpty() }
    }

    // Permission status
    fun updatePermissionStatus(status: PermissionStatus) {
        _permissionStatus.value = status
        prefs.edit {
            putBoolean("perm_notification", status.notificationAccess)
            putBoolean("perm_overlay", status.screenOverlay)
            putBoolean("perm_battery", status.batteryOptimization)
            putBoolean("perm_background", status.backgroundExecution)
        }
    }

    fun getPermissionStatus(): PermissionStatus = PermissionStatus(
        notificationAccess = prefs.getBoolean("perm_notification", false),
        screenOverlay = prefs.getBoolean("perm_overlay", false),
        batteryOptimization = prefs.getBoolean("perm_battery", false),
        backgroundExecution = prefs.getBoolean("perm_background", false)
    )

    // First launch tracking
    fun hasCompletedOnboarding(): Boolean = prefs.getBoolean("onboarding_complete", false)

    fun setOnboardingComplete(complete: Boolean) {
        prefs.edit { putBoolean("onboarding_complete", complete) }
    }

    private fun updateZenConfig(config: ZenConfig) {
        _zenConfig.value = config
    }
}
