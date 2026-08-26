package com.ardabank.aradapay.presentation.activity

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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ardabank.aradapay.presentation.common.MaskedFinancialText
import com.ardabank.aradapay.presentation.components.bounceClick
import com.ardabank.aradapay.presentation.receipt.AradaPayReceipt
import com.ardabank.aradapay.presentation.receipt.AradaPayReceiptModalSheet
import com.ardabank.aradapay.presentation.receipt.ReceiptParticipant
import com.ardabank.aradapay.presentation.receipt.ReceiptType
import com.ardabank.aradapay.presentation.theme.PrimaryEmerald
import com.ardabank.aradapay.presentation.theme.PrimaryEmeraldContainer

data class StatementItem(
    val id: String,
    val title: String,
    val dateGroup: String,
    val time: String,
    val categorySubtitle: String,
    val amount: Double,
    val isIncoming: Boolean,
    val isPending: Boolean = false,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val payerName: String = "Mehmet Dilovan",
    val participants: List<String> = listOf("Mehmet Dilovan (Sen)", "Ahmet Yılmaz", "Zeynep Kaya"),
    val alreadyPaidAmount: Double = 0.0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionHistoryScreen(
    isLocked: Boolean = false,
    onBackClick: () -> Unit = {}
) {
    var selectedFilterIndex by remember { mutableStateOf(0) }
    val filters = listOf("Tümü", "Harcamalar", "Tahsilatlar", "Abonelikler")

    var activeReceiptForModal by remember { mutableStateOf<AradaPayReceipt?>(null) }

    val completedTransactions = remember {
        listOf(
            StatementItem("1", "Starbucks Coffee", "BUGÜN", "14:30", "Yemek • 3 kişi bölüşüldü", 120.0, false, false, Icons.Default.Fastfood, "Mehmet Dilovan", listOf("Mehmet Dilovan (Sen)", "Ahmet Yılmaz", "Zeynep Kaya"), 120.0),
            StatementItem("2", "Ahmet Yılmaz", "BUGÜN", "11:15", "Transfer • FAST ile Hesap Kapatıldı", 350.0, true, false, Icons.Default.Payment, "Ahmet Yılmaz", listOf("Ahmet Yılmaz", "Mehmet Dilovan (Sen)"), 350.0),
            StatementItem("3", "Migros Sanal Market", "DÜN", "19:40", "Market • Ortak Ev Harcaması", 540.0, false, false, Icons.Default.ShoppingCart, "Mehmet Dilovan", listOf("Mehmet Dilovan (Sen)", "Caner Erkin", "Mert Demir"), 540.0),
            StatementItem("4", "Netflix & Spotify", "DÜN", "09:00", "Abonelik • Aylık Paylaşım", 65.0, false, false, Icons.Default.Tv, "Caner Erkin", listOf("Caner Erkin (Ödeyen)", "Mehmet Dilovan (Sen)"), 65.0),
            StatementItem("5", "Shell Akaryakıt", "BU HAFTA", "20 Ağu", "Ulaşım • Yol Masrafı", 280.0, false, false, Icons.Default.LocalGasStation, "Mehmet Dilovan", listOf("Mehmet Dilovan (Sen)", "Ahmet Yılmaz"), 280.0),
            StatementItem("6", "Caner Erkin", "BU HAFTA", "18 Ağu", "Transfer • FAST ile Ödendi", 185.0, true, false, Icons.Default.Payment, "Caner Erkin", listOf("Caner Erkin", "Mehmet Dilovan (Sen)"), 185.0),
            StatementItem("7", "Kahve Dünyası", "GEÇMİŞ", "15 Ağu", "Yemek • 2 kişi fitleşildi", 95.0, false, false, Icons.Default.Fastfood, "Mehmet Dilovan", listOf("Mehmet Dilovan (Sen)", "Zeynep Kaya"), 95.0)
        )
    }

    val filteredList = when (selectedFilterIndex) {
        1 -> completedTransactions.filter { !it.isIncoming && !it.categorySubtitle.contains("Abonelik") }
        2 -> completedTransactions.filter { it.isIncoming }
        3 -> completedTransactions.filter { it.categorySubtitle.contains("Abonelik") }
        else -> completedTransactions
    }

    val groupedItems = filteredList.groupBy { it.dateGroup }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentPadding = PaddingValues(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        // 1. TOP BAR
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledTonalIconButton(
                    onClick = onBackClick,
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = Color(0xFFF1F5F9)
                    ),
                    modifier = Modifier.size(40.dp).bounceClick(onClick = onBackClick)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Geri",
                        tint = Color(0xFF0F172A),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = "Geçmiş İşlemler",
                    color = Color(0xFF0F172A),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.size(40.dp))
            }
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
        }

        // 2. HERO: MONTHLY FLOW OVERVIEW
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "BÖLÜŞÜLEN PAY",
                        color = Color(0xFF64748B),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    MaskedFinancialText(
                        amount = 1100.0,
                        isLocked = isLocked,
                        color = Color(0xFF0F172A),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .height(36.dp)
                        .width(1.dp)
                        .background(Color(0xFFF1F5F9))
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 20.dp)
                ) {
                    Text(
                        text = "TAHSİL EDİLEN",
                        color = Color(0xFF64748B),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    MaskedFinancialText(
                        amount = 535.0,
                        isLocked = isLocked,
                        color = PrimaryEmerald,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    )
                }
            }
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
        }

        // 3. FILTER PILLS
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(filters) { index, filter ->
                    val isSelected = selectedFilterIndex == index
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) Color(0xFF0F172A) else Color(0xFFF1F5F9),
                        modifier = Modifier.bounceClick { selectedFilterIndex = index }
                    ) {
                        Text(
                            text = filter,
                            color = if (isSelected) Color.White else Color(0xFF64748B),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
        }

        // 4. TRANSACTION ROWS
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

            itemsIndexed(items) { _, item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .bounceClick {
                            activeReceiptForModal = AradaPayReceipt(
                                receiptId = "RCP-${item.id}-2026",
                                referenceNo = "AP-TX-${item.id}-8829",
                                type = if (item.isIncoming) ReceiptType.FAST_TRANSFER else ReceiptType.GROUP_EXPENSE,
                                title = item.title,
                                totalAmount = item.amount,
                                senderName = item.payerName,
                                receiverName = "Mehmet Dilovan",
                                date = item.dateGroup,
                                time = item.time,
                                category = if (item.categorySubtitle.contains("Market")) "Market & Alışveriş" else "Restoran & Kafe",
                                participants = item.participants.map { participantName ->
                                    ReceiptParticipant(
                                        name = participantName,
                                        tag = if (participantName.contains("Sen")) "Arda#1453" else "@${participantName.split(" ").first().lowercase()}",
                                        shareAmount = item.amount / item.participants.size,
                                        isPaid = true
                                    )
                                },
                                savingsAmount = if (!item.isIncoming) 45.0 else null,
                                note = item.categorySubtitle
                            )
                        }
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFFF1F5F9),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = null,
                                    tint = if (item.isIncoming) PrimaryEmerald else Color(0xFF0F172A),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = item.title,
                                color = Color(0xFF0F172A),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${item.categorySubtitle} • ${item.time}",
                                color = Color(0xFF64748B),
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = if (item.isIncoming) "tahsil edildi" else "bölüşüldü",
                            color = if (item.isIncoming) PrimaryEmerald else Color(0xFF64748B),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(1.dp))
                        if (isLocked) {
                            MaskedFinancialText(
                                amount = item.amount,
                                isLocked = true,
                                color = if (item.isIncoming) PrimaryEmerald else Color(0xFF0F172A),
                                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            )
                        } else {
                            Text(
                                text = "${if (item.isIncoming) "+" else ""}${String.format(java.util.Locale.US, "%.2f", item.amount)} ₺",
                                color = if (item.isIncoming) PrimaryEmerald else Color(0xFF0F172A),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
            }
        }
    }

    // Official AradaPay Receipt Modal Sheet
    AradaPayReceiptModalSheet(
        receipt = activeReceiptForModal,
        onDismiss = { activeReceiptForModal = null }
    )
}
