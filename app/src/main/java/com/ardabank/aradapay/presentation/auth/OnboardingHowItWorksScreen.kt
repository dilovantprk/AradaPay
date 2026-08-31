package com.ardabank.aradapay.presentation.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ardabank.aradapay.presentation.components.bounceClick
import com.ardabank.aradapay.presentation.theme.PrimaryEmerald
import kotlinx.coroutines.launch

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun OnboardingHowItWorksScreen(
    userName: String = "Mehmet",
    onComplete: () -> Unit = {},
    onCreateTripGroup: () -> Unit = onComplete,
    onCreateHouseGroup: () -> Unit = onComplete
) {
    val haptic = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { 4 })
    val displayName = userName.trim().split(" ").firstOrNull()?.replaceFirstChar { it.uppercase() } ?: "Mehmet"

    // Background gradient dynamic per slide
    val backgroundBrush = when (pagerState.currentPage) {
        0 -> Brush.verticalGradient(listOf(Color(0xFFFFF2EC), Color(0xFFFFDFD3), Color(0xFFFFD1C1)))
        1 -> Brush.verticalGradient(listOf(Color(0xFFEBF8F4), Color(0xFFD6F3E9), Color(0xFFBCECDD)))
        2 -> Brush.verticalGradient(listOf(Color(0xFFF0F6FC), Color(0xFFDEEDF9), Color(0xFFCAE2F5)))
        else -> Brush.verticalGradient(listOf(Color(0xFFEBF8F4), Color(0xFFD9F4EB), Color(0xFFC7EFE1)))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Horizontal Pager with 4 animated slides
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { page ->
                when (page) {
                    0 -> OnboardingSlideOne(displayName = displayName)
                    1 -> OnboardingSlideTwo()
                    2 -> OnboardingSlideThree()
                    3 -> OnboardingSlideFour(
                        onCreateTripGroup = onCreateTripGroup,
                        onCreateHouseGroup = onCreateHouseGroup,
                        onSkipSetup = onComplete
                    )
                }
            }

            // Bottom Navigation Controls (Only shown for slides 0..2)
            AnimatedVisibility(visible = pagerState.currentPage < 3) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 3 Animated Dot Indicators
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (i in 0 until 3) {
                            val isSelected = pagerState.currentPage == i
                            val dotWidth by animateDpAsState(
                                targetValue = if (isSelected) 24.dp else 8.dp,
                                animationSpec = tween(300),
                                label = "dotWidth"
                            )
                            Box(
                                modifier = Modifier
                                    .height(8.dp)
                                    .width(dotWidth)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) PrimaryEmerald else PrimaryEmerald.copy(alpha = 0.3f)
                                    )
                            )
                        }
                    }

                    // Action: Skip tour or Done (Done advances to Slide 4)
                    if (pagerState.currentPage < 2) {
                        Text(
                            text = "Turu Atla (Skip tour)",
                            color = PrimaryEmerald,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            modifier = Modifier
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    coroutineScope.launch { pagerState.animateScrollToPage(3) }
                                }
                                .padding(8.dp)
                        )
                    } else {
                        Button(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                coroutineScope.launch { pagerState.animateScrollToPage(3) }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryEmerald,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .bounceClick {
                                    coroutineScope.launch { pagerState.animateScrollToPage(3) }
                                }
                        ) {
                            Text("Tamamla (Done)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}

// =============================================================================
// SLIDE 1: BALANCES OVERVIEW (SPLITWISE 1:1 PARITY)
// =============================================================================
@Composable
private fun OnboardingSlideOne(displayName: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "float1")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatAnim1"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // Headline
        Text(
            text = "Welcome to AradaPay,\n$displayName!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF1E293B),
            lineHeight = 34.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "AradaPay keeps track of balances between friends.",
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF64748B),
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Floating Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = floatOffset.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Overall, you are owed ",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A),
                            fontSize = 14.sp
                        )
                        Text(
                            text = "64,64 ₺",
                            fontWeight = FontWeight.ExtraBold,
                            color = PrimaryEmerald,
                            fontSize = 15.sp
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Filtre",
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(20.dp)
                    )
                }

                HorizontalDivider(color = Color(0xFFF1F5F9))

                // Item 1
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = PrimaryEmerald,
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.BeachAccess, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Beach trip", fontWeight = FontWeight.Bold, color = Color(0xFF0F172A), fontSize = 14.sp)
                        Text("David owes you 100,00 ₺", color = Color(0xFF64748B), fontSize = 12.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("you are owed", fontSize = 11.sp, color = PrimaryEmerald)
                        Text("100,00 ₺", fontWeight = FontWeight.Bold, color = PrimaryEmerald, fontSize = 14.sp)
                    }
                }

                // Item 2
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF334155),
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Home, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("House stuff", fontWeight = FontWeight.Bold, color = Color(0xFF0F172A), fontSize = 14.sp)
                        Text("You owe Brooklyn S. 105,36 ₺", color = Color(0xFFE11D48), fontSize = 12.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("you owe", fontSize = 11.sp, color = Color(0xFFE11D48))
                        Text("35,36 ₺", fontWeight = FontWeight.Bold, color = Color(0xFFE11D48), fontSize = 14.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Vector Beach Sunset Art Canvas
        BeachSunsetIllustration(modifier = Modifier.fillMaxWidth().height(160.dp))
    }
}

