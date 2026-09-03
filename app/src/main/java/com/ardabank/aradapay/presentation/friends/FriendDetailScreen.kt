package com.ardabank.aradapay.presentation.friends

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.material3.Scaffold
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import com.ardabank.aradapay.presentation.common.BankAppChooserSheet
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import kotlinx.coroutines.delay
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ardabank.aradapay.domain.repository.GroupRepository
import com.ardabank.aradapay.domain.model.User
import com.ardabank.aradapay.presentation.components.FinancialHeroAmountCard
import com.ardabank.aradapay.presentation.components.QuickActionChip
import com.ardabank.aradapay.presentation.components.SlideToConfirmButton
import com.ardabank.aradapay.presentation.components.bounceClick
import com.ardabank.aradapay.presentation.receipt.AradaPayReceipt
import com.ardabank.aradapay.presentation.receipt.AradaPayReceiptModalSheet
import com.ardabank.aradapay.presentation.receipt.ReceiptParticipant
import com.ardabank.aradapay.presentation.receipt.ReceiptType
import com.ardabank.aradapay.presentation.settle.SettleUpScreen
import com.ardabank.aradapay.presentation.theme.AccentRose
import com.ardabank.aradapay.presentation.theme.AccentRoseContainer
import com.ardabank.aradapay.presentation.theme.PrimaryEmerald
import com.ardabank.aradapay.presentation.theme.PrimaryEmeraldContainer
import com.ardabank.aradapay.util.NotificationHelper
import kotlin.math.abs

data class PartialPaymentLog(
    val id: String,
    val payerName: String,
    val amount: Double,
    val method: String,
    val date: String
)

data class ItemizedExpense(
    val id: String,
    val title: String,
    val date: String,          // "24 Ağu, 14:30"
    val categoryName: String,  // "Yeme & İçme", "Market", "Kafe"
    val totalAmount: Double,
    val payerInfo: String,
    val yourInitialShare: Double,
    var alreadyPaidAmount: Double,
    val isIncoming: Boolean,   // true: sen ödedin (alacaklısın), false: arkadaş ödedi (borçlusun)
    val paymentLogs: MutableList<PartialPaymentLog> = mutableListOf(),
    val icon: ImageVector = Icons.AutoMirrored.Filled.ReceiptLong
) {
    val remainingShare: Double
        get() = maxOf(0.0, yourInitialShare - alreadyPaidAmount)

    val isFullySettled: Boolean
        get() = remainingShare <= 0.001
}

