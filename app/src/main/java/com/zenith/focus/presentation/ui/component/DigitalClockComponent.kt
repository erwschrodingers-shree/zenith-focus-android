package com.zenith.focus.presentation.ui.component

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Retro Digital Flip Clock Display
 * Shows [MM:SS] or [HH:MM:SS] in a monospace, glowing style
 */
@Composable
fun DigitalFlipClock(
    minutes: Int = 0,
    seconds: Int = 0,
    modifier: Modifier = Modifier,
    glowing: Boolean = false,
    showHours: Boolean = false,
    hours: Int = 0
) {
    val timeText = if (showHours) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }

    val textColor = if (glowing) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
    } else {
        MaterialTheme.colorScheme.primary
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .background(
                color = if (glowing) {
                    MaterialTheme.colorScheme.surface.copy(alpha = 0.3f)
                } else {
                    MaterialTheme.colorScheme.surface
                },
                shape = MaterialTheme.shapes.medium
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = timeText,
            fontFamily = FontFamily.Monospace,
            fontSize = if (showHours) 56.sp else 72.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            color = textColor,
            letterSpacing = 4.sp
        )
    }
}

/**
 * Progress bar showing session completion percentage
 */
@Composable
fun ZenthProgressBar(
    progress: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(4.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.small
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction = progress / 100f)
                .height(4.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.small
                )
        )
    }
}
