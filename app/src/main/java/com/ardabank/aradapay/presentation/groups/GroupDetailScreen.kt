package com.ardabank.aradapay.presentation.groups

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.material3.Scaffold
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import android.content.Intent
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ardabank.aradapay.data.repository.GroupRepository
import com.ardabank.aradapay.domain.model.ExpenseCategory
import com.ardabank.aradapay.domain.model.GroupMember
import com.ardabank.aradapay.presentation.components.bounceClick
import com.ardabank.aradapay.presentation.common.BankContactPickerScreen
import com.ardabank.aradapay.presentation.expense.ExpenseParticipant
import com.ardabank.aradapay.presentation.receipt.AradaPayReceipt
import com.ardabank.aradapay.presentation.receipt.AradaPayReceiptModalSheet
import com.ardabank.aradapay.presentation.receipt.ReceiptParticipant
import com.ardabank.aradapay.presentation.common.IOSSegmentedControl
import com.ardabank.aradapay.presentation.common.applePressEffect
import com.ardabank.aradapay.presentation.theme.AccentRose
import com.ardabank.aradapay.presentation.theme.AccentRoseContainer
import com.ardabank.aradapay.presentation.theme.LightBackground
import com.ardabank.aradapay.presentation.theme.PrimaryEmerald
import com.ardabank.aradapay.presentation.theme.PrimaryEmeraldContainer
import com.ardabank.aradapay.presentation.theme.SurfaceWhite
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailScreen(
    groupId: String = "grp_1",
    groupRepository: GroupRepository,
    onBackClick: () -> Unit = {},
    onAddExpenseInGroup: (groupName: String, groupId: String) -> Unit = { _, _ -> },
    onNavigateToSettleUp: (amount: Double, creditorId: String?, groupName: String?, groupId: String?) -> Unit = { _, _, _, _ -> }
) {
    val context = LocalContext.current
    var showMembersManagementModal by remember { mutableStateOf(false) }
    var showSimplifyDebtsModal by remember { mutableStateOf(false) }
    var showAddMemberModal by remember { mutableStateOf(false) }
    var showMoreMenu by remember { mutableStateOf(false) }
    var activeReceiptForModal by remember { mutableStateOf<AradaPayReceipt?>(null) }

    val groups by groupRepository.groups.collectAsState()
    val groupExpensesMap by groupRepository.groupExpenses.collectAsState()

    val group = groups.find { it.id == groupId } ?: groups.firstOrNull() ?: return
    val groupExpenses = groupExpensesMap[group.id] ?: emptyList()
    val groupNetBalance = group.userBalance

    fun shareGroupWhatsAppSummary() {
        val steps = groupRepository.getSimplifyDebtsSuggestions(group.id)
        val mySteps = steps.filter { it.fromUserId == "me" || it.toUserId == "me" || it.fromUserId == "1" || it.toUserId == "1" }
        val sb = StringBuilder()
        sb.append("🏖️ ${group.name} - AradaPay Hesap Özeti\n")
        sb.append("Toplam Harcama: ${String.format(java.util.Locale.US, "%.2f", group.totalExpenses)} ₺\n\n")
        val myMember = group.members.find { it.id == "me" || it.id == "1" }
        if (myMember != null) {
            val sign = if (myMember.balanceInGroup >= 0) "+" else ""
            val status = if (myMember.balanceInGroup >= 0) "Alacaklısın" else "Borçlusun"
            sb.append("📊 Senin Durumun: $sign${String.format(java.util.Locale.US, "%.2f", myMember.balanceInGroup)} ₺ ($status)\n")
        }
        if (mySteps.isNotEmpty()) {
            sb.append("\n⚡ Senin FAST Transferlerin:\n")
            mySteps.forEach { s ->
                sb.append("👉 ${s.fromUserName} -> ${s.toUserName}: ${String.format(java.util.Locale.US, "%.2f", s.amount)} ₺\n")
            }
        }
        sb.append("\nAradaPay ile fitleşildi ✨")

        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, sb.toString())
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "${group.name} Özetini Paylaş")
        context.startActivity(shareIntent)
    }

    val candidateFriendsToAdd = listOf(
        "4" to "Elif Şahin",
        "5" to "Burak Öztürk",
        "6" to "Selin Aydın",
        "7" to "Caner Erkin",
        "8" to "Deniz Çelik"
    ).filter { candidate -> group.members.none { it.id == candidate.first } }

    if (showAddMemberModal) {
        val candidateParticipants = candidateFriendsToAdd.map { (id, name) ->
            ExpenseParticipant(
                id = id,
                name = name,
                tag = "@${name.lowercase().replace(" ", "")}",
                avatar = name.take(2).uppercase()
            )
        }
        BankContactPickerScreen(
            title = "Grup Üyesi Ekle",
            allFriends = candidateParticipants,
            selectedIds = emptySet(),
            onDismiss = { showAddMemberModal = false },
            onConfirmSelection = { selectedIds, updatedFriends ->
                selectedIds.forEach { memberId ->
                    val found = updatedFriends.find { it.id == memberId }
                    if (found != null) {
                        groupRepository.addMemberToGroup(
                            groupId = group.id,
                            member = GroupMember(
                                id = found.id,
                                name = found.name,
                                avatar = found.avatar,
                                tag = found.tag,
                                balanceInGroup = 0.0
                            )
                        )
                    }
                }
                showAddMemberModal = false
                Toast.makeText(context, "Grup üyeleri güncellendi", Toast.LENGTH_SHORT).show()
            }
        )
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        // 1. TOP BAR
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
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

                Text(
                    text = group.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    maxLines = 1,
                    modifier = Modifier.weight(1f, fill = false).padding(horizontal = 8.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // WhatsApp & Text Share
                    FilledTonalIconButton(
                        onClick = { shareGroupWhatsAppSummary() },
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = Color(0xFFF1F5F9)
                        ),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Özeti Paylaş",
                            tint = Color(0xFF0F172A),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Direct Edit Group Button
                    FilledTonalIconButton(
                        onClick = { showMembersManagementModal = true },
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = Color(0xFFF1F5F9)
                        ),
                        modifier = Modifier.size(40.dp).bounceClick { showMembersManagementModal = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Grubu Düzenle",
                            tint = Color(0xFF0F172A),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
        }

        // 2. HERO: GRUP BİLGİLERİ VE İSTATİSTİKLER
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Grup kimlik satırı: Kategori ikonu + ad + durum rozeti
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    val categoryIcon = when (group.category.lowercase()) {
                        "tatil", "seyahat" -> Icons.Default.LocalGasStation
                        "ev", "konut" -> Icons.Default.Home
                        "yemek", "restoran" -> Icons.Default.Fastfood
                        else -> Icons.Default.Group
                    }
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFF1F5F9),
                        modifier = Modifier.size(52.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = categoryIcon,
                                contentDescription = null,
                                tint = Color(0xFF0F172A),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    // Grup adı + kategori + üye sayısı
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = group.name,
                            color = Color(0xFF0F172A),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${group.category} • ${group.members.size} Üye",
                            color = Color(0xFF64748B),
                            fontSize = 13.sp
                        )
                    }
                }

                // Üye avatarları satırı
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    group.members.take(5).forEach { member ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFF1F5F9),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = member.avatar,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                            }
                        }
                    }
                    if (group.members.size > 5) {
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "+${group.members.size - 5} kişi",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // İstatistik şeridi
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFF8FAFC))
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${groupExpenses.size}",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.height(1.dp))
                        Text(
                            text = "Harcama",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Box(modifier = Modifier.width(1.dp).height(30.dp).background(Color(0xFFE2E8F0)).align(Alignment.CenterVertically))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val totalSpent = groupExpenses.sumOf { it.totalAmount }
                        Text(
                            text = "${String.format(java.util.Locale.US, "%.0f", totalSpent)} ₺",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.height(1.dp))
                        Text(
                            text = "Toplam",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Box(modifier = Modifier.width(1.dp).height(30.dp).background(Color(0xFFE2E8F0)).align(Alignment.CenterVertically))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val settledCount = groupExpenses.count { it.isSettled }
                        Text(
                            text = "$settledCount/${groupExpenses.size}",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PrimaryEmerald
                        )
                        Spacer(modifier = Modifier.height(1.dp))
                        Text(
                            text = "Fitleşildi",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // CTA Butonları (Harcama Ekle & Öde / Fitleş)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { onAddExpenseInGroup(group.name, group.id) },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryEmerald,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Harcama Ekle",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    FilledTonalButton(
                        onClick = { onNavigateToSettleUp(abs(groupNetBalance), null, group.name, group.id) },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color(0xFF0F172A),
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Payment,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Öde & Fitleş",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
        }

        // 3. GRUP HARCAMALARI LİSTESİ
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "GRUP HARCAMALARI (${groupExpenses.size})",
                    color = Color(0xFF64748B),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
            }
        }

        if (groupExpenses.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Henüz harcama eklenmemiş",
                        color = Color(0xFF64748B),
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { onAddExpenseInGroup(group.name, group.id) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryEmerald,
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("İlk Harcamayı Ekle", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        } else {
            itemsIndexed(groupExpenses) { index, item ->
                val icon = when (item.category) {
                    ExpenseCategory.DINING -> Icons.Default.Fastfood
                    ExpenseCategory.GROCERIES -> Icons.Default.ShoppingCart
                    ExpenseCategory.HOUSING, ExpenseCategory.UTILITIES -> Icons.Default.Home
                    ExpenseCategory.TRAVEL -> Icons.Default.LocalGasStation
                    else -> Icons.Default.ShoppingCart
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { }
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
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = Color(0xFF0F172A),
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
                                text = "${item.payerName} • Toplam ${String.format(java.util.Locale.US, "%.0f", item.totalAmount)} ₺",
                                color = Color(0xFF64748B),
                                fontSize = 12.sp
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        if (item.isSettled) {
                            Text(
                                text = "fitleşildi",
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        } else if (item.yourShare > 0) {
                            Text(
                                text = "+${String.format(java.util.Locale.US, "%.2f", item.yourShare)} ₺",
                                color = PrimaryEmerald,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "sen verdin",
                                color = PrimaryEmerald,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        } else {
                            Text(
                                text = "-${String.format(java.util.Locale.US, "%.2f", abs(item.yourShare))} ₺",
                                color = AccentRose,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "senin payın",
                                color = AccentRose,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                if (index < groupExpenses.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(start = 74.dp),
                        color = Color(0xFFF1F5F9),
                        thickness = 1.dp
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    // =========================================================================
    // 1. GROUP EDIT & MEMBERS MANAGEMENT FULL-PAGE SCREEN
    // =========================================================================
    if (showMembersManagementModal) {
        EditGroupScreen(
            groupId = group.id,
            groupRepository = groupRepository,
            onBackClick = { showMembersManagementModal = false },
            onGroupDeleted = onBackClick
        )
        return
    }

    // =========================================================================
    // 2. SIMPLIFY DEBTS FULL-PAGE INTRINSIC SCREEN
    // =========================================================================
    if (showSimplifyDebtsModal) {
        BackHandler { showSimplifyDebtsModal = false }

        val suggestedSteps = remember(group.id, group.members) {
            groupRepository.getSimplifyDebtsSuggestions(group.id)
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
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // Top Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledTonalIconButton(
                        onClick = { showSimplifyDebtsModal = false },
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
                        text = "Borç Sadeleştirme",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = PrimaryEmeraldContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ElectricBolt,
                            contentDescription = null,
                            tint = PrimaryEmerald,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Akıllı Borç Sadeleştirme",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryEmerald
                            )
                            Text(
                                text = "Gereksiz ara transferleri sıfırlayan doğrudan FAST transfer planı.",
                                color = Color(0xFF0F172A),
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                val myTransferSteps = remember(suggestedSteps) {
                    suggestedSteps.filter { it.fromUserId == "me" || it.toUserId == "me" || it.fromUserId == "1" || it.toUserId == "1" }
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (myTransferSteps.isNotEmpty()) "Sana Ait Sadeleştirilmiş ${myTransferSteps.size} FAST Transferi" else "Senin adına yapılması gereken bir transfer bulunmuyor",
                        color = Color(0xFF64748B),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    myTransferSteps.forEachIndexed { index, step ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${step.fromUserName} ➔ ${step.toUserName}",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "${String.format(java.util.Locale.US, "%.2f", step.amount)} ₺",
                                fontWeight = FontWeight.Bold,
                                color = PrimaryEmerald,
                                fontSize = 15.sp
                            )
                        }

                        if (index < myTransferSteps.lastIndex) {
                            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 0.8.dp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF1E293B),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .bounceClick { showSimplifyDebtsModal = false }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("Tamam, Anladım", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                    }
                }
            }
        }
        return
    }

    AradaPayReceiptModalSheet(
        receipt = activeReceiptForModal,
        onDismiss = { activeReceiptForModal = null }
    )
}
