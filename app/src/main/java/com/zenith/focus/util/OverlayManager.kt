package com.zenith.focus.util

import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import android.view.WindowManager

/**
 * Overlay Manager: Controls system-level display overlay for strict lock
 * Prevents users from escaping Zen Mode via standard navigation
 */
class OverlayManager(private val context: Context) {

    private val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

    /**
     * Enable strict lock - prevents home button, back button, and other navigation
     * Shows immersive full-screen experience
     */
    fun enableStrictLock(activity: android.app.Activity) {
        val window = activity.window

        // Set immersive full-screen flags
        window.decorView.systemUiVisibility = (
            android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
                or android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        )

        // Add flags to keep screen on and prevent lock
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )

        // On Android 12+, handle full-screen intent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            window.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
        }
    }

    /**
     * Disable strict lock - restore normal navigation
     */
    fun disableStrictLock(activity: android.app.Activity) {
        val window = activity.window
        window.decorView.systemUiVisibility = android.view.View.SYSTEM_UI_FLAG_VISIBLE
        window.clearFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )
    }

    /**
     * Dim screen to 5% brightness during focus
     */
    fun setScreenBrightness(activity: android.app.Activity, brightness: Float) {
        val window = activity.window
        val layoutParams = window.attributes
        layoutParams.screenBrightness = brightness.coerceIn(0.01f, 1f)
        window.attributes = layoutParams
    }
}
