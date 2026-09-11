package com.zenith.focus.presentation.ui.component

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Slide-to-Start slider with haptic feedback
 * Triggers onSlideComplete when user drags all the way to the end
 */
@Composable
fun SlideToStartSlider(
    onSlideComplete: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val haptic = LocalHapticFeedback.current
    val offsetX = remember { Animatable(0f) }
    val sliderWidth = remember { mutableStateOf(0f) }
    val isCompleted = remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 16.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        // Background text
        Text(
            text = if (isCompleted.value) "Zen Mode Active" else "Slide to Start Zen",
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 24.dp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )

        // Draggable thumb
        if (enabled && !isCompleted.value) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = offsetX.value.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
                    .padding(8.dp)
                    .height(40.dp)
                    .draggable(
                        state = rememberDraggableState { delta ->
                            val newOffset = (offsetX.value + delta).coerceIn(0f, sliderWidth.value - 56)
                            offsetX.value = newOffset

                            // Haptic feedback on small intervals
                            if (newOffset.toInt() % 50 == 0) {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }

                            // Check if user dragged all the way
                            if (newOffset >= (sliderWidth.value - 56 - 10)) {
                                isCompleted.value = true
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onSlideComplete()
                            }
                        },
                        orientation = Orientation.Horizontal
                    )
            )
        }
    }

    LaunchedEffect(Unit) {
        // Store slider width for drag calculations
        // In real implementation, measure the box width
    }
}
