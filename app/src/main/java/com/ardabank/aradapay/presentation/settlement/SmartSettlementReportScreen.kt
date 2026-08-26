package com.ardabank.aradapay.presentation.settlement

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.SyncAlt
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ardabank.aradapay.presentation.common.applePressEffect
import com.ardabank.aradapay.presentation.receipt.AradaPayReceipt
import com.ardabank.aradapay.presentation.receipt.ReceiptParticipant
import com.ardabank.aradapay.presentation.receipt.ReceiptType
import com.ardabank.aradapay.presentation.theme.LightBackground
import com.ardabank.aradapay.presentation.theme.PrimaryEmerald
import com.ardabank.aradapay.presentation.theme.PrimaryEmeraldContainer
import com.ardabank.aradapay.presentation.theme.SurfaceContainerLow
import com.ardabank.aradapay.presentation.theme.SurfaceWhite
import com.ardabank.aradapay.presentation.theme.TextPrimary
import com.ardabank.aradapay.presentation.theme.TextSecondary
import com.ardabank.aradapay.presentation.theme.TextTertiary

data class SavingsSettlementItem(
    val id: String,
    val title: String,
    val date: String,
    val participantsSummary: String,
    val totalSettledAmount: Double,
    val savedFastFee: Double,
    val preventedTransferCount: Int,
    val method: String,
    val receipt: AradaPayReceipt
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartSettlementReportScreen(
    onBackClick: () -> Unit = {},
    onNavigateToSettleUp: () -> Unit = {}
) {
    var selectedReceiptForModal by remember { mutableStateOf<AradaPayReceipt?>(null) }

    val savingsList = remember {
        listOf(
            SavingsSettlementItem(
                id = "sav_01",
                title = "3'lü Kapalı Döngü Fitleşmesi",
                date = "21 Ağustos 2026",
                participantsSummary = "Arda ➔ Zeynep ➔ Ahmet",
                totalSettledAmount = 450.0,
                savedFastFee = 45.0,
                preventedTransferCount = 3,
                method = "Kapalı Döngü Sıfırlama (0 Transfer)",
                receipt = AradaPayReceipt(
                    receiptId = "rec_sav_01",
                    referenceNo = "AP-FITLESME-2026-991204",
                    type = ReceiptType.CROSS_SETTLEMENT,
                    title = "3'lü Döngüsel Borç Fitleşmesi",
                    totalAmount = 450.0,
                    date = "21.08.2026",
                    time = "18:40",
                    senderName = "Arda (ve Ortak Grup)",
                    receiverName = "Zeynep Kaya & Ahmet Yılmaz",
                    category = "Çapraz Fitleşme",
                    participants = listOf(
                        ReceiptParticipant("Arda", "@arda#1001", 150.0, true),
                        ReceiptParticipant("Zeynep Kaya", "@zeynep#3412", 150.0, true),
                        ReceiptParticipant("Ahmet Yılmaz", "@ahmet#7821", 150.0, true)
                    ),
                    savingsAmount = 45.0,
                    note = "Döngüsel borç zinciri transfer yapılmadan sıfırlanmıştır."
                )
            ),
            SavingsSettlementItem(
                id = "sav_02",
                title = "Karşılıklı Bakiye Netleştirme",
                date = "18 Ağustos 2026",
                participantsSummary = "Elif Şahin & Arda",
                totalSettledAmount = 550.0,
                savedFastFee = 30.0,
                preventedTransferCount = 2,
                method = "Net Bakiye Fitleşmesi",
                receipt = AradaPayReceipt(
                    receiptId = "rec_sav_02",
                    referenceNo = "AP-FITLESME-2026-884102",
                    type = ReceiptType.CROSS_SETTLEMENT,
                    title = "İkili Karşılıklı Bakiye Fitleşmesi",
                    totalAmount = 550.0,
                    date = "18.08.2026",
                    time = "14:15",
                    senderName = "Elif Şahin",
                    receiverName = "Arda",
                    category = "İkili Fitleşme",
                    participants = listOf(
                        ReceiptParticipant("Elif Şahin", "@elif#4420", 350.0, true),
                        ReceiptParticipant("Arda", "@arda#1001", 200.0, true)
                    ),
                    savingsAmount = 30.0,
                    note = "350 TL ve 200 TL alacaklar net 150 TL fark ile tek adımda kapatıldı."
                )
            ),
            SavingsSettlementItem(
                id = "sav_03",
                title = "Grup İçi Çoklu Masraf Tasfiyesi",
                date = "12 Ağustos 2026",
                participantsSummary = "Tatil Grubu (4 Kişi)",
                totalSettledAmount = 3200.0,
                savedFastFee = 60.0,
                preventedTransferCount = 6,
                method = "Çoklu Graf Sadeleştirme",
                receipt = AradaPayReceipt(
                    receiptId = "rec_sav_03",
                    referenceNo = "AP-FITLESME-2026-773190",
                    type = ReceiptType.CROSS_SETTLEMENT,
                    title = "Tatil Grubu Gider Fitleşmesi",
                    totalAmount = 3200.0,
                    date = "12.08.2026",
                    time = "21:10",
                    senderName = "Arda & Ahmet Yılmaz",
                    receiverName = "Selin Aydın & Mert Demir",
                    category = "Grup Tasfiyesi",
                    participants = listOf(
                        ReceiptParticipant("Arda", "@arda#1001", 800.0, true),
                        ReceiptParticipant("Ahmet Yılmaz", "@ahmet#7821", 800.0, true),
                        ReceiptParticipant("Selin Aydın", "@selin#2839", 800.0, true),
                        ReceiptParticipant("Mert Demir", "@mert#9015", 800.0, true)
                    ),
                    savingsAmount = 60.0,
                    note = "12 adet ara transfer yerine 2 ana transferle 4 kişinin borcu sıfırlandı."
                )
            )
        )
    }

    // Apple Wallet Receipt View (Modal / Full View)
    if (selectedReceiptForModal != null) {
        val receipt = selectedReceiptForModal!!
        BackHandler { selectedReceiptForModal = null }

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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledTonalIconButton(
                        onClick = { selectedReceiptForModal = null },
                        shape = RoundedCornerShape(12.dp),
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = SurfaceWhite
                        ),
                        modifier = Modifier
                            .size(40.dp)
                            .border(0.5.dp, Color.Black.copy(alpha = 0.06f), RoundedCornerShape(12.dp))
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
                        text = "Fitleşme Dekontu",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                // Apple Wallet Pass Style Receipt Card
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = SurfaceWhite,
                    border = BorderStroke(0.5.dp, Color.Black.copy(alpha = 0.06f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = PrimaryEmeraldContainer,
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                                        contentDescription = null,
                                        tint = PrimaryEmerald,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = PrimaryEmeraldContainer
                            ) {
                                Text(
                                    text = "BAŞARILI",
                                    color = PrimaryEmerald,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }

                        Column {
                            Text(
                                text = receipt.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Ref: ${receipt.referenceNo}",
                                color = TextSecondary,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp
                            )
                        }

                        Text(
                            text = "${String.format(java.util.Locale.US, "%.2f", receipt.totalAmount)} ₺",
                            fontSize = 34.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary,
                            letterSpacing = (-0.5).sp
                        )

                        HorizontalDivider(color = Color.Black.copy(alpha = 0.05f), thickness = 0.5.dp)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Tarih & Saat", color = TextSecondary, fontSize = 13.sp)
                            Text("${receipt.date} • ${receipt.time}", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Tasarruf Edilen FAST", color = TextSecondary, fontSize = 13.sp)
                            Text("+${String.format(java.util.Locale.US, "%.2f", receipt.savingsAmount)} ₺", color = PrimaryEmerald, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        if (!receipt.note.isNullOrBlank()) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFF1F5F9),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = receipt.note ?: "",
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    } else {
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
                    text = "Mahsuplaşma Tasarrufu",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.size(40.dp))
            }
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

            // 2. HERO TASARRUF VE FAST KARTI
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TOPLAM FAST TASARRUFU",
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
                            text = "14 Transfer Önlendi",
                            color = PrimaryEmerald,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "+185,00 ₺",
                        color = PrimaryEmerald,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp
                    )

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFF1F5F9),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Savings,
                                contentDescription = null,
                                tint = PrimaryEmerald,
                                modifier = Modifier.size(24.dp)
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
                            Text("Engellenen FAST", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("14 Adet", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Netleştirilen Hacim", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("8.450,00 ₺", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("İşlem Hızı", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Anlık (0 Sn)", color = PrimaryEmerald, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

            // 3. MAHSUPLAŞMA NASIL ÇALIŞIR?
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountTree,
                        contentDescription = null,
                        tint = PrimaryEmerald,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "MAHSUPLAŞMA NASIL ÇALIŞIR?",
                        color = TextSecondary,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                }
                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                Column(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AlgorithmStepRow(
                        stepNumber = "1",
                        title = "Döngüsel Borç Birleştirme",
                        desc = "3 veya daha fazla kişi arasındaki zincirleme borçları tek bir net transfere indirger."
                    )
                    HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
                    AlgorithmStepRow(
                        stepNumber = "2",
                        title = "Karşılıklı Bakiye Netleştirme",
                        desc = "Aynı kişiye olan borç ve alacaklarınızı tek bir net fark tutarıyla eşitler."
                    )
                    HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
                    AlgorithmStepRow(
                        stepNumber = "3",
                        title = "FAST Komisyonsuz Sıfırlama",
                        desc = "Onlarca ayrı banka transferi yerine minimum işlemle tüm hesapları anında kapatır."
                    )
                }
            }
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

            // 4. GERÇEKLEŞEN FİTLEŞMELER LİSTESİ
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                            contentDescription = null,
                            tint = PrimaryEmerald,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "GERÇEKLEŞEN FİTLEŞMELER",
                            color = TextSecondary,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                    }

                    Text(
                        text = "${savingsList.size} Kayıt",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                Column(modifier = Modifier.fillMaxWidth()) {
                    savingsList.forEachIndexed { _, item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .applePressEffect(0.98f) { selectedReceiptForModal = item.receipt }
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
                                        Icon(
                                            imageVector = Icons.Default.SyncAlt,
                                            contentDescription = null,
                                            tint = PrimaryEmerald,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = item.title,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "${item.date} • ${item.participantsSummary}",
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "+${String.format(java.util.Locale.US, "%.2f", item.savedFastFee)} ₺",
                                    color = PrimaryEmerald,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${item.preventedTransferCount} FAST Tasarrufu",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                        HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 5. HEMEN FİTLEŞ EYLEM BUTONU
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = PrimaryEmerald,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(horizontal = 20.dp)
                    .applePressEffect(0.96f) { onNavigateToSettleUp() }
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.SyncAlt,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Hemen Fitleşme Başlat",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}
}

@Composable
fun AlgorithmStepRow(stepNumber: String, title: String, desc: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = CircleShape,
            color = PrimaryEmeraldContainer,
            modifier = Modifier.size(24.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = stepNumber,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryEmerald
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                fontSize = 13.sp
            )
            Text(
                text = desc,
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }
    }
}