data class FriendProfileData(
    val fullName: String,
    val tag: String,
    val avatarEmoji: String,
    val iban: String,
    val baseBalance: Double,
    val isCreditor: Boolean,
    val isBalanced: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendDetailScreen(
    friendId: String = "1",
    groupRepository: GroupRepository? = null,
    onBackClick: () -> Unit = {},
    onAddExpenseWithFriend: (friendName: String) -> Unit = {},
    onSettleUpWithFriend: (friendName: String) -> Unit = {},
    onGroupClick: (groupId: String) -> Unit = {}
) {
    val context = LocalContext.current

    val profileMap = mapOf(
        "kaan" to FriendProfileData("Kaan", "Kaan#5674", "K", "TR64 0006 2000 0000 9988 7766 55", 154.0, isCreditor = true, isBalanced = false),
        "1" to FriendProfileData("Ahmet Yılmaz", "Ahmet#7821", "AY", "TR64 0006 2000 0000 1122 3344 55", 350.0, isCreditor = true, isBalanced = false),
        "ahmet" to FriendProfileData("Ahmet Yılmaz", "Ahmet#7821", "AY", "TR64 0006 2000 0000 1122 3344 55", 350.0, isCreditor = true, isBalanced = false),
        "2" to FriendProfileData("Zeynep Kaya", "Zeynep#3412", "ZK", "TR64 0006 2000 0000 2233 4455 66", 180.0, isCreditor = true, isBalanced = false),
        "zeynep" to FriendProfileData("Zeynep Kaya", "Zeynep#3412", "ZK", "TR64 0006 2000 0000 2233 4455 66", 180.0, isCreditor = true, isBalanced = false),
        "3" to FriendProfileData("Mert Demir", "Mert#9015", "MD", "TR64 0006 2000 0000 3344 5566 77", 120.0, isCreditor = false, isBalanced = false),
        "mert" to FriendProfileData("Mert Demir", "Mert#9015", "MD", "TR64 0006 2000 0000 3344 5566 77", 120.0, isCreditor = false, isBalanced = false),
        "4" to FriendProfileData("Elif Şahin", "Elif#4420", "EŞ", "TR64 0006 2000 0000 4455 6677 88", 450.0, isCreditor = true, isBalanced = false),
        "elif" to FriendProfileData("Elif Şahin", "Elif#4420", "EŞ", "TR64 0006 2000 0000 4455 6677 88", 450.0, isCreditor = true, isBalanced = false),
        "5" to FriendProfileData("Burak Öztürk", "Burak#6108", "BÖ", "TR64 0006 2000 0000 5566 7788 99", 230.0, isCreditor = false, isBalanced = false),
        "burak" to FriendProfileData("Burak Öztürk", "Burak#6108", "BÖ", "TR64 0006 2000 0000 5566 7788 99", 230.0, isCreditor = false, isBalanced = false),
        "6" to FriendProfileData("Selin Aydın", "Selin#2839", "SA", "TR64 0006 2000 0000 6677 8899 00", 95.0, isCreditor = true, isBalanced = false),
        "selin" to FriendProfileData("Selin Aydın", "Selin#2839", "SA", "TR64 0006 2000 0000 6677 8899 00", 95.0, isCreditor = true, isBalanced = false),
        "7" to FriendProfileData("Caner Erkin", "Caner#1903", "CE", "TR64 0006 2000 0000 7788 9900 11", 100.0, isCreditor = false, isBalanced = false),
        "caner" to FriendProfileData("Caner Erkin", "Caner#1903", "CE", "TR64 0006 2000 0000 7788 9900 11", 100.0, isCreditor = false, isBalanced = false),
        "8" to FriendProfileData("Deniz Çelik", "Deniz#5522", "DÇ", "TR64 0006 2000 0000 8899 0011 22", 0.0, isCreditor = false, isBalanced = true),
        "deniz" to FriendProfileData("Deniz Çelik", "Deniz#5522", "DÇ", "TR64 0006 2000 0000 8899 0011 22", 0.0, isCreditor = false, isBalanced = true)
    )

    val defaultFriend = FriendProfileData("Arkadaş", "user#0000", "A", "", 0.0, isCreditor = false, isBalanced = true)
    val friend = profileMap[friendId.lowercase()] ?: defaultFriend
    var activeReceiptForModal by remember { mutableStateOf<AradaPayReceipt?>(null) }
    var showSettleUpModal by remember { mutableStateOf(false) }
    var showReminderModal by remember { mutableStateOf(false) }
    var showChartsModal by remember { mutableStateOf(false) }
    var showOptionsMenu by remember { mutableStateOf(false) }
    var isIbanCopied by remember { mutableStateOf(false) }

    LaunchedEffect(isIbanCopied) {
        if (isIbanCopied) {
            delay(2000)
            isIbanCopied = false
        }
    }

    val itemizedExpenses = remember(friendId) {
        mutableStateListOf<ItemizedExpense>()
    }

    var selectedExpenseForDetail by remember { mutableStateOf<ItemizedExpense?>(null) }
    var showSharedGroupsModal by remember { mutableStateOf(false) }

    val currentTotalReceivable = itemizedExpenses.filter { it.isIncoming }.sumOf { it.remainingShare }
    val currentTotalPayable = itemizedExpenses.filter { !it.isIncoming }.sumOf { it.remainingShare }
    val netBalance = currentTotalReceivable - currentTotalPayable

    Scaffold(
        containerColor = Color.White
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
                .statusBarsPadding()
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // 1. TOP BAR (Matching DashboardScreen)
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
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri",
                            tint = Color(0xFF0F172A),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilledTonalIconButton(
                            onClick = {
                                val shareText = "AradaPay Hesap Özeti - ${friend.fullName}\nNet Bakiye: ${if (netBalance >= 0) "+ " else "- "}${String.format(java.util.Locale.US, "%.2f", abs(netBalance))} ₺\nFAST IBAN: ${friend.iban}"
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                }
                                context.startActivity(Intent.createChooser(intent, "${friend.fullName} Hesap Özeti"))
                            },
                            colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = Color(0xFFF1F5F9)),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "Paylaş", tint = Color(0xFF0F172A), modifier = Modifier.size(18.dp))
                        }

                        Box {
                            FilledTonalIconButton(
                                onClick = { showOptionsMenu = true },
                                colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = Color(0xFFF1F5F9)),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(Icons.Default.MoreVert, contentDescription = "Seçenekler", tint = Color(0xFF0F172A), modifier = Modifier.size(18.dp))
                            }

                            DropdownMenu(
                                expanded = showOptionsMenu,
                                onDismissRequest = { showOptionsMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("FAST IBAN Kopyala") },
                                    leadingIcon = { Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp)) },
                                    onClick = {
                                        showOptionsMenu = false
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("IBAN", friend.iban))
                                        Toast.makeText(context, "FAST IBAN Kopyalandı", Toast.LENGTH_SHORT).show()
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text(if (netBalance > 0) "Kişiyi Dürt" else "Fitleş & Öde") },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = if (netBalance > 0) Icons.Default.NotificationsActive else Icons.Default.Handshake,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    },
                                    onClick = {
                                        showOptionsMenu = false
                                        if (netBalance > 0) {
                                            showReminderModal = true
                                        } else {
                                            showSettleUpModal = true
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
            }

            // 2. HERO: KİŞİ PROFİL KARTI (Splitwise tarzı)
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(top = 20.dp, bottom = 6.dp)
                ) {
                    // Büyük Avatar + Kimlik Merkezi
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Büyük Avatar
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = Color(0xFFF1F5F9),
                            modifier = Modifier.size(72.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = friend.avatarEmoji,
                                    color = Color(0xFF0F172A),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 24.sp
                                )
                            }
                        }

                        // İsim + tag + IBAN butonu
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = friend.fullName,
                                color = Color(0xFF0F172A),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = friend.tag,
                                color = Color(0xFF64748B),
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isIbanCopied) PrimaryEmeraldContainer else Color(0xFFF1F5F9),
                                modifier = Modifier.bounceClick {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("IBAN", friend.iban))
                                    isIbanCopied = true
                                    Toast.makeText(context, "IBAN Kopyalandı", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = friend.iban,
                                        color = if (isIbanCopied) PrimaryEmerald else Color(0xFF334155),
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 11.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = if (isIbanCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                                        contentDescription = if (isIbanCopied) "Kopyalandı" else "Kopyala",
                                        tint = if (isIbanCopied) PrimaryEmerald else Color(0xFF64748B),
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                            }
                        }
                    }

                Spacer(modifier = Modifier.height(16.dp))

                // İstatistik Şeridi (Splitwise tarzı)
                Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFF8FAFC),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${itemizedExpenses.size}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF0F172A)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Ortak Harcama",
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B),
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Box(modifier = Modifier.width(1.dp).height(36.dp).background(Color(0xFFE2E8F0)).align(Alignment.CenterVertically))

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                val totalShared = itemizedExpenses.sumOf { it.totalAmount / 2 }
                                Text(
                                    text = "${String.format(java.util.Locale.US, "%.0f", totalShared)} ₺",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF0F172A)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Toplam Bölüşülen",
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B),
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Box(modifier = Modifier.width(1.dp).height(36.dp).background(Color(0xFFE2E8F0)).align(Alignment.CenterVertically))

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                val settledCount = itemizedExpenses.count { it.remainingShare == 0.0 }
                                Text(
                                    text = "$settledCount/${itemizedExpenses.size}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = PrimaryEmerald
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Fitleşildi",
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // CTA ROW
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = PrimaryEmerald,
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .bounceClick { onAddExpenseWithFriend(friend.fullName) }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
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
                            color = if (netBalance > 0) Color(0xFF1E3A5F) else Color(0xFF1E293B),
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .bounceClick {
                                    if (netBalance > 0) {
                                        showReminderModal = true
                                    } else {
                                        showSettleUpModal = true
                                    }
                                }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = if (netBalance > 0) Icons.Default.NotificationsActive else Icons.Default.Payment,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (netBalance > 0) "Hatırlat & Dürt" else "Öde & Fitleş",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
            }

            // 4. SECTION HEADER: ORTAK HARCAMALAR
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Ortak Harcamalar (${itemizedExpenses.size})",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF0F172A)
                    )

                    val sharedGroups = remember(friendId, friend.fullName, groupRepository) {
                        val fromRepo = groupRepository?.getSharedGroupsWithFriend(friendId, friend.fullName) ?: emptyList()
                        if (fromRepo.isNotEmpty()) fromRepo
                        else {
                            listOfNotNull(
                                groupRepository?.getGroupById("grp_1"),
                                groupRepository?.getGroupById("grp_3")
                            )
                        }
                    }
                    if (sharedGroups.isNotEmpty()) {
                        Text(
                            text = "${sharedGroups.size} Ortak Grup",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryEmerald,
                            modifier = Modifier
                                .bounceClick { showSharedGroupsModal = true }
                                .padding(4.dp)
                        )
                    }
                }
                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
            }

            // 5. TRANSACTION ROWS
            if (itemizedExpenses.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 36.dp, horizontal = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Henüz Ortak Harcama Yok",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Bu kişiyle paylaşılan harcama veya işlem bulunmuyor.",
                            fontSize = 13.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            } else {
                itemsIndexed(itemizedExpenses) { index, expense ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedExpenseForDetail = expense }
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
                                    imageVector = expense.icon,
                                    contentDescription = null,
                                    tint = Color(0xFF0F172A),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = expense.title,
                                color = Color(0xFF0F172A),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${expense.payerInfo} • ${expense.date}",
                                color = Color(0xFF64748B),
                                fontSize = 12.sp
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = if (expense.isFullySettled) "fitleşildi" else if (expense.isIncoming) "alacağın" else "borcun",
                            color = Color(0xFF64748B),
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (expense.isFullySettled) "0,00 ₺" else "${if (expense.isIncoming) "+" else "-"} ${String.format(java.util.Locale.US, "%.2f", expense.remainingShare)} ₺",
                            color = if (expense.isFullySettled) Color(0xFF64748B) else if (expense.isIncoming) PrimaryEmerald else AccentRose,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
                if (index < itemizedExpenses.size - 1) {
                    HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
                }
            }
        }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // =========================================================================
    // 0. ORTAK GRUPLAR ALTTAN AÇILIR MODAL SHEET
    // =========================================================================
    if (showSharedGroupsModal) {
        val sharedGroups = remember(friendId, friend.fullName, groupRepository) {
            groupRepository?.getSharedGroupsWithFriend(friendId, friend.fullName) ?: emptyList()
        }

        ModalBottomSheet(
            onDismissRequest = { showSharedGroupsModal = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            dragHandle = { BottomSheetDefaults.DragHandle(color = Color(0xFFCBD5E1)) }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .padding(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Ortak Gruplar (${sharedGroups.size})",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${friend.fullName} ile dahil olduğun gruplar",
                            color = Color(0xFF64748B),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    FilledTonalIconButton(
                        onClick = { showSharedGroupsModal = false },
                        shape = RoundedCornerShape(12.dp),
                        colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = Color(0xFFF1F5F9)),
                        modifier = Modifier.size(36.dp).bounceClick { showSharedGroupsModal = false }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Kapat",
                            tint = Color(0xFF0F172A),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(sharedGroups, key = { it.id }) { grp ->
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFFF8FAFC),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .bounceClick {
                                    showSharedGroupsModal = false
                                    onGroupClick(grp.id)
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = PrimaryEmeraldContainer,
                                        modifier = Modifier.size(42.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = if (grp.emoji.isNotBlank()) grp.emoji else grp.name.take(2).uppercase(),
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = PrimaryEmerald
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Text(
                                            text = grp.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = Color(0xFF0F172A)
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "${grp.members.size} Üye • ${grp.category}",
                                            fontSize = 12.sp,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                }

                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = "Grup Detayı",
                                    tint = Color(0xFF94A3B8),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // =========================================================================
    // 1. REMINDER MODAL SHEET (Ödeme Hatırlat & Dürt)
    // =========================================================================
    if (showReminderModal) {
        ModalBottomSheet(
            onDismissRequest = { showReminderModal = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            dragHandle = { BottomSheetDefaults.DragHandle(color = Color(0xFFCBD5E1)) }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .padding(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${friend.fullName} Kişisine Hatırlat",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Bakiyeyi kapatmak için hatırlatma kanalı seçin",
                            color = Color(0xFF64748B),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    FilledTonalIconButton(
                        onClick = { showReminderModal = false },
                        shape = RoundedCornerShape(12.dp),
                        colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = Color(0xFFF1F5F9)),
                        modifier = Modifier.size(36.dp).bounceClick { showReminderModal = false }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Kapat",
                            tint = Color(0xFF0F172A),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                // Option 1: Send via AradaPay Notification
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .bounceClick {
                            showReminderModal = false
                            NotificationHelper.showSystemNotification(
                                context = context,
                                title = "👀 Masaya Ufak Bir Sinyal",
                                message = "👀 ${friend.fullName} masadaki hesabı sessizce işaret ediyor (${String.format(java.util.Locale.US, "%.2f", abs(netBalance))} ₺)."
                            )
                            Toast.makeText(context, "${friend.fullName} kişisine ufak bir sinyal çakıldı", Toast.LENGTH_SHORT).show()
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = PrimaryEmeraldContainer,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = PrimaryEmerald, modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("AradaPay ile Dürt", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Uygulama içi anlık bildirim ve ödeme dürtmesi gönder", fontSize = 12.sp, color = Color(0xFF64748B))
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Option 2: WhatsApp / SMS / ShareSheet
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .bounceClick {
                            showReminderModal = false
                            val reminderMessage = "Selam ${friend.fullName}, AradaPay'deki ${String.format(java.util.Locale.US, "%.2f", abs(netBalance))} ₺ tutarındaki ortak bakiyemizi kapatmak için FAST / IBAN bilgim: ${friend.iban} (Mehmet Dilovan). Teşekkürler!"
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, reminderMessage)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Dürtmeyi Paylaş"))
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF1F5F9),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Share, contentDescription = null, tint = Color(0xFF0F172A), modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("WhatsApp / SMS ile Dürt", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Hazır IBAN ve tutar mesaj taslağını ilet", fontSize = 12.sp, color = Color(0xFF64748B))
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }

    // =========================================================================
    // 2. HARCAMA İSTATİSTİKLERİ FULL-PAGE INTRINSIC SCREEN
    // =========================================================================
    if (showChartsModal) {
        BackHandler { showChartsModal = false }

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
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledTonalIconButton(
                        onClick = { showChartsModal = false },
                        shape = RoundedCornerShape(12.dp),
                        colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = Color(0xFFF1F5F9)),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri",
                            tint = Color(0xFF0F172A),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "${friend.fullName} ile İstatistikler",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                }

                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                // Summary Numbers Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("TOPLAM ORTAK TUTAR", color = Color(0xFF64748B), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("968,00 ₺", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = Color(0xFF0F172A))
                    }
                    HorizontalDivider(modifier = Modifier.height(36.dp).width(1.dp), color = Color(0xFFF1F5F9))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("İŞLEM SAYISI", color = Color(0xFF64748B), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${itemizedExpenses.size} Adet", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = Color(0xFF0F172A))
                    }
                }

                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                Text(
                    text = "KATEGORİ DAĞILIMI",
                    color = Color(0xFF64748B),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )

                // Category Rows
                val categories = listOf(
                    Triple("Yeme & İçme", "65%", PrimaryEmerald),
                    Triple("Market & Alışveriş", "25%", Color(0xFF3B82F6)),
                    Triple("Ulaşım & Taksi", "10%", Color(0xFFF59E0B))
                )

                categories.forEach { (catName, percent, catColor) ->
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(catName, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF0F172A))
                            Text(percent, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = catColor)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { percent.replace("%", "").toFloat() / 100f },
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                            color = catColor,
                            trackColor = Color(0xFFF1F5F9)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }
        return
    }

    // =========================================================================
    // 3. ARADAPAY DIRECT PROFILE SETTLE UP (FİTLEŞME) FULL SCREEN
    // =========================================================================
    if (showSettleUpModal) {
        val user = remember(friend) {
            User(
                id = friendId,
                email = "${friend.fullName.lowercase().replace(" ", "")}@aradapay.com",
                username = friend.fullName.lowercase().replace(" ", ""),
                fullName = friend.fullName,
                iban = friend.iban,
                tag = friend.tag
            )
        }
        SettleUpScreen(
            creditorUser = user,
            owedAmount = (if (netBalance < 0) abs(netBalance) else netBalance).coerceAtLeast(0.0),
            onCancel = { showSettleUpModal = false },
            onConfirmSettlement = { amt, note ->
                showSettleUpModal = false
                itemizedExpenses.forEach { exp ->
                    if (!exp.isIncoming) exp.alreadyPaidAmount = exp.yourInitialShare
                }
                activeReceiptForModal = AradaPayReceipt(
                    receiptId = "rec_fast_${System.currentTimeMillis()}",
                    referenceNo = "AP-FAST-2026-${(100000..999999).random()}",
                    type = ReceiptType.FAST_TRANSFER,
                    title = "${friend.fullName} - Fitleşme",
                    totalAmount = amt,
                    date = "Bugün",
                    time = "Şimdi",
                    senderName = "Mehmet Dilovan (Sen)",
                    senderIban = "TR64 0006 2000 0000 1122 3344 55",
                    receiverName = friend.fullName,
                    receiverIban = friend.iban,
                    category = "FAST Fitleşme",
                    participants = listOf(
                        ReceiptParticipant("Mehmet Dilovan (Sen)", "@dilovan#1453", amt, true),
                        ReceiptParticipant(friend.fullName, friend.tag, 0.0, true)
                    ),
                    note = note.ifBlank { "AradaPay ile hesap kapatıldı" }
                )
                Toast.makeText(context, "${friend.fullName} ile fitleşildi", Toast.LENGTH_SHORT).show()
            }
        )
        return
    }

    // =========================================================================
    // 4. INDIVIDUAL EXPENSE AUDIT & RECEIPT FULL-PAGE INTRINSIC SCREEN
    // =========================================================================
    selectedExpenseForDetail?.let { expense ->
        BackHandler { selectedExpenseForDetail = null }

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
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        FilledTonalIconButton(
                            onClick = { selectedExpenseForDetail = null },
                            shape = RoundedCornerShape(12.dp),
                            colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = Color(0xFFF1F5F9)),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Geri",
                                tint = Color(0xFF0F172A),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = expense.title,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "${expense.date} • Toplam ${String.format(java.util.Locale.US, "%.2f", expense.totalAmount)} ₺",
                                color = Color(0xFF64748B),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    FilledTonalButton(
                        onClick = {
                            activeReceiptForModal = AradaPayReceipt(
                                receiptId = "rec_exp_${expense.id}",
                                referenceNo = "AP-EXP-2026-${expense.id.padStart(6, '0')}",
                                type = ReceiptType.GROUP_EXPENSE,
                                title = expense.title,
                                totalAmount = expense.totalAmount,
                                date = expense.date.split(",").first(),
                                time = "14:30",
                                senderName = expense.payerInfo.replace(" ödedi", ""),
                                receiverName = friend.fullName,
                                category = expense.categoryName,
                                participants = listOf(
                                    ReceiptParticipant("Mehmet Dilovan (Sen)", "@dilovan#1453", expense.yourInitialShare, expense.isFullySettled),
                                    ReceiptParticipant(friend.fullName, friend.tag, expense.totalAmount - expense.yourInitialShare, expense.isFullySettled)
                                ),
                                note = "Ortak harcama kaydı"
                            )
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color(0xFFF1F5F9)),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = null, modifier = Modifier.size(15.dp), tint = Color(0xFF0F172A))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Dekont", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                    }
                }

                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                // Balance Status Hero Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (expense.isIncoming) "${friend.fullName}'den Kalan Alacağın" else "${friend.fullName}'e Kalan Borcun",
                        color = Color(0xFF64748B),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (expense.isFullySettled) "0,00 ₺" else "${if (expense.isIncoming) "+" else "-"} ${String.format(java.util.Locale.US, "%.2f", expense.remainingShare)} ₺",
                        color = if (expense.isFullySettled) Color(0xFF64748B) else if (expense.isIncoming) PrimaryEmerald else AccentRose,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp
                    )
                }

                LinearProgressIndicator(
                    progress = { if (expense.yourInitialShare > 0) (expense.alreadyPaidAmount / expense.yourInitialShare).toFloat().coerceIn(0f, 1f) else 1f },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                    color = if (expense.isIncoming) PrimaryEmerald else AccentRose,
                    trackColor = Color(0xFFF1F5F9)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Mahsuplaşan: ${String.format(java.util.Locale.US, "%.2f", expense.alreadyPaidAmount)} ₺", color = Color(0xFF64748B), fontSize = 12.sp)
                    Text("Toplam Pay: ${String.format(java.util.Locale.US, "%.2f", expense.yourInitialShare)} ₺", color = Color(0xFF64748B), fontSize = 12.sp)
                }

                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                // Contextual Actions
                if (expense.isFullySettled) {
                    Surface(shape = RoundedCornerShape(12.dp), color = PrimaryEmeraldContainer, modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PrimaryEmerald, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Bu harcama payı tamamen mahsuplaşmış ve kapatılmıştır.", color = PrimaryEmerald, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                } else if (expense.isIncoming) {
                    SlideToConfirmButton(
                        text = "Bi' Sinyal Çak (Dürt)",
                        completedText = "Sinyal İletildi",
                        sliderColor = PrimaryEmerald,
                        thumbColor = Color(0xFF0F172A),
                        onConfirm = {
                            NotificationHelper.showSystemNotification(
                                context = context,
                                title = "👀 Masadaki Harcama Sinyali",
                                message = "👀 ${friend.fullName} masadaki '${expense.title}' payını (${String.format(java.util.Locale.US, "%.2f", expense.remainingShare)} ₺) sessizce işaret ediyor."
                            )
                            Toast.makeText(context, "${friend.fullName} kişisine masadaki pay hatırlatıldı", Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
        return
    }

    // Official AradaPay Receipt Modal Sheet
    AradaPayReceiptModalSheet(
        receipt = activeReceiptForModal,
        onDismiss = { activeReceiptForModal = null }
    )
}
