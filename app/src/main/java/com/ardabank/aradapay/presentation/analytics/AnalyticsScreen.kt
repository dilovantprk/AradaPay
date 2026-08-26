package com.ardabank.aradapay.presentation.analytics

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ardabank.aradapay.presentation.common.IOSSegmentedControl
import com.ardabank.aradapay.presentation.common.applePressEffect
import com.ardabank.aradapay.presentation.theme.AccentRose
import com.ardabank.aradapay.presentation.theme.AccentRoseContainer
import com.ardabank.aradapay.presentation.theme.LightBackground
import com.ardabank.aradapay.presentation.theme.PrimaryEmerald
import com.ardabank.aradapay.presentation.theme.PrimaryEmeraldContainer
import com.ardabank.aradapay.presentation.theme.SurfaceContainerLow
import com.ardabank.aradapay.presentation.theme.SurfaceWhite
import com.ardabank.aradapay.presentation.theme.TextPrimary
import com.ardabank.aradapay.presentation.theme.TextSecondary
import com.ardabank.aradapay.presentation.theme.TextTertiary

data class FriendAnalyticsItem(
    val id: String,
    val name: String,
    val tag: String,
    val avatar: String,
    val totalVolume: Double,
    val netStatus: String,
    val isPositive: Boolean
)

