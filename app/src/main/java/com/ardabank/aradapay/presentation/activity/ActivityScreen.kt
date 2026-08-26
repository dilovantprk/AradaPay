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
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.BottomSheetDefaults
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
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    RECEIVABLES("Alacaklar (+₺)"),
    PAYABLES("Borçlar (-₺)"),
    SETTLEMENTS("Fitleşmeler"),
    REQUESTS("İstek & Onay")
}

enum class ActivityItemKind {
    EXPENSE_ADDED_YOU_GET_BACK, // Harcama eklendi, alacağın var
    EXPENSE_ADDED_YOU_OWE,      // Harcama eklendi, borcun var
    INCOMING_APPROVAL,          // Gelen Onay: Başkası girdi, senin onayını bekliyor
    INCOMING_NUDGE,             // Gelen Dürtme / FAST Ödeme Hatırlatması
    FRIEND_REQUEST,             // Arkadaşlık İsteği
    COMMENT_NOTE_ADDED,         // Harcamaya not / yorum eklendi
    GROUP_CREATED,              // Grup oluşturuldu
    SETTLEMENT_COMPLETED,       // Ödeme / Fitleşme yapıldı
    SMART_CROSS_SETTLEMENT      // 3'lü Akıllı Çapraz Fitleşme
}

data class ActivityActionItem(
    val id: String,
    val title: String,               // FinTech Merchant/Title: "Migros Market", "Shell Yakıt", "Kahve & Tatlı"
    val subtitle: String,            // Subtitle: "4 kişi eşit • Sen ödedin", "Burak Öztürk • Onay bekliyor"
    val actorName: String,
    val actorInitials: String,
    val dateGroup: String,
    val time: String,
    val amount: Double = 0.0,
    val isPositiveFinancial: Boolean? = null,
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
    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedItemForDetail by remember { mutableStateOf<ActivityActionItem?>(null) }

    // FinTech Transaction Data (Standard Banking Format)
    val activityList = remember {
        mutableStateListOf(
            ActivityActionItem(
                id = "act_nudge_1",
                title = "Kahve & Tatlı",
                subtitle = "Zeynep Kaya • Ödeme hatırlatması",
                actorName = "Zeynep Kaya",
                actorInitials = "ZK",
                dateGroup = "BUGÜN",
                time = "18:15",
                amount = 60.0,
                isPositiveFinancial = false,
                kind = ActivityItemKind.INCOMING_NUDGE,
                icon = Icons.Default.NotificationsActive,
                bgTint = Color(0xFFF1F5F9),
                iconTint = Color(0xFF475569),
                otherPartyName = "Zeynep Kaya",
                iban = "TR64 0006 2000 0000 2233 4455 66"
            ),
            ActivityActionItem(
                id = "act_friend_1",
                title = "Caner Erkin",
                subtitle = "@caner#1903 • Arkadaşlık isteği",
                actorName = "Caner Erkin",
                actorInitials = "CE",
                dateGroup = "BUGÜN",
                time = "17:40",
                amount = 0.0,
                isPositiveFinancial = null,
                kind = ActivityItemKind.FRIEND_REQUEST,
                icon = Icons.Default.PersonAdd,
                bgTint = Color(0xFFF1F5F9),
                iconTint = Color(0xFF475569),
                otherPartyName = "Caner Erkin"
            ),
            ActivityActionItem(
                id = "act_exp_1",
                title = "Migros Market",
                subtitle = "4 kişi eşit pay • Sen ödedin",
                actorName = "Sen",
                actorInitials = "Sen",
                dateGroup = "BUGÜN",
                time = "16:20",
                amount = 254.0,
                isPositiveFinancial = true,
                kind = ActivityItemKind.EXPENSE_ADDED_YOU_GET_BACK,
                icon = Icons.Default.ShoppingCart,
                bgTint = PrimaryEmeraldContainer,
                iconTint = PrimaryEmerald,
                otherPartyName = "Ahmet Yılmaz, Zeynep Kaya",
                participants = listOf("Mehmet Dilovan (Sen)", "Ahmet Yılmaz", "Zeynep Kaya", "Mert Demir")
            ),
            ActivityActionItem(
                id = "act_approval_1",
                title = "Shell Yakıt",
                subtitle = "Burak Öztürk girdi • Onayını bekliyor",
                actorName = "Burak Öztürk",
                actorInitials = "BÖ",
                dateGroup = "BUGÜN",
                time = "14:10",
                amount = 175.0,
                isPositiveFinancial = false,
                kind = ActivityItemKind.INCOMING_APPROVAL,
                icon = Icons.Default.LocalGasStation,
                bgTint = Color(0xFFF1F5F9),
                iconTint = Color(0xFF475569),
                otherPartyName = "Burak Öztürk",
                participants = listOf("Burak Öztürk (Ödeyen)", "Mehmet Dilovan (Sen)"),
                iban = "TR64 0006 2000 0000 5566 7788 99"
            ),

            ActivityActionItem(
                id = "act_group_1",
                title = "Kaş Tatili 2026",
                subtitle = "Yeni Grup Kuruldu • 4 Katılımcı",
                actorName = "Sen",
                actorInitials = "Sen",
                dateGroup = "DÜN",
                time = "21:15",
                amount = 0.0,
                isPositiveFinancial = null,
                kind = ActivityItemKind.GROUP_CREATED,
                icon = Icons.Default.Group,
                bgTint = Color(0xFFF1F5F9),
                iconTint = Color(0xFF0F172A),
                otherPartyName = "Grup Üyeleri"
            ),
            ActivityActionItem(
                id = "act_exp_2",
                title = "Akşam Yemeği",
                subtitle = "Ahmet Yılmaz ödedi • 2 kişi bölüşüldü",
                actorName = "Ahmet Yılmaz",
                actorInitials = "AY",
                dateGroup = "DÜN",
                time = "19:40",
                amount = 100.0,
                isPositiveFinancial = false,
                kind = ActivityItemKind.EXPENSE_ADDED_YOU_OWE,
                icon = Icons.Default.Restaurant,
                bgTint = Color(0xFFF1F5F9),
                iconTint = Color(0xFF475569),
                otherPartyName = "Ahmet Yılmaz",
                participants = listOf("Ahmet Yılmaz (Ödeyen)", "Mehmet Dilovan (Sen)")
            ),
            ActivityActionItem(
                id = "act_settle_1",
                title = "FAST Fitleşme",
                subtitle = "Elif Şahin • Transfer tamamlandı",
                actorName = "Elif Şahin",
                actorInitials = "EŞ",
                dateGroup = "DÜN",
                time = "16:05",
                amount = 180.0,
                isPositiveFinancial = true,
                kind = ActivityItemKind.SETTLEMENT_COMPLETED,
                icon = Icons.Default.Payment,
                bgTint = PrimaryEmeraldContainer,
                iconTint = PrimaryEmerald,
                otherPartyName = "Elif Şahin"
            ),
            ActivityActionItem(
                id = "act_cross_1",
                title = "Akıllı Çapraz Fitleşme",
                subtitle = "Ahmet ➔ Burak ➔ Sen (0 Komisyon)",
                actorName = "AradaPay",
                actorInitials = "AP",
                dateGroup = "BU HAFTA",
                time = "22 Ağu",
                amount = 150.0,
                isPositiveFinancial = true,
                kind = ActivityItemKind.SMART_CROSS_SETTLEMENT,
                icon = Icons.Default.AccountTree,
                bgTint = PrimaryEmeraldContainer,
                iconTint = PrimaryEmerald,
                otherPartyName = "Ahmet Yılmaz, Burak Öztürk",
                participants = listOf("Ahmet Yılmaz", "Burak Öztürk", "Mehmet Dilovan (Sen)")
            )
        )
    }

    // Filter Logic
    val filteredList = remember(activityList.toList(), searchQuery) {
        activityList.filter { item ->
            searchQuery.isBlank() ||
                    item.title.contains(searchQuery, ignoreCase = true) ||
                    item.subtitle.contains(searchQuery, ignoreCase = true) ||
                    item.actorName.contains(searchQuery, ignoreCase = true)
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
            // 1. TOP APP BAR (Search, History & Large Title)
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
                    Text(
                        text = "Hareketler",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A),
                        fontSize = 28.sp
                    )

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

                        FilledTonalIconButton(
                            onClick = onNavigateToHistory,
                            shape = RoundedCornerShape(14.dp),
                            colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = Color(0xFFF1F5F9)),
                            modifier = Modifier.size(40.dp).bounceClick(onClick = onNavigateToHistory)
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "Geçmiş",
                                tint = Color(0xFF0F172A),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

            // =========================================================================
            // 2. ACTIVITY FEED LIST (Flat Stream)
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
                                text = "İşlem Bulunamadı",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color(0xFF0F172A)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Seçili filtreye uygun işlem veya bildirim bulunmuyor.",
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
                                color = Color(0xFF64748B),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp,
                                modifier = Modifier.padding(start = 20.dp, top = 16.dp, bottom = 8.dp)
                            )
                            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
                        }

                        itemsIndexed(items) { index, item ->
                            FinTechActivityRow(
                                item = item,
                                isLocked = isLocked,
                                onItemClick = { selectedItemForDetail = item }
                            )
                            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                        FilledTonalButton(
                            onClick = onNavigateToHistory,
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = Color(0xFFF1F5F9),
                                contentColor = PrimaryEmerald
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .bounceClick(onClick = onNavigateToHistory)
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                tint = PrimaryEmerald,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Tüm Finansal Geçmişi Gör",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = PrimaryEmerald
                            )
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
                        val amtColor = if (item.isPositiveFinancial == true) PrimaryEmerald else Color(0xFFE11D48)
                        Text(
                            text = "${if (item.isPositiveFinancial == true) "+" else "-"}${String.format(java.util.Locale.US, "%.2f", item.amount)} ₺",
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

                if (item.iban.isNotBlank() && item.amount > 0) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("FAST IBAN:", color = Color(0xFF64748B), fontSize = 14.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(item.iban.take(15) + "...", color = Color(0xFF0F172A), fontWeight = FontWeight.Medium, fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            FilledTonalIconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("IBAN", item.iban))
                                    Toast.makeText(context, "IBAN kopyalandı", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = Color(0xFFF1F5F9)),
                                modifier = Modifier.size(28.dp).bounceClick {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("IBAN", item.iban))
                                    Toast.makeText(context, "IBAN kopyalandı", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Kopyala", tint = Color(0xFF0F172A), modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }

                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                // Bottom Actions
                when (item.kind) {
                    ActivityItemKind.INCOMING_NUDGE -> {
                        Button(
                            onClick = {
                                item.isPendingActionHandled = true
                                selectedItemForDetail = null
                                Toast.makeText(context, "${item.actorName} kişisine ${String.format(java.util.Locale.US, "%.2f", item.amount)} ₺ FAST ile ödendi", Toast.LENGTH_SHORT).show()
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
                                Text("Payı Onayla", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                            }
                        }
                    }
                    ActivityItemKind.FRIEND_REQUEST -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            FilledTonalButton(
                                onClick = {
                                    activityList.remove(item)
                                    selectedItemForDetail = null
                                    Toast.makeText(context, "İstek silindi", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color(0xFFF1F5F9), contentColor = Color(0xFF64748B)),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                                    .bounceClick { }
                            ) {
                                Text("Sil", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            }

                            Button(
                                onClick = {
                                    item.isPendingActionHandled = true
                                    selectedItemForDetail = null
                                    Toast.makeText(context, "${item.actorName} arkadaş olarak eklendi", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                                modifier = Modifier
                                    .weight(1.5f)
                                    .height(50.dp)
                                    .bounceClick { }
                            ) {
                                Text("Kabul Et", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                            }
                        }
                    }
                    else -> {
                        Button(
                            onClick = { selectedItemForDetail = null },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .bounceClick { selectedItemForDetail = null }
                        ) {
                            Text("Tamam", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// 5. TRUE FINTECH TRANSACTION ROW (Apple Pay / iOS Wallet Standard)
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
        // 1. Transaction Brand / Category Pastel Squircle Badge
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = item.bgTint,
            modifier = Modifier.size(42.dp)
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
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // 2. Left Column: Merchant / Title & Clean Subtitle
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF0F172A),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = item.subtitle,
                fontSize = 12.sp,
                color = Color(0xFF64748B),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // 3. Right Column: Amount & Status
        Column(horizontalAlignment = Alignment.End) {
            if (item.amount > 0) {
                val amountColor = if (item.isPositiveFinancial == true) PrimaryEmerald else Color(0xFF0F172A)
                val amountPrefix = if (item.isPositiveFinancial == true) "+" else ""

                if (isLocked) {
                    MaskedFinancialText(
                        amount = item.amount,
                        isLocked = true,
                        color = amountColor,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    )
                } else {
                    Text(
                        text = "$amountPrefix${String.format(java.util.Locale.US, "%.2f", item.amount)} ₺",
                        color = amountColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(1.dp))
                Text(
                    text = item.time,
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                )
            } else {
                Text(
                    text = item.time,
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp
                )
            }
        }
    }
}
