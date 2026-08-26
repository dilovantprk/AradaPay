package com.ardabank.aradapay.presentation.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun UserAvatar(
    userName: String,
    avatarUrl: String? = null,
    avatarEmoji: String? = null,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    shape: Shape = CircleShape,
    border: BorderStroke? = null,
    backgroundColor: Color = Color(0xFFF1F5F9),
    textColor: Color = Color(0xFF0F172A),
    fontSizeSp: Int? = null
) {
    val initials = when {
        !avatarEmoji.isNullOrBlank() -> avatarEmoji
        userName.isNotBlank() -> {
            val parts = userName.trim().split(" ").filter { it.isNotBlank() }
            if (parts.size >= 2) {
                "${parts[0].take(1)}${parts[1].take(1)}".uppercase()
            } else {
                userName.trim().take(2).uppercase()
            }
        }
        else -> "AP"
    }

    val calculatedFontSize = (fontSizeSp ?: (size.value * 0.38f).toInt().coerceAtLeast(10)).sp

    Surface(
        shape = shape,
        color = backgroundColor,
        border = border,
        modifier = modifier
            .size(size)
            .clip(shape)
    ) {
        if (!avatarUrl.isNullOrBlank()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(avatarUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = userName,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = initials,
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = calculatedFontSize,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}
