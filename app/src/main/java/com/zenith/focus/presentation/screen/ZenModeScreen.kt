package com.zenith.focus.presentation.screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zenith.focus.presentation.viewmodel.TimerViewModel
import com.zenith.focus.presentation.ui.component.DigitalFlipClock
import com.zenith.focus.data.model.AppInfo

/**
 * Zen Mode Locked Screen - Immersive display with flip-clock, allowed apps, and audio controls
 * STRICT LOCK: Cannot be swiped, back-pressed, or home-pressed until timer completes.
 */
@Composable
fun ZenModeScreen(
    timerViewModel: TimerViewModel,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val timerState by timerViewModel.timerState.collectAsState()
    val zenConfig by timerViewModel.zenConfig.collectAsState()

    var screenDimmed by remember { mutableStateOf(false) }
    var audioVolume by remember { mutableStateOf(0.5f) }
    var audioPlaying by remember { mutableStateOf(false) }

    val backgroundAlpha by animateFloatAsState(
        targetValue = if (screenDimmed) 0.05f else 1f
    )

    // Block all gestures except allowed app launches
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.background.copy(alpha = backgroundAlpha)
            )
            .pointerInput(Unit) {
                detectTapGestures { /* Block all taps */ }
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            // Top indicator - "ZEN MODE ACTIVE"
            Text(
                text = "ZEN MODE ACTIVE",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Large Digital Clock (Main Focus)
            DigitalFlipClock(
                minutes = timerState.minutes,
                seconds = timerState.seconds,
                glowing = true,
                showHours = false
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Allowed Apps Grid (Max 6)
            if (zenConfig.allowedApps.isNotEmpty()) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                ) {
                    items(zenConfig.allowedApps.size) { index ->
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.surface,
                                    shape = MaterialTheme.shapes.medium
                                )
                                .pointerInput(Unit) {
                                    detectTapGestures {
                                        // Launch allowed app
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "App",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Audio Controls (if enabled)
            if (zenConfig.enableAudio) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = MaterialTheme.shapes.medium
                        )
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = { audioPlaying = !audioPlaying },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text(if (audioPlaying) "⏸ Pause" else "▶ Play")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Slider(
                        value = audioVolume,
                        onValueChange = { audioVolume = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "Volume: ${(audioVolume * 100).toInt()}%",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Screen Dim Button
            Button(
                onClick = { screenDimmed = !screenDimmed },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (screenDimmed)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.surface
                )
            ) {
                Text(
                    text = if (screenDimmed) "Screen Dimmed (5%)" else "Dim Screen",
                    color = if (screenDimmed)
                        MaterialTheme.colorScheme.onPrimary
                    else
                        MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }

    // Auto-complete when timer finishes
    if (timerState.isLocked && timerViewModel.isTimerFinished()) {
        onComplete()
    }
}
