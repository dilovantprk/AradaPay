package com.ardabank.aradapay.presentation.expense

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ardabank.aradapay.presentation.common.applePressEffect
import com.ardabank.aradapay.presentation.theme.AccentRose
import com.ardabank.aradapay.presentation.theme.AccentRoseContainer
import com.ardabank.aradapay.presentation.theme.OnAccentRoseContainer
import com.ardabank.aradapay.presentation.theme.PrimaryEmerald
import com.ardabank.aradapay.presentation.theme.PrimaryEmeraldContainer
import com.ardabank.aradapay.presentation.theme.ShinyCyan
import com.ardabank.aradapay.presentation.theme.ShinyCyanContainer
import com.ardabank.aradapay.util.NotificationHelper

data class ParticipantPaymentStatus(
    val id: String,
    val name: String,
    val tag: String,
    val avatar: String,
    val shareAmount: Double,
    val isPaid: Boolean,
    val paidAt: String? = null,
    val paymentMethod: String? = null,
    val isPayer: Boolean = false
)

data class ExpenseTimelineEvent(
    val time: String,
    val title: String,
    val description: String,
    val isCompleted: Boolean = true
)

data class ExpenseDetailModel(
    val id: String,
    val title: String,
    val totalAmount: Double,
    val category: String,
    val createdBy: String,
    val createdAt: String,
    val location: String,
    val splitType: String,
    val participants: List<ParticipantPaymentStatus>,
    val timeline: List<ExpenseTimelineEvent>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseDetailScreen(
    expenseId: String = "1",
    onBackClick: () -> Unit = {},
    onSettleUp: (friendId: String) -> Unit = {}
) {
    val expenseDetail = remember {
        ExpenseDetailModel(
            id = expenseId,
            title = "Starbucks Kahve & Yemek",
            totalAmount = 600.0,
            category = "Yeme & İçme",
            createdBy = "Arda (Sen)",
            createdAt = "22 Ağustos 2026, 14:30",
            location = "Kadıköy Starbucks & Burger Lab",
            splitType = "3 Kişi • Eşit Bölüşüm",
            participants = listOf(
                ParticipantPaymentStatus(
                    id = "user_me",
                    name = "Arda (Sen)",
                    tag = "Arda#1453",
                    avatar = "AR",
                    shareAmount = 200.0,
                    isPaid = true,
                    paidAt = "22 Ağu, 14:30",
                    paymentMethod = "Harcamayı Yapan (Kredi Kartı)",
                    isPayer = true
                ),
                ParticipantPaymentStatus(
                    id = "1",
                    name = "Ahmet Yılmaz",
                    tag = "Ahmet#7821",
                    avatar = "AY",
                    shareAmount = 200.0,
                    isPaid = true,
                    paidAt = "22 Ağu, 15:45",
                    paymentMethod = "FAST Transfer (#TR64-FAST)",
                    isPayer = false
                ),
                ParticipantPaymentStatus(
                    id = "2",
                    name = "Zeynep Kaya",
                    tag = "Zeynep#3412",
                    avatar = "ZK",
                    shareAmount = 200.0,
                    isPaid = false,
                    paidAt = null,
                    paymentMethod = null,
                    isPayer = false
                )
            ),
            timeline = listOf(
                ExpenseTimelineEvent(
                    time = "14:30",
                    title = "Harcama Eklendi",
                    description = "Arda tarafından 600,00 ₺ tutarında harcama girildi ve 3 kişiye paylaştırıldı.",
                    isCompleted = true
                ),
                ExpenseTimelineEvent(
                    time = "15:45",
                    title = "Ahmet Yılmaz Ödeme Yaptı",
                    description = "FAST transferi ile 200,00 ₺ pay ödendi ve borç bakiyesi kapatıldı.",
                    isCompleted = true
                ),
                ExpenseTimelineEvent(
                    time = "Bekleniyor",
                    title = "Zeynep Kaya Payı",
                    description = "200,00 ₺ tutarındaki pay için ödeme onayı bekleniyor.",
                    isCompleted = false
                )
            )
        )
    }

    val totalCount = expenseDetail.participants.size
    val remainingAmount = expenseDetail.participants.filter { !it.isPaid }.sumOf { it.shareAmount }
    var activeReceiptForModal by remember { mutableStateOf<com.ardabank.aradapay.presentation.receipt.AradaPayReceipt?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
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
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = Color(0xFFF1F5F9)
                ),
                modifier = Modifier
                    .size(40.dp)
                    .applePressEffect { onBackClick() }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Geri",
                    tint = Color(0xFF0F172A),
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = "Harcama Detayı",
                color = Color(0xFF0F172A),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            FilledTonalIconButton(
                onClick = {
                    activeReceiptForModal = com.ardabank.aradapay.presentation.receipt.AradaPayReceipt(
                        receiptId = "rec_exp_${expenseDetail.id}",
                        referenceNo = "AP-EXP-2026-${(100000..999999).random()}",
                        type = com.ardabank.aradapay.presentation.receipt.ReceiptType.GROUP_EXPENSE,
                        title = expenseDetail.title,
                        totalAmount = expenseDetail.totalAmount,
                        date = expenseDetail.createdAt,
                        time = "14:30",
                        senderName = expenseDetail.createdBy,
                        senderIban = "TR64 0006 2000 0000 1122 3344 55",
                        receiverName = "Ortak Grup Hesabı",
                        category = expenseDetail.category,
                        participants = expenseDetail.participants.map {
                            com.ardabank.aradapay.presentation.receipt.ReceiptParticipant(
                                name = it.name,
                                tag = it.tag,
                                shareAmount = it.shareAmount,
                                isPaid = it.isPaid
                            )
                        },
                        savingsAmount = 75.0,
                        note = "${expenseDetail.location} • Eşit Harcama Bölüşümü"
                    )
                },
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = Color(0xFFF1F5F9)
                ),
                modifier = Modifier
                    .size(40.dp)
                    .applePressEffect {
                        activeReceiptForModal = com.ardabank.aradapay.presentation.receipt.AradaPayReceipt(
                            receiptId = "rec_exp_${expenseDetail.id}",
                            referenceNo = "AP-EXP-2026-${(100000..999999).random()}",
                            type = com.ardabank.aradapay.presentation.receipt.ReceiptType.GROUP_EXPENSE,
                            title = expenseDetail.title,
                            totalAmount = expenseDetail.totalAmount,
                            date = expenseDetail.createdAt,
                            time = "14:30",
                            senderName = expenseDetail.createdBy,
                            senderIban = "TR64 0006 2000 0000 1122 3344 55",
                            receiverName = "Ortak Grup Hesabı",
                            category = expenseDetail.category,
                            participants = expenseDetail.participants.map {
                                com.ardabank.aradapay.presentation.receipt.ReceiptParticipant(
                                    name = it.name,
                                    tag = it.tag,
                                    shareAmount = it.shareAmount,
                                    isPaid = it.isPaid
                                )
                            },
                            savingsAmount = 75.0,
                            note = "${expenseDetail.location} • Eşit Harcama Bölüşümü"
                        )
                    }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                    contentDescription = "Dekont",
                    tint = Color(0xFF0F172A),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // 2. HERO AMOUNT SECTION
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp, horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = expenseDetail.title,
                    color = Color(0xFF0F172A),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "${String.format(java.util.Locale.US, "%.2f", expenseDetail.totalAmount)} ₺",
                    color = Color(0xFF0F172A),
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontSize = 38.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Status Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (remainingAmount == 0.0) PrimaryEmeraldContainer else AccentRoseContainer
                ) {
                    Text(
                        text = if (remainingAmount == 0.0) "Tamamı Ödendi ve Kapandı" else "Zeynep Kaya payı bekleniyor (${String.format(java.util.Locale.US, "%.2f", remainingAmount)} ₺)",
                        color = if (remainingAmount == 0.0) PrimaryEmerald else OnAccentRoseContainer,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                    )
                }
            }
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

            // 3. INFO STRIP (3-column summary)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Ödeyen Kişi", color = Color(0xFF64748B), style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(expenseDetail.createdBy, color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(28.dp)
                        .background(Color(0xFFF1F5F9))
                )

                Column {
                    Text("Tarih", color = Color(0xFF64748B), style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(expenseDetail.createdAt.split(",").first().trim(), color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(28.dp)
                        .background(Color(0xFFF1F5F9))
                )

                Column {
                    Text("Kategori", color = Color(0xFF64748B), style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(expenseDetail.category, color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

            // 4. PARTICIPANTS SECTION
            Text(
                text = "KATILIMCILAR ($totalCount) • KİŞİ BAŞI 200,00 ₺",
                color = Color(0xFF64748B),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp,
                modifier = Modifier.padding(start = 20.dp, top = 16.dp, bottom = 8.dp)
            )
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

            Column(modifier = Modifier.fillMaxWidth()) {
                expenseDetail.participants.forEachIndexed { _, participant ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .applePressEffect(onClick = { })
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
                                        text = participant.avatar,
                                        color = Color(0xFF0F172A),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Text(
                                    text = participant.name,
                                    color = Color(0xFF0F172A),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (participant.isPaid && participant.paidAt != null) "${participant.paidAt} • ${participant.paymentMethod ?: "Ödendi"}" else "${participant.tag} • Ödeme bekleniyor",
                                    color = Color(0xFF64748B),
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            if (participant.isPaid) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = PrimaryEmeraldContainer
                                ) {
                                    Text(
                                        text = "ödendi",
                                        color = PrimaryEmerald,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            } else {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFFEF3C7)
                                ) {
                                    Text(
                                        text = "bekleniyor",
                                        color = Color(0xFFD97706),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "${String.format(java.util.Locale.US, "%.2f", participant.shareAmount)} ₺",
                                color = if (participant.isPaid) PrimaryEmerald else Color(0xFF0F172A),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                    HorizontalDivider(
                        color = Color(0xFFF1F5F9),
                        thickness = 1.dp
                    )
                }
            }

            // 5. TIMELINE SECTION
            Text(
                text = "İŞLEM GEÇMİŞİ & ZAMAN TÜNELİ",
                color = Color(0xFF64748B),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp,
                modifier = Modifier.padding(start = 20.dp, top = 16.dp, bottom = 8.dp)
            )
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                expenseDetail.timeline.forEachIndexed { index, event ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.width(20.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = if (event.isCompleted) PrimaryEmerald else Color(0xFFCBD5E1),
                                modifier = Modifier.size(10.dp)
                            ) {}
                            if (index < expenseDetail.timeline.size - 1) {
                                Box(
                                    modifier = Modifier
                                        .width(1.5.dp)
                                        .height(38.dp)
                                        .background(Color(0xFFF1F5F9))
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = event.title,
                                    color = Color(0xFF0F172A),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = event.time,
                                    color = Color(0xFF94A3B8),
                                    fontSize = 12.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = event.description,
                                color = Color(0xFF64748B),
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

            Spacer(modifier = Modifier.height(16.dp))
        }

        // FIXED BOTTOM ACTIONS
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Button(
                onClick = {
                    activeReceiptForModal = com.ardabank.aradapay.presentation.receipt.AradaPayReceipt(
                        receiptId = "rec_exp_${expenseDetail.id}",
                        referenceNo = "AP-EXP-2026-${(100000..999999).random()}",
                        type = com.ardabank.aradapay.presentation.receipt.ReceiptType.GROUP_EXPENSE,
                        title = expenseDetail.title,
                        totalAmount = expenseDetail.totalAmount,
                        date = expenseDetail.createdAt,
                        time = "14:30",
                        senderName = expenseDetail.createdBy,
                        senderIban = "TR64 0006 2000 0000 1122 3344 55",
                        receiverName = "Ortak Grup Hesabı",
                        category = expenseDetail.category,
                        participants = expenseDetail.participants.map {
                            com.ardabank.aradapay.presentation.receipt.ReceiptParticipant(
                                name = it.name,
                                tag = it.tag,
                                shareAmount = it.shareAmount,
                                isPaid = it.isPaid
                            )
                        },
                        savingsAmount = 75.0,
                        note = "${expenseDetail.location} • Eşit Harcama Bölüşümü"
                    )
                },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryEmerald,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .applePressEffect {
                        activeReceiptForModal = com.ardabank.aradapay.presentation.receipt.AradaPayReceipt(
                            receiptId = "rec_exp_${expenseDetail.id}",
                            referenceNo = "AP-EXP-2026-${(100000..999999).random()}",
                            type = com.ardabank.aradapay.presentation.receipt.ReceiptType.GROUP_EXPENSE,
                            title = expenseDetail.title,
                            totalAmount = expenseDetail.totalAmount,
                            date = expenseDetail.createdAt,
                            time = "14:30",
                            senderName = expenseDetail.createdBy,
                            senderIban = "TR64 0006 2000 0000 1122 3344 55",
                            receiverName = "Ortak Grup Hesabı",
                            category = expenseDetail.category,
                            participants = expenseDetail.participants.map {
                                com.ardabank.aradapay.presentation.receipt.ReceiptParticipant(
                                    name = it.name,
                                    tag = it.tag,
                                    shareAmount = it.shareAmount,
                                    isPaid = it.isPaid
                                )
                            },
                            savingsAmount = 75.0,
                            note = "${expenseDetail.location} • Eşit Harcama Bölüşümü"
                        )
                    }
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ReceiptLong,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Resmi Dekontu Görüntüle & Paylaş",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }

    // Official AradaPay Receipt Modal Sheet
    com.ardabank.aradapay.presentation.receipt.AradaPayReceiptModalSheet(
        receipt = activeReceiptForModal,
        onDismiss = { activeReceiptForModal = null }
    )
}
