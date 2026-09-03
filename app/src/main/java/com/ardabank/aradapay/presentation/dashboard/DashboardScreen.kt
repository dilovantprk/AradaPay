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
import androidx.compose.material.icons.automirrored.filled.Send
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
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.ardabank.aradapay.presentation.activity.ActivityFilterOption
import com.ardabank.aradapay.presentation.activity.ActivityItemKind
import com.ardabank.aradapay.presentation.activity.ActivityActionItem
import com.ardabank.aradapay.presentation.common.MaskedFinancialText
import androidx.compose.material.icons.outlined.Circle
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
import androidx.compose.ui.text.style.TextOverflow
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
import com.ardabank.aradapay.domain.repository.GroupRepository
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
import com.ardabank.aradapay.presentation.common.UserAvatar
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
    userName: String = "Kullanıcı",
    avatarEmoji: String = "AP",
    avatarUrl: String = "",
    netBalance: Double = 0.0,
    totalReceivable: Double = 0.0,
    totalPayable: Double = 0.0,
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

    val coroutineScope = rememberCoroutineScope()
    var selectedFilter by remember { mutableStateOf(ActivityFilterOption.ALL) }
    var selectedItemForDetail by remember { mutableStateOf<ActivityActionItem?>(null) }
    var isLoadingMore by remember { mutableStateOf(false) }
    var hasMoreItems by remember { mutableStateOf(true) }
    var pageNumber by remember { mutableStateOf(1) }

    // Live Realistic FinTech Data
    val activityList = remember {
        mutableStateListOf(
            ActivityActionItem(
                id = "act_1",
                title = "Migros Sanal Market",
                subtitle = "Market • Ahmet Yılmaz • 10:15",
                actorName = "Ahmet Yılmaz",
                actorInitials = "AY",
                dateGroup = "BUGÜN",
                time = "10:15",
                amount = 225.00,
                isPositiveFinancial = null,
                statusBadge = "Onayını Bekliyor",
                statusBgColor = Color(0xFFF1F5F9),
                statusTextColor = Color(0xFF475569),
                kind = ActivityItemKind.INCOMING_APPROVAL,
                icon = Icons.Default.ShoppingCart,
                bgTint = Color(0xFFEFF6FF),
                iconTint = Color(0xFF2563EB)
            ),
            ActivityActionItem(
                id = "act_2",
                title = "Starbucks Coffee",
                subtitle = "Yemek & İçecek • Zeynep Kaya • 16:00",
                actorName = "Zeynep Kaya",
                actorInitials = "ZK",
                dateGroup = "BUGÜN",
                time = "16:00",
                amount = 120.00,
                isPositiveFinancial = true,
                statusBadge = "Arkadaş Onayı",
                statusBgColor = Color(0xFFEFF6FF),
                statusTextColor = Color(0xFF2563EB),
                kind = ActivityItemKind.EXPENSE_ADDED_YOU_GET_BACK,
                icon = Icons.Default.Fastfood,
                bgTint = Color(0xFFECFDF5),
                iconTint = PrimaryEmerald
            ),
            ActivityActionItem(
                id = "act_3",
                title = "Taksi & Ulaşım",
                subtitle = "Ulaşım • Mert Çelik • 23:45",
                actorName = "Mert Çelik",
                actorInitials = "MÇ",
                dateGroup = "DÜN",
                time = "23:45",
                amount = 100.00,
                isPositiveFinancial = true,
                statusBadge = "Arkadaş Onayı",
                statusBgColor = Color(0xFFEFF6FF),
                statusTextColor = Color(0xFF2563EB),
                kind = ActivityItemKind.EXPENSE_ADDED_YOU_GET_BACK,
                icon = Icons.Default.LocalGasStation,
                bgTint = Color(0xFFECFDF5),
                iconTint = PrimaryEmerald
            ),
            ActivityActionItem(
                id = "act_4",
                title = "Ev Kirası & Aidat Payı",
                subtitle = "Konut • Burak Demir • 14:20",
                actorName = "Burak Demir",
                actorInitials = "BD",
                dateGroup = "DÜN",
                time = "14:20",
                amount = 450.00,
                isPositiveFinancial = false,
                statusBadge = "Ödeme Bekliyor",
                statusBgColor = Color(0xFFFEE2E2),
                statusTextColor = Color(0xFFDC2626),
                kind = ActivityItemKind.INCOMING_NUDGE,
                icon = Icons.Default.Home,
                bgTint = Color(0xFFFEF2F2),
                iconTint = Color(0xFFDC2626)
            ),
            ActivityActionItem(
                id = "act_5",
                title = "Konser Bileti",
                subtitle = "Eğlence • Caner Yıldız • 21 Ağu",
                actorName = "Caner Yıldız",
                actorInitials = "CY",
                dateGroup = "BU HAFTA",
                time = "21 Ağu",
                amount = 320.00,
                isPositiveFinancial = true,
                statusBadge = "Tahsilat Bekliyor",
                statusBgColor = Color(0xFFDCFCE7),
                statusTextColor = Color(0xFF16A34A),
                kind = ActivityItemKind.EXPENSE_ADDED_YOU_GET_BACK,
                icon = Icons.Default.ConfirmationNumber,
                bgTint = Color(0xFFF5F3FF),
                iconTint = Color(0xFF7C3AED)
            ),
            ActivityActionItem(
                id = "act_6",
                title = "FAST Fitleşme Ödemesi",
                subtitle = "FAST Havale • Selin Tekin • 19 Ağu",
                actorName = "Selin Tekin",
                actorInitials = "ST",
                dateGroup = "BU HAFTA",
                time = "19 Ağu",
                amount = 180.00,
                isPositiveFinancial = true,
                statusBadge = "Fitleşildi",
                statusBgColor = Color(0xFFDCFCE7),
                statusTextColor = Color(0xFF16A34A),
                kind = ActivityItemKind.SETTLEMENT_COMPLETED,
                icon = Icons.Default.Payment,
                bgTint = Color(0xFFECFDF5),
                iconTint = PrimaryEmerald
            )
        )
    }

    val filteredList = remember(selectedFilter, activityList.toList()) {
        activityList.filter { item ->
            when (selectedFilter) {
                ActivityFilterOption.ALL -> true
                ActivityFilterOption.PENDING -> item.kind == ActivityItemKind.INCOMING_APPROVAL || item.kind == ActivityItemKind.FRIEND_REQUEST || item.kind == ActivityItemKind.INCOMING_NUDGE
                ActivityFilterOption.RECEIVABLES -> item.isPositiveFinancial == true
                ActivityFilterOption.PAYABLES -> item.isPositiveFinancial == false
                ActivityFilterOption.SETTLEMENTS -> item.kind == ActivityItemKind.SETTLEMENT_COMPLETED || item.kind == ActivityItemKind.SMART_CROSS_SETTLEMENT
            }
        }
    }

    val groupedItems = remember(filteredList) {
        filteredList.groupBy { it.dateGroup }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
    ) {
        // 1. STICKY TOP BAR HEADER
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White
        ) {
            Column {
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

                    // Profil Avatar Butonu (onProfileClick)
                    UserAvatar(
                        userName = userName,
                        avatarUrl = avatarUrl,
                        avatarEmoji = avatarEmoji,
                        size = 44.dp,
                        shape = CircleShape,
                        border = BorderStroke(1.5.dp, PrimaryEmerald),
                        backgroundColor = PrimaryEmeraldContainer,
                        textColor = PrimaryEmerald,
                        fontSizeSp = 15,
                        modifier = Modifier.bounceClick { onProfileClick() }
                    )
                }
                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(bottom = 36.dp)
        ) {
            // 2. Hero: Net Durum (Pure Ultra-Minimal Hero)
            item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "MASA DURUMU",
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
                    fontSize = 38.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-1.0).sp
                )
            }
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
        }

        // 3. CTA Row (+ Masaya Bırak & Ödeş & Kapat)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = PrimaryEmerald,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
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
                            text = "Masaya Bırak",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF0F172A),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
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
                            text = "Ödeş & Kapat",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
        }

        // 4. Section: HAREKETLER & İŞLEMLER (Tam ve Entegre FinTech Akışı)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "MASADAKİ HAREKETLER",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B),
                        letterSpacing = 0.8.sp
                    )

                    Text(
                        text = "${filteredList.size} Hareket",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF94A3B8)
                    )
                }

                // Filter Chips Horizontal Row
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(ActivityFilterOption.values()) { opt ->
                        val isSelected = selectedFilter == opt
                        val count = remember(opt, activityList.toList()) {
                            activityList.count { item ->
                                when (opt) {
                                    ActivityFilterOption.ALL -> true
                                    ActivityFilterOption.PENDING -> item.kind == ActivityItemKind.INCOMING_APPROVAL || item.kind == ActivityItemKind.FRIEND_REQUEST || item.kind == ActivityItemKind.INCOMING_NUDGE
                                    ActivityFilterOption.RECEIVABLES -> item.isPositiveFinancial == true
                                    ActivityFilterOption.PAYABLES -> item.isPositiveFinancial == false
                                    ActivityFilterOption.SETTLEMENTS -> item.kind == ActivityItemKind.SETTLEMENT_COMPLETED || item.kind == ActivityItemKind.SMART_CROSS_SETTLEMENT
                                }
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) PrimaryEmeraldContainer else Color(0xFFF1F5F9),
                            border = if (isSelected) BorderStroke(1.dp, PrimaryEmerald) else null,
                            modifier = Modifier
                                .bounceClick { selectedFilter = opt }
                                .padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = "${opt.title} ($count)",
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) PrimaryEmerald else Color(0xFF64748B),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp, modifier = Modifier.padding(top = 10.dp))
        }

        if (filteredList.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 36.dp, horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = PrimaryEmerald,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Masa bomboş.",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Ya herkes kendi hesabını ödedi ya da dışarı çıkma vaktiniz geldi.",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            groupedItems.forEach { (dateGroup, items) ->
                item {
                    Text(
                        text = dateGroup,
                        color = Color(0xFF94A3B8),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(start = 20.dp, top = 16.dp, bottom = 8.dp)
                    )
                    HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
                }

                itemsIndexed(items) { _, item ->
                    FinTechDashboardActivityRow(
                        item = item,
                        isLocked = isLocked,
                        onItemClick = {
                            if (item.kind == ActivityItemKind.INCOMING_NUDGE) {
                                onSettleUpClick()
                            } else {
                                selectedItemForDetail = item
                            }
                        }
                    )
                    HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
                }
            }

            // Pagination Button
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (hasMoreItems) {
                        FilledTonalButton(
                            onClick = {
                                if (!isLoadingMore) {
                                    coroutineScope.launch {
                                        isLoadingMore = true
                                        delay(600)
                                        if (pageNumber == 1) {
                                            activityList.addAll(
                                                listOf(
                                                    ActivityActionItem(
                                                        id = "act_7",
                                                        title = "CarrefourSA Gurme",
                                                        subtitle = "Market • Deniz Arslan • 14 Ağu",
                                                        actorName = "Deniz Arslan",
                                                        actorInitials = "DA",
                                                        dateGroup = "GEÇEN AY",
                                                        time = "14 Ağu",
                                                        amount = 310.50,
                                                        isPositiveFinancial = false,
                                                        statusBadge = "Ödendi",
                                                        kind = ActivityItemKind.EXPENSE_ADDED_YOU_OWE,
                                                        icon = Icons.Default.ShoppingCart,
                                                        bgTint = Color(0xFFEFF6FF),
                                                        iconTint = Color(0xFF2563EB)
                                                    ),
                                                    ActivityActionItem(
                                                        id = "act_8",
                                                        title = "Yemeksepeti Akşam Yemeği",
                                                        subtitle = "Yemek • Zeynep Kaya • 08 Ağu",
                                                        actorName = "Zeynep Kaya",
                                                        actorInitials = "ZK",
                                                        dateGroup = "GEÇEN AY",
                                                        time = "08 Ağu",
                                                        amount = 145.00,
                                                        isPositiveFinancial = true,
                                                        statusBadge = "Fitleşildi",
                                                        kind = ActivityItemKind.SETTLEMENT_COMPLETED,
                                                        icon = Icons.Default.Fastfood,
                                                        bgTint = Color(0xFFECFDF5),
                                                        iconTint = PrimaryEmerald
                                                    )
                                                )
                                            )
                                            pageNumber = 2
                                        } else if (pageNumber == 2) {
                                            activityList.addAll(
                                                listOf(
                                                    ActivityActionItem(
                                                        id = "act_10",
                                                        title = "Spotify Aile Planı",
                                                        subtitle = "Abonelik • Selin Tekin • 25 Tem",
                                                        actorName = "Selin Tekin",
                                                        actorInitials = "ST",
                                                        dateGroup = "TEMMUZ 2026",
                                                        time = "25 Tem",
                                                        amount = 45.00,
                                                        isPositiveFinancial = true,
                                                        statusBadge = "Fitleşildi",
                                                        kind = ActivityItemKind.SETTLEMENT_COMPLETED,
                                                        icon = Icons.Default.ConfirmationNumber,
                                                        bgTint = Color(0xFFF5F3FF),
                                                        iconTint = Color(0xFF7C3AED)
                                                    )
                                                )
                                            )
                                            pageNumber = 3
                                            hasMoreItems = false
                                        }
                                        isLoadingMore = false
                                    }
                                }
                            },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = Color(0xFFF1F5F9),
                                contentColor = PrimaryEmerald
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            if (isLoadingMore) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = PrimaryEmerald,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Yükleniyor...",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = PrimaryEmerald
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    tint = PrimaryEmerald,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Daha Fazla Yükle",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = PrimaryEmerald
                                )
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier.padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = PrimaryEmerald,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Tüm geçmiş hareketler yüklendi",
                                color = Color(0xFF94A3B8),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

    // 6. ACTION: PARA İSTE & DÜRT (Flat & Minimalist FinTech Screen)
    if (showRequestMoneySheet) {
        BackHandler { showRequestMoneySheet = false }

        val friends = emptyList<QuickRequestFriend>()
        var selectedFriend by remember { mutableStateOf<QuickRequestFriend?>(null) }
        var requestSearchQuery by remember { mutableStateOf("") }
        var isRecipientDropdownOpen by remember { mutableStateOf(false) }
        var requestAmount by remember { mutableStateOf("") }
        var requestNote by remember { mutableStateOf("") }

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
                            text = "Bi' Dürt & Masayı Hatırlat",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )

                        Spacer(modifier = Modifier.size(40.dp))
                    }
                    HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                    // 2. KİMDEN İSTENECEK (Seninle ve / Kimden Inline Seçim Barı)
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Kime:",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A),
                                fontSize = 15.sp
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            // Seçili Katılımcı Kapsül Çipi
                            if (selectedFriend != null) {
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = PrimaryEmeraldContainer,
                                    border = BorderStroke(1.dp, PrimaryEmerald),
                                    modifier = Modifier.bounceClick {
                                        isRecipientDropdownOpen = !isRecipientDropdownOpen
                                    }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = PrimaryEmerald,
                                            modifier = Modifier.size(22.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = selectedFriend!!.avatar,
                                                    color = Color.White,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(6.dp))

                                        Text(
                                            text = selectedFriend!!.name.split(" ").first(),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PrimaryEmerald
                                        )

                                        Spacer(modifier = Modifier.width(4.dp))

                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Kaldır",
                                            tint = PrimaryEmerald,
                                            modifier = Modifier
                                                .size(14.dp)
                                                .clickable {
                                                    selectedFriend = null
                                                    isRecipientDropdownOpen = true
                                                }
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(8.dp))
                            }

                            BasicTextField(
                                value = requestSearchQuery,
                                onValueChange = {
                                    requestSearchQuery = it
                                    if (it.isNotEmpty()) isRecipientDropdownOpen = true
                                },
                                singleLine = true,
                                textStyle = TextStyle(
                                    fontSize = 14.sp,
                                    color = Color(0xFF0F172A),
                                    fontWeight = FontWeight.Medium
                                ),
                                cursorBrush = SolidColor(PrimaryEmerald),
                                decorationBox = { innerTextField ->
                                    Box(contentAlignment = Alignment.CenterStart) {
                                        if (requestSearchQuery.isEmpty()) {
                                            Text(
                                                text = if (selectedFriend == null) "Kime ufak bir sinyal çakılacak?..." else "Kişi ara...",
                                                fontSize = 14.sp,
                                                color = Color(0xFF94A3B8)
                                            )
                                        }
                                        innerTextField()
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )

                            FilledTonalIconButton(
                                onClick = { isRecipientDropdownOpen = !isRecipientDropdownOpen },
                                shape = RoundedCornerShape(10.dp),
                                colors = IconButtonDefaults.filledTonalIconButtonColors(
                                    containerColor = if (isRecipientDropdownOpen) PrimaryEmeraldContainer else Color(0xFFF1F5F9)
                                ),
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(
                                    imageVector = if (isRecipientDropdownOpen) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Kişi Seç",
                                    tint = if (isRecipientDropdownOpen) PrimaryEmerald else Color(0xFF64748B),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // ALTA DOĞRU TÜM EKRANA SIĞAN KİŞİLER LİSTESİ
                        if (isRecipientDropdownOpen) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "KİŞİLER (${friends.size})",
                                    color = Color(0xFF64748B),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp,
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )

                                val filteredFriends = friends.filter {
                                    requestSearchQuery.isBlank() ||
                                    it.name.contains(requestSearchQuery, ignoreCase = true) ||
                                    it.tag.contains(requestSearchQuery, ignoreCase = true)
                                }

                                filteredFriends.forEach { friend ->
                                    val isChecked = selectedFriend?.name == friend.name
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .bounceClick {
                                                selectedFriend = friend
                                                isRecipientDropdownOpen = false
                                                requestSearchQuery = ""
                                            }
                                            .padding(vertical = 12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Surface(
                                                shape = RoundedCornerShape(12.dp),
                                                color = if (isChecked) PrimaryEmeraldContainer else Color(0xFFF1F5F9),
                                                modifier = Modifier.size(38.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Text(
                                                        text = friend.avatar,
                                                        color = if (isChecked) PrimaryEmerald else Color(0xFF0F172A),
                                                        fontSize = 13.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.width(12.dp))

                                            Column {
                                                Text(
                                                    text = friend.name,
                                                    fontWeight = if (isChecked) FontWeight.Bold else FontWeight.SemiBold,
                                                    fontSize = 14.sp,
                                                    color = if (isChecked) PrimaryEmerald else Color(0xFF0F172A)
                                                )
                                                Text(
                                                    text = friend.tag,
                                                    color = Color(0xFF64748B),
                                                    fontSize = 11.sp
                                                )
                                            }
                                        }

                                        if (isChecked) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Seçili",
                                                tint = PrimaryEmerald,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        } else {
                                            Icon(
                                                imageVector = Icons.Outlined.Circle,
                                                contentDescription = null,
                                                tint = Color(0xFFCBD5E1),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (!isRecipientDropdownOpen) {
                        HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                        // 3. HATIRLATILACAK PAY TUTARI (Hero Big Amount Input)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "HATIRLATILACAK PAY TUTARI",
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
                                        Text("Örn: Kahveler sendendi sanki?", color = Color(0xFF94A3B8), fontSize = 14.sp)
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
                }

                // 5. BOTTOM ACTIONS (Send Request & Share Payment Link)
                if (!isRecipientDropdownOpen) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Primary: Send Request
                        Button(
                            onClick = {
                                val friendName = selectedFriend?.name ?: "Arkadaş"
                                if (amt > 0) {
                                    NotificationHelper.showSystemNotification(
                                        context = context,
                                        title = "👀 Masaya Ufak Bir Hatırlatma",
                                        message = "$friendName masadaki payını (${String.format(java.util.Locale.US, "%.2f", amt)} ₺) hatırlattın: $requestNote"
                                    )
                                    Toast.makeText(context, "$friendName kişisine masadaki payı sessizce işaret edildi.", Toast.LENGTH_SHORT).show()
                                    showRequestMoneySheet = false
                                } else {
                                    Toast.makeText(context, "Lütfen masadaki pay tutarını girin", Toast.LENGTH_SHORT).show()
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
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isRequestAmountValid) "Bi' Dürt (${String.format(java.util.Locale.US, "%.2f", amt)} ₺)" else "Bi' Dürt (Sinyal Çak)",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Secondary: Share Link
                        FilledTonalButton(
                            onClick = {
                                val friendName = selectedFriend?.name ?: "Arkadaş"
                                val shareText = "Selam $friendName! Masada gözden kaçmış olabilir, ${if (amt > 0) "${String.format(java.util.Locale.US, "%.2f", amt)} ₺ " else ""}payın duruyor: ${requestNote.ifBlank { "Masadaki kahveler hala duruyor :)" }}. FAST ile kolayca ödeşmek istersen: https://aradapay.com/pay/arda1453?amount=$amt"
                                val sendIntent = android.content.Intent().apply {
                                    action = android.content.Intent.ACTION_SEND
                                    putExtra(android.content.Intent.EXTRA_TEXT, shareText)
                                    type = "text/plain"
                                }
                                context.startActivity(android.content.Intent.createChooser(sendIntent, "Sinyali Paylaş"))
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

    // ACTION DETAIL MODAL (Clean Transaction Receipt Sheet)
    selectedItemForDetail?.let { item ->
        ModalBottomSheet(
            onDismissRequest = { selectedItemForDetail = null },
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            dragHandle = { BottomSheetDefaults.DragHandle(color = Color(0xFFCBD5E1)) }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
                    .padding(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${item.actorName} • ${item.dateGroup} ${item.time}",
                            color = Color(0xFF64748B),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = item.bgTint,
                        modifier = Modifier.size(46.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(item.icon, contentDescription = null, tint = item.iconTint, modifier = Modifier.size(24.dp))
                        }
                    }
                }

                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                if (item.amount > 0) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("İşlem Tutarı:", color = Color(0xFF64748B), fontSize = 14.sp)
                        val amtColor = if (item.isPositiveFinancial == true) PrimaryEmerald else if (item.isPositiveFinancial == false) Color(0xFFE11D48) else Color(0xFF0F172A)
                        Text(
                            text = "${if (item.isPositiveFinancial == true) "+" else if (item.isPositiveFinancial == false) "-" else ""}${String.format(java.util.Locale.US, "%.2f", item.amount)} ₺",
                            color = amtColor,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Açıklama:", color = Color(0xFF64748B), fontSize = 14.sp)
                    Text(item.subtitle, color = Color(0xFF0F172A), fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }

                // Action Buttons
                when (item.kind) {
                    ActivityItemKind.INCOMING_NUDGE -> {
                        HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
                        Button(
                            onClick = {
                                item.isPendingActionHandled = true
                                selectedItemForDetail = null
                                onSettleUpClick()
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .bounceClick { }
                        ) {
                            Icon(Icons.Default.Payment, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Borcu Şimdi Öde & Fitleş", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                        }
                    }
                    ActivityItemKind.INCOMING_APPROVAL -> {
                        HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            FilledTonalButton(
                                onClick = {
                                    item.isPendingActionHandled = true
                                    selectedItemForDetail = null
                                    Toast.makeText(context, "Harcama payı reddedildi", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color(0xFFFEE2E2), contentColor = AccentRose),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                                    .bounceClick { }
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Reddet", tint = AccentRose, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Reddet", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }

                            Button(
                                onClick = {
                                    item.isPendingActionHandled = true
                                    selectedItemForDetail = null
                                    Toast.makeText(context, "Harcama payı onaylandı", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                                modifier = Modifier
                                    .weight(1.5f)
                                    .height(50.dp)
                                    .bounceClick { }
                            ) {
                                Icon(Icons.Default.Check, contentDescription = "Onayla", tint = Color.White, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Payı Onayla", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                            }
                        }
                    }
                    else -> {}
                }
            }
        }
    }
}

@Composable
private fun FinTechDashboardActivityRow(
    item: ActivityActionItem,
    isLocked: Boolean,
    onItemClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .bounceClick(onClick = onItemClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. Transaction Brand / Category Pastel Squircle Badge with Corner Status Dot
        Box(modifier = Modifier.size(44.dp)) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = item.bgTint,
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (item.kind == ActivityItemKind.FRIEND_REQUEST) {
                        Text(
                            text = item.actorInitials,
                            color = item.iconTint,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = null,
                            tint = item.iconTint,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // Status Indicator Corner Badge
            val (statusIcon, statusBg, statusTint) = when (item.kind) {
                ActivityItemKind.INCOMING_APPROVAL -> Triple(
                    Icons.Default.HourglassTop,
                    Color(0xFFFEF3C7), // Soft Amber
                    Color(0xFFD97706)
                )
                ActivityItemKind.INCOMING_NUDGE,
                ActivityItemKind.EXPENSE_ADDED_YOU_OWE -> Triple(
                    Icons.Default.ArrowDownward,
                    Color(0xFFFEE2E2), // Soft Rose
                    Color(0xFFDC2626)
                )
                ActivityItemKind.EXPENSE_ADDED_YOU_GET_BACK -> Triple(
                    Icons.Default.ArrowUpward,
                    Color(0xFFDCFCE7), // Soft Emerald
                    Color(0xFF16A34A)
                )
                ActivityItemKind.SETTLEMENT_COMPLETED -> Triple(
                    Icons.Default.Check,
                    Color(0xFFDCFCE7), // Soft Emerald
                    Color(0xFF16A34A)
                )
                ActivityItemKind.FRIEND_REQUEST -> Triple(
                    Icons.Default.Person,
                    Color(0xFFEDE9FE), // Soft Purple
                    Color(0xFF7C3AED)
                )
                ActivityItemKind.SMART_CROSS_SETTLEMENT -> Triple(
                    Icons.Default.SyncAlt,
                    Color(0xFFCCFBF1), // Soft Teal
                    Color(0xFF0D9488)
                )
            }

            Surface(
                shape = CircleShape,
                color = statusBg,
                border = BorderStroke(2.dp, Color.White),
                modifier = Modifier
                    .size(18.dp)
                    .align(Alignment.BottomEnd)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = statusIcon,
                        contentDescription = null,
                        tint = statusTint,
                        modifier = Modifier.size(10.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        // 2. Center Column: Merchant / Title & Clean Subtitle
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = item.subtitle,
                fontSize = 13.sp,
                color = Color(0xFF64748B),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // 3. Right Column: Clean Amount or Time
        Column(horizontalAlignment = Alignment.End) {
            if (item.amount > 0) {
                val amountColor = if (item.isPositiveFinancial == true) PrimaryEmerald else if (item.isPositiveFinancial == false) Color(0xFFE11D48) else Color(0xFF0F172A)
                val amountPrefix = if (item.isPositiveFinancial == true) "+" else if (item.isPositiveFinancial == false) "-" else ""

                if (isLocked) {
                    MaskedFinancialText(
                        amount = item.amount,
                        isLocked = true,
                        color = amountColor,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    )
                } else {
                    Text(
                        text = "$amountPrefix${String.format(java.util.Locale.US, "%.2f", item.amount)} ₺",
                        color = amountColor,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Text(
                    text = item.time,
                    color = Color(0xFF94A3B8),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
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

