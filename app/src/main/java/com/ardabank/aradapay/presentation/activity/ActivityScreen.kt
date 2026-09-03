package com.ardabank.aradapay.presentation.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ardabank.aradapay.domain.model.Expense
import com.ardabank.aradapay.presentation.common.MaskedFinancialText
import com.ardabank.aradapay.presentation.components.bounceClick
import com.ardabank.aradapay.presentation.theme.AccentRose
import com.ardabank.aradapay.presentation.theme.PrimaryEmerald
import com.ardabank.aradapay.presentation.theme.PrimaryEmeraldContainer

enum class ActivityFilterOption(val title: String) {
    ALL("Tümü"),
    PENDING("Askıda Kalanlar"),
    RECEIVABLES("Masada Kalan Paylarım (+₺)"),
    PAYABLES("Payıma Düşenler (-₺)"),
    SETTLEMENTS("Tertemiz Olanlar")
}

enum class ActivityItemKind {
    EXPENSE_ADDED_YOU_GET_BACK, // Harcama eklendi, alacağın var
    EXPENSE_ADDED_YOU_OWE,      // Harcama eklendi, borcun var
    INCOMING_APPROVAL,          // Gelen Onay: Başkası girdi, senin onayını bekliyor
    INCOMING_NUDGE,             // Gelen Dürtme / FAST Ödeme Hatırlatması
    FRIEND_REQUEST,             // Arkadaşlık İsteği
    SETTLEMENT_COMPLETED,       // Ödeme / Fitleşme yapıldı
    SMART_CROSS_SETTLEMENT      // 3'lü Akıllı Çapraz Fitleşme
}

