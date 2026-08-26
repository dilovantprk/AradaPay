package com.ardabank.aradapay.presentation.common

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

data class ConfettiParticle(
    val x: Float,
    val initialY: Float,
    val speed: Float,
    val size: Float,
    val color: Color
)

@Composable
fun ConfettiEffect(
    modifier: Modifier = Modifier,
    durationMs: Int = 2500
) {
    val progress = remember { Animatable(0f) }
    val particles = remember {
        val colors = listOf(Color(0xFF10B981), Color(0xFF06B6D4), Color(0xFFF43F5E), Color(0xFFF59E0B), Color(0xFF8B5CF6))
        List(60) {
            ConfettiParticle(
                x = Random.nextFloat(),
                initialY = Random.nextFloat() * 0.3f,
                speed = 0.5f + Random.nextFloat() * 0.8f,
                size = 12f + Random.nextFloat() * 16f,
                color = colors.random()
            )
        }
    }

    LaunchedEffect(Unit) {
        progress.animateTo(1f, animationSpec = tween(durationMs))
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        particles.forEach { particle ->
            val currentY = (particle.initialY + progress.value * particle.speed) * canvasHeight
            val currentX = particle.x * canvasWidth + Math.sin(progress.value.toDouble() * 10).toFloat() * 20f

            drawRect(
                color = particle.color,
                topLeft = Offset(currentX, currentY),
                size = Size(particle.size, particle.size * 0.6f)
            )
        }
    }
}
