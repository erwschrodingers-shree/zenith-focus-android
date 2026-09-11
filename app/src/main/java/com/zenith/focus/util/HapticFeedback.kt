package com.zenith.focus.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * Haptic Feedback Controller: Provides tactile feedback for user interactions
 */
class HapticFeedback(private val context: Context) {

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        manager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    /**
     * Light tap feedback
     */
    fun tap() {
        vibrate(10)
    }

    /**
     * Medium click feedback
     */
    fun click() {
        vibrate(20)
    }

    /**
     * Strong long press feedback
     */
    fun longPress() {
        vibrate(50)
    }

    /**
     * Success/completion feedback
     */
    fun success() {
        vibrate(listOf(0, 50, 30, 50))
    }

    /**
     * Warning/error feedback
     */
    fun warning() {
        vibrate(listOf(0, 100, 50, 100))
    }

    private fun vibrate(duration: Long) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(
                VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE)
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(duration)
        }
    }

    private fun vibrate(pattern: List<Long>) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(
                VibrationEffect.createWaveform(pattern.toLongArray(), -1)
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(pattern.toLongArray())
        }
    }
}
