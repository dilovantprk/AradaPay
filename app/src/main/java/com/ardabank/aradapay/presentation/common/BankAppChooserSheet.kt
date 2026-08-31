package com.ardabank.aradapay.presentation.common

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ardabank.aradapay.domain.model.SupportedBank
import com.ardabank.aradapay.presentation.components.bounceClick
import com.ardabank.aradapay.presentation.theme.PrimaryEmerald
import com.ardabank.aradapay.presentation.theme.PrimaryEmeraldContainer
import com.ardabank.aradapay.util.BankLauncherHelper
import java.util.Locale

@Composable
fun BankAppChooserScreen(
    recipientName: String,
    recipientIban: String,
    amount: Double,
    note: String = "",
    onDismiss: () -> Unit,
    onBankSelected: (SupportedBank) -> Unit = {}
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }

    BackHandler { onDismiss() }

    val allBanks = remember {
        SupportedBank.values().toList().sortedWith(
            compareByDescending<SupportedBank> { BankLauncherHelper.isBankInstalled(context, it.packageName) }
                .thenBy { it.shortName }
        )
    }

    val filteredBanks = remember(searchQuery, allBanks) {
        if (searchQuery.isBlank()) allBanks
        else allBanks.filter {
            it.bankName.contains(searchQuery, ignoreCase = true) ||
            it.shortName.contains(searchQuery, ignoreCase = true)
        }
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
                .imePadding()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. TOP BAR
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledTonalIconButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp),
                    colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = Color(0xFFF1F5F9)),
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
                    text = "Banka Uygulamasıyla Öde",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = PrimaryEmeraldContainer
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = PrimaryEmerald,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "FAST",
                            color = PrimaryEmerald,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

            // 2. TRANSFER SUMMARY CARD
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFFF8FAFC),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "ALICI",
                                color = Color(0xFF64748B),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.6.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = recipientName,
                                color = Color(0xFF0F172A),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "FAST TUTARI",
                                color = Color(0xFF64748B),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.6.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${String.format(Locale.US, "%.2f", amount)} ₺",
                                color = PrimaryEmerald,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp
                            )
                        }
                    }

                    HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 0.8.dp)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                            Text(
                                text = "ALICI FAST IBAN",
                                color = Color(0xFF64748B),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.6.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = recipientIban,
                                color = Color(0xFF334155),
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = PrimaryEmeraldContainer,
                            modifier = Modifier
                                .bounceClick {
                                    val cleanIban = recipientIban.replace(" ", "").trim()
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("IBAN", cleanIban))
                                    Toast.makeText(context, "IBAN Panoya Kopyalandı", Toast.LENGTH_SHORT).show()
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Kopyala",
                                    tint = PrimaryEmerald,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Kopyala",
                                    color = PrimaryEmerald,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            // 3. SEARCH BAR FOR BANKS
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFF1F5F9)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Ara",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = "Banka adı ara (Garanti, Akbank, İş...)",
                                color = Color(0xFF94A3B8),
                                fontSize = 14.sp
                            )
                        }
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
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    if (searchQuery.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Temizle",
                            tint = Color(0xFF64748B),
                            modifier = Modifier
                                .size(18.dp)
                                .clickable { searchQuery = "" }
                        )
                    }
                }
            }

            // 4. BANKS 3-COLUMN GRID
            Text(
                text = "BANKANIZI SEÇİN",
                color = Color(0xFF64748B),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(filteredBanks, key = { it.packageName }) { bank ->
                    val isInstalled = BankLauncherHelper.isBankInstalled(context, bank.packageName)

                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = if (isInstalled) Color(0xFFF0FDF4) else Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, if (isInstalled) PrimaryEmerald.copy(alpha = 0.3f) else Color(0xFFE2E8F0)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .bounceClick {
                                BankLauncherHelper.openBankOrStore(
                                    context = context,
                                    bank = bank,
                                    iban = recipientIban,
                                    amount = amount,
                                    note = note,
                                    recipientName = recipientName
                                )
                                onBankSelected(bank)
                                onDismiss()
                            }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // REAL OFFICIAL APP ICON OR AUTHENTIC BRAND BADGE
                            val installedDrawable = remember(bank.packageName) {
                                BankLauncherHelper.getBankAppIcon(context, bank.packageName)
                            }
                            val appBitmap = remember(installedDrawable) {
                                try {
                                    installedDrawable?.toBitmap(120, 120)?.asImageBitmap()
                                } catch (_: Exception) {
                                    null
                                }
                            }

                            if (appBitmap != null) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color.White,
                                    shadowElevation = 2.dp,
                                    modifier = Modifier.size(46.dp)
                                ) {
                                    Image(
                                        bitmap = appBitmap,
                                        contentDescription = bank.bankName,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(RoundedCornerShape(12.dp))
                                    )
                                }
                            } else {
                                BankBrandLogoBadge(bank = bank)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = bank.shortName,
                                color = Color(0xFF0F172A),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isInstalled) PrimaryEmeraldContainer else Color(0xFFF1F5F9)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (isInstalled) Icons.Default.CheckCircle else Icons.Default.Download,
                                        contentDescription = null,
                                        tint = if (isInstalled) PrimaryEmerald else Color(0xFF64748B),
                                        modifier = Modifier.size(10.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = if (isInstalled) "Yüklü" else "Yükle",
                                        color = if (isInstalled) PrimaryEmerald else Color(0xFF64748B),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BankBrandLogoBadge(bank: SupportedBank) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(bank.primaryColorHex),
        shadowElevation = 1.dp,
        modifier = Modifier.size(46.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            when (bank) {
                SupportedBank.AKBANK -> {
                    Text("akbank", color = Color.White, fontWeight = FontWeight.Black, fontSize = 10.sp, letterSpacing = (-0.5).sp)
                }
                SupportedBank.GARANTI -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("♣", color = Color(0xFF86EFAC), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("BBVA", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 9.sp)
                    }
                }
                SupportedBank.IS_BANKASI -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("İŞ", color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
                        Text("BANK", color = Color(0xFF93C5FD), fontWeight = FontWeight.Bold, fontSize = 8.sp)
                    }
                }
                SupportedBank.YAPI_KREDI -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("♈", color = Color(0xFF93C5FD), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("YKB", color = Color.White, fontWeight = FontWeight.Black, fontSize = 9.sp)
                    }
                }
                SupportedBank.ZIRAAT -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🌾", color = Color(0xFFFDE047), fontSize = 12.sp)
                        Text("ZİRAAT", color = Color.White, fontWeight = FontWeight.Black, fontSize = 8.sp)
                    }
                }
                SupportedBank.VAKIFBANK -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("V", color = Color(0xFF1E293B), fontWeight = FontWeight.Black, fontSize = 16.sp)
                        Text("VAKIF", color = Color(0xFF334155), fontWeight = FontWeight.ExtraBold, fontSize = 7.sp)
                    }
                }
                SupportedBank.PAPARA -> {
                    Text("papara", color = Color.White, fontWeight = FontWeight.Black, fontSize = 10.sp)
                }
                SupportedBank.ENPARA -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("enpara", color = Color.White, fontWeight = FontWeight.Black, fontSize = 9.sp)
                        Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(Color(0xFFFB923C)))
                    }
                }
                SupportedBank.KUVEYT_TURK -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("❖", color = Color(0xFF86EFAC), fontSize = 13.sp)
                        Text("KUVEYT", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 8.sp)
                    }
                }
                SupportedBank.QNB -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("QNB", color = Color.White, fontWeight = FontWeight.Black, fontSize = 11.sp)
                    }
                }
                SupportedBank.DENIZBANK -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("⛵", color = Color(0xFF67E8F9), fontSize = 12.sp)
                        Text("DENİZ", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 8.sp)
                    }
                }
                SupportedBank.TEB -> {
                    Text("TEB", color = Color.White, fontWeight = FontWeight.Black, fontSize = 13.sp)
                }
                SupportedBank.HALKBANK -> {
                    Text("HALK", color = Color.White, fontWeight = FontWeight.Black, fontSize = 11.sp)
                }
            }
        }
    }
}

// Retro-compatibility delegator for any remaining callers
@Composable
fun BankAppChooserSheet(
    recipientName: String,
    recipientIban: String,
    amount: Double,
    note: String = "",
    onDismiss: () -> Unit,
    onBankSelected: (SupportedBank) -> Unit = {}
) {
    BankAppChooserScreen(
        recipientName = recipientName,
        recipientIban = recipientIban,
        amount = amount,
        note = note,
        onDismiss = onDismiss,
        onBankSelected = onBankSelected
    )
}
