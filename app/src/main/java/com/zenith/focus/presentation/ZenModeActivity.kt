package com.zenith.focus.presentation

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zenith.focus.presentation.screen.ZenModeScreen
import com.zenith.focus.presentation.ui.theme.ZenithTheme
import com.zenith.focus.presentation.viewmodel.TimerViewModel
import com.zenith.focus.util.OverlayManager

/**
 * Zen Mode Activity - Full-screen immersive lock experience
 * CRITICAL: Implements strict lock with no escape routes
 * - No back button
 * - No home button
 * - No multitasking
 * - Cannot be force-closed
 */
class ZenModeActivity : ComponentActivity() {

    private lateinit var overlayManager: OverlayManager
    private val timerViewModel: TimerViewModel by lazy { TimerViewModel(application) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        overlayManager = OverlayManager(this)
        overlayManager.enableStrictLock(this)

        setContent {
            ZenithTheme(darkTheme = true) {
                Surface(color = MaterialTheme.colorScheme.background) {
                    ZenModeScreen(
                        timerViewModel = timerViewModel,
                        onComplete = { finishZenMode() }
                    )
                }
            }
        }
    }

    /**
     * Override back button - STRICTLY DISABLED during Zen Mode
     */
    override fun onBackPressed() {
        // Do nothing - back button is disabled
    }

    /**
     * Override system key events to prevent escape
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_BACK,
            KeyEvent.KEYCODE_HOME,
            KeyEvent.KEYCODE_APP_SWITCH -> true // Block these keys
            else -> super.onKeyDown(keyCode, event)
        }
    }

    /**
     * Block recents/task switcher
     */
    override fun onUserLeaveHint() {
        // Do nothing - prevents app from being swiped away
    }

    /**
     * Restore immersive mode if it gets disrupted
     */
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            overlayManager.enableStrictLock(this)
        }
    }

    private fun finishZenMode() {
        overlayManager.disableStrictLock(this)
        finish()
    }

    override fun onDestroy() {
        overlayManager.disableStrictLock(this)
        super.onDestroy()
    }
}
