package com.ardabank.aradapay.presentation.settle

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ardabank.aradapay.domain.model.User
import com.ardabank.aradapay.presentation.common.BankAppChooserScreen
import com.ardabank.aradapay.presentation.common.BankAppChooserSheet
import com.ardabank.aradapay.presentation.components.bounceClick
import com.ardabank.aradapay.presentation.theme.PrimaryEmerald
import com.ardabank.aradapay.presentation.theme.PrimaryEmeraldContainer
import com.ardabank.aradapay.util.NotificationHelper

enum class SettlementItemType {
    DEBT,     // Borç
    CREDIT    // Alacak / Mahsup
}

private fun Double.ifZero(defaultValue: Double): Double = if (this <= 0.01) defaultValue else this

data class PendingDebtItem(
    val id: String,
    val title: String,
    val date: String,
    val category: String,
    val fullExpenseAmount: Double,
    val yourShare: Double,
    val type: SettlementItemType = SettlementItemType.DEBT,
    val note: String,
    val groupId: String? = null,
    val groupName: String? = null
)

data class DebtRecipient(
    val id: String,
    val name: String,
    val tag: String,
    val avatar: String,
    val bankName: String,
    val iban: String,
    val pendingItems: List<PendingDebtItem>
) {
    val totalDebt: Double
        get() = pendingItems.filter { it.type == SettlementItemType.DEBT }.sumOf { it.yourShare }

    val totalCredit: Double
        get() = pendingItems.filter { it.type == SettlementItemType.CREDIT }.sumOf { it.yourShare }

    val netOwedAmount: Double
        get() = (totalDebt - totalCredit).coerceAtLeast(0.0)

    fun getGroupOnlyDebt(targetGroupId: String): Double {
        val gDebts = pendingItems.filter { it.groupId == targetGroupId && it.type == SettlementItemType.DEBT }.sumOf { it.yourShare }
        val gCredits = pendingItems.filter { it.groupId == targetGroupId && it.type == SettlementItemType.CREDIT }.sumOf { it.yourShare }
        return (gDebts - gCredits).coerceAtLeast(0.0)
    }

    fun getExternalDebt(targetGroupId: String): Double {
        val extDebts = pendingItems.filter { it.groupId != targetGroupId && it.type == SettlementItemType.DEBT }.sumOf { it.yourShare }
        val extCredits = pendingItems.filter { it.groupId != targetGroupId && it.type == SettlementItemType.CREDIT }.sumOf { it.yourShare }
        return (extDebts - extCredits).coerceAtLeast(0.0)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettleUpScreen(
    creditorUser: User? = null,
    owedAmount: Double = 120.0,
    initialGroupName: String? = null,
    initialGroupId: String? = null,
    onConfirmSettlement: (amount: Double, note: String) -> Unit = { _, _ -> },
    onCancel: () -> Unit = {}
) {
    val context = LocalContext.current

    // Sadece Borcun Olduğu Kişiler ve Detayları
    val allRecipients = remember {
        mutableStateListOf(
            DebtRecipient(
                id = "3",
                name = "Mert Demir",
                tag = "Mert#9015",
                avatar = "MD",
                bankName = "Akbank",
                iban = "TR64 0006 2000 0000 3344 5566 77",
                pendingItems = listOf(
                    PendingDebtItem(
                        id = "d1",
                        title = "Sushi & Akşam Yemeği",
                        date = "24 Ağu, 19:45",
                        category = "Yemek",
                        fullExpenseAmount = 240.0,
                        yourShare = 120.0,
                        type = SettlementItemType.DEBT,
                        note = "Mert ödedi • Akşam yemeği payı",
                        groupId = "grp_1",
                        groupName = "Kadıköy Evi"
                    )
                )
            ),
            DebtRecipient(
                id = "5",
                name = "Burak Öztürk",
                tag = "Burak#6108",
                avatar = "BÖ",
                bankName = "QNB Finansbank",
                iban = "TR64 0006 2000 0000 5566 7788 99",
                pendingItems = listOf(
                    PendingDebtItem(
                        id = "d3",
                        title = "Havalimanı Taksi Payı",
                        date = "19 Ağu 2026",
                        category = "Ulaşım",
                        fullExpenseAmount = 460.0,
                        yourShare = 230.0,
                        type = SettlementItemType.DEBT,
                        note = "Burak ödedi • Sabiha Gökçen taksi",
                        groupId = "grp_2",
                        groupName = "Bodrum Tatili 2026"
                    ),
                    PendingDebtItem(
                        id = "c2",
                        title = "Otel & Konaklama Payı",
                        date = "17 Ağu 2026",
                        category = "Tatil",
                        fullExpenseAmount = 300.0,
                        yourShare = 80.0,
                        type = SettlementItemType.CREDIT,
                        note = "Sen ödedin • Mahsup edilecek alacağın",
                        groupId = "grp_2",
                        groupName = "Bodrum Tatili 2026"
                    ),
                    PendingDebtItem(
                        id = "d3_ext",
                        title = "Bireysel Sinema Bileti",
                        date = "15 Ağu 2026",
                        category = "Eğlence",
                        fullExpenseAmount = 180.0,
                        yourShare = 90.0,
                        type = SettlementItemType.DEBT,
                        note = "Burak ödedi • Grup dışı bireysel borç",
                        groupId = null,
                        groupName = null
                    )
                )
            ),
            DebtRecipient(
                id = "7",
                name = "Caner Erkin",
                tag = "Caner#1903",
                avatar = "CE",
                bankName = "İş Bankası",
                iban = "TR64 0006 2000 0000 7788 9900 11",
                pendingItems = listOf(
                    PendingDebtItem(
                        id = "d4",
                        title = "Halı Saha & Maç İçecekleri",
                        date = "18 Ağu 2026",
                        category = "Spor",
                        fullExpenseAmount = 400.0,
                        yourShare = 100.0,
                        type = SettlementItemType.DEBT,
                        note = "Caner ödedi • Maç ücreti",
                        groupId = "grp_1",
                        groupName = "Kadıköy Evi"
                    )
                )
            ),
            DebtRecipient(
                id = "1",
                name = "Ahmet Yılmaz",
                tag = "Ahmet#7821",
                avatar = "AY",
                bankName = "Garanti BBVA",
                iban = "TR64 0006 2000 0000 1122 3344 55",
                pendingItems = listOf(
                    PendingDebtItem(
                        id = "d5",
                        title = "Akşam Yemeği",
                        date = "Dün",
                        category = "Yemek",
                        fullExpenseAmount = 200.0,
                        yourShare = 100.0,
                        type = SettlementItemType.DEBT,
                        note = "Ahmet ödedi",
                        groupId = "grp_1",
                        groupName = "Kadıköy Evi"
                    ),
                    PendingDebtItem(
                        id = "d5_ext",
                        title = "Bireysel Market Harcaması",
                        date = "3 gün önce",
                        category = "Market",
                        fullExpenseAmount = 140.0,
                        yourShare = 70.0,
                        type = SettlementItemType.DEBT,
                        note = "Ahmet ödedi • Grup dışı harcama",
                        groupId = null,
                        groupName = null
                    )
                )
            ),
            DebtRecipient(
                id = "4",
                name = "Elif Şahin",
                tag = "Elif#4420",
                avatar = "EŞ",
                bankName = "İş Bankası",
                iban = "TR64 0006 2000 0000 4455 6677 88",
                pendingItems = listOf(
                    PendingDebtItem(
                        id = "d_elif",
                        title = "Müze Giriş Biletleri",
                        date = "16 Ağu 2026",
                        category = "Kültür",
                        fullExpenseAmount = 360.0,
                        yourShare = 90.0,
                        type = SettlementItemType.DEBT,
                        note = "Elif ödedi • Bodrum Kalesi payı",
                        groupId = "grp_2",
                        groupName = "Bodrum Tatili 2026"
                    )
                )
            ),
            DebtRecipient(
                id = "6",
                name = "Selin Aydın",
                tag = "Selin#2839",
                avatar = "SA",
                bankName = "Garanti BBVA",
                iban = "TR64 0006 2000 0000 6677 8899 00",
                pendingItems = listOf(
                    PendingDebtItem(
                        id = "d_selin",
                        title = "Beach & Şezlong Ücreti",
                        date = "18 Ağu 2026",
                        category = "Eğlence",
                        fullExpenseAmount = 400.0,
                        yourShare = 100.0,
                        type = SettlementItemType.DEBT,
                        note = "Selin ödedi • Plaj payı",
                        groupId = "grp_2",
                        groupName = "Bodrum Tatili 2026"
                    )
                )
            ),
            DebtRecipient(
                id = "2",
                name = "Zeynep Kaya",
                tag = "Zeynep#3412",
                avatar = "ZK",
                bankName = "Yapı Kredi",
                iban = "TR64 0006 2000 0000 2233 4455 66",
                pendingItems = listOf(
                    PendingDebtItem(
                        id = "d6",
                        title = "Kahve & Tatlı Payı",
                        date = "Bugün",
                        category = "Yemek",
                        fullExpenseAmount = 120.0,
                        yourShare = 60.0,
                        type = SettlementItemType.DEBT,
                        note = "Zeynep ödedi",
                        groupId = "grp_1",
                        groupName = "Kadıköy Evi"
                    )
                )
            )
        )
    }

    // Ödeme ekranında grup içinden gelindiyse SADECE o grubun üyeleri listelenir
    val eligibleDebtRecipients = remember(allRecipients.toList(), initialGroupId) {
        if (initialGroupId != null) {
            allRecipients.filter { recipient ->
                recipient.pendingItems.any { it.groupId == initialGroupId }
            }
        } else {
            allRecipients.filter { it.netOwedAmount > 0 }
        }
    }

    // Tek kişi seçimi (Single selection)
    var selectedRecipient by remember(eligibleDebtRecipients) {
        mutableStateOf<DebtRecipient?>(
            if (creditorUser != null) {
                DebtRecipient(
                    id = creditorUser.id,
                    name = creditorUser.fullName.ifEmpty { creditorUser.username },
                    tag = creditorUser.tag ?: "Kullanıcı#1000",
                    avatar = creditorUser.fullName.take(2).uppercase(),
                    bankName = "ArdaBank (FAST)",
                    iban = creditorUser.iban ?: "TR64 0006 2000 0000 1122 3344 55",
                    pendingItems = listOf(
                        PendingDebtItem(
                            id = "cust_1",
                            title = "Ortak Harcama Payı",
                            date = "Bugün",
                            category = "Genel",
                            fullExpenseAmount = owedAmount * 2,
                            yourShare = owedAmount,
                            type = SettlementItemType.DEBT,
                            note = "Ödenmemiş borç bakiyesi",
                            groupId = initialGroupId,
                            groupName = initialGroupName
                        )
                    )
                )
            } else {
                eligibleDebtRecipients.firstOrNull()
            }
        )
    }

    var includeExternalDebts by remember { mutableStateOf(false) }

    val groupOnlyDebt = remember(selectedRecipient, initialGroupId) {
        if (selectedRecipient != null && initialGroupId != null) {
            selectedRecipient!!.getGroupOnlyDebt(initialGroupId)
        } else if (selectedRecipient != null) {
            selectedRecipient!!.netOwedAmount
        } else {
            0.0
        }
    }

    val externalDebt = remember(selectedRecipient, initialGroupId) {
        if (selectedRecipient != null && initialGroupId != null) {
            selectedRecipient!!.getExternalDebt(initialGroupId)
        } else {
            0.0
        }
    }

    val netPayableAmount = remember(selectedRecipient, initialGroupId, includeExternalDebts) {
        if (selectedRecipient != null && initialGroupId != null) {
            if (includeExternalDebts) {
                selectedRecipient!!.netOwedAmount
            } else {
                groupOnlyDebt.ifZero(selectedRecipient!!.netOwedAmount)
            }
        } else {
            selectedRecipient?.netOwedAmount ?: 0.0
        }
    }

    var amountText by remember(selectedRecipient, netPayableAmount) {
        mutableStateOf(String.format(java.util.Locale.US, "%.2f", netPayableAmount))
    }
    var note by remember { mutableStateOf("") }
    var isRecipientDropdownOpen by remember { mutableStateOf(false) }
    var recipientSearchQuery by remember { mutableStateOf("") }
    var showBankChooserSheet by remember { mutableStateOf(false) }

    val amountValue = amountText.replace(",", ".").toDoubleOrNull() ?: 0.0

    // BANK APP CHOOSER FULL-SCREEN (Artık Modal Değil, Bağımsız Tam Sayfa Ekran)
    if (showBankChooserSheet && selectedRecipient != null) {
        BankAppChooserScreen(
            recipientName = selectedRecipient!!.name,
            recipientIban = selectedRecipient!!.iban,
            amount = amountValue,
            note = note.ifBlank { "AradaPay Fitleşme - ${selectedRecipient!!.name}" },
            onDismiss = { showBankChooserSheet = false },
            onBankSelected = { bank ->
                NotificationHelper.showSystemNotification(
                    context = context,
                    title = "FAST Transferi Başlatıldı",
                    message = "${selectedRecipient!!.name} kişisine ${String.format(java.util.Locale.US, "%.2f", amountValue)} ₺ tutarında ${bank.shortName} ile transfer başlatıldı."
                )
                Toast.makeText(context, "${bank.shortName} uygulamasına yönlendiriliyor...", Toast.LENGTH_SHORT).show()
                onConfirmSettlement(amountValue, note)
            }
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // TOP CONTENT
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // 1. TOP BAR (Standard Header & Back Navigation)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledTonalIconButton(
                    onClick = onCancel,
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
                    text = "Fitleş & Hesap Kapat",
                    color = Color(0xFF0F172A),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.size(40.dp))
            }
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

            // 2. KATILIMCI SEÇİM BARI (Tek Kişi Seçimi & Borcun Olan Kişiler Listesi)
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Seninle ve:",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A),
                        fontSize = 15.sp
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Tek Seçili Katılımcı Kapsül Çipi
                    if (selectedRecipient != null) {
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
                                    shape = RoundedCornerShape(6.dp),
                                    color = PrimaryEmerald,
                                    modifier = Modifier.size(22.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = selectedRecipient!!.avatar,
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(6.dp))

                                Text(
                                    text = selectedRecipient!!.name.split(" ").first(),
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
                                            selectedRecipient = null
                                            amountText = "0,00"
                                            isRecipientDropdownOpen = true
                                        }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    BasicTextField(
                        value = recipientSearchQuery,
                        onValueChange = {
                            recipientSearchQuery = it
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
                                if (recipientSearchQuery.isEmpty()) {
                                    Text(
                                        text = if (selectedRecipient == null) "Borcunu ödeyeceğin kişiyi seç..." else "Kişi ara...",
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

                // ALTA DOĞRU GENİŞLEYEN BORCUN OLAN KİŞİLER LİSTESİ (AnimatedVisibility)
                AnimatedVisibility(
                    visible = isRecipientDropdownOpen,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 6.dp)
                    ) {
                        val listHeader = if (!initialGroupName.isNullOrBlank()) {
                            "${initialGroupName.uppercase()} KATILIMCILARI (${eligibleDebtRecipients.size})"
                        } else {
                            "BORCUN OLAN KİŞİLER (${eligibleDebtRecipients.size})"
                        }

                        Text(
                            text = listHeader,
                            color = Color(0xFF64748B),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )

                        val matchingDebts = eligibleDebtRecipients.filter {
                            recipientSearchQuery.isBlank() ||
                            it.name.contains(recipientSearchQuery, ignoreCase = true) ||
                            it.tag.contains(recipientSearchQuery, ignoreCase = true)
                        }

                        if (matchingDebts.isEmpty()) {
                            Text(
                                text = if (initialGroupId != null) "Bu grupta seçilebilecek başka üye bulunmuyor." else "Ödenecek borcunuz olan kimse bulunmuyor.",
                                fontSize = 13.sp,
                                color = Color(0xFF94A3B8),
                                modifier = Modifier.padding(vertical = 12.dp)
                            )
                        } else {
                            matchingDebts.forEachIndexed { index, recipient ->
                                val isSelected = selectedRecipient?.id == recipient.id

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .bounceClick {
                                            // Sadece tek kişi seçimi (Single selection)
                                            selectedRecipient = recipient
                                            amountText = String.format(java.util.Locale.US, "%.2f", recipient.netOwedAmount)
                                            isRecipientDropdownOpen = false
                                            recipientSearchQuery = ""
                                        }
                                        .padding(vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = if (isSelected) PrimaryEmeraldContainer else Color(0xFFF1F5F9),
                                            modifier = Modifier.size(38.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = recipient.avatar,
                                                    color = if (isSelected) PrimaryEmerald else Color(0xFF0F172A),
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column {
                                            Text(
                                                text = recipient.name,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                                fontSize = 14.sp,
                                                color = if (isSelected) PrimaryEmerald else Color(0xFF0F172A)
                                            )
                                            Text(
                                                text = "${recipient.tag} • ${recipient.bankName} • Borç: ${String.format(java.util.Locale.US, "%.2f", recipient.netOwedAmount)} ₺",
                                                color = Color(0xFF64748B),
                                                fontSize = 11.sp
                                            )
                                        }
                                    }

                                    if (isSelected) {
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

                                if (index < matchingDebts.lastIndex) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(start = 50.dp),
                                        color = Color(0xFFF8FAFC),
                                        thickness = 1.dp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            if (!isRecipientDropdownOpen) {
                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

            // 3. HERO SETTLEMENT AMOUNT
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "ÖDENECEK FİTLEŞME TUTARI",
                    color = Color(0xFF64748B),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BasicTextField(
                        value = amountText,
                        onValueChange = { input ->
                            val clean = input.replace(",", ".")
                            if (clean.isEmpty() || clean.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                                amountText = input
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        textStyle = TextStyle(
                            fontSize = 38.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PrimaryEmerald
                        ),
                        cursorBrush = SolidColor(PrimaryEmerald),
                        decorationBox = { innerTextField ->
                            Box(contentAlignment = Alignment.CenterStart) {
                                if (amountText.isEmpty()) {
                                    Text(
                                        text = "0,00",
                                        fontSize = 38.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFFCBD5E1)
                                    )
                                }
                                innerTextField()
                            }
                        },
                        modifier = Modifier
                            .width(IntrinsicSize.Min)
                            .defaultMinSize(minWidth = if (amountText.isEmpty()) 76.dp else 24.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "₺",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryEmerald
                    )
                }

                // Quick chips: Tamamı / Yarısı
                if (netPayableAmount > 0) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFF1F5F9),
                            modifier = Modifier.bounceClick {
                                amountText = String.format(java.util.Locale.US, "%.2f", netPayableAmount)
                            }
                        ) {
                            Text(
                                text = "Tamamı (${String.format(java.util.Locale.US, "%.2f", netPayableAmount)} ₺)",
                                color = PrimaryEmerald,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFF1F5F9),
                            modifier = Modifier.bounceClick {
                                amountText = String.format(java.util.Locale.US, "%.2f", netPayableAmount / 2.0)
                            }
                        ) {
                            Text(
                                text = "Yarısı (${String.format(java.util.Locale.US, "%.2f", netPayableAmount / 2.0)} ₺)",
                                color = Color(0xFF475569),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

            // 4. GRUP DIŞI BORÇ DAHİL ETME (Düz, Kart İçi Kartsız Akış)
            if (initialGroupId != null && externalDebt > 0) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Grup dışı borçları dahil et",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = if (includeExternalDebts) {
                                "+${String.format(java.util.Locale.US, "%.2f", externalDebt)} ₺ bireysel borç eklendi"
                            } else {
                                "${selectedRecipient?.name?.split(" ")?.first() ?: "Arkadaşının"} +${String.format(java.util.Locale.US, "%.2f", externalDebt)} ₺ bireysel borcu var"
                            },
                            fontSize = 12.sp,
                            color = if (includeExternalDebts) PrimaryEmerald else Color(0xFF64748B)
                        )
                    }

                    androidx.compose.material3.Switch(
                        checked = includeExternalDebts,
                        onCheckedChange = { includeExternalDebts = it },
                        colors = androidx.compose.material3.SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = PrimaryEmerald,
                            checkedBorderColor = PrimaryEmerald,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color(0xFFCBD5E1),
                            uncheckedBorderColor = Color(0xFF94A3B8)
                        )
                    )
                }
                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
            }

            // 5. FAST IBAN BİLGİSİ (Düz, Sade Satır)
            if (selectedRecipient != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "ALICI IBAN (${selectedRecipient!!.bankName})",
                            color = Color(0xFF64748B),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = selectedRecipient!!.iban,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF0F172A)
                        )
                    }

                    FilledTonalIconButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("IBAN", selectedRecipient!!.iban))
                            Toast.makeText(context, "IBAN panoya kopyalandı", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = Color(0xFFF1F5F9)
                        ),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Kopyala",
                            tint = Color(0xFF0F172A),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
            }

            // 6. NOT ALANI
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "AÇIKLAMA & NOT",
                    color = Color(0xFF64748B),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )

                BasicTextField(
                    value = note,
                    onValueChange = { note = it },
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF0F172A)
                    ),
                    cursorBrush = SolidColor(PrimaryEmerald),
                    decorationBox = { innerTextField ->
                        Box(contentAlignment = Alignment.CenterStart) {
                            if (note.isEmpty()) {
                                Text(
                                    text = "Örn: AradaPay FAST ile hesap kapatıldı",
                                    fontSize = 14.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                            innerTextField()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
            }
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // BOTTOM FIXED ACTIONS (Side-by-side FinTech Action Bar)
        if (!isRecipientDropdownOpen) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
            val isActionEnabled = selectedRecipient != null && amountValue > 0

            // 1. SECONDARY: ELDEN / NAKİT KAPAT
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (isActionEnabled) Color(0xFF1E293B) else Color(0xFFE2E8F0),
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .then(
                        if (isActionEnabled) {
                            Modifier.bounceClick {
                                onConfirmSettlement(amountValue, note.ifBlank { "AradaPay ile hesap kapatıldı" })
                                Toast.makeText(context, "${selectedRecipient!!.name} ile hesap fitleşildi", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Modifier.clickable {
                                Toast.makeText(context, "Lütfen fitleşilecek kişiyi seçiniz", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = if (isActionEnabled) Color.White else Color(0xFF94A3B8),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Elden / Nakit",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = if (isActionEnabled) Color.White else Color(0xFF94A3B8)
                    )
                }
            }

            // 2. PRIMARY: FAST İLE ÖDE
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (isActionEnabled) PrimaryEmerald else Color(0xFFE2E8F0),
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .then(
                        if (isActionEnabled) {
                            Modifier.bounceClick { showBankChooserSheet = true }
                        } else {
                            Modifier.clickable {
                                if (selectedRecipient == null) {
                                    Toast.makeText(context, "Lütfen fitleşilecek kişiyi seçiniz", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Lütfen geçerli bir tutar girin", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = if (isActionEnabled) Color.White else Color(0xFF94A3B8),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "FAST ile Öde",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = if (isActionEnabled) Color.White else Color(0xFF94A3B8)
                    )
                }
            }
        }
    }
}
}
