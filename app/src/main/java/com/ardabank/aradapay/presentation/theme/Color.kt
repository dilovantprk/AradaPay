package com.ardabank.aradapay.presentation.theme

import androidx.compose.ui.graphics.Color

// Authentic Flat Fintech Design Tokens
val LightBackground = Color(0xFFFFFFFF)          // Pure White Background (#FFFFFF)
val SurfaceWhite = Color(0xFFFFFFFF)             // Pure White Card Surface (#FFFFFF)
val SurfaceContainerLow = Color(0xFFE5E5EA)     // Apple System Fill (Tertiary)
val SurfaceContainer = Color(0xFFFFFFFF)        // Pure White Card Standard
val SurfaceElevated = Color(0xFFFFFFFF)         // Pure White
val SurfaceContainerHigh = Color(0xFFE5E5EA)     // Apple System Fill for secondary pills
val SurfaceBorder = Color.Transparent           // No hard artificial border in HIG

// Banking Brand Colors
val PrimaryEmerald = Color(0xFF00875A)          // Bank Primary Forest Green (Apple SystemGreen)
val PrimaryEmeraldDark = Color(0xFFFFFFFF)
val PrimaryEmeraldContainer = Color(0xFFE8F5E9) // Soft Emerald 50 Tint
val OnPrimaryContainer = Color(0xFF065F46)      // Deep Forest Green

// Financial Debt / Outflow (Cognitive De-escalation)
val AccentRose = Color(0xFFBE123C)              // Subdued Banker Rose/Crimson (Red 700)
val AccentRoseContainer = Color(0xFFFFF1F2)     // Soft Rose 50 Tint
val OnAccentRoseContainer = Color(0xFF9F1239)

// Financial Transfer & Secondary Tones (Refined Slate & Charcoal)
val ShinyCyan = Color(0xFF334155)               // Refined Deep Slate
val ShinyCyanContainer = Color(0xFFE5E5EA)      // Apple System Fill Container
val AccentAmber = Color(0xFF475569)             // Slate 600 Neutral

// Typography (Apple HIG Standard)
val TextPrimary = Color(0xFF0F172A)             // Obsidian Slate 900
val TextSecondary = Color(0xFF64748B)           // Slate 500
val TextTertiary = Color(0xFF94A3B8)            // Slate 400

// Legacy aliases
val DarkBackground = LightBackground
val SurfaceDark = SurfaceWhite
val CardBackground = SurfaceWhite
val ChipBackground = SurfaceContainerLow