data class ActivityActionItem(
    val id: String,
    val title: String,               // FinTech Merchant: "Migros Sanal Market", "Starbucks Coffee", etc.
    val subtitle: String,            // Subtitle: "Market • Ahmet Yılmaz • 10:15"
    val actorName: String,
    val actorInitials: String,
    val dateGroup: String,
    val time: String,
    val amount: Double = 0.0,
    val isPositiveFinancial: Boolean? = null,
    val statusBadge: String = "",
    val statusBgColor: Color = Color(0xFFF1F5F9),
    val statusTextColor: Color = Color(0xFF64748B),
    val kind: ActivityItemKind,
    val icon: ImageVector,
    val bgTint: Color,
    val iconTint: Color,
    val otherPartyName: String = "",
    val participants: List<String> = emptyList(),
    val iban: String = "TR64 0006 2000 0000 1122 3344 55",
    var isPendingActionHandled: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityScreen(
    expensesList: List<Expense> = emptyList(),
    isLocked: Boolean = false,
    onExpenseClick: (expenseId: String) -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    onNavigateToSettleUp: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
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

    var selectedFilter by remember { mutableStateOf(ActivityFilterOption.ALL) }

    // Filter Logic
    val filteredList = remember(activityList.toList(), searchQuery, selectedFilter) {
        activityList.filter { item ->
            val matchesSearch = searchQuery.isBlank() ||
                    item.title.contains(searchQuery, ignoreCase = true) ||
                    item.subtitle.contains(searchQuery, ignoreCase = true) ||
                    item.actorName.contains(searchQuery, ignoreCase = true)

            val matchesFilter = when (selectedFilter) {
                ActivityFilterOption.ALL -> true
                ActivityFilterOption.PENDING -> item.kind == ActivityItemKind.INCOMING_APPROVAL || item.kind == ActivityItemKind.INCOMING_NUDGE
                ActivityFilterOption.RECEIVABLES -> item.isPositiveFinancial == true
                ActivityFilterOption.PAYABLES -> item.isPositiveFinancial == false
                ActivityFilterOption.SETTLEMENTS -> item.kind == ActivityItemKind.SETTLEMENT_COMPLETED || item.kind == ActivityItemKind.SMART_CROSS_SETTLEMENT
            }

            matchesSearch && matchesFilter
        }
    }

    val groupedItems = remember(filteredList) {
        filteredList.groupBy { it.dateGroup }
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
        ) {
            // =========================================================================
            // 1. TOP APP BAR (Minimalist, Clean & Modern Title)
            // =========================================================================
            if (isSearchActive) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledTonalIconButton(
                        onClick = {
                            isSearchActive = false
                            searchQuery = ""
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = Color(0xFFF1F5F9)),
                        modifier = Modifier.size(38.dp).bounceClick {
                            isSearchActive = false
                            searchQuery = ""
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri",
                            tint = Color(0xFF0F172A),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFF1F5F9),
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            BasicTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                singleLine = true,
                                textStyle = TextStyle(
                                    color = Color(0xFF0F172A),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                cursorBrush = SolidColor(PrimaryEmerald),
                                decorationBox = { innerTextField ->
                                    if (searchQuery.isEmpty()) {
                                        Text("Hareketlerde ara...", color = Color(0xFF94A3B8), fontSize = 14.sp)
                                    }
                                    innerTextField()
                                },
                                modifier = Modifier.weight(1f)
                            )
                            if (searchQuery.isNotEmpty()) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Temizle",
                                    tint = Color(0xFF64748B),
                                    modifier = Modifier.size(16.dp).bounceClick { searchQuery = "" }
                                )
                            }
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Hareketler",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0F172A),
                            fontSize = 26.sp,
                            letterSpacing = (-0.5).sp
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilledTonalIconButton(
                            onClick = { isSearchActive = true },
                            shape = RoundedCornerShape(14.dp),
                            colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = Color(0xFFF1F5F9)),
                            modifier = Modifier.size(40.dp).bounceClick { isSearchActive = true }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Ara",
                                tint = Color(0xFF0F172A),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

            // =========================================================================
            // 2. MODERN M3 FILTER CHIPS
            // =========================================================================
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(ActivityFilterOption.values()) { opt ->
                    val isSelected = selectedFilter == opt
                    val count = when (opt) {
                        ActivityFilterOption.ALL -> activityList.size
                        ActivityFilterOption.PENDING -> activityList.count { it.kind == ActivityItemKind.INCOMING_APPROVAL || it.kind == ActivityItemKind.INCOMING_NUDGE }
                        ActivityFilterOption.RECEIVABLES -> activityList.count { it.isPositiveFinancial == true }
                        ActivityFilterOption.PAYABLES -> activityList.count { it.isPositiveFinancial == false }
                        ActivityFilterOption.SETTLEMENTS -> activityList.count { it.kind == ActivityItemKind.SETTLEMENT_COMPLETED }
                    }

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) PrimaryEmeraldContainer else Color(0xFFF8FAFC),
                        border = if (isSelected) BorderStroke(1.dp, PrimaryEmerald) else BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.bounceClick { selectedFilter = opt }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "${opt.title} ($count)",
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) PrimaryEmerald else Color(0xFF64748B)
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

            // =========================================================================
            // 3. ACTIVITY FEED LIST (Flat Modern Stream)
            // =========================================================================
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                if (filteredList.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 64.dp, start = 24.dp, end = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFF1F5F9),
                                modifier = Modifier.size(64.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = PrimaryEmerald,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Masa bomboş.",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color(0xFF0F172A)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Adisyonda bekleyen kayıt yok. Masadaki tüm hesaplar tertemiz!",
                                color = Color(0xFF64748B),
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center
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

                        itemsIndexed(items) { index, item ->
                            FinTechActivityRow(
                                item = item,
                                isLocked = isLocked,
                                onItemClick = {
                                    if (item.kind == ActivityItemKind.INCOMING_NUDGE) {
                                        onNavigateToSettleUp()
                                    } else {
                                        selectedItemForDetail = item
                                    }
                                }
                            )
                            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
                        }
                    }
                }

                if (filteredList.isNotEmpty()) {
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
                                                                statusBgColor = Color(0xFFF1F5F9),
                                                                statusTextColor = Color(0xFF64748B),
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
                                                                amount = 260.00,
                                                                isPositiveFinancial = true,
                                                                statusBadge = "Fitleşildi",
                                                                statusBgColor = Color(0xFFDCFCE7),
                                                                statusTextColor = Color(0xFF16A34A),
                                                                kind = ActivityItemKind.SETTLEMENT_COMPLETED,
                                                                icon = Icons.Default.Fastfood,
                                                                bgTint = Color(0xFFECFDF5),
                                                                iconTint = PrimaryEmerald
                                                            ),
                                                            ActivityActionItem(
                                                                id = "act_9",
                                                                title = "Otoyol & HGS Geçişi",
                                                                subtitle = "Ulaşım • Caner Yıldız • 01 Ağu",
                                                                actorName = "Caner Yıldız",
                                                                actorInitials = "CY",
                                                                dateGroup = "GEÇEN AY",
                                                                time = "01 Ağu",
                                                                amount = 95.00,
                                                                isPositiveFinancial = false,
                                                                statusBadge = "Fitleşildi",
                                                                statusBgColor = Color(0xFFDCFCE7),
                                                                statusTextColor = Color(0xFF16A34A),
                                                                kind = ActivityItemKind.SETTLEMENT_COMPLETED,
                                                                icon = Icons.Default.DirectionsCar,
                                                                bgTint = Color(0xFFFEF2F2),
                                                                iconTint = Color(0xFFDC2626)
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
                                                                statusBgColor = Color(0xFFDCFCE7),
                                                                statusTextColor = Color(0xFF16A34A),
                                                                kind = ActivityItemKind.SETTLEMENT_COMPLETED,
                                                                icon = Icons.Default.ConfirmationNumber,
                                                                bgTint = Color(0xFFF5F3FF),
                                                                iconTint = Color(0xFF7C3AED)
                                                            ),
                                                            ActivityActionItem(
                                                                id = "act_11",
                                                                title = "Netflix Ortak Üyelik",
                                                                subtitle = "Abonelik • Burak Demir • 15 Tem",
                                                                actorName = "Burak Demir",
                                                                actorInitials = "BD",
                                                                dateGroup = "TEMMUZ 2026",
                                                                time = "15 Tem",
                                                                amount = 65.00,
                                                                isPositiveFinancial = false,
                                                                statusBadge = "Fitleşildi",
                                                                statusBgColor = Color(0xFFDCFCE7),
                                                                statusTextColor = Color(0xFF16A34A),
                                                                kind = ActivityItemKind.SETTLEMENT_COMPLETED,
                                                                icon = Icons.Default.ConfirmationNumber,
                                                                bgTint = Color(0xFFEFF6FF),
                                                                iconTint = Color(0xFF2563EB)
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
    }

    // =========================================================================
    // 4. ACTION DETAIL MODAL (Clean Transaction Receipt Sheet)
    // =========================================================================
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

                // Action Buttons only when action is required
                when (item.kind) {
                    ActivityItemKind.INCOMING_NUDGE -> {
                        HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
                        Button(
                            onClick = {
                                item.isPendingActionHandled = true
                                selectedItemForDetail = null
                                onNavigateToSettleUp()
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
                            Text("FAST ile Hemen Öde & Fitleş", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
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
                                    activityList.remove(item)
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
                    else -> {
                        // Clean dismissible sheet with no redundant Kapat button
                    }
                }
            }
        }
    }
}

// =========================================================================
// 5. TRUE FINTECH TRANSACTION ROW (Apple Pay / Material 3 Standard)
// =========================================================================
@Composable
private fun FinTechActivityRow(
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

            // Status Indicator Corner Badge (Replaces AI slop text badges)
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

        // 3. Right Column: Clean Amount or Time (No AI slop text badges!)
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

