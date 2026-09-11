package com.zenith.focus.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zenith.focus.presentation.viewmodel.TimerViewModel
import com.zenith.focus.presentation.ui.component.DigitalFlipClock
import com.zenith.focus.presentation.ui.component.DurationPresetButton
import com.zenith.focus.presentation.ui.component.SettingToggleCard
import com.zenith.focus.presentation.ui.component.SlideToStartSlider
import com.zenith.focus.presentation.ui.component.AllowedAppsDock

/**
 * Main Dashboard Screen - Setup view with preset durations, settings, and slide-to-start
 */
@Composable
fun DashboardScreen(
    timerViewModel: TimerViewModel,
    onStartZen: () -> Unit,
    onThemeToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val timerState by timerViewModel.timerState.collectAsState()
    val zenConfig by timerViewModel.zenConfig.collectAsState()

    var selectedDuration by remember { mutableStateOf(15) }
    var audioEnabled by remember { mutableStateOf(false) }
    var dndEnabled by remember { mutableStateOf(true) }
    var rewardBreakEnabled by remember { mutableStateOf(false) }
    var isDarkTheme by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Zenith",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )

            IconButton(onClick = { isDarkTheme = !isDarkTheme; onThemeToggle(isDarkTheme) }) {
                // Theme toggle icon
                Text(
                    text = if (isDarkTheme) "☀️" else "🌙",
                    fontSize = 20.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Digital Clock Preview
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            DigitalFlipClock(
                minutes = selectedDuration,
                seconds = 0,
                glowing = false
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Duration Presets
        Text(
            text = "Duration",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            listOf(15, 45, 90).forEach { duration ->
                DurationPresetButton(
                    duration = duration,
                    isSelected = selectedDuration == duration,
                    onClick = { selectedDuration = duration },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Settings Toggles
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SettingToggleCard(
                title = "Audio Loop",
                description = "Play ambient audio during focus",
                isEnabled = audioEnabled,
                onToggle = { audioEnabled = it }
            )

            SettingToggleCard(
                title = "Do Not Disturb",
                description = "Suppress all notifications",
                isEnabled = dndEnabled,
                onToggle = { dndEnabled = it }
            )

            SettingToggleCard(
                title = "Reward Break",
                description = "Pause at 50% for a short break",
                isEnabled = rewardBreakEnabled,
                onToggle = { rewardBreakEnabled = it }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Allowed Apps Dock
        AllowedAppsDock(
            selectedApps = emptyList(),
            onAppRemove = {},
            onAddApp = {}
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Slide to Start
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            SlideToStartSlider(
                onSlideComplete = {
                    timerViewModel.initializeTimer(selectedDuration)
                    timerViewModel.lockTimer()
                    onStartZen()
                }
            )
        }

        Spacer(modifier = Modifier.height(48.dp))
    }
}
