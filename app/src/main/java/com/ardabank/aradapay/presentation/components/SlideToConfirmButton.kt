package com.ardabank.aradapay.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ardabank.aradapay.presentation.theme.PrimaryEmerald
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun SlideToConfirmButton(
    modifier: Modifier = Modifier,
    text: String = "Fitleşmek İçin Kaydırın",
    completedText: String = "Fitleşildi",
    sliderColor: Color = PrimaryEmerald,
    thumbColor: Color = Color(0xFF0F172A),
    backgroundColor: Color = Color.White,
    enabled: Boolean = true,
    onConfirm: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current

    var isConfirmed by remember { mutableStateOf(false) }
    val offsetX = remember { Animatable(0f) }

    val thumbSize = 50.dp
    val thumbPadding = 4.dp

    val effectiveBgColor = if (!enabled) Color(0xFFF1F5F9) else backgroundColor
    val effectiveThumbColor = if (!enabled) Color(0xFFCBD5E1) else if (isConfirmed) sliderColor else thumbColor
    val effectiveTextColor = if (!enabled) Color(0xFF94A3B8) else if (isConfirmed) sliderColor else Color(0xFF64748B)

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(58.dp)
            .clip(RoundedCornerShape(29.dp))
            .background(effectiveBgColor)
            .padding(thumbPadding),
        contentAlignment = Alignment.CenterStart
    ) {
        val maxDragPx = with(density) { (maxWidth - thumbSize - (thumbPadding * 2)).toPx() }

        // Green / Color Progress Fill Background
        val progressFraction = if (enabled && maxDragPx > 0) (offsetX.value / maxDragPx).coerceIn(0f, 1f) else 0f

        if (enabled && progressFraction > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction = progressFraction.coerceAtLeast(0.05f))
                    .clip(RoundedCornerShape(25.dp))
                    .background(sliderColor.copy(alpha = 0.15f))
            )
        }

        // Center Hint Text
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isConfirmed) completedText else text,
                color = effectiveTextColor,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        // Draggable Thumb Circle
        Surface(
            shape = CircleShape,
            color = effectiveThumbColor,
            modifier = Modifier
                .size(thumbSize)
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .draggable(
                    enabled = enabled && !isConfirmed,
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        if (enabled) {
                            coroutineScope.launch {
                                val target = (offsetX.value + delta).coerceIn(0f, maxDragPx)
                                offsetX.snapTo(target)
                            }
                        }
                    },
                    onDragStopped = {
                        if (enabled && offsetX.value >= maxDragPx * 0.80f) {
                            // Completed!
                            coroutineScope.launch {
                                offsetX.animateTo(
                                    targetValue = maxDragPx,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioNoBouncy,
                                        stiffness = Spring.StiffnessMedium
                                    )
                                )
                                isConfirmed = true
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onConfirm()
                            }
                        } else {
                            // Spring back
                            coroutineScope.launch {
                                offsetX.animateTo(
                                    targetValue = 0f,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessLow
                                    )
                                )
                            }
                        }
                    }
                )
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = if (isConfirmed) Icons.Default.Check else Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Kaydır",
                    tint = if (!enabled) Color(0xFF94A3B8) else Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}