data class CategoryExpenseStat(
    val categoryName: String,
    val amount: Double,
    val percentage: Int,
    val icon: ImageVector,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    onBackClick: () -> Unit = {},
    onFriendClick: (friendId: String) -> Unit = {}
) {
    var selectedTimePeriod by remember { mutableStateOf("Bu Ay") }
    val timePeriods = listOf("Bu Ay" to "Bu Ay", "Son 3 Ay" to "Son 3 Ay", "2026 Yılı" to "2026", "Tümü" to "Tümü")

    val friendVolumeList = remember {
        listOf(
            FriendAnalyticsItem(
                id = "1",
                name = "Ahmet Yılmaz",
                tag = "@ahmet#7821",
                avatar = "AY",
                totalVolume = 1450.0,
                netStatus = "Hesap Kapalı",
                isPositive = true
            ),
            FriendAnalyticsItem(
                id = "6",
                name = "Selin Aydın",
                tag = "@selin#2839",
                avatar = "SA",
                totalVolume = 920.0,
                netStatus = "Hesap Kapalı",
                isPositive = true
            ),
            FriendAnalyticsItem(
                id = "4",
                name = "Elif Şahin",
                tag = "@elif#4420",
                avatar = "EŞ",
                totalVolume = 680.0,
                netStatus = "+120,00 ₺ Alacak",
                isPositive = true
            ),
            FriendAnalyticsItem(
                id = "5",
                name = "Burak Öztürk",
                tag = "@burak#6108",
                avatar = "BÖ",
                totalVolume = 450.0,
                netStatus = "-230,00 ₺ Borç",
                isPositive = false
            ),
            FriendAnalyticsItem(
                id = "3",
                name = "Mert Demir",
                tag = "@mert#9015",
                avatar = "MD",
                totalVolume = 300.0,
                netStatus = "-220,00 ₺ Borç",
                isPositive = false
            )
        )
    }

    val categoryStats = remember {
        listOf(
            CategoryExpenseStat("Yeme & İçme", 3450.0, 44, Icons.Default.Fastfood, PrimaryEmerald),
            CategoryExpenseStat("Market & Alışveriş", 2100.0, 27, Icons.Default.ShoppingCart, Color(0xFF007AFF)),
            CategoryExpenseStat("Ulaşım & Yakıt", 1200.0, 15, Icons.Default.LocalGasStation, Color(0xFFFF9500)),
            CategoryExpenseStat("Sosyal & Etkinlik", 1100.0, 14, Icons.Default.Movie, Color(0xFFAF52DE))
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 36.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // 1. TOP BAR
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledTonalIconButton(
                    onClick = onBackClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = Color(0xFFF1F5F9)
                    ),
                    modifier = Modifier
                        .size(40.dp)
                        .applePressEffect(0.92f)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Geri",
                        tint = TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = "Harcama Analitiği",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.size(40.dp))
            }
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

            // 2. DÖNEM SEÇİMİ
            Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                IOSSegmentedControl(
                    items = timePeriods,
                    selectedItem = selectedTimePeriod,
                    onItemSelected = { selectedTimePeriod = it }
                )
            }
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

            // 3. APPLE FITNESS / HEALTH TARZI DÖNGÜSEL HALKA & HERO KARTI
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TOPLAM İŞLEM HACMİ",
                        color = TextSecondary,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = PrimaryEmeraldContainer
                    ) {
                        Text(
                            text = "Net +2.600,00 ₺ Alacak",
                            color = PrimaryEmerald,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // Concentric Circular Rings & Total Volume Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "7.850,00 ₺",
                            color = TextPrimary,
                            fontSize = 34.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.5).sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Seçilen dönemdeki toplam ortak harcama",
                            color = TextSecondary,
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 12.sp
                        )
                    }

                    // iOS Activity Rings Visualizer Canvas
                    IOSActivityRingsVisualizer(
                        stats = categoryStats,
                        modifier = Modifier.size(90.dp)
                    )
                }

                // iOS Health Style Segmented Capsule Bar
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        categoryStats.forEach { cat ->
                            Box(
                                modifier = Modifier
                                    .weight(cat.percentage.toFloat())
                                    .fillMaxHeight()
                                    .background(cat.color)
                            )
                        }
                    }
                }

                // Inset 3 Sütunlu Finansal Metrikler
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFF8FAFC),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Ödediğin Pay", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("4.250,00 ₺", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Alınan Pay", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("1.650,00 ₺", color = PrimaryEmerald, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("İşlem Sayısı", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("14 Harcama", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

            // 4. KATEGORİ DAĞILIMI
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "KATEGORİ DAĞILIMI",
                        color = TextSecondary,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )

                    Text(
                        text = "${categoryStats.size} Kategori",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                Column(modifier = Modifier.fillMaxWidth()) {
                    categoryStats.forEachIndexed { _, cat ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = cat.color.copy(alpha = 0.15f),
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = cat.icon,
                                                contentDescription = null,
                                                tint = cat.color,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Text(
                                            text = cat.categoryName,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = "%${cat.percentage} oran",
                                            color = TextSecondary,
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                Text(
                                    text = "${String.format(java.util.Locale.US, "%.2f", cat.amount)} ₺",
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Kategori İlerleme Çubuğu (Rounded Capsule)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(Color(0xFFF1F5F9))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(cat.percentage / 100f)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(cat.color)
                                )
                            }
                        }

                        HorizontalDivider(
                            color = Color(0xFFF1F5F9),
                            thickness = 1.dp
                        )
                    }
                }
            }

            // 5. KİŞİ BAZLI HARCAMA HACMİ
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "KİŞİ BAZLI İŞLEM HACMİ",
                        color = TextSecondary,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )

                    Text(
                        text = "${friendVolumeList.size} Kişi",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                Column(modifier = Modifier.fillMaxWidth()) {
                    friendVolumeList.forEachIndexed { _, item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .applePressEffect(0.98f) { onFriendClick(item.id) }
                                .padding(horizontal = 20.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFF1F5F9),
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = item.avatar,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary,
                                            fontSize = 13.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = item.name,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "${String.format(java.util.Locale.US, "%.0f", item.totalVolume)} ₺ Ortak Hacim",
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (item.netStatus == "Hesap Kapalı") Color(0xFFF1F5F9) else if (item.isPositive) PrimaryEmeraldContainer else AccentRoseContainer
                            ) {
                                Text(
                                    text = item.netStatus,
                                    color = if (item.netStatus == "Hesap Kapalı") TextSecondary else if (item.isPositive) PrimaryEmerald else AccentRose,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                        HorizontalDivider(
                            color = Color(0xFFF1F5F9),
                            thickness = 1.dp
                        )
                    }
                }
            }

            // 6. AKILLI ANALİTİK İPUCU (Flat)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF1F5F9),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.AutoGraph,
                            contentDescription = null,
                            tint = PrimaryEmerald,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "Ağustos Ayı Değerlendirmesi",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Bu ay ortak harcamalarında en yüksek pay Yemek kategorisine ait (%44). 14 işlemde tüm hesaplaşmalar sorunsuz gerçekleşti.",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 17.sp
                    )
                }
            }
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
        }
    }
}

/**
 * Custom Apple Fitness-style Concentric / Segmented Activity Rings Visualizer
 */
@Composable
fun IOSActivityRingsVisualizer(
    stats: List<CategoryExpenseStat>,
    modifier: Modifier = Modifier
) {
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing)
        )
    }

    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val strokeWidth = 7.dp.toPx()
        val spacing = 3.dp.toPx()

        val ringCount = minOf(stats.size, 3)
        for (i in 0 until ringCount) {
            val stat = stats[i]
            val radius = (size.minDimension / 2f) - (strokeWidth / 2f) - (i * (strokeWidth + spacing))
            if (radius <= 0) continue

            // Background track
            drawCircle(
                color = stat.color.copy(alpha = 0.15f),
                radius = radius,
                center = center,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Animated progress arc
            val sweepAngle = (stat.percentage / 100f) * 360f * animatedProgress.value
            drawArc(
                color = stat.color,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }
    }
}

