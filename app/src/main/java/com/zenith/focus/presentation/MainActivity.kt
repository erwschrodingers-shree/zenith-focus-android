package com.zenith.focus.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.zenith.focus.data.repository.SettingsRepository
import com.zenith.focus.presentation.screen.DashboardScreen
import com.zenith.focus.presentation.screen.PermissionOnboardingScreen
import com.zenith.focus.presentation.ui.theme.ZenithTheme
import com.zenith.focus.presentation.viewmodel.TimerViewModel
import com.zenith.focus.presentation.viewmodel.PermissionViewModel

/**
 * Main Entry Point Activity
 * Handles navigation between Dashboard, Zen Mode, and Permission Onboarding screens
 */
class MainActivity : ComponentActivity() {

    private val timerViewModel: TimerViewModel by viewModels()
    private val permissionViewModel: PermissionViewModel by viewModels()
    private lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        settingsRepository = SettingsRepository(this)

        setContent {
            val isDarkTheme = settingsRepository.isDarkTheme()
            var currentTheme by remember { mutableStateOf(isDarkTheme) }

            ZenithTheme(darkTheme = currentTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val hasCompletedOnboarding = settingsRepository.hasCompletedOnboarding()

                    NavHost(
                        navController = navController,
                        startDestination = if (hasCompletedOnboarding) "dashboard" else "permissions"
                    ) {
                        // Permission Onboarding Screen
                        composable("permissions") {
                            PermissionOnboardingScreen(
                                permissionViewModel = permissionViewModel,
                                onComplete = {
                                    settingsRepository.setOnboardingComplete(true)
                                    navController.navigate("dashboard") {
                                        popUpTo("permissions") { inclusive = true }
                                    }
                                }
                            )
                        }

                        // Dashboard Screen
                        composable("dashboard") {
                            DashboardScreen(
                                timerViewModel = timerViewModel,
                                onStartZen = {
                                    navController.navigate("zen_mode")
                                },
                                onThemeToggle = { isDark ->
                                    settingsRepository.setDarkTheme(isDark)
                                    currentTheme = isDark
                                }
                            )
                        }

                        // Zen Mode Locked Screen
                        composable("zen_mode") {
                            ZenModeLockedScreen(
                                timerViewModel = timerViewModel,
                                onComplete = {
                                    navController.navigate("dashboard") {
                                        popUpTo("zen_mode") { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Wrapper around ZenModeScreen with activity-level integration
 */
@androidx.compose.runtime.Composable
fun ZenModeLockedScreen(
    timerViewModel: TimerViewModel,
    onComplete: () -> Unit
) {
    // Start timer when entering Zen Mode
    androidx.compose.runtime.LaunchedEffect(Unit) {
        timerViewModel.startTimer()
    }

    com.zenith.focus.presentation.screen.ZenModeScreen(
        timerViewModel = timerViewModel,
        onComplete = onComplete
    )
}
