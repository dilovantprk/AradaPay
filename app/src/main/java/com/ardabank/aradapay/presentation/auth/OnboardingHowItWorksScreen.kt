package com.ardabank.aradapay.presentation.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ardabank.aradapay.presentation.components.bounceClick
import com.ardabank.aradapay.presentation.theme.LightBackground
import com.ardabank.aradapay.presentation.theme.PrimaryEmerald
import com.ardabank.aradapay.presentation.theme.PrimaryEmeraldContainer

@Composable
fun OnboardingHowItWorksScreen(
    userName: String = "Mehmet",
    onComplete: () -> Unit = {}
) {
    val haptic = LocalHapticFeedback.current
    var step by remember { mutableIntStateOf(1) } // 1: Expense Split, 2: Smart Settlement, 3: FAST & Receipt

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 1. TOP HEADER & SKIP BUTTON
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (s in 1..3) {
                    val isActive = s == step
                    val isDone = s < step
                    val pillWidth by animateDpAsState(
                        targetValue = if (isActive) 32.dp else 14.dp,
                        animationSpec = tween(300),
                        label = "pillStepWidth"
                    )
                    Box(
                        modifier = Modifier
                            .height(6.dp)
                            .width(pillWidth)
                            .clip(RoundedCornerShape(3.dp))
                            .background(
                                color = when {
                                    isActive -> PrimaryEmerald
                                    isDone -> PrimaryEmerald.copy(alpha = 0.5f)
                                    else -> Color(0xFFCBD5E1)
                                }
                            )
                    )
                }
            }

            TextButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onComplete()
                }
            ) {
                Text("Atla", color = Color(0xFF64748B), fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }

        // 2. INTERACTIVE SIMULATION CAROUSEL STEP
        Box(modifier = Modifier.weight(1f)) {
            AnimatedContent(
                targetState = step,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "TourStepAnimation"
            ) { targetStep ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    when (targetStep) {
                        // =========================================================================
                        // ADIM 1: HARCAMA EKLE & PAYLAŞTIR
                        // =========================================================================
                        1 -> {
                            Surface(
                                shape = RoundedCornerShape(22.dp),
                                color = PrimaryEmeraldContainer,
                                modifier = Modifier.size(76.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Groups, contentDescription = null, tint = PrimaryEmerald, modifier = Modifier.size(40.dp))
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            Text(
                                text = "1. Harcama Ekle & Bölüş",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 24.sp,
                                color = Color(0xFF0F172A),
                                textAlign = TextAlign.Center,
                                letterSpacing = (-0.3).sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Fişi veya tutarı girin, arkadaşlarınızı seçin. Sistem herkesin payını kuruşu kuruşuna otomatik hesaplar.",
                                color = Color(0xFF64748B),
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 20.sp,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            // iOS Inset Grouped Simulation Card
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = Color.White,
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Surface(
                                                shape = RoundedCornerShape(10.dp),
                                                color = Color(0xFFF1F5F9),
                                                modifier = Modifier.size(36.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Icon(Icons.Default.Fastfood, contentDescription = null, tint = PrimaryEmerald, modifier = Modifier.size(20.dp))
                                                }
                                            }
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text("Starbucks & Kahve", fontWeight = FontWeight.Bold, color = Color(0xFF0F172A), fontSize = 14.sp)
                                                Text("3 kişi eşit bölüşüm", color = Color(0xFF64748B), fontSize = 12.sp)
                                            }
                                        }
                                        Text("300,00 ₺", fontWeight = FontWeight.ExtraBold, color = Color(0xFF0F172A), fontSize = 16.sp)
                                    }

                                    Surface(shape = RoundedCornerShape(12.dp), color = Color(0xFFF8FAFC), modifier = Modifier.fillMaxWidth()) {
                                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                                Text("Sen (Ödeyen)", color = Color(0xFF64748B), fontSize = 13.sp)
                                                Text("100,00 ₺", fontWeight = FontWeight.Bold, color = PrimaryEmerald, fontSize = 13.sp)
                                            }
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                                Text("Ahmet Yılmaz", color = Color(0xFF64748B), fontSize = 13.sp)
                                                Text("100,00 ₺", fontWeight = FontWeight.Bold, color = Color(0xFF0F172A), fontSize = 13.sp)
                                            }
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                                Text("Zeynep Kaya", color = Color(0xFF64748B), fontSize = 13.sp)
                                                Text("100,00 ₺", fontWeight = FontWeight.Bold, color = Color(0xFF0F172A), fontSize = 13.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // =========================================================================
                        // ADIM 2: AKILLI BORÇ SADELEŞTİRME
                        // =========================================================================
                        2 -> {
                            Surface(
                                shape = RoundedCornerShape(22.dp),
                                color = Color(0xFFE0F2FE),
                                modifier = Modifier.size(76.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.SyncAlt, contentDescription = null, tint = Color(0xFF0284C7), modifier = Modifier.size(40.dp))
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            Text(
                                text = "2. Akıllı Borç Sadeleştirme",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 24.sp,
                                color = Color(0xFF0F172A),
                                textAlign = TextAlign.Center,
                                letterSpacing = (-0.3).sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Ahmet sana 100 ₺, sen Zeynep'e 100 ₺ borçluysan; AradaPay zinciri çözer. Ahmet doğrudan Zeynep'e öder, transfer sayısı %70 azalır.",
                                color = Color(0xFF64748B),
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 20.sp,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            // iOS Inset Grouped Simulation Card
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = Color.White,
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = PrimaryEmerald, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Optimize Edilen Tek Transfer", fontWeight = FontWeight.Bold, color = PrimaryEmerald, fontSize = 13.sp)
                                    }

                                    Surface(shape = RoundedCornerShape(12.dp), color = Color(0xFFF0FDF4), modifier = Modifier.fillMaxWidth()) {
                                        Row(
                                            modifier = Modifier.padding(14.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("Ahmet  ➔  Zeynep", fontWeight = FontWeight.Bold, color = Color(0xFF0F172A), fontSize = 14.sp)
                                            Text("100,00 ₺", fontWeight = FontWeight.ExtraBold, color = PrimaryEmerald, fontSize = 15.sp)
                                        }
                                    }

                                    Text("Senin Borcun: 0,00 ₺ (Otomatik Mahsuplaşma Tamam)", color = Color(0xFF64748B), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                        }

                        // =========================================================================
                        // ADIM 3: FAST & DEKONT
                        // =========================================================================
                        3 -> {
                            Surface(
                                shape = RoundedCornerShape(22.dp),
                                color = Color(0xFFFEF3C7),
                                modifier = Modifier.size(76.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Bolt, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(40.dp))
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            Text(
                                text = "3. FAST ile Fitleş & Dekont Al",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 24.sp,
                                color = Color(0xFF0F172A),
                                textAlign = TextAlign.Center,
                                letterSpacing = (-0.3).sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Tek dokunuşla FAST transferini yap, dijital mühürlü PDF dekontunu doğrudan WhatsApp veya diğer uygulamalarda paylaş.",
                                color = Color(0xFF64748B),
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 20.sp,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            // iOS Inset Grouped Simulation Card
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = Color.White,
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = PrimaryEmeraldContainer,
                                            modifier = Modifier.size(40.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = null, tint = PrimaryEmerald, modifier = Modifier.size(22.dp))
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text("FAST Dekontu Hazır", fontWeight = FontWeight.Bold, color = Color(0xFF0F172A), fontSize = 14.sp)
                                            Text("TR64 0006 ... • 100,00 ₺", color = Color(0xFF64748B), fontSize = 12.sp)
                                        }
                                    }
                                    Surface(shape = RoundedCornerShape(10.dp), color = PrimaryEmeraldContainer) {
                                        Text("Paylaş ➔", color = PrimaryEmerald, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. ACTION BUTTON (NEXT OR COMPLETE)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    if (step < 3) {
                        step++
                    } else {
                        onComplete()
                    }
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
                        if (step < 3) {
                            step++
                        } else {
                            onComplete()
                        }
                    },
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (step < 3) "Devam Et" else "Hadi Başlayalım",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
