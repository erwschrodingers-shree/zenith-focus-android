package com.zenith.focus.presentation.screen

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zenith.focus.presentation.viewmodel.PermissionViewModel
import com.zenith.focus.data.model.PermissionStatus

/**
 * Permission Onboarding Screen - Step-by-step permission request flow
 */
@Composable
fun PermissionOnboardingScreen(
    permissionViewModel: PermissionViewModel,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val permissionStatus by permissionViewModel.permissionStatus.collectAsState()

    var currentStep by remember { mutableStateOf(0) }

    val steps = listOf(
        PermissionStep(
            title = "Notification Access",
            description = "Allow Zenith to intercept notifications and suppress distractions during focus sessions.",
            icon = "🔔",
            isGranted = permissionStatus.notificationAccess,
            onRequest = { permissionViewModel.requestNotificationAccess() }
        ),
        PermissionStep(
            title = "Screen Overlay",
            description = "Display a full-screen lock overlay to prevent app switching during Zen Mode.",
            icon = "🔒",
            isGranted = permissionStatus.screenOverlay,
            onRequest = { permissionViewModel.requestScreenOverlayPermission() }
        ),
        PermissionStep(
            title = "Battery Optimization",
            description = "Exclude Zenith from battery optimization to keep your focus session running.",
            icon = "🔋",
            isGranted = permissionStatus.batteryOptimization,
            onRequest = { permissionViewModel.requestIgnoreBatteryOptimization() }
        ),
        PermissionStep(
            title = "Background Execution",
            description = "Allow Zenith to run in the background and maintain your focus lock.",
            icon = "⚙️",
            isGranted = permissionStatus.backgroundExecution,
            onRequest = { /* Already handled by system */ }
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Text(
            text = "Welcome to Zenith",
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Text(
            text = "To enable strict focus lock, we need a few permissions",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Progress indicator
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(steps.size) { index ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .background(
                            color = if (index <= currentStep)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.surfaceVariant,
                            shape = MaterialTheme.shapes.small
                        )
                )
            }
        }

        // Current Permission Step
        if (currentStep < steps.size) {
            PermissionStepCard(
                step = steps[currentStep],
                onRequestPermission = {
                    steps[currentStep].onRequest()
                }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Navigation Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (currentStep > 0) {
                Button(
                    onClick = { currentStep-- },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Text("Back", color = MaterialTheme.colorScheme.primary)
                }
            }

            Button(
                onClick = {
                    if (currentStep < steps.size - 1) {
                        currentStep++
                    } else {
                        permissionViewModel.refreshPermissionStatus()
                        onComplete()
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    if (currentStep < steps.size - 1) "Next" else "Complete",
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // All permissions granted indicator
        if (permissionStatus.allGranted) {
            Text(
                text = "✓ All permissions granted!",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4CAF50)
            )
        }
    }
}

data class PermissionStep(
    val title: String,
    val description: String,
    val icon: String,
    val isGranted: Boolean,
    val onRequest: () -> Unit
)

@Composable
fun PermissionStepCard(
    step: PermissionStep,
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.medium
            )
            .padding(24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon
            Text(
                text = step.icon,
                fontSize = 64.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Title
            Text(
                text = step.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Description
            Text(
                text = step.description,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )

            // Status Badge
            if (step.isGranted) {
                Box(
                    modifier = Modifier
                        .background(
                            color = Color(0xFF4CAF50).copy(alpha = 0.2f),
                            shape = CircleShape
                        )
                        .padding(12.dp)
                ) {
                    Text(
                        text = "✓ Granted",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    )
                }
            } else {
                Button(
                    onClick = onRequestPermission,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Grant Permission")
                }
            }
        }
    }
}
