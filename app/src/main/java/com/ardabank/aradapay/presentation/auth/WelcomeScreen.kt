package com.ardabank.aradapay.presentation.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ardabank.aradapay.presentation.components.AradaPayLogo
import com.ardabank.aradapay.presentation.components.AradaPayLogoSize
import com.ardabank.aradapay.presentation.components.bounceClick
import com.ardabank.aradapay.presentation.theme.PrimaryEmerald
import com.ardabank.aradapay.presentation.theme.PrimaryEmeraldContainer

@Composable
fun WelcomeScreen(
    onNavigateToRegister: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {},
    onGoogleSignInClick: () -> Unit = {}
) {
    val haptic = LocalHapticFeedback.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // CENTER: AradaPay Logo & Title Hero
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.weight(1f)
        ) {
            AradaPayLogo(
                logoSize = AradaPayLogoSize.XL
            )

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "AradaPay",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A),
                fontSize = 34.sp,
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Masadaki ortak hesabı dert etmeyin; arkadaşlarınla harcamaları kolayca bölüşüp anında ödeşin.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF64748B),
                textAlign = TextAlign.Center,
                fontSize = 15.sp,
                lineHeight = 22.sp,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }

        // BOTTOM ACTION BUTTONS
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Kayıt Ol
            Button(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onNavigateToRegister()
                },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryEmerald,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .bounceClick {
                        onNavigateToRegister()
                    },
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = "Masaya Katıl (Kayıt Ol)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            // 2. Giriş Yap
            OutlinedButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onNavigateToLogin()
                },
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.5.dp, Color(0xFFE2E8F0)),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color(0xFFF8FAFC),
                    contentColor = Color(0xFF0F172A)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .bounceClick {
                        onNavigateToLogin()
                    }
            ) {
                Text(
                    text = "Masaya Dön (Giriş Yap)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            // 3. Google ile Devam Et
            OutlinedButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onGoogleSignInClick()
                },
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF0F172A)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .bounceClick {
                        onGoogleSignInClick()
                    }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "G",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = Color(0xFF4285F4)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Google ile Devam Et",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Kullanım Koşulları • Gizlilik Politikası",
                color = Color(0xFF94A3B8),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}

