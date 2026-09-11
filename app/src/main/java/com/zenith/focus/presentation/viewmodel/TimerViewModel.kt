package com.zenith.focus.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zenith.focus.data.model.TimerUIState
import com.zenith.focus.data.model.ZenConfig
import com.zenith.focus.data.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.max

class TimerViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsRepository = SettingsRepository(application)

    private val _timerState = MutableStateFlow(TimerUIState())
    val timerState: StateFlow<TimerUIState> = _timerState.asStateFlow()

    private val _zenConfig = MutableStateFlow(ZenConfig())
    val zenConfig: StateFlow<ZenConfig> = _zenConfig.asStateFlow()

    private var remainingMillis = 0L
    private var totalMillis = 0L
    private var isTimerRunning = false

    fun initializeTimer(durationMinutes: Int) {
        totalMillis = (durationMinutes * 60 * 1000).toLong()
        remainingMillis = totalMillis
        updateTimerDisplay()
    }

    fun startTimer() {
        if (isTimerRunning) return
        isTimerRunning = true
        _timerState.value = _timerState.value.copy(isRunning = true)
        tickTimer()
    }

    fun pauseTimer() {
        isTimerRunning = false
        _timerState.value = _timerState.value.copy(isRunning = false)
    }

    fun resumeTimer() {
        if (!isTimerRunning && remainingMillis > 0) {
            startTimer()
        }
    }

    fun lockTimer() {
        _timerState.value = _timerState.value.copy(isLocked = true)
    }

    private fun tickTimer() {
        viewModelScope.launch {
            while (isTimerRunning && remainingMillis > 0) {
                delay(100) // Update every 100ms for smooth display
                remainingMillis = max(0, remainingMillis - 100)
                updateTimerDisplay()

                if (remainingMillis <= 0) {
                    isTimerRunning = false
                    _timerState.value = _timerState.value.copy(isRunning = false)
                }
            }
        }
    }

    private fun updateTimerDisplay() {
        val totalSeconds = remainingMillis / 1000
        val minutes = (totalSeconds / 60).toInt()
        val seconds = (totalSeconds % 60).toInt()
        val progressPercent = if (totalMillis > 0) {
            ((totalMillis - remainingMillis).toFloat() / totalMillis) * 100
        } else {
            0f
        }

        _timerState.value = _timerState.value.copy(
            minutes = minutes,
            seconds = seconds,
            progressPercent = progressPercent
        )
    }

    fun updateZenConfig(config: ZenConfig) {
        _zenConfig.value = config
    }

    fun isTimerFinished(): Boolean = remainingMillis <= 0
}
