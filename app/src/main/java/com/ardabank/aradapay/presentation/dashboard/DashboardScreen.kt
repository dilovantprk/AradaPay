package com.ardabank.aradapay.presentation.dashboard

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import com.ardabank.aradapay.presentation.components.bounceClick
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.RequestQuote
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.ardabank.aradapay.data.repository.GroupRepository
import com.ardabank.aradapay.domain.model.Expense
import com.ardabank.aradapay.domain.model.Nudge
import com.ardabank.aradapay.presentation.common.BankContactPickerScreen
import com.ardabank.aradapay.presentation.common.applePressEffect
import com.ardabank.aradapay.presentation.components.FinancialHeroAmountCard
import com.ardabank.aradapay.presentation.components.QuickIncrementChip
import com.ardabank.aradapay.presentation.components.SlideToConfirmButton
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
import com.ardabank.aradapay.util.NotificationHelper

data class PriorityPayment(
    val id: String,
    val recipientName: String,
    val recipientTag: String,
    val recipientAvatar: String,
    val title: String,
    val dueDateText: String,
    val isUrgent: Boolean,
    val amount: Double,
    val bankName: String,
    val category: String
)

data class QuickRequestFriend(val name: String, val tag: String, val avatar: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    userName: String = "Mehmet",
    avatarEmoji: String = "ME",
    netBalance: Double = 850.0,
    totalReceivable: Double = 1250.0,
    totalPayable: Double = 400.0,
    isLocked: Boolean = false,
    nudges: List<Nudge> = emptyList(),
    pendingExpenses: List<Expense> = emptyList(),
    onApproveExpense: (expenseId: String) -> Unit = {},
    onRejectExpense: (expenseId: String) -> Unit = {},
    onToggleLock: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onAddExpenseClick: () -> Unit = {},
    onSettleUpClick: () -> Unit = {},
    onSeeAllActivityClick: () -> Unit = {},
    onAnalyticsClick: () -> Unit = {},
    onSavingsReportClick: () -> Unit = {},
    groupRepository: GroupRepository? = null,
    onGroupClick: (groupId: String) -> Unit = {},
    onSeeAllGroupsClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val firstName = userName.trim().split(" ").firstOrNull() ?: "Kullanıcı"

    var showRequestMoneySheet by remember { mutableStateOf(false) }
    var showQrSheet by remember { mutableStateOf(false) }
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        if (isGranted) {
            Toast.makeText(context, "Kamera izni verildi", Toast.LENGTH_SHORT).show()
        }
    }

    // Top 3 Priority Upcoming Payments (Ordered by nearest deadline/priority)
    val top3PriorityPayments = remember {
        listOf(
            PriorityPayment(
                id = "p1",
                recipientName = "Mert Yılmaz",
                recipientTag = "Mert#4421",
                recipientAvatar = "MY",
                title = "Kadıköy Evi Kira & Fatura Payı",
                dueDateText = "Bugün",
                isUrgent = true,
                amount = 250.0,
                bankName = "Garanti BBVA",
                category = "Kira"
            ),
            PriorityPayment(
                id = "p2",
                recipientName = "Burak Kaya",
                recipientTag = "Burak#9012",
                recipientAvatar = "BK",
                title = "Bodrum Tatili Yemek Masrafı",
                dueDateText = "Yarın",
                isUrgent = false,
                amount = 100.0,
                bankName = "İş Bankası",
                category = "Tatil"
            ),
            PriorityPayment(
                id = "p3",
                recipientName = "Caner Demir",
                recipientTag = "Caner#3304",
                recipientAvatar = "CD",
                title = "Ortak Market & Kahve Masrafı",
                dueDateText = "3 Gün Sonra",
                isUrgent = false,
                amount = 50.0,
                bankName = "Akbank",
                category = "Market"
            )
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding(),
        contentPadding = PaddingValues(bottom = 36.dp)
    ) {
        // 1. Top Bar Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Merhaba, $firstName",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF64748B),
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "AradaPay",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A),
                        fontSize = 28.sp,
                        letterSpacing = (-0.5).sp
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilledTonalIconButton(
                        onClick = onSeeAllActivityClick,
                        shape = RoundedCornerShape(12.dp),
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = Color(0xFFF1F5F9)
                        ),
                        modifier = Modifier
                            .size(40.dp)
                            .bounceClick { onSeeAllActivityClick() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Ara",
                            tint = Color(0xFF0F172A),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    FilledTonalIconButton(
                        onClick = onProfileClick,
                        shape = RoundedCornerShape(12.dp),
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = Color(0xFFF1F5F9)
                        ),
                        modifier = Modifier
                            .size(40.dp)
                            .bounceClick { onProfileClick() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Bildirimler",
                            tint = Color(0xFF0F172A),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
        }

        // 2. Hero: Net Durum (Clean Flat Hero)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "NET BAKİYE DURUMU",
                        color = Color(0xFF64748B),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )

                    IconButton(
                        onClick = onToggleLock,
                        modifier = Modifier
                            .size(28.dp)
                            .bounceClick { onToggleLock() }
                    ) {
                        Icon(
                            imageVector = if (isLocked) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (isLocked) "Bakiyeyi Göster" else "Bakiyeyi Gizle",
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (isLocked) "•••• ₺" else "${if (netBalance >= 0) "+" else ""}${String.format(java.util.Locale.US, "%.2f", netBalance)} ₺",
                    color = if (netBalance >= 0) PrimaryEmerald else AccentRose,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.8).sp
                )
            }
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
        }

        // 3. CTA Row (+ Harcama Ekle & Öde & Fitleş)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = PrimaryEmerald,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .bounceClick { onAddExpenseClick() }
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Harcama Ekle",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF1E293B),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .bounceClick { onSettleUpClick() }
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Payment,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Öde & Fitleş",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
        }

        // 3.1. Quick Shortcuts Bar (Clean Flat Bar)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DashboardQuickActionItem(
                    icon = Icons.Default.RequestQuote,
                    title = "Para İste",
                    tint = PrimaryEmerald,
                    onClick = { showRequestMoneySheet = true }
                )
                DashboardQuickActionItem(
                    icon = Icons.Default.QrCode,
                    title = "QR İşlem",
                    tint = Color(0xFF3B82F6),
                    onClick = { showQrSheet = true }
                )
                DashboardQuickActionItem(
                    icon = Icons.Default.PieChart,
                    title = "Analitik",
                    tint = Color(0xFFF59E0B),
                    onClick = onAnalyticsClick
                )
                DashboardQuickActionItem(
                    icon = Icons.Default.Savings,
                    title = "Tasarruf",
                    tint = Color(0xFFEC4899),
                    onClick = onSavingsReportClick
                )
            }
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
        }

        // 4. Section: En Yakın Ödemeler (Flat Stream)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "EN YAKIN ÖDEMELER",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B),
                    letterSpacing = 0.8.sp
                )

                Text(
                    text = "Tümünü Gör",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryEmerald,
                    modifier = Modifier
                        .bounceClick { onSettleUpClick() }
                        .padding(4.dp)
                )
            }
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
        }

        itemsIndexed(top3PriorityPayments) { index, payment ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .bounceClick { onSettleUpClick() }
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFF1F5F9),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = payment.recipientAvatar,
                                color = Color(0xFF0F172A),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = payment.recipientName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${payment.dueDateText} • ${payment.title}",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "sen borçlusun",
                        color = AccentRose,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isLocked) "•••• ₺" else "- ${String.format(java.util.Locale.US, "%.2f", payment.amount)} ₺",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentRose
                    )
                }
            }
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
        }

        // 5. Section: Son İşlem (Flat Single Item)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SON İŞLEM",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B),
                    letterSpacing = 0.8.sp
                )

                Text(
                    text = "Geçmiş",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryEmerald,
                    modifier = Modifier
                        .bounceClick { onSeeAllActivityClick() }
                        .padding(4.dp)
                )
            }
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .bounceClick { onSeeAllActivityClick() }
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFF1F5F9),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.LocalCafe,
                                contentDescription = null,
                                tint = Color(0xFF0F172A),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = "Starbucks Coffee",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "22 Ağustos • Ortak Harcama",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "sen borçlusun",
                        color = AccentRose,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isLocked) "•••• ₺" else "- 120.00 ₺",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentRose
                    )
                }
            }
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
        }
    }

    // 6. ACTION: PARA İSTE & DÜRT (Flat & Minimalist FinTech Screen)
    if (showRequestMoneySheet) {
        BackHandler { showRequestMoneySheet = false }

        val friends = listOf(
            QuickRequestFriend("Ahmet Yılmaz", "Ahmet#7821", "AY"),
            QuickRequestFriend("Zeynep Kaya", "Zeynep#3412", "ZK"),
            QuickRequestFriend("Mert Demir", "Mert#9015", "MD"),
            QuickRequestFriend("Elif Şahin", "Elif#4420", "EŞ"),
            QuickRequestFriend("Burak Öztürk", "Burak#6108", "BÖ"),
            QuickRequestFriend("Selin Aydın", "Selin#2839", "SA")
        )
        var selectedFriend by remember { mutableStateOf(friends.first()) }
        var requestAmount by remember { mutableStateOf("") }
        var requestNote by remember { mutableStateOf("") }
        var showPickerInRequestSheet by remember { mutableStateOf(false) }

        if (showPickerInRequestSheet) {
            BankContactPickerScreen(
                title = "Para İstenecek Kişiyi Seç",
                allFriends = friends.mapIndexed { idx, f ->
                    com.ardabank.aradapay.presentation.expense.ExpenseParticipant(
                        id = "$idx",
                        name = f.name,
                        tag = f.tag,
                        avatar = f.avatar
                    )
                },
                selectedIds = setOf(),
                onDismiss = { showPickerInRequestSheet = false },
                onConfirmSelection = { selIds, all ->
                    val chosenId = selIds.firstOrNull()
                    if (chosenId != null) {
                        val match = all.find { it.id == chosenId }
                        if (match != null) {
                            selectedFriend = QuickRequestFriend(match.name, match.tag, match.avatar)
                        }
                    }
                    showPickerInRequestSheet = false
                }
            )
            return
        }

        val amt = requestAmount.toDoubleOrNull() ?: 0.0
        val isRequestAmountValid = amt > 0.0

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
                    .imePadding()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
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
                            onClick = { showRequestMoneySheet = false },
                            shape = RoundedCornerShape(12.dp),
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = Color(0xFFF1F5F9)
                            ),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Geri",
                                tint = Color(0xFF0F172A),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Text(
                            text = "Para İste & Hatırlat",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )

                        Spacer(modifier = Modifier.size(40.dp))
                    }
                    HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                    // 2. KİMDEN İSTENECEK (Selected Recipient & Quick Avatars)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "KİMDEN İSTEYECEKSİNİZ?",
                                color = Color(0xFF64748B),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )

                            FilledTonalButton(
                                onClick = { showPickerInRequestSheet = true },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = Color(0xFFF1F5F9),
                                    contentColor = Color(0xFF0F172A)
                                ),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Tüm Kişiler", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        // Selected Recipient Highlight Card
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFFF8FAFC))
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFF1F5F9),
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = selectedFriend.avatar,
                                        color = Color(0xFF0F172A),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = selectedFriend.name,
                                    color = Color(0xFF0F172A),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = selectedFriend.tag,
                                    color = Color(0xFF64748B),
                                    fontSize = 13.sp
                                )
                            }

                            Surface(
                                shape = CircleShape,
                                color = PrimaryEmeraldContainer,
                                modifier = Modifier.size(24.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = PrimaryEmerald,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }

                        // Quick Avatar List
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(friends) { friend ->
                                val isSelected = selectedFriend.name == friend.name
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) PrimaryEmeraldContainer else Color(0xFFF1F5F9),
                                    border = if (isSelected) BorderStroke(1.dp, PrimaryEmerald) else null,
                                    modifier = Modifier.bounceClick { selectedFriend = friend }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (isSelected) PrimaryEmerald else Color.White,
                                            modifier = Modifier.size(22.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = friend.avatar,
                                                    color = if (isSelected) Color.White else Color(0xFF0F172A),
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = friend.name.split(" ").first(),
                                            color = if (isSelected) PrimaryEmerald else Color(0xFF0F172A),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                    HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                    // 3. TALEP EDİLECEK TUTAR (Hero Big Amount Input)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "TALEP EDİLECEK TUTAR",
                            color = Color(0xFF64748B),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            BasicTextField(
                                value = requestAmount,
                                onValueChange = { input ->
                                    val filtered = input.filter { it.isDigit() || it == '.' || it == ',' }
                                    if (filtered.count { it == '.' || it == ',' } <= 1) {
                                        requestAmount = filtered
                                    }
                                },
                                textStyle = TextStyle(
                                    color = if (isRequestAmountValid) PrimaryEmerald else Color(0xFF0F172A),
                                    fontSize = 40.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = (-1).sp
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true,
                                cursorBrush = SolidColor(PrimaryEmerald),
                                modifier = Modifier.weight(1f),
                                decorationBox = { innerTextField ->
                                    if (requestAmount.isEmpty()) {
                                        Text(
                                            text = "0,00",
                                            color = Color(0xFFCBD5E1),
                                            fontSize = 40.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            letterSpacing = (-1).sp
                                        )
                                    }
                                    innerTextField()
                                }
                            )

                            Text(
                                text = "₺",
                                color = if (isRequestAmountValid) PrimaryEmerald else Color(0xFF94A3B8),
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Quick Preset Chips (+50, +100, +250, +500, Temizle)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(50, 100, 250, 500).forEach { addVal ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFFF1F5F9),
                                    modifier = Modifier
                                        .weight(1f)
                                        .bounceClick {
                                            val current = requestAmount.replace(",", ".").toDoubleOrNull() ?: 0.0
                                            val updated = current + addVal
                                            requestAmount = if (updated % 1.0 == 0.0) {
                                                updated.toLong().toString()
                                            } else {
                                                String.format(java.util.Locale.US, "%.2f", updated)
                                            }
                                        }
                                ) {
                                    Box(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "+$addVal ₺",
                                            color = Color(0xFF0F172A),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            if (requestAmount.isNotEmpty()) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFFF1F5F9),
                                    modifier = Modifier.bounceClick { requestAmount = "" }
                                ) {
                                    Box(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Temizle",
                                            tint = Color(0xFF64748B),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                    // 4. AÇIKLAMA / NOT ALANI
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "AÇIKLAMA VEYA NOT (OPSİYONEL)",
                            color = Color(0xFF64748B),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF8FAFC))
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = Color(0xFF64748B),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                                if (requestNote.isEmpty()) {
                                    Text("Örn: Kahve masrafı payın", color = Color(0xFF94A3B8), fontSize = 14.sp)
                                }
                                BasicTextField(
                                    value = requestNote,
                                    onValueChange = { requestNote = it },
                                    textStyle = TextStyle(color = Color(0xFF0F172A), fontSize = 14.sp, fontWeight = FontWeight.Medium),
                                    cursorBrush = SolidColor(PrimaryEmerald),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }

                // 5. BOTTOM ACTIONS (Send Request & Share Payment Link)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Primary: Send Request
                    Button(
                        onClick = {
                            if (amt > 0) {
                                NotificationHelper.showSystemNotification(
                                    context = context,
                                    title = "Ödeme Talebi İletildi",
                                    message = "${selectedFriend.name} kullanıcısından ${String.format(java.util.Locale.US, "%.2f", amt)} ₺ talep edildi: $requestNote"
                                )
                                Toast.makeText(context, "${selectedFriend.name} kişisine ${String.format(java.util.Locale.US, "%.2f", amt)} ₺ ödeme talebi iletildi.", Toast.LENGTH_SHORT).show()
                                showRequestMoneySheet = false
                            } else {
                                Toast.makeText(context, "Lütfen talep tutarı giriniz", Toast.LENGTH_SHORT).show()
                            }
                        },
                        enabled = isRequestAmountValid,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryEmerald,
                            contentColor = Color.White,
                            disabledContainerColor = Color(0xFFE2E8F0),
                            disabledContentColor = Color(0xFF94A3B8)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isRequestAmountValid) "Ödeme Talebi Gönder (${String.format(java.util.Locale.US, "%.2f", amt)} ₺)" else "Ödeme Talebi Gönder",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Secondary: Share Link
                    FilledTonalButton(
                        onClick = {
                            val shareText = "Selam ${selectedFriend.name}! AradaPay üzerinden ${if (amt > 0) "${String.format(java.util.Locale.US, "%.2f", amt)} ₺" else ""} ödeme talebi gönderdim: ${requestNote.ifBlank { "Ortak masraf payı" }}. FAST ile hemen ödemek için: https://aradapay.com/pay/arda1453?amount=$amt"
                            val sendIntent = android.content.Intent().apply {
                                action = android.content.Intent.ACTION_SEND
                                putExtra(android.content.Intent.EXTRA_TEXT, shareText)
                                type = "text/plain"
                            }
                            context.startActivity(android.content.Intent.createChooser(sendIntent, "Ödeme Talebini Paylaş"))
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color(0xFFF1F5F9),
                            contentColor = Color(0xFF0F172A)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "FAST Ödeme Linkini Paylaş ↗",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
        return
    }

    // 7. ACTION: QR İŞLEMLERİ (MY QR & QR TARAYICI) FULL-PAGE SCREEN
    if (showQrSheet) {
        BackHandler { showQrSheet = false }

        var selectedQrTab by remember { mutableIntStateOf(0) }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = LightBackground
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledTonalIconButton(
                        onClick = { showQrSheet = false },
                        shape = RoundedCornerShape(12.dp),
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = SurfaceWhite
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
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "QR İşlemleri",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                TabRow(
                    selectedTabIndex = selectedQrTab,
                    containerColor = SurfaceContainerLow,
                    contentColor = PrimaryEmerald,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedQrTab]),
                            color = PrimaryEmerald
                        )
                    },
                    modifier = Modifier.clip(RoundedCornerShape(14.dp))
                ) {
                    Tab(
                        selected = selectedQrTab == 0,
                        onClick = { selectedQrTab = 0 },
                        text = { Text("Benim QR Kodum", fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.QrCode, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                    Tab(
                        selected = selectedQrTab == 1,
                        onClick = {
                            selectedQrTab = 1
                            if (!hasCameraPermission) {
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        },
                        text = { Text("QR Tara & Öde", fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                }

                if (selectedQrTab == 0) {
                    // My QR Pass Card (Apple Wallet Pass Look)
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = SurfaceWhite,
                        border = BorderStroke(0.5.dp, Color.Black.copy(alpha = 0.06f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = SurfaceContainerLow,
                                border = BorderStroke(0.5.dp, Color.Black.copy(alpha = 0.05f)),
                                modifier = Modifier.size(180.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.QrCode,
                                        contentDescription = "QR Kod",
                                        tint = TextPrimary,
                                        modifier = Modifier.size(150.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "$userName • #1453",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Hızlı transfer ve anında fitleşme için okutunuz",
                                color = TextSecondary,
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 12.sp
                            )
                        }
                    }
                } else {
                    // QR Scanner Live / Simulation Box
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = SurfaceWhite,
                        border = BorderStroke(0.5.dp, Color.Black.copy(alpha = 0.06f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.QrCodeScanner,
                                    contentDescription = null,
                                    tint = if (hasCameraPermission) PrimaryEmerald else TextPrimary,
                                    modifier = Modifier.size(56.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = if (hasCameraPermission) "Kamera Vizörü Aktif" else "Kamera İzni Gerekiyor",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (hasCameraPermission) "Arkadaşınızın QR kodunu vizöre hizalayın..." else "QR okutmak için kamera izni veriniz",
                                    color = TextSecondary,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }

                    Button(
                        onClick = {
                            if (!hasCameraPermission) {
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                            } else {
                                Toast.makeText(context, "QR Okundu: 'Ahmet Yılmaz (#ahmet)' tespit edildi.", Toast.LENGTH_LONG).show()
                                showQrSheet = false
                                onSettleUpClick()
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryEmerald,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .applePressEffect(0.96f)
                    ) {
                        Text(if (hasCameraPermission) "QR Okutmayı Tamamla" else "Kamera İzni Ver", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        return
    }
}

@Composable
fun DashboardQuickActionItem(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    tint: Color = TextPrimary,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .applePressEffect(0.92f, onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = tint.copy(alpha = 0.12f),
            modifier = Modifier.size(48.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = tint,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = title,
            color = TextPrimary,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            fontSize = 11.sp
        )
    }
}