// =============================================================================
// SLIDE 2: ADD EXPENSES (SPLITWISE 1:1 PARITY)
// =============================================================================
@Composable
private fun OnboardingSlideTwo() {
    val infiniteTransition = rememberInfiniteTransition(label = "float2")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatAnim2"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Add expenses",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF1E293B)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "You can split expenses with groups or with individuals.",
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF64748B),
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Floating Expense Input Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = floatOffset.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Category Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFDCFCE7),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = PrimaryEmerald, modifier = Modifier.size(24.dp))
                        }
                    }
                    Column {
                        Text("Groceries", fontWeight = FontWeight.Bold, color = Color(0xFF0F172A), fontSize = 16.sp)
                        HorizontalDivider(
                            color = Color(0xFFE2E8F0),
                            thickness = 1.dp,
                            modifier = Modifier.padding(top = 4.dp).width(160.dp)
                        )
                    }
                }

                // Amount Row with Emerald Underline
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.size(width = 44.dp, height = 48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("₺", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "94.50",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0F172A),
                            letterSpacing = 0.5.sp
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(2.5.dp)
                                .background(PrimaryEmerald)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Vector Car & House Groceries Art Canvas
        GroceriesHomeIllustration(modifier = Modifier.fillMaxWidth().height(160.dp))
    }
}

// =============================================================================
// SLIDE 3: SETTLE UP (SPLITWISE 1:1 PARITY)
// =============================================================================
@Composable
private fun OnboardingSlideThree() {
    val infiniteTransition = rememberInfiniteTransition(label = "float3")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatAnim3"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Settle up",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF1E293B)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Pay your friends back any time with instant FAST transfer.",
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF64748B),
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Dining Table Background Illustration
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            DiningTableIllustration(modifier = Modifier.fillMaxSize())

            // Floating Settlement Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                modifier = Modifier
                    .width(260.dp)
                    .offset(y = floatOffset.dp)
                    .padding(end = 8.dp, bottom = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Transfer Avatars
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(shape = CircleShape, color = Color(0xFFBAE6FD), modifier = Modifier.size(36.dp)) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("🐘", fontSize = 18.sp)
                            }
                        }
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(16.dp))
                        Surface(shape = CircleShape, color = Color(0xFFBE123C), modifier = Modifier.size(36.dp)) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("👩", fontSize = 18.sp)
                            }
                        }
                    }

                    Text("You paid Brooklyn S.", fontSize = 12.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Medium)

                    // Amount Row with underline
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFF8FAFC),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("₺", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("105.36", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF0F172A))
                            Box(modifier = Modifier.fillMaxWidth().height(2.dp).background(PrimaryEmerald))
                        }
                    }
                }
            }
        }
    }
}

// =============================================================================
// VECTOR ILLUSTRATIONS (COMPOSE CANVAS NATIVE GRAPHICS)
// =============================================================================

@Composable
private fun BeachSunsetIllustration(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Sun
        drawCircle(
            brush = Brush.radialGradient(listOf(Color(0xFFFF7A45), Color(0xFFFF5238))),
            radius = h * 0.45f,
            center = Offset(w * 0.5f, h * 0.75f)
        )

        // Deck path
        val path = Path().apply {
            moveTo(w * 0.35f, h)
            lineTo(w * 0.46f, h * 0.7f)
            lineTo(w * 0.54f, h * 0.7f)
            lineTo(w * 0.65f, h)
            close()
        }
        drawPath(path, brush = Brush.verticalGradient(listOf(Color(0xFFCCFBF1), Color(0xFF99F6E4))))

        // Beach Umbrella
        drawCircle(
            color = Color(0xFF8B5CF6),
            radius = h * 0.38f,
            center = Offset(w * 0.18f, h * 0.85f)
        )

        // Surfboard
        drawRoundRect(
            color = Color(0xFFA855F7),
            topLeft = Offset(w * 0.82f, h * 0.35f),
            size = Size(w * 0.12f, h * 0.65f),
            cornerRadius = CornerRadius(28f, 28f)
        )
    }
}

