package com.ardabank.aradapay.presentation.auth

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ardabank.aradapay.presentation.components.AradaPayBrandWordmark
import com.ardabank.aradapay.presentation.components.AradaPayLogoSize
import com.ardabank.aradapay.presentation.components.bounceClick
import com.ardabank.aradapay.presentation.theme.LightBackground
import com.ardabank.aradapay.presentation.theme.PrimaryEmerald
import com.ardabank.aradapay.presentation.theme.PrimaryEmeraldContainer

data class WelcomeValueProp(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val accentColor: Color = PrimaryEmerald
)

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun WelcomeScreen(
    onNavigateToRegister: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {},
    onGoogleSignInClick: () -> Unit = {}
) {
    val haptic = LocalHapticFeedback.current

    val valueProps = listOf(
        WelcomeValueProp(
            title = "Ortak Masrafları Anında Bölüş",
            description = "Restoran, tatil, market ve ev giderlerini saniyeler içinde arkadaşlarınla adilce paylaştır.",
            icon = Icons.Default.Groups,
            accentColor = Color(0xFF00875A)
        ),
        WelcomeValueProp(
            title = "Akıllı Borç Sadeleştirme",
            description = "Çapraz borçları tek bir net tutara indir. 10 ayrı para transferi yerine tek dokunuşla fitleş.",
            icon = Icons.Default.SyncAlt,
            accentColor = Color(0xFF0284C7)
        ),
        WelcomeValueProp(
            title = "7/24 FAST ile Kolay Fitleşme",
            description = "FAST IBAN transferiyle borcunu anında kapat, doğrulanmış dijital dekontunu al.",
            icon = Icons.Default.Bolt,
            accentColor = Color(0xFFD97706)
        )
    )

    val pagerState = rememberPagerState(pageCount = { valueProps.size })

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 1. STANDARDIZED APPLE HIG BRAND HEADER
        AradaPayBrandWordmark(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            logoSize = AradaPayLogoSize.MD,
            textSize = 24
        )

        // 2. CAROUSEL VALUE PROPOSITIONS (iOS Card Glass Style)
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(310.dp)
            ) { page ->
                val item = valueProps[page]
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 6.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp, vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = item.accentColor.copy(alpha = 0.12f),
                            modifier = Modifier.size(76.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = null,
                                    tint = item.accentColor,
                                    modifier = Modifier.size(38.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = item.title,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            color = Color(0xFF0F172A),
                            textAlign = TextAlign.Center,
                            letterSpacing = (-0.3).sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = item.description,
                            color = Color(0xFF64748B),
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // iOS Spring Pager Indicators
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(valueProps.size) { iteration ->
                    val isCurrent = pagerState.currentPage == iteration
                    val indicatorWidth by animateDpAsState(
                        targetValue = if (isCurrent) 24.dp else 8.dp,
                        animationSpec = tween(durationMillis = 300),
                        label = "pagerIndicatorWidth"
                    )
                    val color = if (isCurrent) PrimaryEmerald else Color(0xFFCBD5E1)
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .height(8.dp)
                            .width(indicatorWidth)
                            .clip(RoundedCornerShape(4.dp))
                            .background(color)
                    )
                }
            }
        }

        // 3. ACTION BUTTONS (Apple HIG Tactile Spring Buttons)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
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
                    text = "Hemen Başla (Kayıt Ol)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

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
                    text = "Zaten Hesabım Var (Giriş Yap)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}
