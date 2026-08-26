package com.ardabank.aradapay.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ardabank.aradapay.presentation.theme.PrimaryEmerald

/**
 * Standard Logo Sizes for AradaPay Design System.
 */
enum class AradaPayLogoSize(
    val dpSize: Dp,
    val outerCornerRadius: Dp,
    val innerCornerRadius: Dp
) {
    XS(24.dp, 7.dp, 3.5.dp),
    SM(32.dp, 9.dp, 4.5.dp),
    MD(44.dp, 12.dp, 6.dp),
    LG(64.dp, 18.dp, 9.dp),
    XL(80.dp, 22.dp, 11.dp)
}

/**
 * Official AradaPay Logo: Siyah kare içinde yeşil kare.
 */
@Composable
fun AradaPayLogo(
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    outerCornerRadius: Dp = 12.dp,
    innerCornerRadius: Dp = 6.dp,
    outerColor: Color = Color(0xFF000000),
    innerColor: Color = PrimaryEmerald
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(outerCornerRadius))
            .background(outerColor),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(size * 0.48f)
                .clip(RoundedCornerShape(innerCornerRadius))
                .background(innerColor)
        )
    }
}

/**
 * Convenience overload accepting standardized AradaPayLogoSize enum.
 */
@Composable
fun AradaPayLogo(
    logoSize: AradaPayLogoSize,
    modifier: Modifier = Modifier,
    outerColor: Color = Color(0xFF000000),
    innerColor: Color = PrimaryEmerald
) {
    AradaPayLogo(
        modifier = modifier,
        size = logoSize.dpSize,
        outerCornerRadius = logoSize.outerCornerRadius,
        innerCornerRadius = logoSize.innerCornerRadius,
        outerColor = outerColor,
        innerColor = innerColor
    )
}

/**
 * Standardized Horizontal Brand Lockup: Logo + "AradaPay" Wordmark.
 */
@Composable
fun AradaPayBrandWordmark(
    modifier: Modifier = Modifier,
    logoSize: AradaPayLogoSize = AradaPayLogoSize.SM,
    textColor: Color = Color(0xFF0F172A),
    textSize: Int = 20,
    letterSpacing: Double = -0.3
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        AradaPayLogo(logoSize = logoSize)
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "AradaPay",
            color = textColor,
            fontWeight = FontWeight.ExtraBold,
            fontSize = textSize.sp,
            letterSpacing = letterSpacing.sp
        )
    }
}

/**
 * Standardized Brand Hero Lockup (Used in Onboarding, Splash, Login & Welcome).
 */
@Composable
fun AradaPayBrandHero(
    modifier: Modifier = Modifier,
    logoSize: AradaPayLogoSize = AradaPayLogoSize.XL,
    title: String = "AradaPay",
    subtitle: String? = "Ortak Harcamaları Kolayca Yönetin"
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AradaPayLogo(logoSize = logoSize)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            color = Color(0xFF0F172A),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.5).sp
        )
        if (!subtitle.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = subtitle,
                color = Color(0xFF64748B),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}