@Composable
private fun GroceriesHomeIllustration(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Cozy House
        val housePath = Path().apply {
            moveTo(w * 0.55f, h * 0.5f)
            lineTo(w * 0.72f, h * 0.15f)
            lineTo(w * 0.88f, h * 0.5f)
            lineTo(w * 0.88f, h * 0.95f)
            lineTo(w * 0.55f, h * 0.95f)
            close()
        }
        drawPath(housePath, color = Color(0xFFFFB5A0))

        // Green Car Trunk
        drawRoundRect(
            color = Color(0xFF0F766E),
            topLeft = Offset(-w * 0.1f, h * 0.45f),
            size = Size(w * 0.52f, h * 0.55f),
            cornerRadius = CornerRadius(40f, 40f)
        )

        // Grocery Bags in Trunk
        drawRoundRect(
            color = Color(0xFFFF8A65),
            topLeft = Offset(w * 0.18f, h * 0.52f),
            size = Size(w * 0.18f, h * 0.35f),
            cornerRadius = CornerRadius(12f, 12f)
        )
    }
}

@Composable
private fun DiningTableIllustration(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Round Dining Table
        drawCircle(
            color = Color(0xFFBAE6FD),
            radius = h * 0.48f,
            center = Offset(w * 0.38f, h * 0.48f)
        )

        // Pizza Plate
        drawCircle(
            color = Color.White,
            radius = h * 0.24f,
            center = Offset(w * 0.38f, h * 0.48f)
        )

        // Pizza Slice
        drawCircle(
            color = Color(0xFFFB923C),
            radius = h * 0.20f,
            center = Offset(w * 0.38f, h * 0.48f)
        )
    }
}

// =============================================================================
// SLIDE 4: LET'S GET STARTED (SPLITWISE 1:1 PARITY)
// =============================================================================
@Composable
private fun OnboardingSlideFour(
    onCreateTripGroup: () -> Unit,
    onCreateHouseGroup: () -> Unit,
    onSkipSetup: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.Start
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Celebration Icon / Party Popper
            CelebrationHornIllustration(
                modifier = Modifier
                    .size(68.dp)
                    .padding(bottom = 8.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Let's get started",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1E293B)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "What would you like to do first?",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF64748B),
                lineHeight = 24.sp
            )
        }

        // Action Buttons Stack (1:1 with Splitwise layout)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onCreateTripGroup,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryEmerald,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .bounceClick { onCreateTripGroup() }
            ) {
                Text(
                    text = "✈️  Add a group trip",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Button(
                onClick = onCreateHouseGroup,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryEmerald,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .bounceClick { onCreateHouseGroup() }
            ) {
                Text(
                    text = "🏠  Add your household",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Skip setup for now",
                color = PrimaryEmerald,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                modifier = Modifier
                    .clickable { onSkipSetup() }
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun CelebrationHornIllustration(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Party Popper Cone
        val conePath = Path().apply {
            moveTo(w * 0.15f, h * 0.85f)
            lineTo(w * 0.70f, h * 0.30f)
            lineTo(w * 0.30f, h * 0.70f)
            close()
        }
        drawPath(conePath, color = PrimaryEmerald)

        val coneTop = Path().apply {
            moveTo(w * 0.70f, h * 0.30f)
            lineTo(w * 0.78f, h * 0.40f)
            lineTo(w * 0.40f, h * 0.78f)
            lineTo(w * 0.30f, h * 0.70f)
            close()
        }
        drawPath(coneTop, color = Color(0xFF10B981))

        // Confetti Sparkles / Dots
        drawCircle(color = Color(0xFF0D9488), radius = 5f, center = Offset(w * 0.82f, h * 0.22f))
        drawCircle(color = Color(0xFF14B8A6), radius = 6f, center = Offset(w * 0.65f, h * 0.15f))
        drawCircle(color = Color(0xFF2DD4BF), radius = 4f, center = Offset(w * 0.88f, h * 0.35f))
        drawCircle(color = Color(0xFF059669), radius = 5f, center = Offset(w * 0.50f, h * 0.20f))
        drawCircle(color = Color(0xFF34D399), radius = 4.5f, center = Offset(w * 0.75f, h * 0.52f))
    }
}


